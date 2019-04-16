package com.ibigsea.rpc.invoke;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.ibigsea.rpc.config.ConsumerConfig;
import com.ibigsea.rpc.exception.RpcException;

/**
 * http请求和响应处理
 * 
 * @author jiang
 *
 */
public class HttpInvoke {

	private static final HttpClient httpClient = getHttpClient();

	/**
	 * 单例
	 */
	private static HttpInvoke httpInvoke;

	private HttpInvoke() {

	}

	public static synchronized HttpInvoke getInstance() {
		if (httpInvoke == null) {
			httpInvoke = new HttpInvoke();
		}
		return httpInvoke;
	}

	/**
	 * 发送请求
	 * 
	 * @param request
	 *            服务消费者将 (类信息、方法、参数)封装成请求报文,序列化后的字符串
	 * @param consumerConfig
	 *            服务消费者请求的地址
	 * @return 请求结果
	 * @throws RpcException
	 */
	public String request(String request, String hostUrl) throws RpcException {
		HttpPost post = new HttpPost("http://"+hostUrl+"/invoke");
		// 使用长连接
		post.setHeader("Connection", "Keep-Alive");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", request));

		try {
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
			throw new RpcException(request);
		} catch (Exception e) {
			throw new RpcException("http调用异常", e, request);
		}
	}

	/**
	 * 响应结果 服务提供者根据服务消费者的请求报文执行后返回结果信息
	 * 
	 * @param response
	 * @param outputStream
	 * @throws RpcException
	 */
	public void response(String response, OutputStream outputStream) throws RpcException {
		try {
			outputStream.write(response.getBytes("UTF-8"));
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpClient getHttpClient() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// 连接池最大生成连接数200
		cm.setMaxTotal(200);
		// 默认设置route最大连接数为20
		cm.setDefaultMaxPerRoute(20);
		// 指定专门的route，设置最大连接数为80
		HttpHost localhost = new HttpHost("localhost", 8080);
		cm.setMaxPerRoute(new HttpRoute(localhost), 50);
		// 创建httpClient
		return HttpClients.custom().setConnectionManager(cm).build();
	}

}
