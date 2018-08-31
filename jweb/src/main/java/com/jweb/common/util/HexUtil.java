package com.jweb.common.util;

public class HexUtil {
    public static String decode(String s){
        StringBuffer sb = new StringBuffer();
        if(s!=null){
            String[] hexes = s.trim().split("0x");
            for(String hex:hexes){
                if(hex!=null && !hex.trim().equals("")){
                    hex = "0000".substring(0,4-hex.length())+hex;
                    int code = Integer.parseInt(hex,16);
                    sb.append((char) code);
                }
            }
        }
        return sb.toString();
    }

    public static String encode(String s){
        StringBuffer sb = new StringBuffer();
        if(s!=null){
            for(int i=0;i<s.length();i++){
                sb.append("0x");
                sb.append(Integer.toHexString(s.charAt(i)));
            }
        }
        return sb.toString();
    }
}
