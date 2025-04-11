package com.github.FishMiner.ui.ports.in;

import com.badlogic.ashley.core.Entity;

public interface IPlayer {
    public Entity getPlayerEntity();
    public Entity getHook();

    public Entity getSinker();

    public Entity getReel();

    public int getScore();
}
