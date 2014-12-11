package com.moreopen.media.upload.request;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public class SegmentUploadRequest extends MultipartAwaredUploadRequest {
	
	private int sequence;
	
	private int pkgNum;
	
	private int totalSize;
	
	private String md5;
	
	private String fileName;
	
	public static SegmentUploadRequest newInstance(MultipartHttpServletRequest mrequest) {
		SegmentUploadRequest segmentUploadRequest = new SegmentUploadRequest();
		segmentUploadRequest.setSequence(Integer.parseInt(mrequest.getParameter("sequence")));
		segmentUploadRequest.setPkgNum(Integer.parseInt(mrequest.getParameter("pkgNum")));
		segmentUploadRequest.setTotalSize(Integer.parseInt(mrequest.getParameter("totalSize")));
		segmentUploadRequest.setMd5(mrequest.getParameter("md5"));
		segmentUploadRequest.setFileName(mrequest.getParameter("fileName"));
		segmentUploadRequest.setMultipartFile(mrequest.getFile("file"));
		segmentUploadRequest.setUser(mrequest.getParameter("user"));
		return segmentUploadRequest;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getPkgNum() {
		return pkgNum;
	}

	public void setPkgNum(int pkgNum) {
		this.pkgNum = pkgNum;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	//TODO valid me
	public boolean validate() {
		return true;
	}

}
