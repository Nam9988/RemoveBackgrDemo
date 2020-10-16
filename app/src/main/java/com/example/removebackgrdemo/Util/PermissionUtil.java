package com.example.removebackgrdemo.Util;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionUtil {
    private PermissionUtil() {
        // No-op
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isCameraPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isReadExternalPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isWriteExternalPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }
}
