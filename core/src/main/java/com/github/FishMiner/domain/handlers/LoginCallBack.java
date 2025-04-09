package com.github.FishMiner.domain.handlers;

public interface LoginCallBack {
    void onSuccess();
    void onFailure(String errorMessage);
}
