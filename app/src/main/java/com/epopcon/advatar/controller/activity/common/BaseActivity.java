package com.epopcon.advatar.controller.activity.common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.epopcon.advatar.R;
import com.epopcon.advatar.application.AdvatarApplication;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.brand.BrandRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.controller.activity.brand.BrandChoiceActivity;
import com.epopcon.advatar.controller.activity.online.OnlineListActivity;
import com.epopcon.advatar.controller.activity.online.OnlineLoginActivity;
import com.epopcon.advatar.controller.activity.user.FindIdActivity;
import com.epopcon.advatar.controller.activity.user.FindPwActivity;
import com.epopcon.advatar.controller.activity.user.JoinActivity;
import com.epopcon.advatar.controller.activity.user.LoginActivity;
import com.epopcon.advatar.controller.activity.user.UpdatePwActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.security.MessageDigest;
import java.util.ArrayList;

import static com.epopcon.advatar.common.util.MyBrandUtil.getBrandCodeList;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    protected MessageDao mMessageDao = MessageDao.getInstance();
    public static final int SLIDE_DEFAULT = 0;
    public static final int SLIDE_LEFT_IN_RIGHT_OUT = 1;
    public static final int SLIDE_RIGHT_IN_LEFT_OUT = 2;
    protected int mActivityAnimationType = SLIDE_DEFAULT;

    protected AdvatarApplication mApplication;
    protected DisplayImageOptions mImageLoaderOptions;

    public ArrayList<BrandRepo> mBrandList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (AdvatarApplication) getApplication();

        mImageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_loading)
                .showImageForEmptyUri(R.drawable.ic_default_image)
                .showImageOnFail(R.drawable.ic_default_image)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .build();

        setActivityAnimationType();
        startActivityAnimation();
    }

    public void startActivityAnimation() {
        switch (mActivityAnimationType) {
            case SLIDE_LEFT_IN_RIGHT_OUT:
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                break;

            case SLIDE_RIGHT_IN_LEFT_OUT:
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                break;
        }
    }

    public void finishActivityAnimation() {
        switch (mActivityAnimationType) {
            case SLIDE_LEFT_IN_RIGHT_OUT:
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                break;

            case SLIDE_RIGHT_IN_LEFT_OUT:
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        finishActivityAnimation();
    }

    private void setActivityAnimationType() {

        if (this instanceof MainActivity ||
            this instanceof BrandChoiceActivity ||
            this instanceof WebViewActivity ||
            this instanceof OnlineListActivity ||
            this instanceof OnlineLoginActivity ||
            this instanceof FindIdActivity ||
            this instanceof FindPwActivity ||
            this instanceof JoinActivity ||
            this instanceof LoginActivity ||
            this instanceof UpdatePwActivity) {

            mActivityAnimationType = SLIDE_RIGHT_IN_LEFT_OUT;
        }
    }

    public void getBrandListAPI() {
        try {
            RestAdvatarProtocol.getInstance().getBrandList(50, new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    mBrandList.addAll((ArrayList<BrandRepo>) result);

                    for (int i = 0; i < mBrandList.size(); i++) {
                        if (getBrandCodeList().contains(mBrandList.get(i).brandCode)) {
                            mBrandList.get(i).setMyBrandYn(true);
                        } else {
                            mBrandList.get(i).setMyBrandYn(false);
                        }
                    }

                    setBrandList(mBrandList);
                }

                @Override
                public void onRequestFailure(Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPasswordEncryption(String userId, String password) {
        String encrypt = "";

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((password + userId).getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            encrypt = hexString.toString();

        } catch(Exception ex){
            throw new RuntimeException(ex);
        }

        return encrypt;
    }

    public ArrayList<BrandRepo> getBrandList() {
        return mBrandList;
    }

    public void setBrandList(ArrayList<BrandRepo> brandList) {
        this.mBrandList = brandList;
    }
}
