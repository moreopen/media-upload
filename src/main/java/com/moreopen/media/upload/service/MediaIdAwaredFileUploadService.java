package com.moreopen.media.upload.service;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.moreopen.media.upload.response.BaseResponse;
import com.moreopen.media.upload.response.MediaIdAwaredResponse;
import com.moreopen.media.upload.support.MediaIdBuilder;
import com.moreopen.media.upload.utils.Constants;

@Component("MediaIdAwaredFileUploadService")
public class MediaIdAwaredFileUploadService extends AbstractFileUploadService {
	
	@Resource(name="DesBasedMediaIdBuilder")
	private MediaIdBuilder<String> mediaIdBuilder;

	@Override
	protected BaseResponse buildResponse(String fileName) throws Exception {
		String _fileName = fileName.replaceFirst(Constants.UPLOAD_TOP_DIR, StringUtils.EMPTY);
		String mediaId = mediaIdBuilder.build(_fileName);
		return new MediaIdAwaredResponse(mediaId);
	}

	@Override
	public File getFile(String mid) throws Exception {
		String fileName = mediaIdBuilder.resolve(mid);
		String fullName = Constants.UPLOAD_TOP_DIR + fileName;
		File file = new File(fullName);
		if (file.exists() && file.isFile()) {
			return file;
		} else {
			return null;
		}
	}

}
