package com.padimi.android.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public final class AppLauncher {
    public static boolean open(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        String target = appName == null ? "" : appName.trim().toLowerCase();
        for (android.content.pm.ApplicationInfo info : pm.getInstalledApplications(0)) {
            CharSequence label = pm.getApplicationLabel(info);
            if (label != null && label.toString().toLowerCase().equals(target)) {
                Intent launch = pm.getLaunchIntentForPackage(info.packageName);
                if (launch != null) { launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); context.startActivity(launch); return true; }
            }
        }
        return false;
    }
}
