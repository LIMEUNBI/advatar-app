package com.epopcon.advatar.controller.activity.user;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.common.BaseActivity;

public class UpdatePwActivity extends BaseActivity {

    private EditText mEditPw1;
    private EditText mEditPw2;

    private Button mBtnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pw);

        mEditPw1 = (EditText) findViewById(R.id.edit_pw1);
        mEditPw2 = (EditText) findViewById(R.id.edit_pw2);

        mBtnConfirm = (Button) findViewById(R.id.btn_update_pw);

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(mEditPw2);
                try {
                    if (mEditPw1.equals(mEditPw2)) {
                        RestAdvatarProtocol.getInstance().userUpdatePw(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, null),
                                mEditPw1.getText().toString(), new RequestListener() {
                                    @Override
                                    public void onRequestSuccess(int requestCode, Object result) {
                                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다. 로그인해주세요.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onRequestFailure(Throwable t) {

                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "비밀번호가 다릅니다. 다시 한번 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }
        });

    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}