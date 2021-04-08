package com.itheima.base64;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * Base64加密解密
 * @date 2020/11/7 0007 15:59
 */
public class Base64Test {
    /***
     * 加密测试
     */
    @Test
    public void testEncode() throws UnsupportedEncodingException {
        byte[] encode = Base64.getEncoder().encode("abcdefg".getBytes());
        String encodestr = new String(encode,"UTF-8");
        System.out.println("加密后的密文:"+encodestr);
    }

    /***
     * 解密Base64
     */
    @Test
    public void testDecode()throws Exception{
        String encodestr = "YWJjZGVmZw==";
        byte[] decode = Base64.getDecoder().decode(encodestr);
        String string = new String(decode, "UTF-8");
        System.out.println("解密后的密文:"+string);
    }

}