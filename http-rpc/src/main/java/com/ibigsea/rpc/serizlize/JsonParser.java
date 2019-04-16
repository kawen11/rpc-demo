package com.ibigsea.rpc.serizlize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 反序列化
 * 
 * @author jiang
 *
 */
public class JsonParser {
	/**
	 * 反序列化请求 将请求反序列化成一个请求报文
	 * 
	 * @param param
	 * @return
	 */
	public static Request reqParse(String param) {
		return JSON.parseObject(param, Request.class);
	}

	/**
	 * 反序列化响应 将响应反序列化成一个响应报文
	 * 
	 * @param result
	 * @return
	 */
	public static <T> T resbParse(String result) {
		return (T) JSON.parse(result);
	}

}
