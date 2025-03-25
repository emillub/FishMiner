package com.github.FishMiner.ui.controller;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.InputProcessor;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.impl.FireInputEvent;
import com.github.FishMiner.domain.ecs.util.HookUtil;

public class InputController implements InputProcessor {
    private final Engine engine;

    public InputController(Engine engine) {
        this.engine = engine;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        GameEventBus.getInstance().post(new FireInputEvent(HookUtil.getHook(engine)));
        return true;
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
