package com.moreopen.media.upload.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.moreopen.media.upload.exception.FileDownloadException;
import com.moreopen.media.upload.exception.FileUploadException;
import com.moreopen.media.upload.response.BaseResponse;

@Component
public class ExceptionInterceptor implements HandlerExceptionResolver {

	protected final Logger logger = Logger.getLogger(ExceptionInterceptor.class);

	@Override
	public ModelAndView resolveException(
			HttpServletRequest request,
			HttpServletResponse response, 
			Object handler, 
			Exception ex) {
		if (logger.isInfoEnabled()) {
			logger.info("ExceptionInterceptor caught exception:", ex);
		}
		BaseResponse _response = null;
		if (ex instanceof FileUploadException) {
			_response = new BaseResponse(BaseResponse.UPLOAD_FAILED, ex.getMessage());
		} else if (ex instanceof FileDownloadException) {
			response.setHeader("DL-CODE", BaseResponse.DOWNLOAD_FAILED);
			_response = new BaseResponse(BaseResponse.DOWNLOAD_FAILED, ex.getMessage());
		} else {
			_response = BaseResponse.SERVER_ERROR;
		}
		try {
			response.getWriter().write(_response.toJson());
		} catch (Exception e) {
			logger.error("error", e);
		}
		//XXX must return a new ModelAndView, if return null the exception stack info will be written to response 
		return new ModelAndView();
	}
}
