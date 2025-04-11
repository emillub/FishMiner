package com.github.FishMiner.domain.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;

public class MusicManager implements IGameEventListener<MusicEvent> {
    private final Music backgroundMusic;
    private final Music playMusic;
    private Music currentlyPlaying;

    public MusicManager() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("StartMusic.ogg"));
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("TownTheme.ogg"));
        ValidateUtil.validateMultipleNotNull(
            playMusic, "playMusic",
            backgroundMusic, "backgroundMusic"
        );

        backgroundMusic.setLooping(true);
        playMusic.setLooping(true);

        float volume = Configuration.getInstance().getMusicVolume();
        backgroundMusic.setVolume(volume);
        playMusic.setVolume(volume);
    }

    public void applyVolume(float volume) {
        backgroundMusic.setVolume(volume);
        playMusic.setVolume(volume);
        if (volume <= 0f) {
            stopAll();
        } else if (currentlyPlaying != null && !currentlyPlaying.isPlaying()) {
            currentlyPlaying.play();
        }
    }

    public void applyVolume() {
        float volume = Configuration.getInstance().getMusicVolume();
        this.applyVolume(volume);
    }

    public void playBackground() {
        if (currentlyPlaying != backgroundMusic) {
            backgroundMusic.play();
            playMusic.stop();
            currentlyPlaying = backgroundMusic;
        }
    }

    public void playGame() {
        if (currentlyPlaying != playMusic) {
            playMusic.play();
            backgroundMusic.stop();
            currentlyPlaying = playMusic;
        }
    }

    public void stopAll() {
        if (currentlyPlaying.isPlaying()) {
            currentlyPlaying.stop();
        }
    }

    public void resumeCurrentlyPlaying() {
        if (currentlyPlaying != null && !currentlyPlaying.isPlaying()) {
            currentlyPlaying.play();
        }
    }

    @Override
    public void onEvent(MusicEvent event) {
        if (event.isHandled()) return;

        switch (event.getCommand()) {
            case PLAY_BACKGROUND -> playBackground();
            case PLAY_GAME       -> playGame();
            case ADJUST_VOLUME   -> applyVolume();
            case STOP_ALL        -> stopAll();
            case RESUME          -> resumeCurrentlyPlaying();

        }
        event.setHandled();
    }

    @Override
    public Class<MusicEvent> getEventType() {
        return MusicEvent.class;
    }
}
