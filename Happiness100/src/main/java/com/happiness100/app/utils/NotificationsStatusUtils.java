package com.happiness100.app.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

public class NotificationsStatusUtils {

	private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
	private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

	/**
	 * API19以上的，能够获取是否开启通知的状态,开启返回true,关闭返回false, 19以下的不能，返回true
	 * */

	@TargetApi(19)
	public static boolean isNotificationEnabled(Context context) {
		final int version = Build.VERSION.SDK_INT;
		if (version >= 19) {
			AppOpsManager mAppOps = (AppOpsManager) context
					.getSystemService(Context.APP_OPS_SERVICE);

			ApplicationInfo appInfo = context.getApplicationInfo();

			String pkg = context.getApplicationContext().getPackageName();

			int uid = appInfo.uid;

			Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

			try {

				appOpsClass = Class.forName(AppOpsManager.class.getName());

				Method checkOpNoThrowMethod = appOpsClass.getMethod(
						CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
						String.class);

				Field opPostNotificationValue = appOpsClass
						.getDeclaredField(OP_POST_NOTIFICATION);
				int value = (int) opPostNotificationValue.get(Integer.class);

				return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid,
						pkg) == AppOpsManager.MODE_ALLOWED);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			return true;
		}
	}

}
