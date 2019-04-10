package com.example.b00641907.mcnicholl_caravans;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.adapter.CaravansImageAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.CaravanInfo;
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
import java.util.ArrayList;
import java.util.List;

public class EditCaravanActivity extends BaseActivity implements View.OnClickListener, CaravansImageAdapter.OnCaravanImageActionListener {

    // Caravan Information
    CaravanInfo mCaravanInfo;

    EditText edtName;
    EditText edtDescription;
    EditText edtPrice;

    ListView lvImages;
    CaravansImageAdapter mCaravansImageAdapter;
    List<String> mImageList = new ArrayList<>();
    List<String> mNewImageList = new ArrayList<>();
    List<String> mTrashImageList = new ArrayList<>();

    private static final String FILE_UPLOADING = "Uploading";
    private static final String FILE_FAILED = "Failed";

    int updateImageIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_caravans);

        // Check Data
        mCaravanInfo = getIntent().getParcelableExtra("caravan_info");
        if (mCaravanInfo ==  null) {
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mImageList.addAll(mCaravanInfo.getImages());

        // Init UI and show original information
        edtName = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);

        edtName.setText(mCaravanInfo.getName());
        edtDescription.setText(mCaravanInfo.getDescription());
        edtPrice.setText(String.valueOf(mCaravanInfo.getPrice()));

        lvImages = findViewById(R.id.lvImages);
        mCaravansImageAdapter = new CaravansImageAdapter(mContext, mImageList, this);
        lvImages.setAdapter(mCaravansImageAdapter);

        // Add Image Button
        findViewById(R.id.btnAddImage).setOnClickListener(this);

        // Update Caravan Button
        findViewById(R.id.btnUpdate).setOnClickListener(this);

        if (mCaravanInfo.getLock() > 0) {
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
        if (viewId == R.id.btnAddImage) {
            updateImageIndex = -1;

            showImageSource();
        } else if (viewId == R.id.btnUpdate) {
            updateCaravanInfo();
        }
    }

    @Override
    public void onItemImageEdit(int position) {
        updateImageIndex = position;

        showImageSource();
    }

    @Override
    public void onItemImageRemove(final int position) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setTitle("Confirm remove");
        alertDialogBuilder.setMessage("Would you like to remove image?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                String imagePath = mImageList.get(position);

                mImageList.remove(position);
                mCaravansImageAdapter.notifyDataSetChanged();

                // Remove Old File
                if (!TextUtils.isEmpty(imagePath)) {
                    if (imagePath.startsWith("http")) {
                        mTrashImageList.add(imagePath);
                    } else {
                        new File(imagePath).delete();
                    }
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateCaravanInfo() {
        final String gasName = edtName.getText().toString().trim();
        final String gasDescription = edtDescription.getText().toString().trim();
        final String gasPrice = edtPrice.getText().toString().trim();

        hideKeyboard(edtName);
        hideKeyboard(edtDescription);
        hideKeyboard(edtPrice);

        // Check Input Fields
        if (TextUtils.isEmpty(gasName) || TextUtils.isEmpty(gasDescription) || TextUtils.isEmpty(gasPrice)) {
            showToastMessage(R.string.error_input_information);
            return;
        }

        // Check Gas Price
        int fPrice = 0;
        try {
            fPrice = Integer.parseInt(gasPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (fPrice == 0f) {
            showToastMessage(R.string.error_input_price);
            return;
        }

        if (mImageList.isEmpty()) {
            showToastMessage(R.string.error_select_image);
            return;
        }

        mCaravanInfo.setName(gasName);
        mCaravanInfo.setDescription(gasDescription);
        mCaravanInfo.setPrice(fPrice);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        int uploadFileCnt = 0;
        mNewImageList.clear();
        for (String imagePath : mImageList) {
            if (imagePath.startsWith("http")) {
                mNewImageList.add(imagePath);
            } else {
                mNewImageList.add(FILE_UPLOADING);
                uploadFileCnt++;
            }
        }

        if (uploadFileCnt > 0) {
            showProgressDialog();
            for (int i = 0; i < mImageList.size(); i++) {
                final int imageIndex = i;

                String imagePath = mImageList.get(i);

                if (!imagePath.startsWith("http")) {
                    Uri file = Uri.fromFile(new File(imagePath));
                    final StorageReference photoRef = storageReference.child(FireBaseConstants.DB_CARAVAN).child(mCaravanInfo.retrieveNodeKey()).child(file.getLastPathSegment());

                    UploadTask uploadTask = photoRef.putFile(file);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                hideProgressDialog();
                                mNewImageList.set(imageIndex, FILE_FAILED);

                                checkFileUploadingStatus();

                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return photoRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                mNewImageList.set(imageIndex, downloadUri.toString());
                            } else {
                                mNewImageList.set(imageIndex, FILE_FAILED);
                            }

                            checkFileUploadingStatus();
                        }
                    });
                }
            }
        } else {
            // Directly Update Caravan Information
            uploadCaravanInfo();
        }
    }

    private void checkFileUploadingStatus() {
        // Check Pending Upload
        for (String fileStatus : mNewImageList) {
            if (FILE_UPLOADING.equals(fileStatus)) {
                return;
            }
        }

        hideProgressDialog();

        // Remove Failed Item File
        for (int i = mNewImageList.size() - 1; i >=0; i--) {
            if (FILE_FAILED.equals(mNewImageList.get(i))) {
                mNewImageList.remove(i);
            }
        }

        // Check Image Data Validity
        if (mNewImageList.isEmpty()) {
            showAlert(R.string.error_upload_image);
            return;
        }

        uploadCaravanInfo();
    }

    private void uploadCaravanInfo() {
        mCaravanInfo.setImages(mNewImageList);

        showProgressDialog();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(FireBaseConstants.DB_CARAVAN).child(mCaravanInfo.retrieveNodeKey()).setValue(mCaravanInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressDialog();

                if (databaseError == null) {
                    showAlert(R.string.success_update_caravan, new View.OnClickListener() {
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

        // Remove Storage Files
        for (String imagePath : mTrashImageList) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(imagePath);
            if (fileReference != null) {
                fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        }
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
                    intent.putExtra("aspectX", CARAVAN_IMAGE_ASPECT_X);
                    intent.putExtra("aspectY", CARAVAN_IMAGE_ASPECT_Y);
                    intent.putExtra("outputX", CARAVAN_IMAGE_OUTPUT_X);
                    intent.putExtra("outputY", CARAVAN_IMAGE_OUTPUT_Y);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mResultImageUri);
                    startActivityForResult(intent, REQUEST_CROP);
                }
            } else if (requestCode == REQUEST_CROP) {
                File newImageFile = new File(strCropedFilePath);

                if (newImageFile.exists()) {

                    // Old File path
                    String oldFilePath = "";

                    if (updateImageIndex == -1) {
                        mImageList.add(strCropedFilePath);
                    } else {
                        // Get Old File path
                        oldFilePath = mImageList.get(updateImageIndex);

                        // Update it with New Path
                        mImageList.set(updateImageIndex, strCropedFilePath);
                    }

                    // Update Image List
                    mCaravansImageAdapter.notifyDataSetChanged();

                    // Remove Old File
                    if (!TextUtils.isEmpty(oldFilePath)) {
                        new File(oldFilePath).delete();
                    }
                }
            }
        }
    }
}
