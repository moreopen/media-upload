package com.moreopen.media.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SegmentUploadTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String url = "http://127.0.0.1:8081/media/upload/segment.htm";

	private File file = new File("/Users/yekai/Documents/photo/xitang.jpg");
	
	private String user = "yk";
	
	private String md5;
	
	private long totalSize;
	
	//size of pkg (byte)
	private int pkgSize = 100 * 1024;
	
	private int pkgNum;
	
	@Before
	public void before() throws FileNotFoundException, IOException {
		org.springframework.util.Assert.isTrue(file.exists());
		totalSize = file.length();
		int num = (int) totalSize / pkgSize;
		pkgNum = totalSize > num * pkgSize ? num + 1 : num;
		md5 = DigestUtils.md5Hex(new FileInputStream(file));
		if (logger.isInfoEnabled()) {
			logger.info(String.format("segment upload file [%s], totalSize [%s], pkgSize [%s], pkgNum [%s], md5 [%s]", file.getName(), totalSize, pkgSize, pkgNum, md5));
		}
	}
	
	@Test
	public void test() {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			httpPost = new HttpPost(url);
			
			for (int i = 0; i < pkgNum; i++) {
				MultipartEntity httpEntity = buildEntity(i);
				httpPost.setEntity(httpEntity);
				HttpResponse response = httpClient.execute(httpPost);
				if (logger.isInfoEnabled()) {
					logger.info("Http response : " + response);
					//must use EntityUtils#consumeContent or toString to release current connection
					logger.info("Http response entyty : " + EntityUtils.toString(response.getEntity(), "UTF-8"));
				}
			}
		} catch (Exception e) {
			logger.error("segment upload file failed", e);
		} finally {
			if (httpPost != null) {
				httpPost.abort();
				//close connection at once
				httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
			}
		}
	}

	private MultipartEntity buildEntity(int sequence) throws IOException {
		RandomAccessFile raf = null;
		try {
			byte[] data = null;
			if (sequence == pkgNum -1) {
				//last pkg
				data = new byte[(int) (totalSize - pkgSize * sequence)];
			} else {
				data = new byte[pkgSize];
			}
			raf = new RandomAccessFile(file, "r");
			raf.seek(sequence * pkgSize);
			raf.read(data);
			MultipartEntity httpEntity = new MultipartEntity();
			ContentBody contentBody = new ByteArrayBody(data, file.getName());
			httpEntity.addPart("file", contentBody);
			httpEntity.addPart("user", new StringBody(user));
			httpEntity.addPart("sequence", new StringBody(sequence + ""));
			httpEntity.addPart("pkgNum", new StringBody(pkgNum + ""));
			httpEntity.addPart("totalSize", new StringBody(totalSize + ""));
			httpEntity.addPart("fileName", new StringBody(file.getName()));
			httpEntity.addPart("md5", new StringBody(md5));
			return httpEntity;
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

}
