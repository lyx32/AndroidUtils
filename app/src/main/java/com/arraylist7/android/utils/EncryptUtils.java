package com.arraylist7.android.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Administrator on 2016/11/16.
 */
public class EncryptUtils {

    // 噪音最小位数
    private final static int NOISE_DATA_MIN = 1024;
    // 噪音最大位数
    private final static int NOISE_DATA_MAX = 1024 * 4;
    // 加密头位数
    private final static int ENCRYPT_HEADER = 31;
    // 存放噪音长度
    private final static int ENCRYPT_NOISE_LENGTH = 4;
    // 加密方式（未使用）
    private final static int ENCRYPT_TYPE_LENGTH = 1;
    // 简单加密默认key长度
    private final static int SIMPLE_ENCRYPT_KEY_LENGTH = 16;
    // 复杂加密默认key长度
    private final static int COMPLEX_ENCRYPT_KEY_LENGTH = 8;
    private final static String CHARSET = "UTF-8";


    public static byte[] decryptData(byte[] data, String key) {
        try {
            return decryptData(data, key.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptData(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] encryptData(String content, String key) {
        try {
            return encryptData(content.getBytes(CHARSET), key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encryptData(byte[] bytes, String key) {
        try {
            return encryptData(bytes, key.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encryptData(byte[] bytes, byte[] keys) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(keys);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keys);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }


    /**
     * 复杂加密函数
     *
     * @param content 加密数据
     * @param key     密钥
     * @return
     */
    public static String complexEncrypt(String content, String key) {
        if (StringUtils.isNullOrEmpty(content)) return "";
        if (StringUtils.isNullOrEmpty(key)) return "";
        if (key.length() > 8)
            key = key.substring(0, 8);
        byte[] contentData = encryptData(content, key);
        byte[] noiseData_before = new byte[(int) StringUtils.random(NOISE_DATA_MIN, NOISE_DATA_MAX)];
        byte[] noiseData_after = new byte[NOISE_DATA_MIN];
        byte[] headerAndNoiseAndContent = new byte[ENCRYPT_HEADER + noiseData_before.length + contentData.length];
        byte[] headerAndNoiseAndContentEncrypt = null;
        byte[] headerData = new byte[ENCRYPT_HEADER];
        byte[] allData = null;
        /*
            加密流程：
            1（31位加密头，25位无效数据+4位噪音数据+1位加密方式数据）
            2（内容字节）
            3（1024至4096位前置噪音数据）
            4（1024位后置噪音数据）
            5（判断1+3+2长度是单数还是双数。单数在使用4的前16位作为加密key。双数则使用4的后16位作为加密key）
            6（1+3+2的内容组合在加密。加密key使用5）（加密后：5的key+（1+3+2）加密后的内容）
            7（1+6+4判断长度是单数还是双数。单数在使用6的前16位作为加密key。双数则使用6的后16位作为加密key）
            8（1+6+4的组合在加密，加密key使用7）（加密后：7的key+（1+6+4）加密后的内容）
            9（base64编码）
            加密的内容会增加2K——5K。
         */
        SecureRandom secureRandom = new SecureRandom();
        // 3
        secureRandom.nextBytes(noiseData_before);
        // 4
        secureRandom.nextBytes(noiseData_after);
        secureRandom.nextBytes(headerData);
        // 1.写入加密头
        System.arraycopy((noiseData_before.length + "").getBytes(), 0, headerData, ENCRYPT_HEADER - ENCRYPT_NOISE_LENGTH - ENCRYPT_TYPE_LENGTH, ENCRYPT_NOISE_LENGTH);
        headerData[headerData.length - 1] = 00;

        // 5.获取加密key
        byte[] encryptKey_5 = new byte[COMPLEX_ENCRYPT_KEY_LENGTH];
        int len = headerData.length + noiseData_before.length + contentData.length;
        if (0 == len % 2) {
            System.arraycopy(noiseData_before, noiseData_before.length - encryptKey_5.length, encryptKey_5, 0, encryptKey_5.length);
        } else {
            System.arraycopy(noiseData_before, 0, encryptKey_5, 0, encryptKey_5.length);
        }

        // 6.写入加密头，噪音，真实数据
        System.arraycopy(headerData, 0, headerAndNoiseAndContent, 0, headerData.length);
        System.arraycopy(noiseData_before, 0, headerAndNoiseAndContent, headerData.length, noiseData_before.length);
        System.arraycopy(contentData, 0, headerAndNoiseAndContent, headerData.length + noiseData_before.length, contentData.length);
        byte[] encryptByte_6 = encryptData(headerAndNoiseAndContent, encryptKey_5);
        headerAndNoiseAndContentEncrypt = new byte[encryptByte_6.length + COMPLEX_ENCRYPT_KEY_LENGTH];
        System.arraycopy(encryptKey_5, 0, headerAndNoiseAndContentEncrypt, 0, encryptKey_5.length);
        System.arraycopy(encryptByte_6, 0, headerAndNoiseAndContentEncrypt, encryptKey_5.length, encryptByte_6.length);

        // 7.获取新一轮加密key
        byte[] encryptKey_7 = new byte[COMPLEX_ENCRYPT_KEY_LENGTH];
        len = headerAndNoiseAndContentEncrypt.length;
        if (0 == len % 2) {
            System.arraycopy(headerAndNoiseAndContentEncrypt, headerAndNoiseAndContentEncrypt.length - encryptKey_7.length, encryptKey_7, 0, encryptKey_7.length);
        } else {
            System.arraycopy(headerAndNoiseAndContentEncrypt, 0, encryptKey_7, 0, encryptKey_7.length);
        }

        // 8.写入加密头，1次加密，后置噪音
        allData = new byte[ENCRYPT_HEADER + headerAndNoiseAndContentEncrypt.length + noiseData_after.length];
        System.arraycopy((noiseData_after.length + "").getBytes(), 0, headerData, ENCRYPT_HEADER - ENCRYPT_NOISE_LENGTH - ENCRYPT_TYPE_LENGTH, ENCRYPT_NOISE_LENGTH);
        System.arraycopy(headerData, 0, allData, 0, headerData.length);
        System.arraycopy(headerAndNoiseAndContentEncrypt, 0, allData, headerData.length, headerAndNoiseAndContentEncrypt.length);
        System.arraycopy(noiseData_after, 0, allData, headerData.length + headerAndNoiseAndContentEncrypt.length, noiseData_after.length);
        byte[] encryptData = encryptData(allData, encryptKey_7);
        byte[] endEncryptData = new byte[encryptData.length + COMPLEX_ENCRYPT_KEY_LENGTH];
        System.arraycopy(encryptKey_7, 0, endEncryptData, 0, COMPLEX_ENCRYPT_KEY_LENGTH);
        System.arraycopy(encryptData, 0, endEncryptData, COMPLEX_ENCRYPT_KEY_LENGTH, encryptData.length);
        String result =  encodeBase64(endEncryptData);
        endEncryptData = noiseData_after = noiseData_before = headerAndNoiseAndContent = headerAndNoiseAndContentEncrypt = encryptByte_6 = encryptKey_7 = encryptKey_5 = contentData = headerData = allData = null;
        return result;
    }


    public static String complexDecrypt(String content, String key) {
        if (StringUtils.isNullOrEmpty(content)) return "";
        if (StringUtils.isNullOrEmpty(key)) return "";
        if (key.length() > 8)
            key = key.substring(0, 8);
        byte[] allData = null;
        byte[] headerData = new byte[ENCRYPT_HEADER];
        byte[] encryptLength = new byte[ENCRYPT_NOISE_LENGTH];
        byte[] encryptData_8 = null;
        byte[] encryptData_6 = null;
        byte[] decryptData_6 = null;
        byte[] decryptContent = null;
        byte[] encryptContent = null;
        byte[] key_7 = new byte[COMPLEX_ENCRYPT_KEY_LENGTH];
        byte[] key_5 = new byte[COMPLEX_ENCRYPT_KEY_LENGTH];
        String result = "";
        try {
            byte[] allDatas = decodeBase64(content);
            System.arraycopy(allDatas, 0, key_7, 0, COMPLEX_ENCRYPT_KEY_LENGTH);
            allData = new byte[allDatas.length - COMPLEX_ENCRYPT_KEY_LENGTH];
            System.arraycopy(allDatas, COMPLEX_ENCRYPT_KEY_LENGTH, allData, 0, allData.length);
            allData = decryptData(allData, key_7);
            // 读取加密头
            System.arraycopy(allData, 0, headerData, 0, headerData.length);
            encryptData_8 = new byte[allData.length - ENCRYPT_HEADER - NOISE_DATA_MIN];
            // 读取噪音和真实内容组合（加密的）
            System.arraycopy(allData, ENCRYPT_HEADER, encryptData_8, 0, encryptData_8.length);

            encryptData_6 = new byte[encryptData_8.length - COMPLEX_ENCRYPT_KEY_LENGTH];
            System.arraycopy(encryptData_8, 0, key_5, 0, COMPLEX_ENCRYPT_KEY_LENGTH);
            System.arraycopy(encryptData_8, COMPLEX_ENCRYPT_KEY_LENGTH, encryptData_6, 0, encryptData_6.length);
            decryptData_6 = decryptData(encryptData_6, key_5);

            System.arraycopy(decryptData_6, 0, headerData, 0, headerData.length);
            System.arraycopy(headerData, ENCRYPT_HEADER - ENCRYPT_NOISE_LENGTH - ENCRYPT_TYPE_LENGTH, encryptLength, 0, ENCRYPT_NOISE_LENGTH);
            int noiseLength = Integer.valueOf(new String(encryptLength));

            encryptContent = new byte[decryptData_6.length - ENCRYPT_HEADER - noiseLength];
            System.arraycopy(decryptData_6, ENCRYPT_HEADER + noiseLength, encryptContent, 0, encryptContent.length);
            decryptContent = decryptData(encryptContent, key);
            result =  new String(decryptContent, CHARSET);
        } catch (Exception e) {
            LogUtils.e("解密失败", e);
        } finally {
            encryptData_6 = decryptData_6 = encryptData_8 = allData = headerData = encryptLength = decryptContent = encryptContent = key_7 = key_5 = null;
        }
        return result;
    }


    /**
     * 中等加密函数
     *
     * @param content 加密数据
     * @param key     密钥
     * @return 返回加密后的数据
     */

    public static String mediumEncrypt(String content, String key) {
        if (StringUtils.isNullOrEmpty(content)) return "";
        if (StringUtils.isNullOrEmpty(key)) return "";
        if (key.length() > 8)
            key = key.substring(0, 8);
        byte[] contentData = encryptData(content, key);
        byte[] noiseData = new byte[(int) StringUtils.random(NOISE_DATA_MIN, NOISE_DATA_MAX)];
        byte[] noiseAndContentData = new byte[noiseData.length + contentData.length];
        byte[] headerData = new byte[ENCRYPT_HEADER];
        byte[] allData = null;
        String result = "";
        /*
            加密流程：
            1（31位加密头，25位无效数据+4位噪音数据+1位加密方式数据）
            2（内容字节）
            3（1024至4096位噪音数据）
            4（2+3的内容组合在加密）
            5（1+4的组合在加密）
            6（base64编码）
            加密的内容会增加1K——4K。
         */
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(noiseData);
        secureRandom.nextBytes(headerData);
        // 写入噪音和真实数据
        System.arraycopy(contentData, 0, noiseAndContentData, 0, contentData.length);
        System.arraycopy(noiseData, 0, noiseAndContentData, contentData.length, noiseData.length);
        // 将噪音数据和真实数据一起加密
        byte[] noiseAndContentencryptData = encryptData(noiseAndContentData, key);
        // 创建完整加密内容
        allData = new byte[headerData.length + noiseAndContentencryptData.length];
        // 写入加密头（25位无效数据+4位噪音数据+1位加密方式数据）
        System.arraycopy((noiseData.length + "").getBytes(), 0, headerData, ENCRYPT_HEADER - ENCRYPT_NOISE_LENGTH - ENCRYPT_TYPE_LENGTH, ENCRYPT_NOISE_LENGTH);
        headerData[headerData.length - 1] = "A".getBytes()[0];
        for (int i = 0; i < (ENCRYPT_HEADER - ENCRYPT_NOISE_LENGTH - ENCRYPT_TYPE_LENGTH); i += 5) {
            headerData[i] = headerData[headerData.length - 1];
        }
        System.arraycopy(headerData, 0, allData, 0, headerData.length);
        System.arraycopy(noiseAndContentencryptData, 0, allData, headerData.length, noiseAndContentencryptData.length);
        byte[] encryptData = encryptData(allData, key);
        result = encodeBase64(encryptData);
        encryptData = noiseAndContentencryptData = noiseData = headerData = allData = null;
        return result;
    }


    /**
     * 中等解密函数
     *
     * @param content 解密数据
     * @param key     密钥
     * @return 返回解密后的数据
     */
    public static String mediumDecrypt(String content, String key) {
        if (StringUtils.isNullOrEmpty(content)) return "";
        if (StringUtils.isNullOrEmpty(key)) return "";
        if (key.length() > 8)
            key = key.substring(0, 8);
        byte[] allData = null;
        byte[] headerData = new byte[ENCRYPT_HEADER];
        byte[] encryptLength = new byte[ENCRYPT_NOISE_LENGTH];
        byte[] noiseAndContentEncrypt = null;
        byte[] noiseAndContent = null;
        byte[] decryptData = null;
        String result = "";
        try {
            allData = decryptData(decodeBase64(content), key);
            // 读取加密头
            System.arraycopy(allData, 0, headerData, 0, headerData.length);
            // 读取噪音长度
            System.arraycopy(headerData, ENCRYPT_HEADER - ENCRYPT_NOISE_LENGTH - ENCRYPT_TYPE_LENGTH, encryptLength, 0, ENCRYPT_NOISE_LENGTH);
            int noiseLength = Integer.valueOf(new String(encryptLength));
            noiseAndContentEncrypt = new byte[allData.length - ENCRYPT_HEADER];
            // 读取噪音和真实内容组合（加密的）
            System.arraycopy(allData, ENCRYPT_HEADER, noiseAndContentEncrypt, 0, noiseAndContentEncrypt.length);
            // 读取噪音和真实内容组合（解密的）
            noiseAndContent = decryptData(noiseAndContentEncrypt, key);
            // 读取真实数据
            decryptData = new byte[noiseAndContent.length - noiseLength];
            System.arraycopy(noiseAndContent, 0, decryptData, 0, decryptData.length);
            decryptData = decryptData(decryptData, key);
            result =  new String(decryptData, CHARSET);
        } catch (Exception e) {
            LogUtils.e("解密失败", e);
        } finally {
            allData = headerData = encryptLength = noiseAndContentEncrypt = noiseAndContent = decryptData = null;
        }
        return result;
    }

    /**
     * 简单加密函数
     *
     * @param content 加密数据
     * @return 返回加密后的数据
     */
    public static String simpleEncrypt(String content, String key) {
        byte[] keyBytes = null;
        byte[] encrypt = null;
        byte[] allData = null;
        String result = "";
        /*
            加密流程
            1（判断加密key的字节是否有16位，不足随机补上，超过则只取16位）
            2（将内容加密）
            3（用于加密的key加上加密之后的内容组合在一起）
            4（base64编码）
            加密力度不是很大，
         */
        try {
            keyBytes = key.getBytes(CHARSET);
            byte[] _keyBytes = new byte[SIMPLE_ENCRYPT_KEY_LENGTH];
            if (keyBytes.length < SIMPLE_ENCRYPT_KEY_LENGTH) {
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(_keyBytes);
                System.arraycopy(keyBytes, 0, _keyBytes, 0, keyBytes.length);
            } else {
                System.arraycopy(keyBytes, 0, _keyBytes, 0, SIMPLE_ENCRYPT_KEY_LENGTH);
            }
            keyBytes = _keyBytes;
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            IvParameterSpec IVSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES"), IVSpec);
            encrypt = cipher.doFinal(content.getBytes(CHARSET));
            allData = new byte[encrypt.length + keyBytes.length];
            System.arraycopy(keyBytes, 0, allData, 0, keyBytes.length);
            System.arraycopy(encrypt, 0, allData, keyBytes.length, encrypt.length);
            result =  encodeBase64(allData);
        } catch (Exception e) {
            LogUtils.e("加密失败", e);
        } finally {
            keyBytes = encrypt = allData = null;
        }
        return result;
    }

    /**
     * 简单解密函数
     *
     * @param content 解密数据
     * @return 返回解密后的数据
     */

    public static String simpleDecrypt(String content, String key) {
        byte[] keyBytes = new byte[SIMPLE_ENCRYPT_KEY_LENGTH];
        byte[] decrypt = null;
        byte[] encrypt = null;
        byte[] allData = null;
        String result = "";
        try {
            allData = decodeBase64(content);
            encrypt = new byte[allData.length - keyBytes.length];
            System.arraycopy(allData, 0, keyBytes, 0, keyBytes.length);
            System.arraycopy(allData, keyBytes.length, encrypt, 0, encrypt.length);
            byte[] _keyBytes = key.getBytes(CHARSET);
            if (_keyBytes.length < SIMPLE_ENCRYPT_KEY_LENGTH) {
                System.arraycopy(_keyBytes, 0, keyBytes, 0, _keyBytes.length);
            } else {
                System.arraycopy(_keyBytes, 0, keyBytes, 0, SIMPLE_ENCRYPT_KEY_LENGTH);
            }
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            IvParameterSpec IVSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES"), IVSpec);
            decrypt = cipher.doFinal(encrypt);
            result = new String(decrypt, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("解密失败", e);
        } finally {
            keyBytes = decrypt = encrypt = allData = null;
        }
        return result;
    }


    public static String encodeBase64(byte[] b) {
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static byte[] decodeBase64(String base64String) throws Exception {
        return Base64.decode(base64String.getBytes(CHARSET), Base64.NO_WRAP);
    }

    public static String byteToHex(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] hexToByte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
