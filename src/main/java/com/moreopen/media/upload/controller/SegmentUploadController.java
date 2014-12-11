package com.moreopen.media.upload.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.moreopen.media.upload.request.SegmentUploadRequest;
import com.moreopen.media.upload.response.BaseResponse;
import com.moreopen.media.upload.service.SegmentFileUploadService;

@Controller
public class SegmentUploadController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private SegmentFileUploadService segmentFileUploadService;
	
	@RequestMapping("/upload/segment")
	public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartHttpServletRequest mrequest = (MultipartHttpServletRequest) request;
		SegmentUploadRequest segmentUploadRequest = SegmentUploadRequest.newInstance(mrequest);
		if (!segmentUploadRequest.validate()) {
			logger.warn("segment request is invalid");
			response.getWriter().write(new BaseResponse(BaseResponse.INVALID_INPUT, "segment request is invalid").toJson());
			return;
		}
		BaseResponse segmentUploadResponse = segmentFileUploadService.save(segmentUploadRequest);
		response.getWriter().write(segmentUploadResponse.toJson());
	}
	

}
