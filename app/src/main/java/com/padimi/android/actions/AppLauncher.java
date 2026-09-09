package com.padimi.android.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class AppLauncher {
    private AppLauncher() {}

    public static boolean open(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        String target = normalize(appName);
        if (target.isEmpty()) return false;

        Map<String, String> aliases = new HashMap<>();
        aliases.put("chrome", "chrome");
        aliases.put("google chrome", "chrome");
        aliases.put("browser", "chrome");
        aliases.put("chat gpt", "chatgpt");
        aliases.put("chatgpt", "chatgpt");
        aliases.put("whatsapp", "whatsapp");
        aliases.put("telegram", "telegram");
        aliases.put("youtube", "youtube");
        aliases.put("settings", "settings");
        String wanted = aliases.containsKey(target) ? aliases.get(target) : target;

        ApplicationInfo best = null;
        int bestScore = 0;
        for (ApplicationInfo info : pm.getInstalledApplications(PackageManager.MATCH_ALL)) {
            CharSequence label = pm.getApplicationLabel(info);
            if (label == null || pm.getLaunchIntentForPackage(info.packageName) == null) continue;
            String name = normalize(label.toString());
            int score = score(wanted, name);
            if (score > bestScore) {
                bestScore = score;
                best = info;
            }
        }

        if (best == null || bestScore < 60) return false;
        Intent launch = pm.getLaunchIntentForPackage(best.packageName);
        if (launch == null) return false;
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(launch);
        return true;
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private static int score(String wanted, String actual) {
        if (wanted.equals(actual)) return 100;
        if (actual.startsWith(wanted) || wanted.startsWith(actual)) return 85;
        if (actual.contains(wanted) || wanted.contains(actual)) return 75;
        String[] a = wanted.split(" ");
        String[] b = actual.split(" ");
        int hits = 0;
        for (String x : a) for (String y : b) if (x.equals(y)) hits++;
        if (hits > 0) return 60 + Math.min(15, hits * 5);
        return 0;
    }
}
