package com.github.FishMiner.domain.managers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.github.FishMiner.infrastructure.Assets;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.ValidateUtil;
import com.github.FishMiner.ui.ports.out.domain.events.MusicEvent;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEventListener;

public class MusicManager implements IGameEventListener<MusicEvent> {
    private final Music backgroundMusic;
    private final Music playMusic;
    private float previousVolume = Configuration.getInstance().getMusicVolume();
    private Music currentlyPlaying;

    private static MusicManager instance;

    private MusicManager() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(Assets.BACKGROUND_MUSIC_PATH));
        playMusic = Gdx.audio.newMusic(Gdx.files.internal(Assets.PLAY_MUSIC_PATH));
        ValidateUtil.validateMultipleNotNull(
            playMusic, "playMusic",
            backgroundMusic, "backgroundMusic"
        );

        backgroundMusic.setLooping(true);
        playMusic.setLooping(true);

        backgroundMusic.setVolume(Configuration.getInstance().getMusicVolume());
        playMusic.setVolume(Configuration.getInstance().getMusicVolume());
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            synchronized (MusicManager.class) {
                if (instance == null) {
                    instance = new MusicManager();
                }
            }
        }
        return instance;
    }

    public void applyVolume(float volume) {
        ValidateUtil.validateNotNull(volume, "Volume");
        ValidateUtil.validateRange(volume, 0f, 1f, "Volume");
        Configuration.getInstance().setMusicVolume(volume);
        backgroundMusic.setVolume(volume);
        playMusic.setVolume(volume);
    }

    private void switchMusic(Music newMusic) {
        if (newMusic == currentlyPlaying) {
            Logger.getInstance().debug("MusicManager", "Already playing: " + newMusic);
            return;
        }
        boolean lastSongPlaying = currentlyPlaying != null && currentlyPlaying.isPlaying();
        if (lastSongPlaying) {
            currentlyPlaying.stop();
        }
        currentlyPlaying = newMusic;
        currentlyPlaying.setVolume(Configuration.getInstance().getMusicVolume());
        currentlyPlaying.play();
        Logger.getInstance().debug("MusicManager", "Switching music to: " + newMusic);
    }

    public void playBackground() {
        switchMusic(backgroundMusic);
    }

    public void playGame() {
        switchMusic(playMusic);
    }

    private void mute() {
        if (currentlyPlaying != null) {
            previousVolume = Configuration.getInstance().getMusicVolume();
            if (previousVolume > 0f) {
                Configuration.getInstance().setMusicVolume(0f);
                currentlyPlaying.setVolume(0f);
                Logger.getInstance().debug("MusicManager", "Music muted.");
            }
        }
    }

    private void unMute() {
        if (currentlyPlaying != null) {
            float volumeToRestore = previousVolume > 0f ? previousVolume : Configuration.DEFAULT_MUSIC_VOLUME;
            Configuration.getInstance().setMusicVolume(volumeToRestore);
            currentlyPlaying.setVolume(volumeToRestore);
            Logger.getInstance().debug("MusicManager", "Music unmuted. Restored volume: " + volumeToRestore);
        }
    }

    private void toggleMute() {
        Logger.getInstance().debug("MusicManager", "Toggling mute: " + Configuration.getInstance().isMusicEnabled());
        if (Configuration.getInstance().isMusicEnabled()) {
            mute();
        } else {
            unMute();
        }
    }

    @Override
    public void onEvent(MusicEvent event) {
        if (event.isHandled()) return;

        switch (event.getCommand()) {
            case PLAY_BACKGROUND -> playBackground();
            case PLAY_GAME       -> playGame();
            case ADJUST_VOLUME -> applyVolume(event.getVolume());
            case TOGGLE_MUTE -> toggleMute();

        }
        event.setHandled();
    }

    @Override
    public Class<MusicEvent> getEventType() {
        return MusicEvent.class;
    }
}
