package com.happiness100.app.utils;/**
 * Created by Administrator on 2016/9/5.
 */

/**
 * 作者：justin on 2016/9/5 21:38
 */
public final class ScoreUtils {

    public static int getLv(Integer score) {
        if (score > 3500000) {
            return (int) Math.ceil( Math.pow((score + 1465494) / 26, 1 / 3.104));
        } else {
            return (int) Math.ceil( Math.pow((score / 28), 1 / 3.0));
        }

    }

    /*
        经验值≤3500000]  等级经验值= 等级^3*28    四舍五入保持个位为0    等级=3√经验值/28

                [3500000<经验值]  等级经验值= 等级^3.104*26-1465494    四舍五入保持个位为0    等级=3.104√（经验值+1465494）/26
    */
    public static int getUpLvExp(int score) {

        if (score > 3500000) {
            return (int)(Math.pow(getLv(score),3.104)*26-1465494);
        } else {
            return (int)(Math.pow(getLv(score),3)*28);
        }
    }
}
