package com.github.FishMiner;

import com.badlogic.gdx.Game;
import com.github.FishMiner.ui.MenuScreen;
import com.github.FishMiner.ui.PlayScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class FishMinerGame extends Game {
    @Override
    public void create() {
        setScreen(new PlayScreen());
    }
}
