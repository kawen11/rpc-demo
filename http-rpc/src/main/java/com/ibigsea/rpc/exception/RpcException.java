package com.ibigsea.rpc.exception;

/**
 * 异常
 * 
 * @author jiang
 *
 */
public class RpcException extends Throwable {
	private Object data;

	public RpcException(String message, Throwable cause, Object data) {
		super(message, cause);
		this.data = data;
	}

	public RpcException(Object data) {
		super();
		this.data = data;
	}

	public Object getData() {
		return data;
	}
}
