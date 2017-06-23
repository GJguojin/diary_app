package com.gj.diary.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util
{
	/**
	 * md5 16位加密
	 * 
	 * @param str
	 * @return 16位加密字符
	 */
	public static String getMD516( String str )
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance( "MD5" );

			messageDigest.reset();

			messageDigest.update( str.getBytes( "UTF-8" ) );
		}
		catch( NoSuchAlgorithmException e )
		{
			System.out.println( "NoSuchAlgorithmException caught!" );
			System.exit( -1 );
		}
		catch( UnsupportedEncodingException e )
		{
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for( int i = 0; i < byteArray.length; i++ )
		{
			if( Integer.toHexString( 0xFF & byteArray[i] ).length() == 1 )
				md5StrBuff.append( "0" ).append( Integer.toHexString( 0xFF & byteArray[i] ) );
			else
				md5StrBuff.append( Integer.toHexString( 0xFF & byteArray[i] ) );
		}
		// 16位加密，从第9位到25位
		return md5StrBuff.substring( 8, 24 ).toString().toUpperCase();
	}

	/**
	 * md5 32位加密
	 * 
	 * @param sourceStr
	 * @return 32位加密字符
	 */
	public static String getMd532( String sourceStr )
	{
		String result = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			md.update( sourceStr.getBytes() );
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer( "" );
			for( int offset = 0; offset < b.length; offset++ )
			{
				i = b[offset];
				if( i < 0 )
					i += 256;
				if( i < 16 )
					buf.append( "0" );
				buf.append( Integer.toHexString( i ) );
			}
			result = buf.toString();
		}
		catch( NoSuchAlgorithmException e )
		{
			System.out.println( "NoSuchAlgorithmException caught!" );
		}
		return result.toUpperCase();
	}

	public static String getMd532l( String sourceStr )
	{
		String result = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			md.update( sourceStr.getBytes() );
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer( "" );
			for( int offset = 0; offset < b.length; offset++ )
			{
				i = b[offset];
				if( i < 0 )
					i += 256;
				if( i < 16 )
					buf.append( "0" );
				buf.append( Integer.toHexString( i ) );
			}
			result = buf.toString();
		}
		catch( NoSuchAlgorithmException e )
		{
			System.out.println( "NoSuchAlgorithmException caught!" );
		}
		return result.toLowerCase();
	}

}
