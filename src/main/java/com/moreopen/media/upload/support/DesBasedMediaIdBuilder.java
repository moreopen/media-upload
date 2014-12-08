package com.moreopen.media.upload.support;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.snda.youni.commons.security.encrypt.Base64Encoder;
import com.snda.youni.commons.security.encrypt.DesEncrypter;
import com.snda.youni.commons.security.encrypt.EncrypterException;

@Component("DesBasedMediaIdBuilder")
public class DesBasedMediaIdBuilder implements MediaIdBuilder<String>{
	
	private static final String key = "re*34&^)";
	
	private DesEncrypter desEncrypter;
	
	@PostConstruct
	public void init() throws EncrypterException {
		desEncrypter = new DesEncrypter(key, new Base64Encoder());
//		desEncrypter = new DesEncrypter(key, new HexByteEncoder());
	}

	/**
	 * build mid by full file name
	 * @throws EncrypterException 
	 */
	@Override
	public String build(String file) throws EncrypterException {
		return desEncrypter.encrypt(file);
	}

	/**
	 * resolve full file name by mid
	 * @throws EncrypterException 
	 */
	@Override
	public String resolve(String mid) throws EncrypterException {
		return desEncrypter.decrypt(mid);
	}
	
	

}
