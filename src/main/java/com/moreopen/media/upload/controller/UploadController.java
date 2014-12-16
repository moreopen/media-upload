package com.moreopen.media.upload.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.moreopen.media.upload.exception.FileDownloadException;
import com.moreopen.media.upload.request.MultipartAwaredUploadRequest;
import com.moreopen.media.upload.response.BaseResponse;
import com.moreopen.media.upload.service.FileUploadService;
import com.moreopen.media.upload.utils.Constants;
import com.moreopen.media.upload.utils.MimeTypeUtils;

@Controller
public class UploadController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
//	@Resource(name="DefaultFileUploadService")
	@Resource(name="MediaIdAwaredFileUploadService")
	private FileUploadService fileUploadService;
	
	private boolean acceptRange;
	
	@RequestMapping("/upload")
	public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartHttpServletRequest mrequest = (MultipartHttpServletRequest) request;
		String user = mrequest.getParameter("user");
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("user [%s] do upload", user));
		}
		MultipartFile multipartFile = mrequest.getFile("media");
		if (multipartFile == null) {
			logger.error("upload file can't be null");
			response.getWriter().write(new BaseResponse(BaseResponse.INVALID_INPUT, "upload file can't be null").toJson());
			return;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("file info, name [%s], originalFileName [%s], content type [%s], size [%s]", 
						multipartFile.getName(), 
						multipartFile.getOriginalFilename(), 
						multipartFile.getContentType(), 
						multipartFile.getSize())
				);
			}
		}
		BaseResponse result = fileUploadService.save(new MultipartAwaredUploadRequest(user, multipartFile));
		response.getWriter().write(result.toJson());
	}
	
	@RequestMapping("/get")
	public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mid = request.getParameter("mid");
		if (StringUtils.isBlank(mid)) {
			throw new FileDownloadException("mid is required");
		}
		File file = null;
		try {
			file = fileUploadService.getFile(mid);
		} catch (Exception e) {
			throw new FileDownloadException(String.format("get file failed by mid [%s]", mid), e);
		}
		output(request, response, file, mid);
	}

	private void output(HttpServletRequest request, HttpServletResponse response, File file, String mid) throws FileDownloadException, IOException {
		if (file == null) {
			throw new FileDownloadException(String.format("can't find file by mid [%s]", mid));
		} else {
			response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
			response.setCharacterEncoding("UTF-8");
			setContentType(response, file);
			if (!acceptRange) {
				outputAll(response, file, mid);
			} else {
				response.setHeader("Accept-Ranges", "bytes");
				int[] ranges = com.moreopen.media.upload.utils.FileUtils.parseRange(request.getHeader("Range"), (int) file.length());
				if (ranges == null) { 
					outputAll(response, file, mid);
				} else {
					outputByRange(response, file, mid, ranges);
				}
			}
			response.getOutputStream().flush();
		}
	}

	private void setContentType(HttpServletResponse response, File file) {
		String contentType = Constants.DEFAULT_CONTENT_TYPE;
		try {
			int pos = file.getName().lastIndexOf(".");
			if (pos != -1) {
				String suffix = file.getName().substring(pos + 1);
				String mimeType = MimeTypeUtils.getMimeType(suffix);
				if (mimeType != null) {
					contentType = mimeType;
				}
			}
		} catch (Exception e) {
		}
		response.setContentType(contentType);
	}

	private void outputByRange(HttpServletResponse response, File file, String mid, int[] ranges) throws FileDownloadException {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			int readLength = ranges[1] - ranges[0] + 1;
			int bufferSize = 1024;
			// 起始位置
			int start = ranges[0];
			// 分段数量
			int spanSize = (int) readLength / bufferSize;
			// 最后一段的大小
			int left = (int) readLength % bufferSize;
			byte[] buffer = new byte[bufferSize];
			for (int i = 0; i < spanSize; i++) {
				raf.seek(start);
				int len = raf.read(buffer, 0, bufferSize);
				if (len != bufferSize) {
					logger.error(String.format("Error: readed bytes length [%s] is not equal the buffer size [%s] ????"), len, bufferSize);
					throw new FileDownloadException("read RAF error");
				}
				response.getOutputStream().write(buffer, 0, len);
				start = start + len;
			}
			if (left > 0) {
				byte[] bytes = new byte[left];
				raf.seek(start);
				int len = raf.read(bytes, 0, left);
				response.getOutputStream().write(bytes, 0, len);
			}
			response.setContentLength(readLength);
			String contentRange = ranges[0] + "-" + ranges[1] + "/" + file.length();
			response.setHeader("Content-Range", contentRange);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("range download file [%s] success, range [%s]", mid, contentRange));
			}
		} catch (Exception e) {
			throw new FileDownloadException(String.format("range[%s, %s] download file [%s] failed", ranges[0], ranges[1], mid), e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch(IOException ioe) {
					//quietly
				}
			}
		}
	}

	private void outputAll(HttpServletResponse response, File file, String mid) throws FileDownloadException {
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			long length = IOUtils.copy(input, response.getOutputStream());
			response.setContentLength((int) length);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("download file [%s] finished, size [%s]", mid, length));
			}
		} catch (Exception e) {
			throw new FileDownloadException("write to response failed", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

}
