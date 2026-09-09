package com.padimi.android.memory;

import android.content.Context;
import android.content.SharedPreferences;

public final class MemoryStore {
    private static final String PREFS = "padi_memory";
    private final SharedPreferences prefs;
    public MemoryStore(Context context) { prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    public void remember(String value) {
        if (value == null || value.trim().isEmpty()) return;
        String old = prefs.getString("memories", "");
        String next = old.isEmpty() ? value.trim() : old + "\n" + value.trim();
        String[] items = next.split("\\n");
        int start = Math.max(0, items.length - 20);
        StringBuilder kept = new StringBuilder();
        for (int i = start; i < items.length; i++) { if (kept.length() > 0) kept.append('\n'); kept.append(items[i]); }
        prefs.edit().putString("memories", kept.toString()).putString("last_memory", value.trim()).apply();
    }
    public String lastMemory() { return prefs.getString("last_memory", ""); }
    public String allMemories() { return prefs.getString("memories", ""); }
}
