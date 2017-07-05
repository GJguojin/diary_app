package com.gj.diary.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ImageSplitUtil {

    public static void main(String[] args) throws IOException {
    }
    public static Bitmap mergeImage(String readPath, String key) throws IOException {
        if (key == null || "".equals(key)) {
            return null;
        }
        key = ImageUtil.salt.toUpperCase()+key.substring(8+ImageUtil.salt.length(),32);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(readPath)));
        String returnString = "";
        String line = null;
        boolean flag = false;
        while ((line = reader.readLine()) != null) {
            if (flag && line.equals(ImageUtil.PHOTO_LINE)) {
                break;
            }
            if (flag) {
                returnString += new String(DESUtil.decrypt(line, key.getBytes("UTF-8"),"base64"),"UTF-8");
            }
            if (!flag && line.equals(ImageUtil.PHOTO_LINE)) {
                flag = true;
            }
        }
        reader.close();
        Bitmap mergeImage = mergeImage(readPath, returnString.split("&")[1], returnString.split("&")[0]);
        return mergeImage;
    }

    public static Bitmap mergeImage(String filePath, String codeString, String whString) throws IOException {
        String[][] code = getCode(codeString);
        int rows = code.length;
        int cols = code[0].length;

        Bitmap[][] imgs = splitImage(filePath, Integer.parseInt(whString.split("#")[0]), Integer.parseInt(whString.split("#")[1]), null, null);
        Bitmap[][] newImgs = new Bitmap[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String[] split = code[i][j].split("_");
                newImgs[Integer.parseInt(split[0])][Integer.parseInt(split[1])] = imgs[i][j];
            }
        }
        Bitmap imageBuffer = mergeImage(newImgs);
        return imageBuffer;

    }

    /**
     * 分割图片并保存
     *
     * @param destFile    保存的目的地
     * @param filePath    要处理的图片
     * @param chunkWidth  分割的宽
     * @param chunkHeight 分割的高
     * @return
     * @throws IOException
     */
    public static String splitImage(File destFile, String filePath, Integer chunkWidth, Integer chunkHeight) throws IOException {
        Map<String, Object> map = splitImage(filePath, chunkWidth, chunkHeight);
        Bitmap imageBuffer = (Bitmap) map.get("imageBuffer");
        ImageUtil.storeImage(imageBuffer, destFile);
        String codeString = (String) map.get("codeString");
        String whString = (String) map.get("whString");
        return whString + "&" + codeString;
    }

    public static Map<String, Object> splitImage(String filePath, Integer chunkWidth, Integer chunkHeight) throws IOException {
        Bitmap[][] imgs = splitImage(filePath, chunkWidth, chunkHeight, null, null);
        int rows = imgs.length;
        int cols = imgs[0].length;
        String[][] code = getCode(rows, cols);
        // 输出小图
        Bitmap[][] newImgs = new Bitmap[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //ImageIO.write(imgs[i][j], "jpg", new File("E:\\test\\split\\img" + i + j+".jpg"));
                String[] split = code[i][j].split("_");
                newImgs[i][j] = imgs[Integer.parseInt(split[0])][Integer.parseInt(split[1])];
            }
        }
        Bitmap mergeImage = mergeImage(newImgs);
        String codeString = getCodeString(code);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("codeString", codeString);
        returnMap.put("whString", "" + chunkWidth + "#" + chunkHeight);
        returnMap.put("imageBuffer", mergeImage);
        return returnMap;
    }

    /**
     * @param filePath    要分割的图片
     * @param chunkWidth  分割块的宽度
     * @param chunkHeight 分割快的高度
     * @param rows
     * @param cols
     * @return
     * @throws IOException
     */
    private static Bitmap[][] splitImage(String filePath, Integer chunkWidth, Integer chunkHeight, Integer rows, Integer cols) throws IOException {
        Bitmap image = ImageUtil.ratio(filePath);

        int width = image.getWidth(); // 原图宽
        int height = image.getHeight();// 原图高

        if (chunkWidth != null && chunkHeight != null) {
            rows = height % chunkHeight == 0 ? height / chunkHeight : (int) Math.ceil(1.0 * height / chunkHeight);
            cols = width % chunkWidth == 0 ? width / chunkWidth : (int) Math.ceil(1.0 * width / chunkWidth);
        } else if (rows != null && cols != null) {
            chunkWidth = width % cols == 0 ? width / cols : width / (cols - 1);
            chunkHeight = height % rows == 0 ? height / rows : height / (rows - 1);
        } else {
            chunkWidth = 50;
            chunkWidth = 50;
            rows = (int) Math.ceil(1.0 * height / chunkHeight);
            cols = (int) Math.ceil(1.0 * width / chunkWidth);
        }


        Bitmap imgs[][] = new Bitmap[rows][cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int smallWidth = 0; //分割后图片的宽
                int smallHeight = 0;//分割后图片的高
                if (rows - x != 1 && cols - y != 1) {//非边缘
                    smallWidth = chunkWidth;
                    smallHeight = chunkHeight;
                } else if (rows - x == 1 && cols - y != 1) {//最后一行
                    smallWidth = chunkWidth;
                    smallHeight = height % chunkHeight == 0 ? chunkHeight : height % chunkHeight;
                } else if (rows - x != 1 && cols - y == 1) {//最后一列
                    smallWidth = width % chunkWidth == 0 ? chunkWidth : width % chunkWidth;
                    smallHeight = chunkHeight;
                } else {
                    smallWidth = width % chunkWidth == 0 ? chunkWidth : width % chunkWidth;
                    smallHeight = height % chunkHeight == 0 ? chunkHeight : height % chunkHeight;
                }
                // 设置小图的大小和类型
                imgs[x][y] = Bitmap.createBitmap(image, chunkWidth * y, chunkHeight * x, smallWidth, smallHeight);
            }
        }
        return imgs;
    }


    /**
     * 合并图片
     *
     * @param imgs
     * @return
     * @throws IOException
     */
    private static Bitmap mergeImage(Bitmap[][] imgs) throws IOException {
        int imageWidth = 0;//原图宽
        int imageHeight = 0;//原图高
        for (int i = 0; i < imgs[0].length; i++) {
            imageWidth += imgs[0][i].getWidth();
        }
        for (Bitmap[] img : imgs) {
            imageHeight += img[0].getHeight();
        }
        int chunkWidth = imgs[0][0].getWidth();
        int chunkHeight = imgs[0][0].getHeight();
        //int type = imgs[0][0].getType();
        int rows = imgs.length;
        int cols = imgs[0].length;
        // 设置拼接后图的大小和类型
        Bitmap finalImg = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalImg);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //finalImg.createGraphics().drawImage(imgs[i][j], chunkWidth * j, chunkHeight * i, null);
                canvas.drawBitmap(imgs[i][j], chunkWidth * j, chunkHeight * i, null);
            }
        }
        return finalImg;
    }

    //得到二维随机数列
    private static String[][] getCode(int rows, int cols) {
        String[][] ss = new String[rows][cols];

        //非边缘
        int count = (rows - 1) * (cols - 1);
        int[] randoms = RandomUtil.getRandoms(count);
        for (int i = 0; i < randoms.length; i++) {
            String num = "" + (randoms[i] / (cols - 1)) + "_" + (randoms[i] % (cols - 1));
            ss[i / (cols - 1)][i % (cols - 1)] = num;
        }
        //右边缘
        randoms = RandomUtil.getRandoms(rows - 1);
        for (int i = 0; i < randoms.length; i++) {
            ss[i][cols - 1] = "" + randoms[i] + "_" + (cols - 1);
        }
        //下边缘
        randoms = RandomUtil.getRandoms(cols - 1);
        for (int i = 0; i < randoms.length; i++) {
            ss[(rows - 1)][i] = "" + (rows - 1) + "_" + randoms[i];
        }

        //最后一个
        ss[rows - 1][cols - 1] = "" + (rows - 1) + "_" + (cols - 1);

	/*	 for(int i=0;i<rows;i++){
			 for(int j=0;j<cols;j++){
				 System.out.print(ss[i][j]+",");
			 }
			 System.out.println();
		 }*/
        return ss;
    }

    private static String getCodeString(String[][] code) {
        String returnString = "";
        for (String[] rows : code) {
            for (String col : rows) {
                returnString += col + "#";
            }
            if (returnString.endsWith("#")) {
                returnString = returnString.substring(0, returnString.length() - 1);
            }
            returnString += "@";
        }
        if (returnString.endsWith("@")) {
            returnString = returnString.substring(0, returnString.length() - 1);
        }
        return returnString;
    }

    private static String[][] getCode(String codeString) {
        String[] rows = codeString.split("@");
        int rowsNum = rows.length;
        int colsNum = rows[0].split("#").length;
        String[][] code = new String[rowsNum][colsNum];
        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].split("#");
            for (int j = 0; j < cols.length; j++) {
                code[i][j] = cols[j];
            }
        }
        return code;
    }
}
