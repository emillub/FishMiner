package com.github.FishMiner.domain.handlers;

public interface IAuthHandler {
    void onAuthSuccess();
    void onAuthFailure(String error);
}
