package com.moreopen.media.upload.support;

public interface MediaIdBuilder<T> {
	
	String build(T t) throws Exception;
	
	T resolve(String mid) throws Exception;

}
