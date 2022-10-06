package com.example.navu.chatterbox;

/**
 * Created by Navu on 24-May-18.
 */

public class Chats {

    public long timestamp;
    public static boolean seen;

    public Chats() {
    }

    public Chats(long timestamp, boolean seen) {
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}