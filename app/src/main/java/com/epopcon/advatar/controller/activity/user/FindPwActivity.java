package com.epopcon.advatar.controller.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.controller.activity.common.BaseActivity;

public class FindPwActivity extends BaseActivity {

    private EditText mEditId;
    private EditText mEditName;
    private EditText mEditPhone;

    private Button mBtnFindPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        mEditId = (EditText) findViewById(R.id.edit_id);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditPhone = (EditText) findViewById(R.id.edit_phone);

        mBtnFindPw = (Button) findViewById(R.id.btn_find_pw);

        mBtnFindPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(mEditPhone);
                try {
                    RestAdvatarProtocol.getInstance().userFindPw(mEditId.getText().toString(), mEditName.getText().toString(), mEditPhone.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            if (result.toString().equals("SUCCESS")) {
                                // 비밀번호 변경
                                Intent intent = new Intent(FindPwActivity.this, UpdatePwActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "잘못된 정보입니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}