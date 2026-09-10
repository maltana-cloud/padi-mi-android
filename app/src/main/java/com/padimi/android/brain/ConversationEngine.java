package com.padimi.android.brain;

import com.padimi.android.memory.MemoryStore;
import java.util.Locale;

/**
 * Lightweight local conversation layer. It keeps ordinary conversation separate
 * from device commands so PADI MI does not treat every sentence as an action.
 * A future online/local language model can implement the same boundary without
 * changing the phone-control layer.
 */
public final class ConversationEngine {
    private final MemoryStore memory;

    public ConversationEngine(MemoryStore memory) {
        this.memory = memory;
    }

    public String reply(String raw) {
        String text = raw == null ? "" : raw.trim();
        String s = text.toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return "I'm listening.";

        if (isGreeting(s)) {
            return "Hey! I'm here. You can talk to me normally, or ask me to control your phone.";
        }
        if (s.contains("who are you") || s.contains("what are you")) {
            return "I'm PADI MI, your personal Android assistant. I can talk with you and, when phone control is enabled, carry out actions on your phone.";
        }
        if (s.contains("how are you")) {
            return "I'm good, padi mi. I'm ready to help.";
        }
        if (s.contains("thank you") || s.equals("thanks") || s.equals("thank you padi mi")) {
            return "You're welcome, padi mi.";
        }
        if (s.equals("good morning") || s.equals("good afternoon") || s.equals("good evening")) {
            return capitalizeFirst(text) + ". I'm here with you.";
        }
        if (s.contains("my last memory") || s.contains("what was the last thing i asked you to remember")) {
            String last = memory.lastMemory();
            return last.isEmpty() ? "You haven't asked me to remember anything yet." : "The last thing you asked me to remember was: " + last;
        }
        if (s.contains("do you remember me")) {
            String memories = memory.allMemories();
            return memories.isEmpty() ? "I don't have any saved memories about you yet." : "Yes. I have some things you've asked me to remember.";
        }

        // Safe offline fallback: acknowledge conversation instead of pretending
        // that an unimplemented command was successfully executed.
        return "I understand you. I'm still building my deeper conversation brain, but I can already talk with you, remember things, and control supported phone actions.";
    }

    private boolean isGreeting(String s) {
        return s.equals("hi") || s.equals("hello") || s.equals("hey") ||
                s.equals("hey padi mi") || s.equals("hi padi mi") ||
                s.equals("hello padi mi") || s.equals("good to see you");
    }

    private String capitalizeFirst(String text) {
        if (text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
