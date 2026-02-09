package com.idd.shared.util;

import com.idd.shared.crypto.CryptoService;

public final class CryptoUtil {

	public static String encrypt(String secretKey, String plainText) {
		return new CryptoService(secretKey).encrypt(plainText);
	}
	
	public static String decrypt(String secretKey, String plainText) {
		return new CryptoService(secretKey).decrypt(plainText);
	}
	
//	public static void main(String[] args) {
//		System.out.println(encrypt("tYtPIDwQBTuevzK8NhOXQw==", "los@13579"));
//	}
}
