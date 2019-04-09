package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.EventInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class EditEventActivity extends BaseActivity implements View.OnClickListener {

    EventInfo eventInfo;

    // Image Loader
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    ImageView ivEvent;
    File eventImgFile;

    EditText edtName;
    EditText edtDescription;
    EditText edtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventInfo = getIntent().getParcelableExtra("event_info");
        if (eventInfo == null) {
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext.getApplicationContext()));
        mImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_placeholder)
                .showImageOnFail(R.drawable.ic_placeholder)
                .showImageForEmptyUri(R.drawable.ic_placeholder)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ivEvent = findViewById(R.id.ivEvent);
        edtName = findViewById(R.id.edtTitle);
        edtTime = findViewById(R.id.edtTime);
        edtDescription = findViewById(R.id.edtDescription);

        ivEvent.setOnClickListener(this);
        findViewById(R.id.btnUpdate).setOnClickListener(this);

        mImageLoader.displayImage(eventInfo.getImage(), ivEvent, mImageOptions);
        edtName.setText(eventInfo.getName());
        edtTime.setText(String.valueOf(eventInfo.getTime()));
        edtDescription.setText(eventInfo.getDescription());

        if (eventInfo.getLock() > 0) {
            showAlert(R.string.error_disable_edit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.ivEvent) {
            showImageSource();
        } else if (viewId == R.id.btnUpdate) {
            updateEventInformation();
        }
    }

    private void updateEventInformation() {
        final String eventName = edtName.getText().toString().trim();
        final String eventDescription = edtDescription.getText().toString().trim();
        final String eventTime = edtTime.getText().toString().trim();

        hideKeyboard(edtName);
        hideKeyboard(edtDescription);
        hideKeyboard(edtTime);

        // Check Photo
        if (eventImgFile != null && !eventImgFile.exists()) {
            showToastMessage(R.string.error_select_event_image);
            return;
        }

        // Check Input Fields
        if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventTime)) {
            showToastMessage(R.string.error_input_information);
            return;
        }

        eventInfo.setName(eventName);
        eventInfo.setDescription(eventDescription);
        eventInfo.setTime(eventTime);

        if (eventImgFile != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(eventInfo.getImage());
            if (fileReference != null) {
                fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadEventImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToastMessage(e.getMessage());
                    }
                });
            }
        } else {
            updateEventInfo();
        }
    }

    private void uploadEventImage() {

        // Add New Node
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(FireBaseConstants.DB_EVENT).child(eventImgFile.getName());

        showProgressDialog();
        UploadTask uploadTask = storageRef.putFile(Uri.fromFile(eventImgFile));
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()) {
                    hideProgressDialog();

                    throw task.getException();
                } else {

                    // Continue with the task to get the download URL
                    return storageRef.getDownloadUrl();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                hideProgressDialog();

                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    // Update Gas Image
                    eventInfo.setImage(downloadUri.toString());

                    // Update Gas Information
                    updateEventInfo();
                } else {
                    showToastMessage(R.string.error_upload_image);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hideProgressDialog();
                showToastMessage(exception.getMessage());
            }
        });
    }

    private void updateEventInfo() {
        String nodeKey = eventInfo.retrieveNodeKey();

        showProgressDialog();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(FireBaseConstants.DB_EVENT).child(nodeKey).setValue(eventInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressDialog();

                if (databaseError == null) {
                    showAlert(R.string.success_update_event, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {
                    showToastMessage(databaseError.getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {

            if (requestCode == REQUEST_CAMERA || requestCode == REQUEST_ALBUM) {
                String mSelectedFile = "";
                if (requestCode == REQUEST_CAMERA) {
                    if (!TextUtils.isEmpty(strCameraOutputFilePath))
                        mSelectedFile = strCameraOutputFilePath;
                } else if (data != null && data.getData() != null) {
                    mSelectedFile = getRealPathFromURI(data.getData());
                }

                if (!TextUtils.isEmpty(mSelectedFile)) {
                    //Uri justTakenPictureUri = Uri.fromFile(new File(mSelectedFile));

                    File cameraFile = new File(mSelectedFile);
                    cameraFile.setReadable(true);

                    Uri justTakenPictureUri = getFileUri(cameraFile);  // Uri.fromFile(new File(mSelectedFile));

                    File outputFile = makeTempFile(JPEG_FILE_SUFFIX);
                    if (outputFile == null)
                        return;

                    strCropedFilePath = outputFile.getAbsolutePath();

                    Uri mResultImageUri = Uri.fromFile(outputFile);

                    //Uri mResultImageUri = getFileUri(outputFile);
                    //grantUriPermission("com.android.camera.action.CROP", justTakenPictureUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    Intent intent = new Intent("com.android.camera.action.CROP");

                    // Most important
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    intent.setDataAndType(/*requestCode == REQUEST_PICTURE_FROM_CAMERA ? */justTakenPictureUri/* : data.getData()*//*justTakenPictureUri*/, TYPE_IMAGE);
                    intent.putExtra("scale", true);
                    intent.putExtra("scaleUpIfNeeded", true);
                    intent.putExtra("aspectX", GAS_IMAGE_ASPECT_X);
                    intent.putExtra("aspectY", GAS_IMAGE_ASPECT_Y);
                    intent.putExtra("outputX", GAS_IMAGE_OUTPUT_X);
                    intent.putExtra("outputY", GAS_IMAGE_OUTPUT_Y);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mResultImageUri);
                    startActivityForResult(intent, REQUEST_CROP);
                }
            } else if (requestCode == REQUEST_CROP) {
                eventImgFile = new File(strCropedFilePath);
                Bitmap photoBmp = null;

                if (eventImgFile.exists()) {


                    String decodedImgUri = Uri.fromFile(eventImgFile).toString();
                    mImageLoader.displayImage(decodedImgUri, ivEvent, mImageOptions);
                }
            }
        }
    }
}
