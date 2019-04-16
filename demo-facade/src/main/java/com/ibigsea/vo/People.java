package com.ibigsea.vo;

import java.io.Serializable;

/**
 * 
 * @author jiang
 *
 */
public class People implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;

	public People() {
	}

	public People(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
