package com.epopcon.advatar.controller.activity.user;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.user.UserFindIdRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.DialogClickListener;
import com.epopcon.advatar.common.util.DialogUtil;
import com.epopcon.advatar.controller.activity.common.BaseActivity;

public class FindIdActivity extends BaseActivity {

    private EditText mEditEmail;
    private EditText mEditName;
    private Button mBtnFindId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditName = (EditText) findViewById(R.id.edit_name);

        mBtnFindId = (Button) findViewById(R.id.btn_find_id);

        mBtnFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(mEditName);
                try {
                    RestAdvatarProtocol.getInstance().userFindId(mEditEmail.getText().toString(), mEditName.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            UserFindIdRepo userFindIdRepo = (UserFindIdRepo) result;

                            if (userFindIdRepo.result.equals("SUCCESS")) {
                                // Dialog 로 아이디 띄워주기
                                String userId = userFindIdRepo.userId;
                                DialogUtil.showCommonDialog(getApplicationContext(), FindIdActivity.this, "아이디 찾기", "고객님의 아이디는 " + userId + " 입니다.",
                                        false, true, "로그인하러 가기", null, new DialogClickListener() {
                                            @Override
                                            public void onPositiveClick() {
                                                finish();
                                            }

                                            @Override
                                            public void onNegativeClick() {

                                            }
                                        });

                            } else if (userFindIdRepo.result.equals("NO USER")) {
                                Toast.makeText(getApplicationContext(), "회원정보가 없습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
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
