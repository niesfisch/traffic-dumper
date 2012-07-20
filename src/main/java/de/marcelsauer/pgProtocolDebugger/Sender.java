package de.marcelsauer.pgProtocolDebugger;

/**
* @author msauer
*/
public enum Sender {
    FRONTEND("F"),
    BACKEND("B");

    private final String shortname;

    Sender(String shortname) {
        this.shortname = shortname;
    }

    public String getShortname() {
        return shortname;
    }
}
