package com.moreopen.media.upload.utils;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class MimeTypeUtils {
	
	private static Properties mimes = new Properties();
	
	static {
		try {
			mimes = PropertiesLoaderUtils.loadAllProperties("mime.properties");
		} catch (IOException e) {
			throw new RuntimeException("============== load mime.properties failed");
		}
	}
	
	public static String getMimeType(String ext) {
		return mimes.getProperty(ext);
	}

}
