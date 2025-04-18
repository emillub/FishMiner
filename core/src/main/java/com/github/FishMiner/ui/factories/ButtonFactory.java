package com.github.FishMiner.ui.factories;

import java.io.ObjectInputFilter.Config;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;

public class ButtonFactory {
    public static enum ButtonSize {
        SMALL,
        MEDIUM,
        LARGE
    }

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

    public static ImageTextButton createImageTextButton(String text, ButtonSize size,
            Runnable onClickAction) {
        float fontScale = 1f;

        switch (size) {
            case SMALL:
                fontScale = Configuration.getInstance().getSmallFontScale();
                break;
            case MEDIUM:
                fontScale = Configuration.getInstance().getMediumFontScale();
                break;
            case LARGE:
                fontScale = Configuration.getInstance().getLargeFontScale();
                break;
        }

        ImageTextButton button = new ImageTextButton(text, Assets.getUiskin());
        button.getLabel().setFontScale(fontScale);

        float scaledWidth = button.getLabel().getPrefWidth() * fontScale;
        float scaledHeight = button.getLabel().getPrefHeight() * fontScale;

        button.setSize(scaledWidth, scaledHeight);

        float buttonScaleHeight = scaledHeight / button.getPrefHeight();
        button.getStyle().pressedOffsetY = -16 * buttonScaleHeight;

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onClickAction.run();
            }
        });
        return button;
    }

    public static TextButton createTextButton(String text, ButtonSize size,
                    Runnable onClickAction) {
        float fontScale = 1f;
        switch (size) {
            case SMALL:
                fontScale = Configuration.getInstance().getSmallFontScale();
                break;
            case MEDIUM:
                fontScale = Configuration.getInstance().getMediumFontScale();
                break;
            case LARGE:
                fontScale = Configuration.getInstance().getLargeFontScale();
                break;
        }

        TextButton button = new TextButton(text, Assets.getUiskin());
        button.getLabel().setFontScale(fontScale);

        float scaledWidth = button.getLabel().getPrefWidth() * fontScale;
        float scaledHeight = button.getLabel().getPrefHeight() * fontScale
                + Configuration.getInstance().getSmallPadding();

        button.setSize(scaledWidth, scaledHeight);

        float buttonScaleHeight = scaledHeight / button.getPrefHeight();
        button.getStyle().pressedOffsetY = -16 * buttonScaleHeight;

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onClickAction.run();
            }
        });
        return button;
    }
}
