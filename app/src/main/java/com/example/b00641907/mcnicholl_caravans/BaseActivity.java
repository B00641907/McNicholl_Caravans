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

    public static final String GLOBAL_SETTING = "cscs";
    protected static final int REQUEST_REGISTER = 100;
    protected static final int REQUEST_LOCATION = 200;
    protected static final int REQUEST_CAMERA = 300;
    protected static final int REQUEST_ALBUM = 400;
    protected static final int REQUEST_CROP = 500;
    // Permission Requests
    protected static final int PERMISSION_REQUEST_CODE_CAMERA = 100;
    protected static final String[] PERMISSION_REQUEST_CAMERA_STRING = {Manifest.permission.CAMERA};
    protected static final int PERMISSION_REQUEST_CODE_PHOTO = 101;
    protected static final String[] PERMISSION_REQUEST_PHOTO_STRING = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    protected static final int PERMISSION_REQUEST_CODE_GALLERY = 102;
    protected static final String[] PERMISSION_REQUEST_GALLERY_STRING = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    protected static final int PERMISSION_REQUEST_CODE_LOCATION = 103;
    protected static final String[] PERMISSION_REQUEST_LOCATION_STRING = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
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
    protected ProgressDialog mProgress;
    protected Bitmap mBitmap;
    protected AppSettings appSettings;
    protected String TAG = "AppCommon";
    // ---------------------- User Profile Photo ------------------------
    protected String strCameraOutputFilePath;
    protected String strCropedFilePath;
    Context mContext;
    MyApplication mMyApp;
    Typeface mAppFont;
    SharedPreferences mSettings;
    String mUserId;
    int mUserType;

    public static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(GLOBAL_SETTING, Context.MODE_PRIVATE);
    }

    /****** CHECK NETWORK CONNECTION *******/
    public static boolean isOnline(Context conn) {
        ConnectivityManager cm = (ConnectivityManager) conn.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //*****************************************************************
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int pxToDp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    // This will be used in Android6.0(Marshmallow) or above
    public static boolean checkPermissions(Context context, String[] permissions, boolean showHintMessage, int requestCode) {

        if (permissions == null || permissions.length == 0)
            return true;

        boolean allPermissionSetted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionSetted = false;
                break;
            }
        }

        if (allPermissionSetted)
            return true;

        // Should we show an explanation?
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                shouldShowRequestPermissionRationale = true;
                break;
            }
        }

        if (showHintMessage && shouldShowRequestPermissionRationale) {
            // Show an expanation to the user *asynchronously* -- don't
            // block
            // this thread waiting for the user's response! After the
            // user
            // sees the explanation, try again to request the
            // permission.
            String strPermissionHint = context.getString(R.string.request_permission_hint);
            Toast.makeText(context, strPermissionHint, Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);

        return false;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mMyApp = (MyApplication) getApplication();

        mSettings = getPreferences(this);
        appSettings = new AppSettings(mContext);

        //TextView mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        //mTxtTitle.setText(getString(R.string.mSettings));

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }*/

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

    public void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String loadPreferences(String key) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(GLOBAL_SETTING, MODE_PRIVATE);
            String strSavedMemo = sharedPreferences.getString(key, "");
            return strSavedMemo;
        } catch (NullPointerException nullPointerException) {

            return null;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) BaseActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = BaseActivity.this.getCurrentFocus();
        if (view == null) {
            view = new View(BaseActivity.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void msg(int resId) {
        String msg = getString(resId);
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    public void msg(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alert.show();
    }

    public void closeMsg(int msgId) {
        closeMsg(getString(msgId));
    }

    public void closeMsg(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.alert_close),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alert.show();

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

    public boolean isPasswordValid(String password) {
        return password.length() >= 5;
    }

    public boolean isEmailValid(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    protected void openLink(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    protected String getAndroidId() {
        TelephonyManager tm =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ID", "Android ID: " + androidId);
        return androidId;
    }

    protected boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (gps_enabled || network_enabled) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Function to show settings alert dialog
     */
    public void showLocationSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    protected void shareApp() {
        try {
            // Uri imageUri = Uri.fromFile(imageFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_body));
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String chooserTitle = getResources().getString(R.string.share_title);
            startActivity(Intent.createChooser(shareIntent, chooserTitle));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //List and Signout options at the top of the home page. Possibility of being used as navigation.
    protected void Signout() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setTitle("Confirm logout");
        alertDialogBuilder.setMessage("Would you like to logout?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                // Firebase Logout
                if (firebaseAuth != null) {
                    firebaseAuth.signOut();
                }

                // Empty User Info
                appSettings.saveUser(null);

                finish();
                startActivity(new Intent(mContext, SignInActivity.class));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public File makeCameraOutputFile() {

        String extStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folderName = "DCIM"/*getString(R.string.app_name)*/;
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

    public void getCameraImage() {
        if (checkPermissions(mContext, PERMISSION_REQUEST_PHOTO_STRING, false, PERMISSION_REQUEST_CODE_PHOTO)) {
            startCameraActivity();
        }
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

    public Bitmap getRealBitmap(Bitmap bitmap, String photoPath) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            return rotatedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    protected void startGalleryActivity() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_ALBUM);
    }

    protected void startCameraActivity() {
        File cameraOutputFile = makeCameraOutputFile();
        if (cameraOutputFile == null) {
            return;
        }

        strCameraOutputFilePath = cameraOutputFile.getAbsolutePath();

        Uri imageUri = getFileUri(cameraOutputFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri/*Uri.fromFile(cameraOutputFile)*/);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    protected Uri getFileUri(File file) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            Uri fileUri = Uri.fromFile(file);
            return fileUri;
        } else {
            Uri fileUri = FileProvider.getUriForFile(
                    getApplicationContext(),
                    "com.example.b00641907.mcnicholl_caravans.provider", // (use your app signature + ".provider" )
                    file);
            return fileUri;
        }
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, // Which
                // columns
                // to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
    //----------------------------------------------------------------------------------------------------

    public File makeTempFile(String suffix) {

        if (TextUtils.isEmpty(suffix))
            suffix = JPEG_FILE_SUFFIX;

        String extStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folderName = "DCIM"/*getString(R.string.app_name)*/;
        String appFolder = extStoragePath + File.separator + folderName;
        String tmpPicFolder = appFolder + File.separator + "NcNicholl"/*"tmpPic"*/;
        File mediaStorageDir = new File(tmpPicFolder);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Temp File Manager", "Required media storage does not exist");
                return null;
            }
        }

        String strFileName = String.format("%s%d", IMG_FILE_PREFIX, System.currentTimeMillis());

        File tempImage = null;
        try {
            tempImage = File.createTempFile(strFileName, // prefix
                    suffix, // suffix
                    mediaStorageDir // directory
            );

            tempImage.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempImage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) {
            if (requestCode == PERMISSION_REQUEST_CODE_PHOTO) {
                startCameraActivity();
            } else if (requestCode == PERMISSION_REQUEST_CODE_GALLERY) {
                startGalleryActivity();
            }
        } else {
            showAlert("Need permissions to use function.");
        }
    }
}



