package com.moreopen.media.upload.service;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.moreopen.media.upload.exception.FileUploadException;
import com.moreopen.media.upload.request.MultipartAwaredUploadRequest;
import com.moreopen.media.upload.response.BaseResponse;
import com.moreopen.media.upload.utils.Constants;

public abstract class AbstractFileUploadService implements FileUploadService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected abstract BaseResponse buildResponse(String fileName) throws Exception;

	@Override
	public BaseResponse save(MultipartAwaredUploadRequest multipartAwaredUploadRequest) throws FileUploadException, IOException {
		MultipartFile multipartFile = multipartAwaredUploadRequest.getMultipartFile();
		//XXX TODO md5 check, if has exist the file with the same md5 value, just return the url, not do save 
		String path = generateSavedPath();
		File fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
//			com.moreopen.media.upload.utils.FileUtils.makeDir(path);
			logger.warn(String.format("dir [%s] is not exist, created", path));
		}
		String savedFullFileName = path + "/" + generateSavedFileName(multipartFile);
		File dest = new File(savedFullFileName);
		if (!dest.exists()) {
			try {
				multipartFile.transferTo(dest);
			} catch (Exception e) {
				logger.error(String.format("save file [%s] exception", savedFullFileName), e);
				throw new FileUploadException("save file exception", e);
			}
		} else {
			//XXX maybe should retry several times
			logger.warn(String.format("file [%s] already exists, save failed", savedFullFileName));
			throw new FileUploadException(String.format("file with the same name [%s] exist", savedFullFileName));
		}
		try {
			BaseResponse response = buildResponse(savedFullFileName);
			return response;
		} catch (Exception e) {
			//delete saved file
			if(dest.delete()) {
				logger.warn(String.format("build response failed, so deleted file [%s]", savedFullFileName));
			} else {
				logger.warn(String.format("build response failed, but delete file [%s] failed too", savedFullFileName));
			}
			throw new FileUploadException("build upload response failed", e);
		}
	}

	//XXX TODO should check file name is exist in the path
	private String generateSavedFileName(MultipartFile media) {
		String originalFileName = media.getOriginalFilename();
		int pos = originalFileName.lastIndexOf(".");
		if (pos == -1) {
			throw new IllegalArgumentException("invalid file type");
		}
		String fileType = originalFileName.substring(pos);
		String fileName = RandomStringUtils.randomAlphanumeric(10) + "" + System.currentTimeMillis() + fileType;
		return fileName;
	}

	protected String generateSavedPath() {
		Calendar calendar = Calendar.getInstance();
		return Constants.UPLOAD_TOP_DIR + "/" + calendar.get(Calendar.YEAR) + "/"
				+ (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.DAY_OF_MONTH);  
	}

}
