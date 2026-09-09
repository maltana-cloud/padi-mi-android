package com.padimi.android.memory;

import android.content.Context;
import android.content.SharedPreferences;

public final class MemoryStore {
    private static final String PREFS = "padi_memory";
    private final SharedPreferences prefs;
    public MemoryStore(Context context) { prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    public void remember(String value) { if (value != null && !value.trim().isEmpty()) prefs.edit().putString("last_memory", value.trim()).apply(); }
    public String lastMemory() { return prefs.getString("last_memory", ""); }
}
