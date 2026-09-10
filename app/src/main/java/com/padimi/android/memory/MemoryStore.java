package com.padimi.android.memory;

import android.content.Context;
import android.content.SharedPreferences;

public final class MemoryStore {
    private static final String PREFS = "padi_memory";
    private static final String MEMORIES = "memories";
    private static final String CONVERSATION = "conversation";
    private final SharedPreferences prefs;

    public MemoryStore(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void remember(String value) {
        if (value == null || value.trim().isEmpty()) return;
        String old = prefs.getString(MEMORIES, "");
        String next = old.isEmpty() ? value.trim() : old + "\n" + value.trim();
        String[] items = next.split("\\n");
        int start = Math.max(0, items.length - 50);
        StringBuilder kept = new StringBuilder();
        for (int i = start; i < items.length; i++) {
            if (kept.length() > 0) kept.append('\n');
            kept.append(items[i]);
        }
        prefs.edit().putString(MEMORIES, kept.toString())
                .putString("last_memory", value.trim()).apply();
    }

    public String lastMemory() { return prefs.getString("last_memory", ""); }
    public String allMemories() { return prefs.getString(MEMORIES, ""); }

    /** Keep a small local dialogue window so ordinary conversation has context. */
    public void addConversationTurn(String user, String assistant) {
        String u = user == null ? "" : user.trim();
        String a = assistant == null ? "" : assistant.trim();
        if (u.isEmpty() && a.isEmpty()) return;
        String old = prefs.getString(CONVERSATION, "");
        String turn = "U: " + u + "\nA: " + a;
        String next = old.isEmpty() ? turn : old + "\n---\n" + turn;
        String[] turns = next.split("\\n---\\n");
        int start = Math.max(0, turns.length - 10);
        StringBuilder kept = new StringBuilder();
        for (int i = start; i < turns.length; i++) {
            if (kept.length() > 0) kept.append("\n---\n");
            kept.append(turns[i]);
        }
        prefs.edit().putString(CONVERSATION, kept.toString()).apply();
    }

    public String conversation() { return prefs.getString(CONVERSATION, ""); }
}
