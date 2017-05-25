package netty.server;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装类似HttpRequest 请求,可以获取各种参数
 * @author chenxiangxu 2015年5月22日上午9:28:02
 */
public class RequestDecoder {
	private HttpPostRequestDecoder post;
	private QueryStringDecoder get;
	private HttpMethod method;
	private FullHttpRequest request;
	private boolean isPost;
	
	public String url(){
		return request.uri();
	}

	public RequestDecoder(FullHttpRequest request) {
		method=request.method();
		this.request=request;
		isPost=(method==HttpMethod.POST);
		if(isPost){
			post = new HttpPostRequestDecoder(this.request);
		}else{
			get = new QueryStringDecoder(request.uri()); 
		}
	}
	
	public String getBodyHttpData(String key){
		if(isPost){
			InterfaceHttpData data = post.getBodyHttpData(key);
			try {
				if(data!=null){
					return ((Attribute)data).getValue();
				}
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			List<String> arrs = get.parameters().get(key);
			if(arrs.iterator().hasNext())
				return arrs.iterator().next();
			return null;
		}
	}
	
	/**
	 * 获取参数集合
	 */
	public List<String> getBodyHttpDatas(String key){
		if(isPost){
			List<InterfaceHttpData> data = post.getBodyHttpDatas(key);
			try {
				List<String> list = new ArrayList<>();
				for (int i = 0; i < data.size(); i++) {
					list.add(((Attribute)data).getValue());
				}
				return list;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return get.parameters().get(key);
		}
	}
	public List<InterfaceHttpData> getBodyHttpDatas(){
		if(isPost){
			return post.getBodyHttpDatas();
		}else{
			return new ArrayList<>();
		}
	}
	public FileUpload getfile(String key){
		InterfaceHttpData data = post.getBodyHttpData(key);
		if(data!=null&&data.getHttpDataType()==HttpDataType.FileUpload){
			return (FileUpload) data;
		}
		return null;
	}
	
	public HttpPostRequestDecoder postRequestDecoder() {
		return post;
	}
	
	public QueryStringDecoder queryStringDecoder() {
		return get;
	}
	
	public FullHttpRequest fullHttpRequest() {
		return request;
	}
	public HttpMethod method() {
		return method;
	}
	public boolean isPost() {
		return isPost;
	}
}
