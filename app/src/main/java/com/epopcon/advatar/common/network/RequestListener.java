package com.epopcon.advatar.common.network;

public interface RequestListener {

    void onRequestSuccess(int requestCode, Object result);

    void onRequestFailure(Throwable t);
}
