package ru.itscan.bot.command.enums;

public enum CommandName {
    START("START_COMMAND"),
    TEST("TEST_COMMAND"),
    ASK("ASK_COMMAND"),
    PROFILE("PROFILE_COMMAND");

    private final String name;

    CommandName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
