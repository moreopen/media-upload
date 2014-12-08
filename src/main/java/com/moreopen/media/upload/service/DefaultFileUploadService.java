package com.moreopen.media.upload.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.moreopen.media.upload.response.BaseResponse;
import com.moreopen.media.upload.response.FileUploadResponse;
import com.moreopen.media.upload.utils.Constants;

@Component("DefaultFileUploadService")
public class DefaultFileUploadService extends AbstractFileUploadService implements InitializingBean {
	
	@Override
	protected BaseResponse buildResponse(String fileName) {
		return new FileUploadResponse(generateAbsoluetUrl(fileName));
	}
	
	private String generateAbsoluetUrl(String savedFullFileName) {
		String path = buildRelativeUrl(savedFullFileName);
		return Constants.DOWNLOAD_URL_PREFIX + path;
	}

	private String buildRelativeUrl(String savedFullFileName) {
		return savedFullFileName.replaceFirst(Constants.UPLOAD_TOP_DIR, StringUtils.EMPTY);
	}

	@Override
	public File getFile(String mid) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("============== init DefaultFileUploadService");
		}
	}

}
