package com.example.exp5.model;

public class Res<T> {
    private T data;
    private String message;
    private String code;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static <T> Res<T> success(String message, T data, String code){
        Res<T> res = new Res<T>();
        res.setCode(code);
        res.setMessage(message);
        res.setData(data);
        return res;
    }
    public static <T> Res<T> success(String message){
        Res<T> res = new Res<T>();
        res.setMessage(message);
        return res;
    }
    public static <T> Res<T> err(String message){
        Res<T> res = new Res<T>();
        res.setMessage(message);
        return res;
    }

}