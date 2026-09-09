package com.padimi.android.brain;

public final class Command {
    public enum Type { OPEN_APP, HOME, BACK, VOLUME_UP, VOLUME_DOWN, STOP, REMEMBER, UNKNOWN }
    public final Type type;
    public final String argument;
    public Command(Type type, String argument) { this.type = type; this.argument = argument == null ? "" : argument; }
}
