package com.justin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public final class GsonUtils {

	public static <T> T parseJSON(String json, Class<T> clazz) {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
//		Gson gson = new Gson();
		T info = gson.fromJson(json, clazz);
		return info;
	}
	
	/**
	 * Type type = new 
			TypeToken&lt;ArrayList&lt;TypeInfo>>(){}.getType();
	   Type所在的包：java.lang.reflect
	   TypeToken所在的包：com.google.gson.reflect.TypeToken	
	 * @param jsonArr
	 * @param type
	 * @return
	 */
	public static <T> T parseJSONArray(String jsonArr, Type type) {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		T infos = gson.fromJson(jsonArr, type);
		return infos;
	}
	
	
	
	private GsonUtils(){}
}
