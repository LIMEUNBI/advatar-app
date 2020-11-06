package com.epopcon.advatar.common.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.param.online.OnlinePickProductParam;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.extra.common.utils.ExecutorPool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogUtil {

    public static void showCommonDialog(final Context context, final Activity activity,
                                        String title, String contents,
                                        boolean isPositiveButton, boolean isNegativeButton,
                                        String positiveText, String negativeText,
                                        final DialogClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_default, null);

        // title 표시
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.alert_title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        // content 표시
        TextView tvContent = (TextView) dialogView.findViewById(R.id.alert_content);
        if (!TextUtils.isEmpty(contents)) {
            tvContent.setText(contents);
            tvContent.setVisibility(View.VISIBLE);
        } else {
            tvContent.setVisibility(View.GONE);
        }

        TextView hLine = (TextView) dialogView.findViewById(R.id.h_line);
        TextView vLine = (TextView) dialogView.findViewById(R.id.v_line);
        // 가로 구분선 표시
        if (isPositiveButton || isNegativeButton) {
            hLine.setVisibility(View.VISIBLE);
        } else {
            hLine.setVisibility(View.GONE);
        }

        // 확인,취소 세로 구분선 표시
        if (isPositiveButton && isNegativeButton) {
            vLine.setVisibility(View.VISIBLE);
        } else {
            vLine.setVisibility(View.GONE);
        }

        TextView tvConfirm = (TextView) dialogView.findViewById(R.id.btn_confirm);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        // 확인버튼 문구
        if (!TextUtils.isEmpty(positiveText)) {
            tvConfirm.setText(positiveText);
        }
        // 취소버튼 문구
        if (!TextUtils.isEmpty(negativeText)) {
            tvCancel.setText(negativeText);
        }

        // 확인버튼 표시 여부
        if (isPositiveButton) {
            tvConfirm.setVisibility(View.VISIBLE);
        } else {
            tvConfirm.setVisibility(View.GONE);
        }
        // 취소버튼 표시 여부
        if (isNegativeButton) {
            tvCancel.setVisibility(View.VISIBLE);
        } else {
            tvCancel.setVisibility(View.GONE);
        }

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (clickListener != null)
                    clickListener.onPositiveClick();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (clickListener != null)
                    clickListener.onNegativeClick();
            }
        });
    }

    public static void showUserJoinDialog(final Activity activity, final DialogClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_user_join, null);

        TextView btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (clickListener != null)
                    clickListener.onPositiveClick();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (clickListener != null)
                    clickListener.onNegativeClick();
            }
        });
    }

    public static void showSharedUrlDialog(final Activity activity, final String productUrl, final OnlinePickProductParam onlinePickProductParam, final DialogClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_shared_url, null);

        TextView txtShareUrl = dialogView.findViewById(R.id.txt_shared_url);
        final EditText editSharedUrl = dialogView.findViewById(R.id.edit_shared_url);
        final CheckBox checkCon1 = dialogView.findViewById(R.id.check_con1);
        final CheckBox checkCon2 = dialogView.findViewById(R.id.check_con2);

        if (TextUtils.isEmpty(productUrl)) {
            txtShareUrl.setVisibility(View.GONE);
            editSharedUrl.setVisibility(View.VISIBLE);
        } else {
            txtShareUrl.setVisibility(View.VISIBLE);
            txtShareUrl.setText(productUrl);
            editSharedUrl.setVisibility(View.GONE);
        }

        if (onlinePickProductParam.collectionType != null) {
            if (onlinePickProductParam.collectionType.equals("A")) {
                checkCon1.setChecked(true);
                checkCon2.setChecked(false);
            } else if (onlinePickProductParam.collectionType.equals("B")) {
                checkCon1.setChecked(false);
                checkCon2.setChecked(true);
            }
        } else {
            checkCon1.setChecked(true);
            checkCon2.setChecked(false);
        }

        checkCon1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    checkCon2.setChecked(false);
                }
            }
        });

        checkCon2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    checkCon1.setChecked(false);
                }
            }
        });

        TextView btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "A";
                if (checkCon1.isChecked()) {
                    type = "A";
                } else if (checkCon2.isChecked()) {
                    type = "B";
                }

                if (onlinePickProductParam == null) {
                    final String storeName = getStoreName(editSharedUrl.getText().toString());
                    if (storeName.equals("미지원")) {
                        Toast.makeText(activity, "지원하지 않는 쇼핑몰 링크입니다.", Toast.LENGTH_LONG).show();
                    }
                    final String finalType = type;
                    ExecutorPool.NETWORK.execute(new Runnable() {
                        @Override
                        public void run() {
                            getOnlinePickInfo(activity, dialog, clickListener, storeName, editSharedUrl.getText().toString(), finalType);
                        }
                    });
                } else {
                    onlinePickProductParam.collectionType = type;
                    onlinePickProduct(onlinePickProductParam, new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            dialog.dismiss();
                            if (clickListener != null)
                                clickListener.onPositiveClick();
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {
                            Toast.makeText(activity, "Pick! 상품이 서버로 전달되지 못했습니다. 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (clickListener != null)
                    clickListener.onNegativeClick();
            }
        });
    }

    private static void onlinePickProduct(OnlinePickProductParam onlinePickProductParam, RequestListener requestListener) {
        try {
            RestAdvatarProtocol.getInstance().onlinePickProduct(onlinePickProductParam, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStoreName(String url) {
        String storeName;

        if (url.contains("11st.co.kr")) {
            storeName = "top.11st.co.kr";
        } else if (url.contains("gmarket.co.kr")) {
            storeName = "top.gmarket.co.kr";
        } else if (url.contains("auction.co.kr")) {
            storeName = "top.auction.co.kr";
        } else if (url.contains("shopping.naver.com")) {
            storeName = "top.naverstore.com";
        } else if (url.contains("interpark.com")) {
            storeName = "top.interpark.com";
        } else if (url.contains("coupang.com")) {
            storeName = "top.coupang.com";
        } else if (url.contains("tmon.co.kr")) {
            storeName = "top.tmon.co.kr";
        } else if (url.contains("wemakeprice.com")) {
            storeName = "top.wemakeprice.com";
        } else if (url.contains("ssg.com")) {
            storeName = "top.ssg.com";
        } else if (url.contains("lotteon.com")) {
            storeName = "top.lotteon.com";
        } else if (url.contains("hyundaihmall.com")) {
            storeName = "top.hyundaihmall.com";
        } else if (url.contains("cjmall.com")) {
            storeName = "top.cjmall.com";
        } else if (url.contains("akmall.com")) {
            storeName = "top.akmall.com";
        } else {
            storeName = "미지원";
        }

        return storeName;
    }

    private static void getOnlinePickInfo(Activity activity, AlertDialog dialog, DialogClickListener clickListener, final String siteName, final String productUrl, String type) {
        final OnlinePickProductParam onlinePickProductParam = new OnlinePickProductParam();
        try {
            URL url = new URL(productUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStreamReader in = new InputStreamReader((InputStream) connection.getContent(), "euc-kr");
                BufferedReader br = new BufferedReader(in);
                String line;
                String text = "";
                while ((line = br.readLine()) != null) {
                    text += line;
                }

                switch (siteName) {
                    case "top.11st.co.kr":
                        productParser11st(activity, dialog, clickListener, siteName, productUrl, type, onlinePickProductParam, text);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void productParser11st(Activity activity, final AlertDialog dialog, final DialogClickListener clickListener, String siteName,
                                          String productUrl, String type, OnlinePickProductParam onlinePickProductParam, String content) {
        try {
            Document doc = Jsoup.parse(content);

            String productName = doc.select("div.dt_title > h1").text();
            String noSale = doc.select("div.dt_price > p.no_sale").text();
            if (noSale.equals("현재 판매중인 상품이 아닙니다.")) {
                return;
            }
            int productPrice = Integer.valueOf(doc.select("div.dt_price > div.price > span > b").text().replace(",", ""));
            String delivery = doc.select("div.d_delivery > a > strong").text();
            int deliveryAmount;
            if (delivery.equals("무료배송")) {
                deliveryAmount = 0;
            } else {
                deliveryAmount = Integer.valueOf(doc.select("div.d_delivery > a > strong").text().replace("배송비", "")
                        .replace(",", "").replace("원", "").trim());

            }
            String productImg = doc.select("div.zone > ul > li > img").attr("src");

            onlinePickProductParam.productName = productName;
            onlinePickProductParam.productPrice = productPrice;
            onlinePickProductParam.deliveryAmount = deliveryAmount;
            onlinePickProductParam.collectionType = type;
            onlinePickProductParam.productImg = productImg;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());

            onlinePickProductParam.userId = SharedPreferenceBase.getPrefString(activity, Config.USER_ID, null);
            onlinePickProductParam.siteName = siteName;
            onlinePickProductParam.productUrl = productUrl;
            onlinePickProductParam.dateTime = dateTime;

            try {
                RestAdvatarProtocol.getInstance().onlinePickProduct(onlinePickProductParam, new RequestListener() {
                    @Override
                    public void onRequestSuccess(int requestCode, Object result) {
                        dialog.dismiss();
                        if (clickListener != null)
                            clickListener.onPositiveClick();
                    }

                    @Override
                    public void onRequestFailure(Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
