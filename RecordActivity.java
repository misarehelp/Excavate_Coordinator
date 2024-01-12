package ru.volganap.nikolay.haircut_schedule;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity implements Constants, Enums, Contract.ActivityReciever, Contract.RecordFragmentToRecordActivity,
        Contract.ClientFragmentToRecordActivity, Contract.PhotoFragmentToRecordActivity, Contract.SomeFragmentToRecordActivity,
        Contract.Recycle.HistoryInterface {

    private BroadcastReceiver recordBroadcastReceiver;
    private Contract.RecordActivityToFragmentBroadcast callbackToRecordFragmentBroadcast, callbackToClientFragmentBroadcast, callbackToHistoryFragmentBroadcast;
    private Contract.RecordActivityToRecordFragment callbackToRecordFragment;
    private Contract.RecordActivityToSomeFragment callback_HR_toHistoryFragment, callback_HR_toRecordFragment, callback_HR_toClientFragment;
    private Contract.RecordActivityToPhotoFragment callbackToPhotoFragment;
    private ViewPager2 vp_record;
    private Uri uri;
    private File imageFile = null;

    private int REQUEST_CAMERA = 11;
    private int REQUEST_GALLERY = 22;
    private int RECORD_TAB = 0;
    private int PHOTO_TAB = 1;
    private int CLIENT_TAB = 2;
    private int HISTORY_TAB = 3;
    private String filename;
    private SharedPreferences sharedPrefs;

    private ActivityResultLauncher<Intent> launchGaleryActivity = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(), result -> getBitmapFromIntent (result, REQUEST_GALLERY));

    private ActivityResultLauncher<Intent> launchCameraActivity = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(), result -> getBitmapFromIntent (result, REQUEST_CAMERA));

    private void getBitmapFromIntent (androidx.activity.result.ActivityResult result, int request) {

        if ( (result.getResultCode() == AppCompatActivity.RESULT_OK) ) {
            try {

                if ( request == REQUEST_GALLERY ) {
                    uri = result.getData().getData();
                }

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if (null != imageFile) {
                    imageFile.delete();
                }
                callbackToPhotoFragment.saveBitmapToRepository(bitmap, filename, RECORD_HOST );
                vp_record.setCurrentItem(PHOTO_TAB);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPrefs.edit();
        ed.putString(CHILD_ACTIVITY, RECORD_ACTIVITY);
        ed.apply();

        Intent intent = getIntent();
        String date = intent.getStringExtra(DATE_CODE);
        String time = intent.getStringExtra(TIME_CODE);
        String index = intent.getStringExtra(INDEX_CODE);
        String type = intent.getStringExtra(TYPE_CODE);
        int theme = intent.getIntExtra(THEME, 0);
        setTheme(theme);

        setContentView(R.layout.activity_record);
        initRecordViewPager(index, date, time, type);
    }

    @Override
    public void doPictureAction(Enums.PhotoType picture_type, String filename ) {
        this.filename = filename;

        switch (picture_type) {
            // Record Data was changed on the server
            case REPOSITORY:
                callbackToPhotoFragment.getSavedFileByName(filename, RECORD_HOST);
                break;

            case CAMERA:
                runCameraIntent();
                break;

            case GALLERY:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                this.launchGaleryActivity.launch(intent);

            default:
                break;
        }
    }

    public void runCameraIntent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FILE_STORAGE);
            if (!path.exists()) {
                try {
                    path.mkdir();
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Exception e: " + e);
                }
            }

            imageFile = File.createTempFile("tempFile", ".jpg", path);
            Context context = this.getBaseContext();
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", imageFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.launchCameraActivity.launch(intent);

        } catch (Exception e) {
            Log.d(LOG_TAG, "Main - openCameraApp, e: " + e);
        }
    }

    @Override
    public void deletePictureFile(String filename) {
        callbackToPhotoFragment.deletePictureFile(filename);
    }

    @Override
    public void passClientDataToActivity(String name, String phone) {
        callback_HR_toClientFragment.onGetClientDataToFragment( new ClientData(name, phone) );
        vp_record.setCurrentItem(CLIENT_TAB);
    }

    @Override
    public void onSavePictureResult(boolean value) {
        callbackToRecordFragment.onLoadPictureResult( value );
    }

    @Override
    public void onLoadPictureResult(boolean value) {
        callbackToRecordFragment.onLoadPictureResult( value );
    }

    @Override
    public void onPhotoFragmentViewCreated() {
        callbackToRecordFragment.onPhotoFragmentViewCreatedToRecord();
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String p : permissions) {
                    String msg = "";
                    if (this.checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED)
                        msg = "Permission Granted for " + p;
                    else
                        msg = "Permission not Granted for " + p;
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    */

    private void initRecordViewPager( String index, String date, String time, String type ) {

        String [] page_names = getResources().getStringArray(R.array.record_pages);
        vp_record = findViewById(R.id.vp_record);

        MainPagerAdapter fragmentStateAdapter = new MainPagerAdapter(this );
        // define fragments of viewpager2
        Fragment recordFragment = new RecordFragment( index, date, time, type );
        Fragment clientListFragment = new ClientListFragment();
        Fragment historyFragment = new HistoryListFragment();
        Fragment photoFragment = new PhotoFragment(date, sharedPrefs);

        // Init BroadcastReceiver
        try {
            callbackToRecordFragment = (Contract.RecordActivityToRecordFragment) recordFragment;
            callback_HR_toHistoryFragment = (Contract.RecordActivityToSomeFragment) historyFragment;
            callback_HR_toRecordFragment = (Contract.RecordActivityToSomeFragment) recordFragment;
            callback_HR_toClientFragment = (Contract.RecordActivityToSomeFragment) clientListFragment;
            callbackToRecordFragmentBroadcast = (Contract.RecordActivityToFragmentBroadcast) recordFragment;
            callbackToClientFragmentBroadcast = (Contract.RecordActivityToFragmentBroadcast) clientListFragment;
            callbackToHistoryFragmentBroadcast = (Contract.RecordActivityToFragmentBroadcast) historyFragment;
            callbackToPhotoFragment = (Contract.RecordActivityToPhotoFragment) photoFragment;

        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement Contract.RecordActivity.ToRecordFragment");
        }
        initBroadcastReceiver();

        fragmentStateAdapter.addFragment(recordFragment);

        if (type.equals(INDEX_NOTE)) {
            page_names[0] = getResources().getString(R.string.tv_note_title);
        } else {
            fragmentStateAdapter.addFragment(photoFragment);
            fragmentStateAdapter.addFragment(clientListFragment);
            fragmentStateAdapter.addFragment(historyFragment);
        }

        vp_record.setOffscreenPageLimit(page_names.length -1);
        vp_record.setAdapter(fragmentStateAdapter);

        TabLayout tab_record = findViewById(R.id.tab_record);
        new TabLayoutMediator(tab_record, vp_record, false, (tab, position) -> {
            //tab.view.setBackgroundColor(Color.WHITE);
            tab.setText(page_names[position]);

        }).attach();
    }

    @Override
    public void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        //Set filter by Class name
        filter.addAction(getClass().getSimpleName());

        recordBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String command = intent.getStringExtra(COMMAND);

                    if (command.equals(SERVER_ADD_CLIENT) || command.equals(SERVER_CHANGE_CLIENT) || command.equals(SERVER_DELETE_CLIENT)
                            || command.equals(SERVER_GET_CLIENT_ID)) {
                        callbackToClientFragmentBroadcast.onBroadcastReceive(intent);

                    } else if (command.equals(SERVER_GET_ARCHIVE_BY_ID)) {
                        callbackToHistoryFragmentBroadcast.onBroadcastReceive(intent);
                        vp_record.setCurrentItem(HISTORY_TAB);

                    } else {
                        callbackToRecordFragmentBroadcast.onBroadcastReceive(intent);
                    }
                }
            }
        };
        registerReceiver(recordBroadcastReceiver, filter);
    }

    @Override
    public void finishRecordActivity() {
        finish();
    }

    @Override
    public void onGetClientDataToActivity ( ClientData clientData, boolean show_hystory ) {
        if (show_hystory) {
            callback_HR_toHistoryFragment.onGetClientDataToFragment(clientData);
        } else {
            callback_HR_toRecordFragment.onGetClientDataToFragment(clientData);
            vp_record.setCurrentItem(RECORD_TAB);
        }
    }

    @Override
    public void onItemClick(String filename) {
        callbackToPhotoFragment.getSavedFileByName(filename, HISTORY_LIST_HOST);
        vp_record.setCurrentItem(PHOTO_TAB);
    }

    @Override
    public void backToRecordFragment(int host) {
        vp_record.setCurrentItem(host);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "RecordActivity: onDestroy ");
        unregisterReceiver(recordBroadcastReceiver);
        super.onDestroy();

    }
}

             /*
             File currentFile = File.createTempFile("currentFile", ".jpg", path);
             Uri currentUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".fileprovider", currentFile);
             List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
             for (ResolveInfo resolveInfo : resInfoList) {
                 String packageName = resolveInfo.activityInfo.packageName;
                 context.grantUriPermission(packageName, currentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
             } */
//startActivityForResult(intent, REQUEST_CAMERA);