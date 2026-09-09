package com.padimi.android.brain;

import java.util.Locale;

public final class CommandInterpreter {
    public Command parse(String raw) {
        String text = raw == null ? "" : raw.trim();
        String s = text.toLowerCase(Locale.ROOT);
        if (s.equals("go home") || s.equals("home") || s.equals("take me home")) return new Command(Command.Type.HOME, "");
        if (s.equals("go back") || s.equals("back") || s.equals("go backwards")) return new Command(Command.Type.BACK, "");
        if (s.contains("volume up") || s.contains("turn up the volume") || s.contains("increase volume")) return new Command(Command.Type.VOLUME_UP, "");
        if (s.contains("volume down") || s.contains("turn down the volume") || s.contains("decrease volume")) return new Command(Command.Type.VOLUME_DOWN, "");
        if (s.equals("stop") || s.equals("stop listening") || s.equals("cancel")) return new Command(Command.Type.STOP, "");
        if (s.startsWith("remember that ")) return new Command(Command.Type.REMEMBER, text.substring(14).trim());
        if (s.startsWith("remember ")) return new Command(Command.Type.REMEMBER, text.substring(9).trim());
        if (s.startsWith("open ")) return new Command(Command.Type.OPEN_APP, text.substring(5).trim());
        return new Command(Command.Type.UNKNOWN, text);
    }
}
