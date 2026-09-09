package com.padimi.android.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public final class PadiAccessibilityService extends AccessibilityService {
    private static PadiAccessibilityService instance;
    public static PadiAccessibilityService getInstance() { return instance; }
    @Override protected void onServiceConnected() { instance = this; }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
    @Override public void onDestroy() { if (instance == this) instance = null; super.onDestroy(); }
}
