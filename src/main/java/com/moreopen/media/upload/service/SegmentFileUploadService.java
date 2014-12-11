package com.moreopen.media.upload.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.moreopen.media.upload.exception.FileUploadException;
import com.moreopen.media.upload.request.MultipartAwaredUploadRequest;
import com.moreopen.media.upload.request.SegmentUploadRequest;
import com.moreopen.media.upload.response.BaseResponse;
import com.moreopen.media.upload.utils.Constants;

@Component
//若客户端并发的分包上传，需要加上同步保护(或者限定客户端只能分包顺序上传直至完成)
public class SegmentFileUploadService extends DefaultFileUploadService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public BaseResponse save(MultipartAwaredUploadRequest multipartAwaredUploadRequest) throws FileUploadException, IOException {
		SegmentUploadRequest segmentUploadRequest = (SegmentUploadRequest) multipartAwaredUploadRequest; 
		File tmpDir = new File(Constants.UPLOAD_TEMP_DIR + "/" + new SimpleDateFormat("yyyyMM").format(new Date()));
		tmpDir.mkdirs();
		String tmpFileName = tmpDir.getAbsolutePath() + "/" + segmentUploadRequest.getUser() + "-" + segmentUploadRequest.getMd5() + "-" + segmentUploadRequest.getFileName();
		File tmpFile = new File(tmpFileName);
		if (segmentUploadRequest.getSequence() == 0) {
			logger.warn(String.format("this is the first pkg for file [%s], create a new temp file [%s]", segmentUploadRequest.getFileName(), tmpFileName));
			if (tmpFile.exists() && tmpFile.delete()) {
				logger.warn(String.format("deleted the existed tmp file [%s]", tmpFileName));
			}
			if (!tmpFile.createNewFile()) {
				throw new FileUploadException(String.format("create tmp file [%s] failed", tmpFileName));
			}
		} else if (!tmpFile.exists()) {
			logger.warn(String.format("segment upload failed, the file [%s] must be exist for sequence [%s]", tmpFileName, segmentUploadRequest.getSequence()));
			return new BaseResponse(BaseResponse.UPLOAD_FAILED, "upload failed");
		}
		
		RandomAccessFile raf = null;
		try {
			byte[] bytes = segmentUploadRequest.getMultipartFile().getBytes();
			raf = new RandomAccessFile(tmpFile, "rw");
			raf.seek(raf.length());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("======== seek to position [%s], write bytes [%s] to file [%s]", raf.length(), bytes.length, segmentUploadRequest.getFileName()));
			}
			raf.write(bytes);
			if (raf.length() == segmentUploadRequest.getTotalSize()) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format(
							"segment upload file completed, user [%s], file [%s], total size [%s]", 
							segmentUploadRequest.getUser(), 
							segmentUploadRequest.getFileName(), 
							segmentUploadRequest.getTotalSize())
					);
				}
				File destFile = new File(generateSavedPath() + "/" 
									+ (RandomStringUtils.randomAlphanumeric(10) + "" 
										+ System.currentTimeMillis() + "-" 
											+ segmentUploadRequest.getFileName())
								);
				FileUtils.copyFile(tmpFile, destFile);
				tmpFile.delete();
				return buildResponse(destFile.getAbsolutePath());
			} else if (raf.length() < segmentUploadRequest.getTotalSize()) {
				//error if this is the last segment but length still < totalSize
				if (segmentUploadRequest.getSequence() == segmentUploadRequest.getPkgNum() - 1) {
					//error
					logger.error(String.format("segment upload file failed, uploaded finished but size is smaller than expected total size [%s]", segmentUploadRequest.getTotalSize()));
					tmpFile.delete();
					return new BaseResponse(BaseResponse.UPLOAD_FAILED, "uploaded size is smaller than the expected total size : " + segmentUploadRequest.getTotalSize());
				} else {
					if (logger.isInfoEnabled()) {
						logger.info(String.format(
								"segment upload file not completed, user [%s], file [%s], total size [%s], seq [%s], pkgNum [%s]", 
								segmentUploadRequest.getUser(), 
								segmentUploadRequest.getFileName(), 
								segmentUploadRequest.getTotalSize(),
								segmentUploadRequest.getSequence(),
								segmentUploadRequest.getPkgNum())
						);
					} 
					return new BaseResponse();
				}
			} else {
				//error
				logger.error(String.format("segment upload file failed, uploaded size is larger than expected total size [%s]", segmentUploadRequest.getTotalSize()));
				tmpFile.delete();
				return new BaseResponse(BaseResponse.UPLOAD_FAILED, "uploaded size exceed the expected total size : " + segmentUploadRequest.getTotalSize());
			}
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

}
