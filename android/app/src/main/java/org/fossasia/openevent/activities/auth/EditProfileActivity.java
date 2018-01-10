package org.fossasia.openevent.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.auth.UploadImage;
import org.fossasia.openevent.data.auth.User;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.AuthUtil;
import org.fossasia.openevent.utils.CircleTransform;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.JWTUtils;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Utils;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.text_input_layout_first_name)
    TextInputLayout firstNameWrapper;
    @BindView(R.id.text_input_layout_last_name)
    TextInputLayout lastNameWrapper;
    @BindView(R.id.btnSave)
    Button save;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;

    private User user;
    private Disposable updateUserDisposable;
    private Disposable uploadImageDisposable;

    private int PICK_IMAGE_REQUEST = 100;
    private Uri imageUri;
    private String encodedImage;
    private String uploadedImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        firstNameInput = (TextInputEditText) firstNameWrapper.getEditText();
        lastNameInput = (TextInputEditText) lastNameWrapper.getEditText();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = RealmDataRepository.getDefaultInstance().getUser();
        user.addChangeListener(userModel -> {
            showUserData(user);
            Timber.d("User data loaded from database");
        });

        avatar.setOnClickListener(v -> {
            showFileChooser();
        });

        save.setOnClickListener(v -> {
            hideKeyBoard();
            saveChanges();
        });
    }

    private void saveChanges() {
        if (!Utils.isEmpty(encodedImage)) {
            //Upload image and update User
            uploadImage(encodedImage);
        } else {
            //Update User
            updateUser();
        }
    }

    private void uploadImage(String encodedImage) {
        uploadImageDisposable = APIClient.getOpenEventAPI().uploadImage(new UploadImage(encodedImage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageResponse -> {
                            uploadedImageUrl = imageResponse.getUrl();
                            Timber.d("Image uploaded successfully." + " Url: " + uploadImageDisposable);
                        },
                        throwable -> {
                            showProgressBar(false);
                            Toast.makeText(EditProfileActivity.this, R.string.error_uploading_image, Toast.LENGTH_SHORT).show();
                            Timber.d("Error uploading image: " + throwable.getMessage());
                        },
                        () -> {
                            showProgressBar(false);
                            //Image uploaded successfully updating user...
                            updateUser();
                        },
                        disposable -> showProgressBar(true));
    }

    private void updateUser() {
        if (firstNameInput == null || lastNameInput == null)
            return;

        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();

        int id = 0;
        try {
            id = JWTUtils.getIdentity(AuthUtil.getAuthorization());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        User.UserBuilder builder = User.builder();
        builder.id(id).firstName(firstName).lastName(lastName);

        if (!Utils.isEmpty(uploadedImageUrl))
            builder.avatarUrl(uploadedImageUrl);

        User updateUser = builder.build();
        updateUserDisposable = APIClient.getOpenEventAPI().updateUser(updateUser, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                            RealmDataRepository
                                    .getDefaultInstance()
                                    .saveUser(user)
                                    .subscribe();
                            this.user = user;
                            showUserData(user);
                            showProgressBar(false);
                            Timber.d("User data saved in database");
                        },
                        throwable -> {
                            showProgressBar(false);
                            Toast.makeText(EditProfileActivity.this, R.string.error_updating_data, Toast.LENGTH_SHORT).show();
                            Timber.d("Error updating data" + throwable.getMessage());
                        },
                        () -> {
                            showProgressBar(false);
                            Toast.makeText(EditProfileActivity.this, R.string.updated_succesfully, Toast.LENGTH_SHORT).show();
                            Timber.d("User data Updated");
                            finish();
                        },
                        disposable -> showProgressBar(true));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (updateUserDisposable != null && !updateUserDisposable.isDisposed())
            updateUserDisposable.dispose();
        if (uploadImageDisposable != null && !uploadImageDisposable.isDisposed())
            uploadImageDisposable.dispose();
    }

    @Override
    public void onBackPressed() {

        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();

        if (!user.isValid() || TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
            super.onBackPressed();
            return;
        }

        if (!firstName.equals(user.getFirstName()) || !lastName.equals(user.getLastName()) || !Utils.isEmpty(encodedImage)) {
            //Show confirmation dialog
            AlertDialog confirmationDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.edit_profile_confirmation)
                    .setPositiveButton(R.string.save, (dialog, which) -> saveChanges())
                    .setNegativeButton(R.string.discard, (dialog, which) -> {
                        dialog.dismiss();
                        super.onBackPressed();
                    })
                    .create();

            confirmationDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            Timber.d(imageUri.toString());
            UCrop.of(imageUri,Uri.fromFile(new File(getCacheDir(),imageUri.getUserInfo()+".png"))).start(this);

        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(resultUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            encodedImage = encodeImage(selectedImage);

            OpenEventApp.picassoWithCache
                    .load(resultUri)
                    .transform(new CircleTransform())
                    .into(avatar);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Timber.d("EditProfileActivity", "UCrop Error" + cropError);
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void showUserData(User user) {
        if (user == null || !user.isValid())
            return;

        if (firstNameInput == null || lastNameInput == null)
            return;

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String avatarUrl = user.getAvatarUrl();

        SharedPreferencesUtil.putString(ConstantStrings.USER_FIRST_NAME, user.getFirstName());
        SharedPreferencesUtil.putString(ConstantStrings.USER_LAST_NAME, user.getLastName());

        if (firstName != null) {
            firstNameInput.setText(firstName.trim());
            firstNameInput.setSelection(firstName.length());
        }
        if (lastName != null)
            lastNameInput.setText(lastName.trim());

        if (avatarUrl != null) {
            OpenEventApp.picassoWithCache.load(avatarUrl)
                    .transform(new CircleTransform())
                    .into(avatar);
        }
    }

    private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                //do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
