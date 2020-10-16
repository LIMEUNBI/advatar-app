package com.epopcon.advatar.controller.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.common.BaseActivity;
import com.epopcon.advatar.controller.activity.common.WebViewActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class UserModifyActivity extends BaseActivity {

    private static String TAG = UserModifyActivity.class.getSimpleName();

    private ScrollView mScrollView;

    private TextView mTxtUserId;
    private EditText mEditPw1;
    private EditText mEditPw2;
    private EditText mEditEmail1;
    private EditText mEditEmail2;
    private EditText mEditPhone1;
    private EditText mEditPhone2;
    private EditText mEditPhone3;
    private TextView mTxtUserName;
    private EditText mEditBirth1;
    private EditText mEditBirth2;
    private EditText mEditBirth3;
    private CheckBox mCheckMan;
    private CheckBox mCheckWoman;
    private TextView mTxtPostNumber;
    private Button mBtnAddressSearch;
    private TextView mTxtAddress;
    private EditText mEditAddress;
    private Button mBtnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        mTxtUserId = (TextView) findViewById(R.id.txt_user_id);
        mEditPw1 = (EditText) findViewById(R.id.edit_pw1);
        mEditPw2 = (EditText) findViewById(R.id.edit_pw2);
        mEditEmail1 = (EditText) findViewById(R.id.edit_email1);
        mEditEmail2 = (EditText) findViewById(R.id.edit_email2);
        mEditPhone1 = (EditText) findViewById(R.id.edit_phone1);
        mEditPhone2 = (EditText) findViewById(R.id.edit_phone2);
        mEditPhone3 = (EditText) findViewById(R.id.edit_phone3);

        mTxtUserName = (TextView) findViewById(R.id.txt_user_name);
        mEditBirth1 = (EditText) findViewById(R.id.edit_birth_year);
        mEditBirth2 = (EditText) findViewById(R.id.edit_birth_month);
        mEditBirth3 = (EditText) findViewById(R.id.edit_birth_day);
        mCheckMan = (CheckBox) findViewById(R.id.check_man);
        mCheckWoman = (CheckBox) findViewById(R.id.check_woman);
        mTxtPostNumber = (TextView) findViewById(R.id.txt_post_number);
        mBtnAddressSearch = (Button) findViewById(R.id.btn_address_search);
        mTxtAddress = (TextView) findViewById(R.id.txt_address);
        mEditAddress = (EditText) findViewById(R.id.edit_address);
        mBtnModify = (Button) findViewById(R.id.btn_modify);

        mTxtUserId.setText(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, ""));
        String email = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_EMAIL, "");
        mEditEmail1.setText(email.substring(0, email.indexOf("@")));
        mEditEmail2.setText(email.substring(email.indexOf("@") + 1));

        String phone = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_PHONE, "");
        String[] phones = phone.split("-");
        mEditPhone1.setText(phones[0]);
        mEditPhone2.setText(phones[1]);
        mEditPhone3.setText(phones[2]);

        mTxtUserName.setText(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_NAME, ""));

        String birth = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_BIRTH, "");
        String[] births = birth.split("-");
        mEditBirth1.setText(births[0]);
        mEditBirth2.setText(births[1]);
        mEditBirth3.setText(births[2]);

        String gender = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_GENDER, "");
        if (gender.equals("M")) {
            mCheckMan.setChecked(true);
            mCheckWoman.setChecked(false);
        } else {
            mCheckWoman.setChecked(true);
            mCheckMan.setChecked(false);
        }

        mCheckMan.setClickable(false);
        mCheckWoman.setClickable(false);

        String address = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ADDRESS, "");
        String[] addressArray = address.split("/ ");
        mTxtAddress.setText(addressArray[0]);
        mEditAddress.setText(addressArray[1]);

        mBtnAddressSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserModifyActivity.this, WebViewActivity.class);
                startActivityForResult(intent, 10000);
            }
        });

        mBtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = mTxtUserId.getText().toString();
                final String pw1 = mEditPw1.getText().toString();
                String pw2 = mEditPw2.getText().toString();
                final String birth = mEditBirth1.getText().toString() + "-" + mEditBirth2.getText().toString() + "-" + mEditBirth3.getText().toString();
                final String phone = mEditPhone1.getText().toString() + "-" + mEditPhone2.getText().toString() + "-" + mEditPhone3.getText().toString();
                final String email = mEditEmail1.getText().toString()+ "@" + mEditEmail2.getText().toString();
                final String address = mTxtAddress.getText().toString() + "/ " + mEditAddress.getText().toString();

                if (pw1.length() < 8 || pw2.length() < 8) {
                    Toast.makeText(getApplicationContext(), "비밀번호는 8자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pw1.equals(pw2)) {
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

                if (address.equals(" ")) {
                    Toast.makeText(getApplicationContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(mEditAddress.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "상세 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    RestAdvatarProtocol.getInstance().userInfoModify(id, pw1, birth, phone, address, email, new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            Toast.makeText(getApplicationContext(), "회원정보 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
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
        });
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
