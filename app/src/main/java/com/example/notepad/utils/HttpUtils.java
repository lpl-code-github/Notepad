package com.example.notepad.utils;

public class HttpUtils {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String URL = "http://121.199.44.171:8585";

    public static Request postRequestBuilder(String action, NotepadBean req) {
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(req));
        Request request = new Request.Builder().url(String.format("%s/%s", URL, action)).post(body)
                .addHeader("content-type", "application/json").build();
        return request;
    }

    public static Request getRequestBuilder(String action) {
        return new Request.Builder().url(String.format("%s/%s", URL, action)).build();
    }
}