package com.justin.app;

/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.justin.utils.SharedPreferencesContext;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.bmob.v3.Bmob;

public class BaseApp extends Application {

	/**
	 * SDK初始化建议放在启动页
	 */
	public static String APPID = "60d80d6c6723fc70c960347bd90bdcab";//外

	public void onCreate() {
		super.onCreate();
		SharedPreferencesContext.init(this);
		ActiveAndroid.initialize(this);
		initImageLoader(getApplicationContext());
		initConfig(getApplicationContext());
		Bmob.initialize(getApplicationContext(), APPID);
	}

	
	/**
	 * 初始化文件配置
	 * @param context
	 */
	public static void initConfig(Context context) {
		BmobConfiguration config = new BmobConfiguration.Builder(context).customExternalCacheDir("Smile").build();
		BmobPro.getInstance(context).initConfig(config);
	}


	// @Override
	// public void onTerminate() {
	// super.onTerminate();
	// ActiveAndroid.dispose();
	// }

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}