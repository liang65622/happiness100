package com.justin.utils;

import android.content.Context;
import android.text.TextUtils;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.datatype.BmobSmsState;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QuerySMSStateListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class SMSCodeBMobUtils {
	/**
	 * 请求短信验证码
	 * 
	 * @method requestSmsCode
	 * @return void
	 * @exception
	 */
	public static void requestSmsCode(Context context, String number, final RequestSMSCodeListener listener) {
		if (!TextUtils.isEmpty(number)) {
			BmobSMS.requestSMSCode(context, number, "注册模板",
					new RequestSMSCodeListener() {

						@Override
						public void done(Integer smsId, BmobException ex) {
							listener.done(smsId, ex);

						}
					});
		} else {
//			toast("请输入手机号码");
		}
	}

	/**
	 * 验证短信验证码
	 * 
	 * @method requestSmsCode
	 * @return void
	 * @exception
	 */
	public static void verifySmsCode(Context context, String number, String code, final VerifySMSCodeListener listener) {
		if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(code)) {
			BmobSMS.verifySmsCode(context, number, code,
					new VerifySMSCodeListener() {

						@Override
						public void done(BmobException ex) {
							listener.done(ex);
//							if (ex == null) {// 短信验证码已验证成功
//								toast("验证通过");
//							} else {
//								toast("验证失败：code =" + ex.getErrorCode()
//										+ ",msg = " + ex.getLocalizedMessage());
//							}
						}
					});
		} else {
//			toast("请输入手机号和验证码");
		}
	}

	/**
	 * 查询短信状态
	 * 
	 * @method querySmsState
	 * @return void
	 * @exception
	 */
	public static void querySmsState(Context context, int smsID, final QuerySMSStateListener listener) {
		BmobSMS.querySmsState(context, smsID, new QuerySMSStateListener() {

			@Override
			public void done(BmobSmsState state, BmobException ex) {
				listener.done(state, ex);
//				if (ex == null) {
//					toast("短信状态：" + state.getSmsState() + ",验证状态："
//							+ state.getVerifyState());
//				}
			}
		});
	}
}
