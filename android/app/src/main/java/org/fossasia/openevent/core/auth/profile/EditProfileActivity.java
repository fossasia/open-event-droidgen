package org.fossasia.openevent.core.auth.profile;

import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.image.CircleTransform;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.auth.AuthUtil;
import org.fossasia.openevent.core.auth.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
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
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int PICK_IMAGE_REQUEST = 100;
    private String encodedImage;
    private EditProfileActivityViewModel editProfileActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        firstNameInput = (TextInputEditText) firstNameWrapper.getEditText();
        lastNameInput = (TextInputEditText) lastNameWrapper.getEditText();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editProfileActivityViewModel = ViewModelProviders.of(this).get(EditProfileActivityViewModel.class);
        editProfileActivityViewModel.getUser().observe(this, user -> {
            this.user = user;
            showUserData(user);
        });

        avatar.setOnClickListener(v -> showFileChooser());

        save.setOnClickListener(v -> {
            Views.hideKeyboard(this, this.getCurrentFocus());
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
        showProgressBar(true);
        editProfileActivityViewModel.uploadImage(encodedImage).observe(this, response -> {
            switch (response){
                case EditProfileActivityViewModel.SUCCESSFUL:
                    updateUser();
                    break;
                case EditProfileActivityViewModel.FAILED:
                    Toast.makeText(EditProfileActivity.this, R.string.error_uploading_image, Toast.LENGTH_SHORT).show();
                    break;
                case EditProfileActivityViewModel.COMPLETE:
                    updateUser();
                    break;
                default:
                    //No implementation
            }
            showProgressBar(false);
        });
    }
    
    private void updateUser() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        showProgressBar(true);

        editProfileActivityViewModel.updateUser(firstName, lastName, AuthUtil.getAuthorization()).observe(this, response -> {
            switch (response){
                case EditProfileActivityViewModel.SUCCESSFUL:
                    Toast.makeText(EditProfileActivity.this, R.string.updated_succesfully, Toast.LENGTH_SHORT).show();
                    showUserData(user);
                    finish();
                    break;
                case EditProfileActivityViewModel.FAILED:
                    Toast.makeText(EditProfileActivity.this, R.string.error_updating_data, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    //No Implementation

            }
            showProgressBar(false);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
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

            Uri imageUri = data.getData();
            Timber.d(imageUri.toString());
            UCrop.of(imageUri,Uri.fromFile(new File(getCacheDir(), imageUri.getUserInfo()+".png"))).start(this);

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

            StrategyRegistry.getInstance().getHttpStrategy().getPicassoWithCache()
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
            StrategyRegistry.getInstance().getHttpStrategy().getPicassoWithCache().load(avatarUrl)
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
