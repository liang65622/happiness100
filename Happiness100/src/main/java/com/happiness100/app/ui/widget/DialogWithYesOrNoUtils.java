package com.happiness100.app.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.happiness100.app.R;


public class DialogWithYesOrNoUtils {

    private static DialogWithYesOrNoUtils instance = null;

    public static DialogWithYesOrNoUtils getInstance() {
        if (instance == null) {
            instance = new DialogWithYesOrNoUtils();
        }
        return instance;
    }

    private DialogWithYesOrNoUtils() {
    }

    public void showDialog(Context context, String title, String titleInfo, final DialogCallBack callBack) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        alterDialog.setTitle(title);
        alterDialog.setMessage(titleInfo);
        alterDialog.setCancelable(true);

        alterDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.exectEvent();
                dialog.cancel();
            }
        });
        alterDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alterDialog.show();
    }

    public interface DialogCallBack {
        void exectEvent();

        void exectEditEvent(String editText);

        void updatePassword(String oldPassword, String newPassword);
    }


    public void showEditDialog(Context context, String title, String titleInfo, String hintText, String OKText, final DialogCallBack callBack) {
        final EditText et_search;
        AlertDialog.Builder dialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        dialog.setTitle(title);
        if (!titleInfo.isEmpty()) {
            dialog.setMessage(titleInfo);
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_edit, null);
        dialog.setView(layout);
        et_search = (EditText) layout.findViewById(R.id.view_edit);
        et_search.setHint(hintText);
        dialog.setPositiveButton(OKText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String s = et_search.getText().toString().trim();
                callBack.exectEditEvent(s);
            }
        });

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        dialog.show();
    }


    public void showEditPasswordDialog(Context context, String title, String titleInfo, String hintText, String OKText, final DialogCallBack callBack) {
        final EditText et_search;
        AlertDialog.Builder dialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        dialog.setTitle(title);
        if (!titleInfo.isEmpty()) {
            dialog.setMessage(titleInfo);
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_edit_password, null);
        dialog.setView(layout);
        et_search = (EditText) layout.findViewById(R.id.view_edit);
        et_search.setHint(hintText);
        dialog.setPositiveButton(OKText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String s = et_search.getText().toString().trim();
                callBack.exectEditEvent(s);
            }
        });

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        dialog.show();
    }

    public void showUpdatePasswordDialog(final Context context, final DialogCallBack callBack) {
//        final EditText oldPasswordEdit, newPasswrodEdit, newPassword2Edit;
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialogchangeview, null);
//        dialog.setView(layout);
//        oldPasswordEdit = (EditText) layout.findViewById(R.id.old_password);
//        newPasswrodEdit = (EditText) layout.findViewById(R.id.new_password);
//        newPassword2Edit = (EditText) layout.findViewById(R.id.new_password2);
//        dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                String old = oldPasswordEdit.getText().toString().trim();
//                String new1 = newPasswrodEdit.getText().toString().trim();
//                String new2 = newPassword2Edit.getText().toString().trim();
//                String cachePassword = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("loginpassword", "");
//                if (TextUtils.isEmpty(old)) {
//                    NToast.shortToast(context, R.string.original_password);
//                    return;
//                }
//                if (TextUtils.isEmpty(new1)) {
//                    NToast.shortToast(context, R.string.new_password_not_null);
//                    return;
//                }
//                if (TextUtils.isEmpty(new2)) {
//                    NToast.shortToast(context, R.string.confirm_password_not_null);
//                    return;
//                }
//                if (!cachePassword.equals(old)) {
//                    NToast.shortToast(context, R.string.original_password_mistake);
//                    return;
//                }
//                if (!new1.equals(new2)) {
//                    NToast.shortToast(context, R.string.passwords_do_not_match);
//                    return;
//                }
//                callBack.updatePassword(old, new1);
//            }
//        });
//
//        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//
//        });
//        dialog.show();
    }

}
