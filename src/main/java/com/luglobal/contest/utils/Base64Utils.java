package com.luglobal.contest.utils;
  
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;


/** *//** 
 * <p> 
 * BASE64编码解码工具包 
 * </p> 
 * <p> 
 * 依赖javabase64-1.3.1.jar 
 * </p> 
 *  
 * @author IceWee 
 * @date 2012-5-19 
 * @version 1.0 
 */  
public class Base64Utils {  
  
    /** *//** 
     * 文件读取缓冲区大小 
     */  
    private static final int CACHE_SIZE = 1024;  
      
    /** *//** 
     * <p> 
     * BASE64字符串解码为二进制数据 
     * </p> 
     *  
     * @param base64 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decode(String base64) throws Exception {  
        return Base64.decodeBase64(base64.getBytes());  
    }  
      
    /** *//** 
     * <p> 
     * 二进制数据编码为BASE64字符串 
     * </p> 
     *  
     * @param bytes 
     * @return 
     * @throws Exception 
     */  
    public static String encode(byte[] bytes) throws Exception {  
        return new String(Base64.encodeBase64(bytes));  
    }  
      
    /** *//** 
     * <p> 
     * 将文件编码为BASE64字符串 
     * </p> 
     * <p> 
     * 大文件慎用，可能会导致内存溢出 
     * </p> 
     *  
     * @param filePath 文件绝对路径 
     * @return 
     * @throws Exception 
     */  
    public static String encodeFile(String filePath) throws Exception {  
        byte[] bytes = fileToByte(filePath);  
        return encode(bytes);  
    }  
      
    /** *//** 
     * <p> 
     * BASE64字符串转回文件 
     * </p> 
     *  
     * @param filePath 文件绝对路径 
     * @param base64 编码字符串 
     * @throws Exception 
     */  
    public static void decodeToFile(String filePath, String base64) throws Exception {  
        byte[] bytes = decode(base64);  
        byteArrayToFile(bytes, filePath);  
    }  
      
    /** *//** 
     * <p> 
     * 文件转换为二进制数组 
     * </p> 
     *  
     * @param filePath 文件路径 
     * @return 
     * @throws Exception 
     */  
    public static byte[] fileToByte(String filePath) throws Exception {  
        byte[] data = new byte[0];  
        File file = new File(filePath);  
        if (file.exists()) {  
            FileInputStream in = new FileInputStream(file);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);  
            byte[] cache = new byte[CACHE_SIZE];  
            int nRead = 0;  
            while ((nRead = in.read(cache)) != -1) {  
                out.write(cache, 0, nRead);  
                out.flush();  
            }  
            out.close();  
            in.close();  
            data = out.toByteArray();  
         }  
        return data;  
    }  
      
    /** *//** 
     * <p> 
     * 二进制数据写文件 
     * </p> 
     *  
     * @param bytes 二进制数据 
     * @param filePath 文件生成目录 
     */  
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {  
        InputStream in = new ByteArrayInputStream(bytes);     
        File destFile = new File(filePath);  
        if (!destFile.getParentFile().exists()) {  
            destFile.getParentFile().mkdirs();  
        }  
        destFile.createNewFile();  
        OutputStream out = new FileOutputStream(destFile);  
        byte[] cache = new byte[CACHE_SIZE];  
        int nRead = 0;  
        while ((nRead = in.read(cache)) != -1) {     
            out.write(cache, 0, nRead);  
            out.flush();  
        }  
        out.close();  
        in.close();  
    }

    public static String byteArray2String(byte[] bs) {
        StringBuffer sbLogRet = new StringBuffer();
        for (int i = 0; i < bs.length; i++) {
            String inTmp = leftPad(Integer.toHexString(bs[i]), 2, '0');
            sbLogRet.append(inTmp);
        }
        return sbLogRet.toString();
    }

    public static String leftPad(String text, int length, char c) {
        if (text.length() >= length) {
            return text.substring(text.length() - length, text.length());
        }
        char[] array = new char[length];
        Arrays.fill(array, 0, length - text.length(), c);
        System.arraycopy(text.toCharArray(), 0, array, length - text.length(), text.length());
        return new String(array);
    }



    public static boolean GenerateImage(String imgStr)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "d://222.jpg";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}  
