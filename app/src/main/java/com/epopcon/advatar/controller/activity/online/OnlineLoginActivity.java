package com.epopcon.advatar.controller.activity.online;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.model.OnlineBizType;
import com.epopcon.advatar.common.model.OnlineType;
import com.epopcon.advatar.common.util.event.Event;
import com.epopcon.advatar.common.util.event.EventTrigger;
import com.epopcon.advatar.controller.activity.BaseActivity;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.common.exception.PException;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiry;
import com.epopcon.extra.online.OnlineDeliveryInquiryHandler;
import com.epopcon.extra.online.OnlineDeliveryInquiryHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class OnlineLoginActivity extends BaseActivity {

    private final String TAG = OnlineLoginActivity.class.getSimpleName();

    private OnlineBizType bizType = null;
    private OnlineConstant type;
    private OnlineDeliveryInquiry inquiry;

    private EditText mEditId;
    private EditText mEditPw;

    private Button mBtnLogin;
    private ProgressDialog progressDialog;

    private AtomicBoolean lock = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_login);

        Intent intent = getIntent();
        String code = intent.getStringExtra("code");

        mEditId = (EditText) findViewById(R.id.online_store_id);
        mEditPw = (EditText) findViewById(R.id.online_store_password);

        mEditPw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                tryLogin(textView);
                return true;
            }
        });

        mBtnLogin = (Button) findViewById(R.id.btn_online_login);

        try {
            bizType = new OnlineBizType(this);
            type = OnlineConstant.valueOf(code);
            inquiry = ExtraClassLoader.getInstance().newOnlineDeliveryInquiry(type, new OnlineDeliveryInquiryHandler() {
                @Override
                public void onReadyFailure(int action, final PException exception) {
                    if (action == OnlineConstant.ACTION_TRY_LOGIN) {
                        Log.d(TAG, "onReadyFailure -> " + exception.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                                Toast.makeText(OnlineLoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                inquiry.removeIdAndPassword();
                            }
                        });
                    }
                }

                @Override
                public void onReadySuccess(int action) {
                    Log.d(TAG, "onReadySuccess -> " + action);
                    if (action == OnlineConstant.ACTION_TRY_LOGIN) {

                        switch (type) {
                            case NAVER:
                                // 기기등록이 필요 할 경우
                                boolean loginAndDeviceAdd = this.getObject("loginAndDeviceAdd", false);
                                // 기기등록에 필요한 Form 파라미터
                                Map<String, List<String>> form = this.getObject("form", Collections.EMPTY_MAP);

                                if (loginAndDeviceAdd) {
                                    synchronized (lock) {
                                        try {
                                            lock.wait();
                                        } catch (InterruptedException e) {
                                        }
                                    }

                                    if (!lock.get()) {
                                        inquiry.removeIdAndPassword();
                                        finish();
                                        return;
                                    }
                                    break;
                                }
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(OnlineLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }

                        // 로그인 상태로 변경
                        OnlineDeliveryInquiryHelper.setStatus(getApplicationContext(), type, OnlineConstant.ONLINE_STORE_PROCESS_STATUS_LOGIN);

                        // 주문목록을 가져온다.
                        Event event = new Event(Event.Type.IMPORT_ONLINE_STORE);
                        event.setEventCode(String.format("%s[%s]", Event.Type.IMPORT_ONLINE_STORE, type));
                        event.putObject("type", type);

                        EventTrigger.getInstance(getApplicationContext()).triggerService(event);
                        // 이전 화면으로 이동
                        Intent intent = new Intent();

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
            // 온라인상점 로고
            ImageView logo = (ImageView) findViewById(R.id.online_store_logo);
            logo.setImageResource(OnlineType.onlineStoreLogoMap.get(type));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void tryLogin(View v) {

        final String id = mEditId.getText().toString();
        final String password = mEditPw.getText().toString();

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
            Toast.makeText(OnlineLoginActivity.this, R.string.settings_online_store_login_valid, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(getString(R.string.wait_message));

        if (inquiry.storeIdAndPassword(id, password)) {
            inquiry.tryLoginIfNotAuthenticated();
        } else {
            hideProgress();
            Toast.makeText(OnlineLoginActivity.this, R.string.error_result_save, Toast.LENGTH_SHORT).show();
        }
    }

    public void showHomepage(View v) {
        String url = bizType.url(type);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri u = Uri.parse(url);

        intent.setData(u);
        startActivity(intent);
    }

    private void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null)
            progressDialog.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inquiry != null)
            inquiry.destory();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

}
