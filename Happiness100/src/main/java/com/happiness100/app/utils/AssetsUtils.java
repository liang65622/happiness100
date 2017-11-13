package com.happiness100.app.utils;/**
 * Created by Administrator on 2016/9/7.
 */

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 作者：justin on 2016/9/7 23:06
 */
public class AssetsUtils {
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
