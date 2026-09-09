package com.padimi.android.brain;

public final class Command {
    public enum Type {
        OPEN_APP, HOME, BACK, VOLUME_UP, VOLUME_DOWN,
        TAP, SWIPE, SCROLL_UP, SCROLL_DOWN, TYPE_TEXT,
        SEARCH_WEB, READ_SCREEN, REMEMBER, WHAT_DID_I_REMEMBER,
        STOP, UNKNOWN
    }

    public final Type type;
    public final String argument;
    public final String secondary;

    public Command(Type type, String argument) { this(type, argument, ""); }

    public Command(Type type, String argument, String secondary) {
        this.type = type;
        this.argument = argument == null ? "" : argument;
        this.secondary = secondary == null ? "" : secondary;
    }
}
