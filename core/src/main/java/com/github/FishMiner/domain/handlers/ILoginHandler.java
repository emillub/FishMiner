package com.github.FishMiner.domain.handlers;


public interface ILoginHandler {
    void login(String email, String password, LoginCallBack callback);
}
