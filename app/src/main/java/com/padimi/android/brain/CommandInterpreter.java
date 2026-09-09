package com.padimi.android.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CommandInterpreter {
    public List<Command> parseAll(String raw) {
        List<Command> out = new ArrayList<>();
        String text = raw == null ? "" : raw.trim();
        if (text.isEmpty()) return out;
        String[] parts = text.split("\\s+(?:and then|then|and)\\s+|\\s*[,;]\\s*", 0);
        if (parts.length == 1) { out.add(parse(text)); return out; }
        for (String part : parts) { Command c = parse(part); if (c.type != Command.Type.UNKNOWN) out.add(c); }
        if (out.isEmpty()) out.add(parse(text));
        return out;
    }

    public Command parse(String raw) {
        String text = raw == null ? "" : raw.trim();
        String s = text.toLowerCase(Locale.ROOT);
        if (s.equals("go home") || s.equals("home") || s.equals("take me home")) return new Command(Command.Type.HOME, "");
        if (s.equals("go back") || s.equals("back") || s.equals("go backwards")) return new Command(Command.Type.BACK, "");
        if (s.contains("volume up") || s.contains("turn up the volume") || s.contains("increase volume") || s.equals("louder")) return new Command(Command.Type.VOLUME_UP, "");
        if (s.contains("volume down") || s.contains("turn down the volume") || s.contains("decrease volume") || s.equals("quieter")) return new Command(Command.Type.VOLUME_DOWN, "");
        if (s.equals("stop") || s.equals("stop listening") || s.equals("cancel")) return new Command(Command.Type.STOP, "");
        if (s.equals("what do you remember") || s.equals("what did you remember") || s.equals("what do you know about me")) return new Command(Command.Type.WHAT_DID_I_REMEMBER, "");
        if (s.startsWith("remember that ")) return new Command(Command.Type.REMEMBER, text.substring(14).trim());
        if (s.startsWith("remember ")) return new Command(Command.Type.REMEMBER, text.substring(9).trim());
        if (s.startsWith("open ")) return new Command(Command.Type.OPEN_APP, text.substring(5).trim());
        if (s.startsWith("tap ") || s.startsWith("click ") || s.startsWith("press ")) return new Command(Command.Type.TAP, text.substring(text.indexOf(' ') + 1).trim());
        if (s.startsWith("type ") || s.startsWith("write ") || s.startsWith("enter ")) return new Command(Command.Type.TYPE_TEXT, text.substring(text.indexOf(' ') + 1).trim());
        return new Command(Command.Type.UNKNOWN, text);
    }
}
