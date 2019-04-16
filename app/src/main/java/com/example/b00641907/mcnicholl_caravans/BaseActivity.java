package com.example.b00641907.mcnicholl_caravans;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseActivity extends AppCompatActivity {

    Context mContext;
    MyApplication mMyApp;
    protected ProgressDialog mProgress;

    SharedPreferences mSettings;
    protected AppSettings appSettings;

    public static final String GLOBAL_SETTING = "cscs";

    protected static final int REQUEST_LOCATION = 200;
    protected static final int REQUEST_CAMERA = 300;
    protected static final int REQUEST_ALBUM = 400;
    protected static final int REQUEST_CROP = 500;

    // Permission Requests for Barcode, Images, Location.
    protected static final int PERMISSION_REQUEST_CODE_CAMERA = 100;
    protected static final String[] PERMISSION_REQUEST_CAMERA_STRING = {Manifest.permission.CAMERA};

    protected static final int PERMISSION_REQUEST_CODE_PHOTO = 101;
    protected static final String[] PERMISSION_REQUEST_PHOTO_STRING = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    protected static final int PERMISSION_REQUEST_CODE_GALLERY = 102;
    protected static final String[] PERMISSION_REQUEST_GALLERY_STRING = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    protected static final int PERMISSION_REQUEST_CODE_LOCATION = 103;


    protected String TAG = "AppCommon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mMyApp = (MyApplication) getApplication();

        mSettings = getPreferences(this);
        appSettings = new AppSettings(mContext);

        mProgress = new ProgressDialog(mContext, R.style.DialogTheme);
        mProgress.setMessage(getString(R.string.loading));
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(GLOBAL_SETTING, Context.MODE_PRIVATE);
    }

    public void showProgressDialog() {
        if (mProgress.isShowing())
            return;

        mProgress.show();
        mProgress.setContentView(R.layout.dialog_loading);
    }

    public void hideProgressDialog() {
        if (mProgress.isShowing())
            mProgress.dismiss();
    }

    // Remove EditText Keyboard
    public void hideKeyboard(EditText et) {
        if (et != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    public void showToastMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToastMessage(int msgId) {
        Toast.makeText(mContext, msgId, Toast.LENGTH_SHORT).show();
    }

    public void showAlert(int resId) {
        String alertMsg = getString(resId);
        showAlert(alertMsg, null);
    }

    public void showAlert(int resId, View.OnClickListener clickListener) {
        String alertMsg = getString(resId);
        showAlert(alertMsg, clickListener);
    }

    public void showAlert(String message) {
        showAlert(message, null);
    }

    public void showAlert(String message, final View.OnClickListener clickListener) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_error, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextView tvAlert = (TextView) dialogView.findViewById(R.id.tvAlert);
        tvAlert.setText(message);
        dialogView.findViewById(R.id.btnCloseAlert).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    protected String strCameraOutputFilePath;
    protected String strCropedFilePath;

    // image crop parameter
    protected static final String TYPE_IMAGE = "image/*";
    protected static final int GAS_IMAGE_ASPECT_X = 3;
    protected static final int GAS_IMAGE_ASPECT_Y = 2;
    protected static final int GAS_IMAGE_OUTPUT_X = 750;
    protected static final int GAS_IMAGE_OUTPUT_Y = 500;

    protected static final int CARAVAN_IMAGE_ASPECT_X = 3;
    protected static final int CARAVAN_IMAGE_ASPECT_Y = 2;
    protected static final int CARAVAN_IMAGE_OUTPUT_X = 750;
    protected static final int CARAVAN_IMAGE_OUTPUT_Y = 500;

    final static String IMG_FILE_PREFIX = "fg";
    final static String CAMERA_FILE_PREFIX = "CAMERA";
    final static String JPEG_FILE_SUFFIX = ".jpg";

    public File makeCameraOutputFile() {

        String extStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folderName = "DCIM";
        String appFolder = extStoragePath + File.separator + folderName;
        String cameraFolder = appFolder + File.separator + "NationalSales";

        File mediaStorageDir = new File(cameraFolder);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraOutputFileManager", "Required media storage does not exist");
                return null;
            }
        }

        String strFileName = String.format("%s%d", CAMERA_FILE_PREFIX, System.currentTimeMillis());

        File cameraImage = null;
        try {
            cameraImage = File.createTempFile(strFileName, // prefix
                    JPEG_FILE_SUFFIX, // suffix
                    mediaStorageDir // directory
            );
            cameraImage.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cameraImage;
    }
    protected void showImageSource() {
        final Dialog customDlg = new Dialog(this);
        customDlg.setContentView(R.layout.dialog_custom_photo);
        customDlg.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDlg.dismiss();
            }
        });

        customDlg.findViewById(R.id.choosephoto).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                customDlg.dismiss();
                if (checkPermissions(mContext, PERMISSION_REQUEST_GALLERY_STRING, false, PERMISSION_REQUEST_CODE_GALLERY)) {
                    startGalleryActivity();
                }
            }
        });
        customDlg.findViewById(R.id.takephoto).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                customDlg.dismiss();

                if (checkPermissions(mContext, PERMISSION_REQUEST_PHOTO_STRING, false, PERMISSION_REQUEST_CODE_PHOTO)) {
                    startCameraActivity();
                }
            }
        });

        customDlg.show();
    }
}



