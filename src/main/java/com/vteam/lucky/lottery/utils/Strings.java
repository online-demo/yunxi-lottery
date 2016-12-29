package com.vteam.lucky.lottery.utils;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月29日
 * @since soter 1.0.0
 */
public class Strings {
    public static boolean isNumeric(String str){
        try {
            Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
