package com.epopcon.advatar.common.util;

public interface Encrypter {
	
	/**
	 * get encrypted data from plain text
	 * @param plain
	 * @return
	 * @throws Exception
	 */
	 public String encrypt(String plain) throws Exception;

	 /**
	  * get plain text from encrypted data
	  * @param encrypt
	  * @return
	  * @throws Exception
	  */
	 public String decrypt(String encrypt) throws Exception;

}
