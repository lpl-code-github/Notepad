package com.example.notepad.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {
    //获取当前日期
    public static final String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}