package com.moreopen.media.upload.utils;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

public class FileUtils {
	
	/**
	 * @deprecated plz use {@see File#mkdirs()}
	 * 创建多重目录
	 * @param folders:多级目录 ex: a/b/c/d or /a/b/c/d ...
	 */
	public static void makeDir(final String folders) {
        StringTokenizer st = new StringTokenizer(folders, File.separator);
        StringBuilder builder = new StringBuilder();
        if (folders.startsWith(File.separator)) {
        	builder.append(File.separator);
        }
        while (st.hasMoreTokens()) {
            builder.append(st.nextToken());
            File file = new File(builder.toString());
            if (!file.exists()) {
                file.mkdir();
            }
            builder.append(File.separator);
        }
 
    }
	
	/**
	 *  根据 request header 中 range 的值得到的真实 range 数组
	 *  ex1.表示头500个字节：Range: bytes=0-499
     *	ex2.表示第二个500字节：Range: bytes=500-999
     *  ex3.表示最后500个字节：Range: bytes=-500
     *  ex4.表示500字节以后的范围：Range: bytes=500-
	 */
	public static int[] parseRange(String rangeHeaderValue, int totalSize) {
		if (StringUtils.isBlank(rangeHeaderValue)) {
			return null;
		}
		int pos = rangeHeaderValue.lastIndexOf("=");
		if (pos == -1) {
			return null;
		}
		String ranges = rangeHeaderValue.substring(pos + 1);
		String[] splitted = ranges.split("-");
		int[] result = new int[2];
		if (splitted.length == 2) {
			//as ex3
			if (StringUtils.isEmpty(splitted[0])) {
				result[0] = totalSize - Integer.parseInt(splitted[1]);
				result[1] = totalSize - 1;
			} else { // same as ex1, ex2
				result[0] = Integer.parseInt(splitted[0]);
				result[1] = Integer.parseInt(splitted[1]);
			}
			return result;
		} else if (splitted.length == 1) {
			//as ex4
			result[0] = Integer.parseInt(splitted[0]);
			result[1] = totalSize - 1;
			return result;
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(RandomStringUtils.randomAlphabetic(6));
		System.out.println(RandomStringUtils.randomAlphanumeric(10));
		System.out.println(RandomStringUtils.randomNumeric(6));
	}

}
