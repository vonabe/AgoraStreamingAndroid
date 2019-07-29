package ru.vonabe.audiostreaming.only.model;

public class EngineConfig {
    public int mClientRole;

    public int mUid = 0;

    public String mChannel;

    public void reset() {
        mChannel = null;
    }

    public EngineConfig() {
    }
}
