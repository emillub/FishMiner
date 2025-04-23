package com.github.FishMiner.ui.ports.out.domain.events;

import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEvent;

public class MusicEvent implements IGameEvent {

    public enum MusicCommand {
        PLAY_BACKGROUND,
        PLAY_GAME,
        ADJUST_VOLUME,
        TOGGLE_MUTE,
    }

    private boolean handled;
    private MusicCommand command;
    private float volume;


    public MusicEvent(MusicCommand command) {
        this.command = command;
        this.handled = false;
    }

    public MusicCommand getCommand() {
        return command;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }

    @Override
    public void setHandled() {
        this.handled = true;
    }

    @Override
    public boolean isHandled() {
        return handled;
    }
}
