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

import com.example.b00641907.mcnicholl_caravans.Utils.DateUtil;
import com.example.b00641907.mcnicholl_caravans.adapter.CaravansImageAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.CaravanInfo;
import com.example.b00641907.mcnicholl_caravans.model.UserInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewCaravanActivity extends BaseActivity implements View.OnClickListener, CaravansImageAdapter.OnCaravanImageActionListener {

    EditText edtName;
    EditText edtDescription;
    EditText edtPrice;

    ListView lvImages;
    CaravansImageAdapter caravansImageAdapter;
    List<String> imageList = new ArrayList<>();
    List<String> doneList = new ArrayList<>();

    private static final String FILE_UPLOADING = "Uploading";
    private static final String FILE_FAILED = "Failed";

    CaravanInfo mCaravanInfo;

    int updateImageIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_caravans);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        edtName = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);

        // Add Image Button
        findViewById(R.id.btnAddImage).setOnClickListener(this);

        lvImages = findViewById(R.id.lvImages);
        caravansImageAdapter = new CaravansImageAdapter(mContext, imageList, this);
        lvImages.setAdapter(caravansImageAdapter);

        // Add Caravan Button
        findViewById(R.id.btnAdd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnAddImage) {
            updateImageIndex = -1;

            showImageSource();
        } else if (viewId == R.id.btnAdd) {
            addGasInformation();
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

                String imagePath = imageList.get(position);

                imageList.remove(position);
                caravansImageAdapter.notifyDataSetChanged();

                // Remove Old File
                if (!TextUtils.isEmpty(imagePath) && !imagePath.startsWith("http")) {
                    new File(imagePath).delete();
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


    private void addGasInformation() {
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

        if (imageList.isEmpty()) {
            showToastMessage(R.string.error_select_image);
            return;
        }

        // Upload Contents
        UserInfo userInfo = appSettings.getUser();
        String nodeKey = String.format("Caravan(%s)From_%s", DateUtil.toStringFormat_11(new Date()), userInfo.uName);

        mCaravanInfo = new CaravanInfo();
        mCaravanInfo.setName(gasName);
        mCaravanInfo.setDescription(gasDescription);
        mCaravanInfo.setPrice(fGasPrice);
        mCaravanInfo.setStatus(0);
        mCaravanInfo.saveNodeKey(nodeKey);
        mCaravanInfo.setFolder(nodeKey);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        doneList.clear();
        for (int i = 0; i < imageList.size(); i++) {
            doneList.add(FILE_UPLOADING);
        }

        showProgressDialog();
        for (int i = 0; i < imageList.size(); i++) {
            final int imageIndex = i;

            String photo = imageList.get(i);

            Uri file = Uri.fromFile(new File(photo));
            final StorageReference photoRef = storageReference.child(FireBaseConstants.DB_CARAVAN).child(nodeKey).child(file.getLastPathSegment());

            UploadTask uploadTask = photoRef.putFile(file);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        hideProgressDialog();
                        doneList.set(imageIndex, FILE_FAILED);

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
                        doneList.set(imageIndex, downloadUri.toString());
                    } else {
                        doneList.set(imageIndex, FILE_FAILED);
                    }

                    checkFileUploadingStatus();
                }
            });
        }
    }

    private void checkFileUploadingStatus() {
        for (String fileStatus : doneList) {
            if (FILE_UPLOADING.equals(fileStatus)) {
                return;
            }
        }

        hideProgressDialog();

        // Remove Filed File
        for (int i = doneList.size() - 1; i >=0; i--) {
            if (FILE_FAILED.equals(doneList.get(i))) {
                doneList.remove(i);
            }
        }

        if (doneList.isEmpty()) {
            showAlert(R.string.error_upload_image);
            return;
        }

        mCaravanInfo.setImages(doneList);

        showProgressDialog();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(FireBaseConstants.DB_CARAVAN).child(mCaravanInfo.retrieveNodeKey()).setValue(mCaravanInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressDialog();

                if (databaseError == null) {
                    showAlert(R.string.success_add_new_caravan, new View.OnClickListener() {
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
                        imageList.add(strCropedFilePath);
                    } else {
                        // Get Old File path
                        oldFilePath = imageList.get(updateImageIndex);

                        // Update it with New Path
                        imageList.set(updateImageIndex, strCropedFilePath);
                    }

                    // Update Image List
                    caravansImageAdapter.notifyDataSetChanged();

                    // Remove Old File, Check local file first
                    if (!TextUtils.isEmpty(oldFilePath) && !oldFilePath.startsWith("http")) {
                        new File(oldFilePath).delete();
                    }
                }
            }
        }
    }
}
