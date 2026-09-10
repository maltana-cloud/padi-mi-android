package com.padimi.android.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Converts ordinary spoken phrases into deterministic device commands. */
public final class CommandInterpreter {
    public List<Command> parseAll(String raw) {
        List<Command> out = new ArrayList<>();
        String text = raw == null ? "" : raw.trim();
        if (text.isEmpty()) return out;
        String[] parts = text.split("\\s*(?:,|;|\\band then\\b|\\bthen\\b)\\s*|\\s+\\band\\s+(?=(?:open|launch|go|take|turn|increase|decrease|raise|lower|make|tap|click|press|select|type|write|enter|input|remember|scroll|swipe|search|read|stop)\\b)", 0);
        for (String part : parts) {
            Command command = parse(part);
            if (command.type != Command.Type.UNKNOWN || parts.length == 1) out.add(command);
        }
        if (out.isEmpty()) out.add(parse(text));
        return out;
    }

    public Command parse(String raw) {
        String text = raw == null ? "" : raw.trim();
        String s = text.toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return new Command(Command.Type.UNKNOWN, "");
        if (s.equals("go home") || s.equals("home") || s.equals("take me home") || s.equals("go to home") || s.equals("home screen")) return new Command(Command.Type.HOME, "");
        if (s.equals("go back") || s.equals("back") || s.equals("go backwards") || s.equals("previous page") || s.equals("previous screen")) return new Command(Command.Type.BACK, "");
        if (isVolumeUp(s)) return new Command(Command.Type.VOLUME_UP, "");
        if (isVolumeDown(s)) return new Command(Command.Type.VOLUME_DOWN, "");
        if (s.equals("scroll down") || s.equals("scroll lower") || s.equals("swipe up") || s.equals("move down")) return new Command(Command.Type.SCROLL_DOWN, "");
        if (s.equals("scroll up") || s.equals("scroll higher") || s.equals("swipe down") || s.equals("move up")) return new Command(Command.Type.SCROLL_UP, "");
        if (s.equals("read the screen") || s.equals("read my screen") || s.equals("what is on my screen") || s.equals("what's on my screen")) return new Command(Command.Type.READ_SCREEN, "");
        if (s.equals("stop") || s.equals("stop listening") || s.equals("cancel") || s.equals("never mind") || s.equals("be quiet")) return new Command(Command.Type.STOP, "");
        if (s.equals("what do you remember") || s.equals("what did you remember") || s.equals("what do you know about me") || s.equals("what have you remembered")) return new Command(Command.Type.WHAT_DID_I_REMEMBER, "");
        if (s.startsWith("remember that ")) return new Command(Command.Type.REMEMBER, text.substring(14).trim());
        if (s.startsWith("remember ")) return new Command(Command.Type.REMEMBER, text.substring(9).trim());
        if (s.startsWith("open ")) return new Command(Command.Type.OPEN_APP, cleanTarget(text.substring(5)));
        if (s.startsWith("launch ")) return new Command(Command.Type.OPEN_APP, cleanTarget(text.substring(7)));
        if (s.startsWith("go to ")) return new Command(Command.Type.OPEN_APP, cleanTarget(text.substring(6)));
        if (s.startsWith("tap ") || s.startsWith("click ") || s.startsWith("press ") || s.startsWith("select ")) return new Command(Command.Type.TAP, text.substring(text.indexOf(' ') + 1).trim());
        if (s.startsWith("type ") || s.startsWith("write ") || s.startsWith("enter ") || s.startsWith("input ")) return new Command(Command.Type.TYPE_TEXT, text.substring(text.indexOf(' ') + 1).trim());
        if (s.startsWith("search for ")) return new Command(Command.Type.SEARCH_WEB, text.substring(11).trim());
        if (s.startsWith("search ")) return new Command(Command.Type.SEARCH_WEB, text.substring(7).trim());
        if (s.startsWith("google ")) return new Command(Command.Type.SEARCH_WEB, text.substring(7).trim());
        if (s.startsWith("swipe ")) return new Command(Command.Type.SWIPE, text.substring(6).trim());
        return new Command(Command.Type.UNKNOWN, text);
    }

    private boolean isVolumeUp(String s) {
        return s.contains("volume up") || s.contains("turn up the volume") || s.contains("turn the volume up") || s.contains("increase volume") || s.contains("increase the volume") || s.contains("raise the volume") || s.equals("louder") || s.equals("make it louder") || s.equals("make the sound louder");
    }
    private boolean isVolumeDown(String s) {
        return s.contains("volume down") || s.contains("turn down the volume") || s.contains("turn the volume down") || s.contains("decrease volume") || s.contains("decrease the volume") || s.contains("lower the volume") || s.equals("quieter") || s.equals("make it quieter") || s.equals("make the sound quieter");
    }
    private String cleanTarget(String value) { return value == null ? "" : value.trim().replaceAll("[.!?]+$", ""); }
}
