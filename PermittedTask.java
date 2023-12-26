package ru.volganap.nikolay.haircut_schedule;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class PermittedTask {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =101;
    private static final int PHONE_LOG  = 1;
    private ActivityResultLauncher<String> launcher;
    private String permission;
    private AppCompatActivity activity;

    public PermittedTask(AppCompatActivity activity, String permission) {
        this.activity = activity;
        this.permission = permission;
        this.launcher =  activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(result) {
                        granted();
                    } else {
                        denied();
                    }
                }
        );
        sendSMSMessage();
    }

    protected abstract void granted();

    protected void denied() {}

    private void showRequestPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permissions needed")
                .setMessage("App needs permissions to do that. You can allow or deny in next screen. Proceed?")
                .setPositiveButton("OK", (dialog, which) -> launcher.launch(permission))
                .setNegativeButton("Cancel", (dialog, which) -> denied())
                .show();
    }

    public void run() {

        checkCallLogPermission();

        if ((ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
           ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)) {
            granted();
        } else
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showRequestPermissionRationale();
        } else {
                launcher.launch(permission);
        }
    }

    private void checkCallLogPermission() {

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_CALL_LOG, android.Manifest.permission.WRITE_CALL_LOG},PHONE_LOG);
        }
    }

    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }
}
