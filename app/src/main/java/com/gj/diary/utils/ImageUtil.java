package com.gj.diary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageUtil {
	
	private final static String MESSAGE_LINE= "=======================================================================";
	
	public final static String PHOTO_LINE= "***********************************************************************";

	public static final float W = 2049;//缩放后宽
	public static final float H = 1152;//缩放后高
	
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
		bitmap.compress(Bitmap.CompressFormat.JPEG, 75, os);
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
		ratio(image);
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

		if(reSize != (float)1.0){
			Matrix matrix = new Matrix();
			matrix.postScale(reSize, reSize);
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
		}
		return image;
	}

	public static void writeMessage(File file,String text,Integer type) throws Exception{
		text = "2050年的自己：\n"+text;
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
		for(String msg : text.split( "\n" )){
			bufferedWriter.newLine();
			bufferedWriter.write(DESUtil.Encrypt( msg, key.substring( 0, 24 ).getBytes("UTF-8") ) );
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
			for(String msg :messages){
				msg =msg.replaceAll( "\n", "" );
				key = key.replaceAll( "\n", "" );
				returnMessage = DESUtil.Decrypt( msg, key.substring( 0, 24 ).getBytes("UTF-8") )+"\n"+returnMessage;
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
		bufferedWriter.write( DESUtil.Encrypt( splitText, key.substring(8).getBytes("UTF-8") ) );
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
		while( ( c = in.read( buffer ) ) != -1 ) {
			bufferedWriter.newLine();
			bufferedWriter.write( DESUtil.Encrypt( buffer, key.substring(8).getBytes("UTF-8") ) );
		}
		bufferedWriter.newLine();
		bufferedWriter.write( PHOTO_LINE );
		in.close();
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
/*	public static File getPhotoMessage(String readPath,String key) throws Exception{
		if(key == null || "".equals( key )){
			return null;
		}
		key = key.substring( 8,32 );
		BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(readPath) ) );
		File file = new File(DiaryUtil.outerConfigPath+"tempPhoto" );
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
				out.write( DESUtil.Decrypt1( line, key.getBytes("UTF-8") ) );
			}
			if(!flag && line.equals( PHOTO_LINE )){
				flag =true;
			}
		}
		reader.close();
		out.flush();
		out.close();
		return file;
	}*/
	
	public static void main( String[] args ) throws Exception {
		String returnMessage="";
		String msg = "CB7B46347705F063295462350327B3754429584652706EF7593CAB37406CAD6DF9D42179949527F036EA29B62F088F313B223FAB3F9C4A3A42D561F3BACB633D6AAA8A84681C5936082777E1FB05C22EB1CFF5BEFF0AE5BF9ABDF01A1F30B3C03481C1F9A702AB7E7E0D7EF318C99B744A11B9BA9FC28BD1163E1FC4054B6B542C82EE3AFF6C5E708B2528E4C78BEE657F2B4537FF42CB9D791C0AEE348888914FB33FC2DB60A39FF43D03910723A2E58D5B4716F0D191512914D8EFA29FE4B384D0E2D5B6F1C34AA7C9F8618C41C11809F7F614ECF6F49C0407FB6207CA4C64";
        String key ="13E0787ED11A1E4740F0FCA11AE27B63TRUE";
        returnMessage = DESUtil.Decrypt( msg, key.substring( 0, 24 ).getBytes("UTF-8") )+"\n"+returnMessage;
        System.out.println( returnMessage );

	}
	
	
}
