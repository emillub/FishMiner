package com.github.FishMiner.view;

import com.badlogic.gdx.utils.Array;
import com.github.FishMiner.view.GameView;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.FishMiner.controller.dialog.NotEnoughPlayersErrorController;
import com.github.FishMiner.service.ControlsService;
import com.github.FishMiner.service.controls.Control;

/** Thanks to View annotation, this class will be automatically found and initiated.
 *
 * This is application's main view, displaying a menu with several options. */
@View(id = "menu", value = "ui/templates/menu.lml", themes = "music/theme.ogg")
public class MenuView implements ActionContainer {
    @Inject private InterfaceService interfaceService;
    @Inject private ControlsService controlsService;

    @LmlAction("startGame")
    public void startPlaying() {
        if (isAnyPlayerActive()) {
            interfaceService.show(GameView.class);
        } else {
            interfaceService.showDialog(NotEnoughPlayersErrorController.class);
        }
    }

    private boolean isAnyPlayerActive() {
        final Array<Control> controls = controlsService.getControls();
        for (final Control control : controls) {
            if (control.isActive()) {
                return true;
            }
        }
        return false;
    }
}
