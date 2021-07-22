package com.example.timeassistant.model;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonConverter {
    public static <T> T fromStringToType(String json, Type type) { // v2
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }
    public static <T> String fromTypeToString(T type) {
        Gson gson = new Gson();
        String json = gson.toJson(type);
        return json;
    }
}