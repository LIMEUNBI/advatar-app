package com.epopcon.advatar.common.util.event;

public interface Deferred {

    void onProgress(Object... o);

    void onSuccess(Object... o);

    void onFailure(Throwable e, Object... o);
}
