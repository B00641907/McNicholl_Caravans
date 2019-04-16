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
import com.example.b00641907.mcnicholl_caravans.model.GasInfo;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;

public class EditGasActivity extends BaseActivity implements View.OnClickListener {

    GasInfo gasInfo;

    // Image Loader
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    ImageView ivGas;
    File gasImgFile;

    EditText edtGasName;
    EditText edtGasDescription;
    EditText edtGasPrice;
    EditText edtGasWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gas);

        gasInfo = getIntent().getParcelableExtra("gas_info");
        if (gasInfo == null) {
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

        ivGas = findViewById(R.id.ivGas);
        edtGasName = findViewById(R.id.edtTitle);
        edtGasDescription = findViewById(R.id.edtDescription);
        edtGasPrice = findViewById(R.id.edtPrice);
        edtGasWeight = findViewById(R.id.edtWeight);

        ivGas.setOnClickListener(this);
        findViewById(R.id.btnEditGas).setOnClickListener(this);

        mImageLoader.displayImage(gasInfo.getImage(), ivGas, mImageOptions);
        edtGasName.setText(gasInfo.getName());
        edtGasDescription.setText(gasInfo.getDescription());
        edtGasPrice.setText(String.valueOf(gasInfo.getPricePerBox()));
        edtGasWeight.setText(String.valueOf(gasInfo.getWeightPerBox()));

        if (gasInfo.getLock() > 0) {
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
        if (viewId == R.id.ivGas) {
            showImageSource();
        } else if (viewId == R.id.btnEditGas) {
            addGasInformation();
        }
    }

    private void addGasInformation() {
        final String gasName = edtGasName.getText().toString().trim();
        final String gasDescription = edtGasDescription.getText().toString().trim();
        final String gasPrice = edtGasPrice.getText().toString().trim();
        final String gasWeight = edtGasWeight.getText().toString().trim();

        hideKeyboard(edtGasName);
        hideKeyboard(edtGasDescription);
        hideKeyboard(edtGasPrice);
        hideKeyboard(edtGasWeight);

        // Check Photo
        if (gasImgFile != null && !gasImgFile.exists()) {
            showToastMessage(R.string.error_select_gas_image);
            return;
        }

        // Check Input Fields
        if (TextUtils.isEmpty(gasName) || TextUtils.isEmpty(gasDescription) || TextUtils.isEmpty(gasPrice) || TextUtils.isEmpty(gasWeight)) {
            showToastMessage(R.string.error_input_information);
            return;
        }

        // Check Gas Price
        int fGasPrice = 0;
        try {
            fGasPrice = Integer.parseInt(gasPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (fGasPrice == 0f) {
            showToastMessage(R.string.error_input_price);
            return;
        }

        // Check Gas Weight
        int fGasWeight = 0;
        try {
            fGasWeight = Integer.parseInt(gasWeight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (fGasWeight == 0f) {
            showToastMessage(R.string.error_input_weight);
            return;
        }

        gasInfo.setName(gasName);
        gasInfo.setDescription(gasDescription);
        gasInfo.setPricePerBox(fGasPrice);
        gasInfo.setWeightPerBox(fGasWeight);

        if (gasImgFile != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(gasInfo.getImage());
            if (fileReference != null) {
                fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadGasImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToastMessage(e.getMessage());
                    }
                });
            }
        } else {
            updateGasInfo();
        }
    }

    private void uploadGasImage() {

        // Add New Node
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(FireBaseConstants.DB_GAS).child(gasImgFile.getName());

        showProgressDialog();
        UploadTask uploadTask = storageRef.putFile(Uri.fromFile(gasImgFile));
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()) {
                    hideProgressDialog();

                    throw task.getException();
                } else {
                    // Start new Task to get Download Url
                    String storageMetadata = task.getResult().getMetadata().getReference().toString();
                    gasInfo.setReference(storageMetadata);

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
                    gasInfo.setImage(downloadUri.toString());

                    // Update Gas Information
                    updateGasInfo();
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

    private void updateGasInfo() {
        String nodeKey = gasInfo.retrieveNodeKey();

        showProgressDialog();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(FireBaseConstants.DB_GAS).child(nodeKey).setValue(gasInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressDialog();

                if (databaseError == null) {
                    showAlert(R.string.success_update_gas, new View.OnClickListener() {
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
                gasImgFile = new File(strCropedFilePath);
                Bitmap photoBmp = null;

                if (gasImgFile.exists()) {


                    String decodedImgUri = Uri.fromFile(gasImgFile).toString();
                    mImageLoader.displayImage(decodedImgUri, ivGas, mImageOptions);
                }
            }
        }
    }
}
