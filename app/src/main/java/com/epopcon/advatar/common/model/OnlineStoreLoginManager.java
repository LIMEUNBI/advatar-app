package com.epopcon.advatar.common.model;

import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.OnlineStoreStatusRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.controller.activity.online.OnlineListActivity;

import java.util.ArrayList;
import java.util.List;

public class OnlineStoreLoginManager {
    private static final String TAG = OnlineListActivity.class.getSimpleName();

    private static List<OnlineStoreStatusRepo> mLoginStatus;
    private static OnlineStoreLoginManager instance = null;

    public OnlineStoreLoginManager() {
        mLoginStatus = new ArrayList<>();
    }

    public static synchronized OnlineStoreLoginManager getInstance() {
        if (instance == null) {
            synchronized (OnlineStoreLoginManager.class) {
                if (instance == null) {
                    instance = new OnlineStoreLoginManager();
                }
            }
        }
        return instance;
    }

    public void setCardCompanyStatus(OnlineStoreStatusRepo onlineStoreStatusRepo) {
        mLoginStatus.add(onlineStoreStatusRepo);
    }

    public boolean getOnlineStoreStatus(int storeCode) {
        boolean status = true;

        if (mLoginStatus == null) {
            try {
                RestAdvatarProtocol.getInstance().getOnlineStoreStatus(new RequestListener() {
                    @Override
                    public void onRequestSuccess(int requestCode, Object result) {
                        mLoginStatus.addAll((List<OnlineStoreStatusRepo>) result);

                        for (OnlineStoreStatusRepo onlineStoreRepo : mLoginStatus) {
                            setCardCompanyStatus(onlineStoreRepo);
                        }
                    }

                    @Override
                    public void onRequestFailure(Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mLoginStatus.get(storeCode) == null) {
                return true;
            }
        }

        for (int i = 0 ; i < mLoginStatus.size() ; i++) {
            if (mLoginStatus.get(i).storeId == storeCode) {
                if ("Y".equals(mLoginStatus.get(i).status))
                    status = true;
                else
                    status = false;
            }
        }

        return status;
    }
}
