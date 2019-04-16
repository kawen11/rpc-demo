package com.ibigsea.rpc.serizlize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 序列化
 * 
 * @author jiang
 *
 */
public class JsonFormatter {

	/**
	 * 将请求序列化成字符串
	 * 
	 * @param clazz
	 * @param method
	 * @param param
	 * @return
	 */
	public static String reqFormatter(Class clazz, String method, Object param) {
		Request request = new Request(clazz, method, param);
		return JSON.toJSONString(request, SerializerFeature.WriteClassName);
	}

	/**
	 * 将响应序列化成字符串
	 * 
	 * @param param
	 * @return
	 */
	public static String resbFormatter(Object param) {
		return JSON.toJSONString(param, SerializerFeature.WriteClassName);
	}

}
