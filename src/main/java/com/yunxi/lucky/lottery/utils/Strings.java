package com.yunxi.lucky.lottery.utils;

/**
 * @author 无双老师【云析学院】
 * @version 1.0.0 2019年12月29日
 * @since  1.0.0
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
