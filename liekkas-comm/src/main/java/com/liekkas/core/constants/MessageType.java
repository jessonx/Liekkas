package com.liekkas.core.constants;

public enum MessageType {
    REQUEST(1), RPC(2);
    private int type;

    MessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
