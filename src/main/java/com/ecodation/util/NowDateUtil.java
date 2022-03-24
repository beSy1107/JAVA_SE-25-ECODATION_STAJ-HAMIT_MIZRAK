package com.ecodation.util;

import java.util.Date;

public class NowDateUtil {

    public static String DateUtil(){
        return new Date(System.currentTimeMillis()).toString();
    }

}
