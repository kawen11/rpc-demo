package com.ibigsea.service;

import org.springframework.stereotype.Service;

import com.ibigsea.facade.WordInterface;
@Service("wordInterface")
public class WordService implements WordInterface {

	public String PrintHelloWorld(String key) {
		return key;
	}

}
