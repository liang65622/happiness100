package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/1.
 */

import java.lang.reflect.Field;

/**
 * 作者：justin on 2016/9/1 17:56
 */
public class Test extends Family {

    public Test(){

    }
    public static void main(String[] args) throws IllegalAccessException {

        /*
        * 实列化类 方法2
        */
        Test bean = new Test();

        //得到类对象
        Class userCla = (Class) bean.getClass();

       /*
        * 得到类中的所有属性集合
        */
        Field[] fs = userCla.getDeclaredFields();
        for(int i = 0 ; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); //设置些属性是可以访问的
            Object val = null;//得到此属性的值
            try {
                val = f.get(bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            System.out.println("name:" + f.getName() + "\t value = " + val);


        }
    }
}
