package com.gj.diary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.gj.diary.activity.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageUtil {
	
	private final static String MESSAGE_LINE= "=======================================================================";
	
	public final static String PHOTO_LINE= "***********************************************************************";

	public static final String salt = "diary";
//	public static final String salt = "lcc1107";

	public static final float W = 2080;//缩放后宽
	public static final float H = 1168;//缩放后高
	
	public static void coptFile( String picturePath,File destFile ) throws IOException {
		Bitmap bitmap = ratio(picturePath);
		coptFile(bitmap,destFile);
	}

	public static void coptFile( Bitmap bitmap,File destFile ) throws IOException {
		bitmap = ratio(bitmap);
		storeImage(bitmap,destFile);
	}

	/**
	 * Get bitmap from specified image path
	 *
	 * @param imgPath
	 * @return
	 */
	public static Bitmap getBitmap(String imgPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		// Do not compress
		newOpts.inSampleSize = 1;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(imgPath, newOpts);
	}

	/**
	 * Store bitmap into specified image path
	 *
	 * @param bitmap
	 * @param outPath
	 * @throws FileNotFoundException
	 */
	public static void storeImage(Bitmap bitmap, File outPath) throws IOException {
		FileOutputStream os = new FileOutputStream(outPath);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 73, os);
        os.flush();
        os.close();
	}

	/**
	 * Compress image by size, this will modify image width/height.
	 * Used to get thumbnail
	 *
	 * @param picturePath
	 * @return
	 */

	public static Bitmap ratio( String picturePath) throws IOException {
		Bitmap image =  BitmapFactory.decodeFile(picturePath);
		image = ratio(image);
		return image;
	}

	public static Bitmap ratio(Bitmap image) throws IOException {
		// 计算缩放比例
		int w = image.getWidth();
		int h = image.getHeight();
		// 计算缩放比例
		float reSize = (float) 1.0;
		if (w > h && w > W) {//如果宽度大的话根据宽度固定大小缩放
			reSize =  W/w;
		} else if (w < h && h > H) {//如果高度高的话根据宽度固定大小缩放
			reSize = H/h;
		}

		if(reSize < (float)1.0){
			Matrix matrix = new Matrix();
			matrix.postScale(reSize, reSize);
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
		}
		return image;
	}

	public static void writeMessage(File file,String text,Integer type) throws Exception{
		text = PropertiesUtil.PROPERTIES.get("diary_text_title")+"\n"+text;
		String key = MD5Util.getMd532( text );

		BufferedWriter bufferedWriter = null;
		bufferedWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file, true ),"UTF-8"));
		bufferedWriter.newLine();
		bufferedWriter.write( MESSAGE_LINE );
		bufferedWriter.newLine();
		if(1 == type){
			bufferedWriter.write(key +"TRUE");
		}else if(2 == type){
			bufferedWriter.write(key +"TRUE1");
		}else{
			bufferedWriter.write(key);
		}
		
		bufferedWriter.newLine();
		bufferedWriter.write( MESSAGE_LINE );
		String newkey = salt.toUpperCase()+key;
		for(String msg : text.split( "\n" )){
			bufferedWriter.newLine();
			bufferedWriter.write(DESUtil.encrypt( msg, newkey.substring( 0, 24 ).getBytes("UTF-8"),"base64" ) );
		}
		bufferedWriter.close();
		file.setReadOnly();
	}
	
	public static Map<String,String> getMessage(String pathName){
		 Map<String,String> returnMap = new HashMap<String,String>();
		String returnMessage = "";
		String key = "";
		RandomAccessFile rf = null;
		int flag = 0;
		List<String> messages = new ArrayList<String>();
		try {
			rf = new RandomAccessFile( pathName, "r" );
			long fileLength = rf.length();
			long start = rf.getFilePointer();// 返回此文件中的当前偏移量
			long readIndex = start + fileLength - 1;
			String line;
			rf.seek( readIndex );// 设置偏移量为文件末尾
			int c = -1;
			while( readIndex > start ) {
				c = rf.read();
				if( c == '\n' || c == '\r' ) {
					line = rf.readLine();
					if( MESSAGE_LINE.equals( line ) ) {
						flag ++;
					}
					if( line != null ) {
					   if(flag == 1){
						   flag++;
					   }else if(flag == 2){
						   key = line;
						   returnMap.put( "key", key );
						   break;
					   }else{
						   messages.add( line );
					   }
					} else {
						System.out.println( line );
					}
					readIndex--;
				}
				readIndex--;
				rf.seek( readIndex );
				if( readIndex == 0 ) {// 当文件指针退至文件开始处，输出第一行
					System.out.println( rf.readLine() );
				}
			}
			key = key.replaceAll( "\n", "" );
			key = salt.toUpperCase()+key;
			for(String msg :messages){
				msg =msg.replaceAll( "\n", "" );
				String decrypt = new String(DESUtil.decrypt(msg, key.substring(0, 24).getBytes("UTF-8"),"base64"),"UTF-8");
				if(decrypt.startsWith(" ")){
					decrypt = "\t\t\t\t\t"+decrypt.trim();
				}
				returnMessage = decrypt+"\n"+returnMessage;
			}
			returnMap.put( "message", returnMessage );
		} catch( Exception e ) {
			e.printStackTrace();
		}finally {
			try {
				if( rf != null ) {
					rf.close();
				}
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		return returnMap;
	}
	
	public static void writeSplitMessage(String splitText, File desFile,String text) throws Exception{
		text = "2050年的自己：\n"+text;
		String key = MD5Util.getMd532( text );
		BufferedWriter bufferedWriter = null;
		bufferedWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( desFile,true ) ) );
		bufferedWriter.newLine();
		bufferedWriter.write( PHOTO_LINE );
		bufferedWriter.newLine();
		String newKey = salt.toUpperCase()+key.substring(8+salt.length());
		bufferedWriter.write( DESUtil.encrypt( splitText, newKey.getBytes("UTF-8") ,"base64") );
		bufferedWriter.newLine();
		bufferedWriter.write( PHOTO_LINE );
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public static void writePhotoMessage(File tempFile,File desFile,String text) throws Exception{
		text = "2050年的自己：\n"+text;
		String key = MD5Util.getMd532( text );
		FileInputStream in = new FileInputStream( tempFile);
		BufferedWriter bufferedWriter = null;
		bufferedWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( desFile,true ) ) );
		int c;
		byte buffer[] = new byte[1024*5];
		bufferedWriter.newLine();
		bufferedWriter.write( PHOTO_LINE );
		String newKey = salt.toUpperCase()+key.substring(8+salt.length());
		while( ( c = in.read( buffer ) ) != -1 ) {
			bufferedWriter.newLine();
			bufferedWriter.write( DESUtil.encrypt( buffer, newKey.getBytes("UTF-8"),"base64" ) );
		}
		bufferedWriter.newLine();
		bufferedWriter.write( PHOTO_LINE );
		in.close();
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public static File getPhotoMessage(String readPath,String key) throws Exception{
		if(key == null || "".equals( key )){
			return null;
		}
		key = salt.toUpperCase()+key.substring(8+salt.length(),32);
		BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(readPath) ) );
		File file = new File(MainActivity.rootDir, MainActivity.FILE_PATH + "temp");
		if( !file.exists() ) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream( file );
		String line = null;
		boolean flag = false;
		while( ( line = reader.readLine() ) != null ) {
			if(flag && line.equals( PHOTO_LINE)){
				break;
			}
			if(flag){
				out.write( DESUtil.decrypt( line, key.getBytes("UTF-8"),"base64" ) );
			}
			if(!flag && line.equals( PHOTO_LINE )){
				flag =true;
			}
		}
		reader.close();
		out.flush();
		out.close();
		return file;
	}

	
}
