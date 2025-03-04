package com.github.FishMiner.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.czyzby.autumn.annotation.Inject;

import com.github.FishMiner.service.Box2DService;
import com.github.FishMiner.view.GameView;

/** Creates Box2D world. */
    public class GameController {

        @Inject
        private final GameView gameView;

        @Inject
        private final Box2DService box2dService;

        public GameController(GameView gameView, Box2DService box2dService) {
            this.gameView = gameView;
            this.box2dService = box2dService;
        }

        public void startGame() {
            // Perform any pre-game initialization or logic here.
            // The view handles rendering and input setup.
        }
}
