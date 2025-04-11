package com.github.FishMiner.ui.ports.in;

import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.ui.events.LoginRequestEvent;
import com.github.FishMiner.ui.events.RegisterUserRequest;

public interface IRequestManager {
    IGameEventListener<LoginRequestEvent> getLoginRequestListener();
    IGameEventListener<RegisterUserRequest> getRegistrationRequestListener();
    // add the others here

}
