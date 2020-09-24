package com.epopcon.advatar.controller.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.BaseActivity;
import com.epopcon.advatar.controller.activity.common.WebViewActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JoinActivity extends BaseActivity {

    private ScrollView mScrollView;

    private EditText mEditId;
    private Button mBtnDuplicate;
    private EditText mEditPw;
    private EditText mEditPw2;
    private EditText mEditEmail1;
    private EditText mEditEmail2;
    private EditText mEditName;
    private EditText mEditBirthYear;
    private EditText mEditBirthMonth;
    private EditText mEditBirthDay;
    private EditText mEditPhone1;
    private EditText mEditPhone2;
    private EditText mEditPhone3;
    private CheckBox mCheckMan;
    private CheckBox mCheckWoman;
    private TextView mTxtPostNumber;
    private Button mBtnAddressSearch;
    private TextView mTxtAddress;
    private EditText mEditAddress;

    private TextView mPersonalInfo;
    private CheckBox mCheckPersonalInfo;
    private Button mBtnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        mEditId = (EditText) findViewById(R.id.edit_id);
        mEditPw = (EditText) findViewById(R.id.edit_pw);
        mEditPw2 = (EditText) findViewById(R.id.edit_pw2);

        mBtnDuplicate = (Button) findViewById(R.id.btn_duplicate);

        mEditEmail1 = (EditText) findViewById(R.id.edit_email1);
        mEditEmail2 = (EditText) findViewById(R.id.edit_email2);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditBirthYear = (EditText) findViewById(R.id.edit_birth_year);
        mEditBirthMonth = (EditText) findViewById(R.id.edit_birth_month);
        mEditBirthDay = (EditText) findViewById(R.id.edit_birth_day);
        mEditPhone1 = (EditText) findViewById(R.id.edit_phone1);
        mEditPhone2 = (EditText) findViewById(R.id.edit_phone2);
        mEditPhone3 = (EditText) findViewById(R.id.edit_phone3);

        mCheckMan = (CheckBox) findViewById(R.id.check_man);
        mCheckWoman = (CheckBox) findViewById(R.id.check_woman);

        mTxtPostNumber = (TextView) findViewById(R.id.txt_post_number);
        mBtnAddressSearch = (Button) findViewById(R.id.btn_address_search);
        mTxtAddress = (TextView) findViewById(R.id.txt_address);
        mEditAddress = (EditText) findViewById(R.id.edit_address);

        mPersonalInfo = (TextView) findViewById(R.id.txt_personal_info);
        mCheckPersonalInfo = (CheckBox) findViewById(R.id.check_agree);
        mBtnJoin = (Button) findViewById(R.id.btn_join);

        mCheckMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mCheckWoman.setChecked(false);
                }
            }
        });

        mCheckWoman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mCheckMan.setChecked(false);
                }
            }
        });

        mBtnAddressSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinActivity.this, WebViewActivity.class);
                startActivityForResult(intent, 10000);
            }
        });

        mTxtPostNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnAddressSearch.performClick();
            }
        });

        mTxtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnAddressSearch.performClick();
            }
        });

        mPersonalInfo.setMovementMethod(new ScrollingMovementMethod());

        mPersonalInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mCheckPersonalInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    SharedPreferenceBase.putPrefBoolean(getApplicationContext(), Config.PERSONAL_INFO_AGREE_YN, true);
                } else {
                    SharedPreferenceBase.putPrefBoolean(getApplicationContext(), Config.PERSONAL_INFO_AGREE_YN, false);
                }
            }
        });

        mBtnDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(mEditId);
                if (mEditId.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        RestAdvatarProtocol.getInstance().userDuplicateCheck(mEditId.getText().toString(), new RequestListener() {
                            @Override
                            public void onRequestSuccess(int requestCode, Object result) {
                                if (result.toString().equals("SUCCESS")) {
                                    Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    SharedPreferenceBase.putPrefBoolean(getApplicationContext(), Config.ID_DUPLICATE_CHECK_YN, true);
                                } else if (result.toString().equals("DUPLICATE")) {
                                    Toast.makeText(getApplicationContext(), "이미 사용중인 아이디입니다. 다른 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
            }
        });

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceBase.getPrefBoolean(getApplicationContext(), Config.PERSONAL_INFO_AGREE_YN, false)) {
                    if (SharedPreferenceBase.getPrefBoolean(getApplicationContext(), Config.ID_DUPLICATE_CHECK_YN, false)) {
                        userJoin();
                    } else {
                        Toast.makeText(getApplicationContext(), "아이디 중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "개인정보 이용에 동의해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userJoin() {

        try {
            final String id = mEditId.getText().toString();
            final String pw = mEditPw.getText().toString();
            String pw2 = mEditPw2.getText().toString();
            String fcmToken = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.FCM_TOKEN, "");
            final String name = mEditName.getText().toString();
            final String birth = mEditBirthYear.getText().toString() + "-" + mEditBirthMonth.getText().toString() + "-" + mEditBirthDay.getText().toString();
            final String phone = mEditPhone1.getText().toString() + mEditPhone2.getText().toString() + mEditPhone3.getText().toString();
            final String email = mEditEmail1.getText().toString()+ "@" + mEditEmail2.getText().toString();
            final String address = mTxtAddress.getText().toString() + " " + mEditAddress.getText().toString();

            if (pw.length() < 8 || pw2.length() < 8) {
                Toast.makeText(getApplicationContext(), "비밀번호는 8자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pw.equals(pw2)) {
                Toast.makeText(getApplicationContext(), "비밀번호가 다릅니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String pattern = "(\\d{3})[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})";
            if (!phone.matches(pattern)) {
                Toast.makeText(getApplicationContext(), "전화번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.length() < 2) {
                Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean birthValidate;
            try {
                dateFormat.setLenient(false);
                dateFormat.parse(birth);
                birthValidate = true;
            } catch (ParseException e) {
                birthValidate = false;
            }

            if (!birthValidate) {
                Toast.makeText(getApplicationContext(), "생년월일을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!mCheckWoman.isChecked() && !mCheckMan.isChecked()) {
                Toast.makeText(getApplicationContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (address.equals(" ")) {
                Toast.makeText(getApplicationContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(mEditAddress.getText().toString())) {
                Toast.makeText(getApplicationContext(), "상세 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = "";
            if (mCheckMan.isChecked()) {
                gender = "M";
            } else {
                gender = "W";
            }

            final String finalGender = gender;
            RestAdvatarProtocol.getInstance().userJoin(id, pw, Build.MODEL, fcmToken,
                    name, birth, gender, phone, address, email, new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_NAME, name);
                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_EMAIL, email);
                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_BIRTH, birth);
                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_GENDER, finalGender);
                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_PHONE, phone);
                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_ADDRESS, address);

                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다. 로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onRequestFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), "오류가 발생하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case 10000:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        int index = data.indexOf(", ");

                        String postCode = data.substring(0, index);
                        String address = data.substring(index+1);
                        mTxtPostNumber.setText(postCode);
                        mTxtAddress.setText(address);
                    }
                }
                break;
        }
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
