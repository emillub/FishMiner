package com.github.FishMiner.ui.factories;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.FishMiner.common.Assets;

public class ButtonFactory {

    public static ImageButton createImageButton(Assets.ButtonEnum buttonEnum, Runnable onClickAction) {
        ImageButton button = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(Assets.getInstance()
                        .getAsset(Assets.BYTTON_PATHS.get(buttonEnum)
                                .get(Assets.ButtonStateEnum.UP), Texture.class))),
                new TextureRegionDrawable(new TextureRegion(Assets.getInstance()
                        .getAsset(Assets.BYTTON_PATHS.get(buttonEnum)
                                .get(Assets.ButtonStateEnum.DOWN), Texture.class))));

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onClickAction.run();
                // Add more cases here if needed for other button types
            }
        });

        return button;
    }

    public static ImageButton createToggleButton(boolean isChecked, Assets.ButtonEnum buttonOff, Assets.ButtonEnum toggledButtonEnum, Runnable onToggleAction) {
        TextureRegionDrawable buttonOffUp = new TextureRegionDrawable(new TextureRegion(Assets.getInstance()
                .getAsset(Assets.BYTTON_PATHS.get(buttonOff)
                        .get(Assets.ButtonStateEnum.UP), Texture.class)));
        TextureRegionDrawable buttonOffDown = new TextureRegionDrawable(new TextureRegion(Assets.getInstance()
                .getAsset(Assets.BYTTON_PATHS.get(buttonOff)
                        .get(Assets.ButtonStateEnum.DOWN), Texture.class)));

        TextureRegionDrawable buttonOnUp = new TextureRegionDrawable(new TextureRegion(Assets.getInstance()
                .getAsset(Assets.BYTTON_PATHS.get(toggledButtonEnum)
                        .get(Assets.ButtonStateEnum.UP), Texture.class)));
        TextureRegionDrawable buttonOnDown = new TextureRegionDrawable(new TextureRegion(Assets.getInstance()
                .getAsset(Assets.BYTTON_PATHS.get(toggledButtonEnum)
                        .get(Assets.ButtonStateEnum.DOWN), Texture.class)));

        ImageButton toggleButton = new ImageButton(buttonOffUp, buttonOffDown, buttonOnUp);
        toggleButton.setChecked(isChecked);

        toggleButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (toggleButton.isChecked()) {
                    toggleButton.getStyle().up = buttonOnUp;
                    toggleButton.getStyle().down = buttonOnDown;
                } else {
                    toggleButton.getStyle().up = buttonOffUp;
                    toggleButton.getStyle().down = buttonOffDown;
                }
                onToggleAction.run();
            }
        });

        return toggleButton;
    }
}
