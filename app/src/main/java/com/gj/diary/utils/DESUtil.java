package com.gj.diary.utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class DESUtil {
    private static final String Algorithm = "DESede"; // 定义 加密算法,3DES

    public static String encrypt(byte[] str, byte[] key, String returnType) {
        byte[] encrypt = null;
        try {
            encrypt = encryptMode(key, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (returnType == null || "hex".equals(returnType)) {
            return byte2hex(encrypt);
        } else {
            String string = new String(Base64.getEncoder().encode(encrypt));
            return string;
        }
    }

    public static String encrypt(String str, byte[] key, String returnType) {
        try {
            return encrypt(str.getBytes("UTF-8"), key, returnType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 解密
    public static byte[] decrypt(String str, byte[] key, String returnType) {
        try {
            byte[] srcByte = null;
            if (returnType == null || "hex".equals(returnType)) {
                srcByte = hex2byte(str);
            } else {
                srcByte = Base64.getDecoder().decode(str.getBytes());
            }
            byte[] decrypt = decryptMode(key, srcByte);
            return decrypt;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b) { // 一个字节的数，
        // 转成16进制字符串
        String hs = "";
        String tmp = "";
        for (byte element : b) {
            tmp = (java.lang.Integer.toHexString(element & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); // 转成大写
    }

    /**
     * 字符串转java字节码
     *
     * @param str
     * @return
     */
    public static byte[] hex2byte(String str) {
        byte[] b = str.getBytes();
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;

    }

    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    // keybyte为加密密钥，长度为24字节

    // src为加密后的缓冲区

    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

            // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }

        return null;

    }

    public static byte[] Str2Hex(String str) {
        char[] ch = str.toCharArray();
        byte[] b = new byte[ch.length / 2];
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == 0) {
                break;
            }
            if (ch[i] >= '0' && ch[i] <= '9') {
                ch[i] = (char) (ch[i] - '0');
            } else if (ch[i] >= 'A' && ch[i] <= 'F') {
                ch[i] = (char) (ch[i] - 'A' + 10);
            }
        }
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) (((ch[2 * i] << 4) & 0xf0) + (ch[2 * i + 1] & 0x0f));
        }
        return b;
    }

    /**
     * 将byte数组转换为可见的字符串
     * @param b 待转换的byte数组
     * @return 转换后的字符串
     */
    public static String Hex2Str(byte[] b) {
        StringBuffer d = new StringBuffer(b.length * 2);
        for (byte element : b) {
            char hi = Character.forDigit((element >> 4) & 0x0F, 16);
            char lo = Character.forDigit(element & 0x0F, 16);
            d.append(Character.toUpperCase(hi));
            d.append(Character.toUpperCase(lo));
        }
        return d.toString();
    }

}
