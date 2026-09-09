package com.padimi.android.actions;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.media.AudioManager;

public final class SystemActions {
    private SystemActions() {}
    public static void home(AccessibilityService service) { service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME); }
    public static void back(AccessibilityService service) { service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK); }
    public static void volume(Context context, boolean up) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) am.adjustStreamVolume(AudioManager.STREAM_MUSIC, up ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }
}
