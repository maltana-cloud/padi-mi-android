package com.padimi.android.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class AppLauncher {
    private AppLauncher() {}

    public static boolean open(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        String target = normalize(appName);
        if (target.isEmpty()) return false;

        Map<String, String[]> packages = new HashMap<>();
        packages.put("chrome", new String[]{"com.android.chrome"});
        packages.put("google chrome", new String[]{"com.android.chrome"});
        packages.put("browser", new String[]{"com.android.chrome"});
        packages.put("chatgpt", new String[]{"com.openai.chatgpt"});
        packages.put("chat gpt", new String[]{"com.openai.chatgpt"});
        packages.put("whatsapp", new String[]{"com.whatsapp"});
        packages.put("telegram", new String[]{"org.telegram.messenger"});
        packages.put("youtube", new String[]{"com.google.android.youtube"});
        packages.put("settings", new String[]{"com.android.settings"});

        String[] candidates = packages.get(target);
        if (candidates != null) {
            for (String packageName : candidates) {
                if (launchPackage(context, pm, packageName)) return true;
            }
            // If the user explicitly asked for Chrome, do not silently open a
            // different browser when Chrome is absent.
            if (target.equals("chrome") || target.equals("google chrome")) return false;
        }

        // "browser" means the user's default web browser. This works even if
        // Chrome is not installed.
        if (target.equals("browser")) return openDefaultBrowser(context);

        ApplicationInfo best = null;
        int bestScore = 0;
        for (ApplicationInfo info : pm.getInstalledApplications(PackageManager.MATCH_ALL)) {
            CharSequence label = pm.getApplicationLabel(info);
            if (label == null || pm.getLaunchIntentForPackage(info.packageName) == null) continue;
            String name = normalize(label.toString());
            int score = score(target, name);
            if (score > bestScore) {
                bestScore = score;
                best = info;
            }
        }
        if (best == null || bestScore < 60) return false;
        return launchPackage(context, pm, best.packageName);
    }

    private static boolean openDefaultBrowser(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static boolean launchPackage(Context context, PackageManager pm, String packageName) {
        try {
            Intent launch = pm.getLaunchIntentForPackage(packageName);
            if (launch == null) return false;
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launch);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[.!?,]+$", "")
                .replaceAll("\\s+", " ");
    }

    private static int score(String wanted, String actual) {
        if (wanted.equals(actual)) return 100;
        if (actual.startsWith(wanted) || wanted.startsWith(actual)) return 85;
        if (actual.contains(wanted) || wanted.contains(actual)) return 75;
        String[] a = wanted.split(" ");
        String[] b = actual.split(" ");
        int hits = 0;
        for (String x : a) for (String y : b) if (x.equals(y)) hits++;
        return hits > 0 ? 60 + Math.min(15, hits * 5) : 0;
    }
}
