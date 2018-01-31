package org.fossasia.openevent.core.auth.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.core.auth.model.UploadImage;
import org.fossasia.openevent.core.auth.model.User;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.api.JWTUtils;
import org.fossasia.openevent.common.utils.Utils;
import org.json.JSONException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class EditProfileActivityViewModel extends ViewModel {

    public static final int SUCCESSFUL = 1;
    public static final int FAILED = 2;
    public static final int COMPLETE = 3;

    private RealmDataRepository realmRepo;
    private User user;
    private LiveData<User> userLiveData;
    private String uploadedImageUrl;
    private MutableLiveData<Integer> imageUploadResponse;
    private MutableLiveData<Integer> userUpdateResponse;
    final private CompositeDisposable compositeDisposable;

    public EditProfileActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<User> getUser() {
        if(userLiveData == null) {
            user = realmRepo.getUser();
            userLiveData = RealmDataRepository.asLiveDataForObject(user);
        }
        return userLiveData;
    }

    public LiveData<Integer> uploadImage(String encodedImage) {
        if(imageUploadResponse == null){
            imageUploadResponse = new MutableLiveData<>();
        }
        Disposable uploadDisposable;
        uploadDisposable = APIClient.getOpenEventAPI().uploadImage(new UploadImage(encodedImage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageResponse -> {
                    uploadedImageUrl = imageResponse.getUrl();
                    imageUploadResponse.setValue(SUCCESSFUL);
                    Timber.d("Image uploaded successfully." + " Url: " + uploadedImageUrl);
                },throwable -> {
                    imageUploadResponse.setValue(FAILED);
                    Timber.d("Error uploading image: " + throwable.getMessage());
                },() -> {
                    imageUploadResponse.setValue(COMPLETE);
                });

        compositeDisposable.add(uploadDisposable);
        return imageUploadResponse;
    }

    public LiveData<Integer> updateUser (String firstName, String lastName, String auth) {
        if (userUpdateResponse == null) {
            userUpdateResponse = new MutableLiveData<>();
        }
        int id = 0;
        try {
            id = JWTUtils.getIdentity(auth);
        } catch (JSONException e) {
            userUpdateResponse.setValue(FAILED);
            return userUpdateResponse;
        }

        User.UserBuilder builder = User.builder();
        builder.id(id).firstName(firstName).lastName(lastName);

        if (!Utils.isEmpty(uploadedImageUrl))
            builder.avatarUrl(uploadedImageUrl);

        User userUpdate = builder.build();

        Disposable updateUserDisposable;
        updateUserDisposable = APIClient.getOpenEventAPI().updateUser(userUpdate, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(user -> RealmDataRepository
                        .getDefaultInstance()
                        .saveUser(user))
                .subscribe(() -> {
                            this.user = user;
                            userUpdateResponse.setValue(SUCCESSFUL);
                            Timber.d("User data saved in database");
                        },throwable -> {
                            userUpdateResponse.setValue(FAILED);
                            Timber.d("Error updating data" + throwable.getMessage());
                        });

        compositeDisposable.add(updateUserDisposable);
        return userUpdateResponse;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
