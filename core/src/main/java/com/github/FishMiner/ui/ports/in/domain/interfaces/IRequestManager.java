package com.github.FishMiner.ui.ports.in.domain.interfaces;

import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEventListener;
import com.github.FishMiner.domain.ports.in.data.events.LoginRequestEvent;
import com.github.FishMiner.domain.ports.in.data.events.RegisterUserRequest;

public interface IRequestManager {
    IGameEventListener<LoginRequestEvent> getLoginRequestListener();
    IGameEventListener<RegisterUserRequest> getRegistrationRequestListener();
    // add the others here

}
