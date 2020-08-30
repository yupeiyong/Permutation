package com.ypy.filelib;

public class StringUtils {
    public final static boolean isNumeric(String str){
        if(str!=null && !"".equals(str.trim())){
            return str.matches("^[0-9]*$");
        }else{
            return false;
        }
    }
}
