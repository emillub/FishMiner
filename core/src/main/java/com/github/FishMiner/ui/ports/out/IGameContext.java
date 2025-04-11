package com.github.FishMiner.ui.ports.out;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.ui.ports.in.IPlayer;

public interface IGameContext {
    void update(float delta);
    IPlayer getPlayer();
    World getWorld();
    OrthographicCamera getCam();
    ShapeRenderer getRenderer();
    SpriteBatch getBatch();
}
