package com.jweb.common.util;

import org.junit.Test;

public class HexUtilTest {
    @Test
    public void test(){
        String hexString = "0x330x4f190x330x330x260x200x33";
        System.out.println(HexUtil.decode(hexString));
        System.out.println(HexUtil.encode(HexUtil.decode(hexString)));

        System.out.println(HexUtil.decode("0x6d0x650x6e0x750x490x640x3d"));
    }

}