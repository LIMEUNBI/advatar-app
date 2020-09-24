package com.epopcon.advatar.common.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.epopcon.advatar.R;

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

        // 가로 구분선 표시
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
}
