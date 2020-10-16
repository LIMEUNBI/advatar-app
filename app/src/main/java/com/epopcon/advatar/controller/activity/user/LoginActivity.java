package com.epopcon.advatar.controller.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.user.UserLoginRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.DialogClickListener;
import com.epopcon.advatar.common.util.DialogUtil;
import com.epopcon.advatar.common.util.MyBrandUtil;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.common.BaseActivity;
import com.epopcon.advatar.controller.activity.brand.BrandChoiceActivity;
import com.epopcon.advatar.controller.activity.common.MainActivity;
import com.epopcon.advatar.network.NaverProfile;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends BaseActivity {

    private EditText mEditId;
    private EditText mEditPw;

    private Button mBtnLogin;
    private Button mBtnJoin;

    private TextView mTxtFindId;
    private TextView mTxtFindPw;

    private OAuthLoginButton mOAuthLoginButton;

    private RelativeLayout loginNaver;

    private String OAUTH_CLIENT_ID = "9QxeKQyEVhUsoNcFsiap";
    private String OAUTH_CLIENT_SECRET = "ghnLtu2dSh";
    private String OAUTH_CLIENT_NAME = "Advatar";

    public static OAuthLogin mOAuthLoginInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditId = (EditText) findViewById(R.id.edit_id);
        mEditPw = (EditText) findViewById(R.id.edit_pw);

        mBtnJoin = (Button) findViewById(R.id.btn_join);
        mBtnLogin = (Button) findViewById(R.id.btn_login);

        mTxtFindId = (TextView) findViewById(R.id.find_id);
        mTxtFindPw = (TextView) findViewById(R.id.find_pw);

        mEditPw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE :
                        mBtnLogin.performClick();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(mEditPw);
                if (TextUtils.isEmpty(mEditId.getText().toString()) || TextUtils.isEmpty(mEditPw.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    getLogin();
                }
            }
        });

        mTxtFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindIdActivity.class);
                startActivity(intent);
            }
        });

        mTxtFindPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPwActivity.class);
                startActivity(intent);
            }
        });

        String userId = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, "");
        String userPw = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_PW, "");

        if (!TextUtils.isEmpty(userId)) {
            mEditId.setText(userId);
            if (!TextUtils.isEmpty(userPw)) {
                mEditPw.setText(userPw);
            }
        }

        // Naver API 초기화
        mOAuthLoginInstance = OAuthLogin.getInstance();

        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(getApplicationContext(), OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

        loginNaver = (RelativeLayout) findViewById(R.id.layout_naver_login);

        // 네이버 로그인
        loginNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOAuthLoginButton.performClick();
            }
        });

        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.btn_naver_login);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

        mOAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OAuthLogin.getInstance().startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });
    }

    private void getLogin() {
        try {
            String fcmToken = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.FCM_TOKEN, "");
            RestAdvatarProtocol.getInstance().userLogin(mEditId.getText().toString(), mEditPw.getText().toString(), fcmToken, Build.MODEL, new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    UserLoginRepo userLoginRepo = (UserLoginRepo) result;
                    if (userLoginRepo.result.equals("SUCCESS")) {
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_ID, mEditId.getText().toString());
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_PW, mEditPw.getText().toString());
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_NAME, userLoginRepo.userName);
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_BIRTH, userLoginRepo.userBirth);
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_PHONE, userLoginRepo.userPhone);
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_GENDER, userLoginRepo.userGender);
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_ADDRESS, userLoginRepo.userAddress);
                        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_EMAIL, userLoginRepo.userEmail);
                        MyBrandUtil.putBrandCodeList(userLoginRepo.userBrandCodes);
                        MyBrandUtil.putBrandNameList(userLoginRepo.userBrandNames);

                        Intent intent;
                        if (TextUtils.isEmpty(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, ""))) {
                            intent = new Intent(LoginActivity.this, BrandChoiceActivity.class);
                            intent.putExtra("finish", false);
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else if (userLoginRepo.result.equals("NO_USER")) {
                        Toast.makeText(getApplicationContext(), "등록되지 않은 아이디입니다. 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                    } else if (userLoginRepo.result.equals("PASSWORD_ERROR")) {
                        Toast.makeText(getApplicationContext(), "잘못된 비밀번호입니다. 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onRequestFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), "오류가 발생하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OAuthLoginHandler를 startOAuthLoginActivity() 메서드 호출 시 파라미터로 전달하거나 OAuthLoginButton
     객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run(boolean success) {
            if (success) {
                final String accessToken = mOAuthLoginInstance.getAccessToken(getApplicationContext());

                AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        NaverProfile naverProfile = new NaverProfile(accessToken);
                        String result = naverProfile.getProfile();

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            final JSONObject response = (JSONObject) jsonObject.get("response");

                            final String id = (String) response.get("id");
                            final String gender = (String) response.get("gender");
                            final String email = (String) response.get("email");
                            final String name = (String) response.get("name");
                            final String birth = (String) response.get("birthday");

                            final String userGender;
                            if (gender.equals("F")) {
                                userGender = "W";
                            } else {
                                userGender = "M";
                            }

                            // SNS 로그인
                            RestAdvatarProtocol.getInstance().userSNSLogin(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, ""),
                                    email, name, SharedPreferenceBase.getPrefString(getApplicationContext(), Config.FCM_TOKEN, ""), Build.MODEL, new RequestListener() {
                                        @Override
                                        public void onRequestSuccess(int requestCode, Object result) {
                                            UserLoginRepo userLoginRepo = (UserLoginRepo) result;
                                            if (userLoginRepo.result.equals("SUCCESS")) {
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_ID, mEditId.getText().toString());
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_PW, mEditPw.getText().toString());
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_NAME, userLoginRepo.userName);
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_BIRTH, userLoginRepo.userBirth);
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_PHONE, userLoginRepo.userPhone);
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_GENDER, userLoginRepo.userGender);
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_ADDRESS, userLoginRepo.userAddress);
                                                SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_EMAIL, userLoginRepo.userEmail);
                                                MyBrandUtil.putBrandCodeList(userLoginRepo.userBrandCodes);
                                                MyBrandUtil.putBrandNameList(userLoginRepo.userBrandNames);

                                                Intent intent;
                                                if (TextUtils.isEmpty(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, ""))) {
                                                    intent = new Intent(LoginActivity.this, BrandChoiceActivity.class);
                                                } else {
                                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                                }
                                                startActivity(intent);
                                                finish();
                                            } else if (result.toString().equals("NO USER")) {
                                                DialogUtil.showUserJoinDialog(LoginActivity.this, new DialogClickListener() {
                                                    @Override
                                                    public void onPositiveClick() {
                                                        Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                                                        intent.putExtra(Config.USER_NAME, name);
                                                        intent.putExtra(Config.USER_EMAIL, email);
                                                        intent.putExtra(Config.USER_GENDER, userGender);
                                                        intent.putExtra(Config.USER_BIRTH, birth);
                                                        intent.putExtra(Config.USER_ID, "Naver_" + id);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onNegativeClick() {

                                                    }
                                                });
                                            } else if (result.toString().equals("FAIL")){
                                                Toast.makeText(getApplicationContext(), "잘못된 시도입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onRequestFailure(Throwable t) {

                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        result.length();
                    }
                };
                asyncTask.execute();

            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(getApplicationContext()).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(getApplicationContext());
                Toast.makeText(getApplicationContext(), "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
