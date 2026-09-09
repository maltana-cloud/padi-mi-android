package com.padimi.android.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityEvent;

/** Device UI bridge used only for actions explicitly requested by the user. */
public final class PadiAccessibilityService extends AccessibilityService {
    private static PadiAccessibilityService instance;
    public static PadiAccessibilityService getInstance() { return instance; }

    @Override protected void onServiceConnected() { instance = this; }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}

    public boolean tapText(String wanted) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null || wanted == null || wanted.trim().isEmpty()) return false;
        AccessibilityNodeInfo node = findNode(root, wanted.trim().toLowerCase());
        if (node == null) return false;
        return clickNode(node);
    }

    private AccessibilityNodeInfo findNode(AccessibilityNodeInfo node, String wanted) {
        if (node == null) return null;
        CharSequence text = node.getText();
        CharSequence desc = node.getContentDescription();
        String t = text == null ? "" : text.toString().trim().toLowerCase();
        String d = desc == null ? "" : desc.toString().trim().toLowerCase();
        if (t.equals(wanted) || d.equals(wanted) || t.contains(wanted) || d.contains(wanted)) return node;
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            AccessibilityNodeInfo found = findNode(child, wanted);
            if (found != null) return found;
        }
        return null;
    }

    private boolean clickNode(AccessibilityNodeInfo node) {
        if (node == null) return false;
        if (node.isClickable() && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true;
        AccessibilityNodeInfo parent = node.getParent();
        return parent != null && clickNode(parent);
    }

    public boolean typeText(String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;
        AccessibilityNodeInfo focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (focused == null) return false;
        Bundle args = new Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text == null ? "" : text);
        return focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
    }

    public boolean swipe(float x1, float y1, float x2, float y2, long durationMs) {
        Path path = new Path();
        path.moveTo(x1, y1); path.lineTo(x2, y2);
        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, 0, Math.max(50, durationMs)))
                .build();
        return dispatchGesture(gesture, null, null);
    }

    public boolean scroll(boolean forward) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;
        return scrollNode(root, forward);
    }

    private boolean scrollNode(AccessibilityNodeInfo node, boolean forward) {
        if (node == null) return false;
        if (node.isScrollable()) {
            return node.performAction(forward ? AccessibilityNodeInfo.ACTION_SCROLL_FORWARD : AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (scrollNode(node.getChild(i), forward)) return true;
        }
        return false;
    }

    /** Collect visible text/content descriptions for voice responses. */
    public String readVisibleText() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return "";
        StringBuilder out = new StringBuilder();
        collectText(root, out);
        return out.toString().trim();
    }

    private void collectText(AccessibilityNodeInfo node, StringBuilder out) {
        if (node == null) return;
        CharSequence text = node.getText();
        CharSequence desc = node.getContentDescription();
        appendUnique(out, text);
        appendUnique(out, desc);
        for (int i = 0; i < node.getChildCount(); i++) collectText(node.getChild(i), out);
    }

    private void appendUnique(StringBuilder out, CharSequence value) {
        if (value == null) return;
        String s = value.toString().trim();
        if (s.isEmpty() || out.indexOf(s) >= 0) return;
        if (out.length() > 0) out.append(" | ");
        out.append(s);
    }

    @Override public void onDestroy() {
        if (instance == this) instance = null;
        super.onDestroy();
    }
}
