package com.circle.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Created by fomky on 15-7-13.
 */
public class HttpBack {
	public final static String SAFE_CODE = "UTF-8";
	public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
	public static final int HTTP_CACHE_SECONDS = 60;
	private static final String RESPOSE_CONTEXT = "application/json";
	private static final String TEXT_HTML = "text/html";
	private static Logger logger = LoggerFactory.getLogger(HttpBack.class);

	public static FullHttpResponse response() {
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		return response;
	}

	/**
	 * 返回 json 类型的响应.
	 * @method jsonResponse
	 */
	public static FullHttpResponse jsonResponse(byte[] array) {
		FullHttpResponse response = response();
		response.headers().add(HttpHeaderNames.CONTENT_TYPE, RESPOSE_CONTEXT);
		response.headers().add(HttpHeaderNames.CONTENT_ENCODING, SAFE_CODE);
		response.content().writeBytes(array);
		return response;
	}

	public static FullHttpResponse write(String array) {
		return texthtmlResponse(safeBytes(array));
	}

	public static FullHttpResponse texthtmlResponse(byte[] array) {
		FullHttpResponse response = response();
		response.headers().add(HttpHeaderNames.CONTENT_TYPE, TEXT_HTML);
		response.headers().add(HttpHeaderNames.CONTENT_ENCODING, SAFE_CODE);
		response.content().writeBytes(array);
		return response;
	}

	public static FullHttpResponse jsonRes(byte[] array) {
		FullHttpResponse response = response();
		response.headers().add(HttpHeaderNames.CONTENT_TYPE, RESPOSE_CONTEXT);
		response.headers().add(HttpHeaderNames.CONTENT_ENCODING, SAFE_CODE);
		response.content().writeBytes(array);
		return response;
	}

	private static FullHttpResponse jsonResponse(BackModel model) {
		return jsonResponse(safeBytes(model.buildJson()));
	}


	private static byte[] safeBytes(String str) {
		try {
			return str.getBytes(SAFE_CODE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 未找到相关链接
	 *
	 * @param ctx
	 */
	@Deprecated
	public static FullHttpResponse back_404(ChannelHandlerContext ctx) {
		// 创建返回模型
		FullHttpResponse response = response();
		// 设置 404 没有找到相关链接
		response.setStatus(HttpResponseStatus.NOT_FOUND);
		return  response;
	}
	/**
	 * 未找到相关链接
	 */
	public static FullHttpResponse back_404() {
		// 创建返回模型
		FullHttpResponse response = response();
		// 设置 404 没有找到相关链接
		response.setStatus(HttpResponseStatus.NOT_FOUND);
		return  response;
	}
	@Deprecated
	public static FullHttpResponse back_error(ChannelHandlerContext ctx, String errorCode) {
		// 创建返回模型
		BackModel model = new BackModel();
		model.add(JsonParams.STA, 0);
		model.add(JsonParams.ERR, errorCode);
		return  back_200_JSON(ctx, model);
	}
	public static FullHttpResponse back_error(String errorCode) {
		// 创建返回模型
		BackModel model = new BackModel();
		model.add(JsonParams.STA, 0);
		model.add(JsonParams.ERR, errorCode);
		return  back_200_JSON(model);
	}

	@Deprecated
	public static FullHttpResponse back_200_JSON(ChannelHandlerContext ctx, BackModel model) {
		// 创建返回模型
		Object err = model.getMap().get(JsonParams.ERR);
		if(err==null){
			model.add(JsonParams.STA, 1);
		}else {
			model.add(JsonParams.STA, 0);
		}
		return jsonResponse(model);
	}

	public static FullHttpResponse back_200_JSON(BackModel model) {
		// 创建返回模型
		Object err = model.getMap().get(JsonParams.ERR);
		if(err==null){
			model.add(JsonParams.STA, 1);
		}else {
			model.add(JsonParams.STA, 0);
		}
		return jsonResponse(model);
	}
	@Deprecated
	public static FullHttpResponse back_200(ChannelHandlerContext ctx) {
		BackModel model = new BackModel();
		return back_200_JSON(ctx, model);
	}

	public static FullHttpResponse back_200() {
		BackModel model = new BackModel();
		return back_200_JSON(model);
	}
	@Deprecated
	public static FullHttpResponse back_500(ChannelHandlerContext ctx) {
		// 创建返回模型
		return back_error(ctx, ErrorCode.SYS_ERR);
	}

	public static FullHttpResponse back_500() {
		// 创建返回模型
		return back_error(ErrorCode.SYS_ERR);
	}

	public static FullHttpResponse _200() {
		// 创建返回模型
		FullHttpResponse response = response();
		response.setStatus(HttpResponseStatus.OK);
		return response;
	}

	public static String params(HttpPostRequestDecoder decoder,String key){
		try {
			Attribute attribute = (Attribute)decoder.getBodyHttpData(key);
			if(attribute == null) return  null;
			return attribute.getValue();
		} catch (IOException e) {
			logger.warn("getParam error. key="+key,e);
			return null;
		}
	}

	public static void indexHtml(ChannelHandlerContext ctx) {
		HttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.SEE_OTHER);
		response.headers().add(HttpHeaderNames.LOCATION,"/index.html");
		ctx.writeAndFlush(response);
	}
	@Deprecated
	public static void asyn_back_404(ChannelHandlerContext ctx) {
		// 创建返回模型
		FullHttpResponse response = response();
		// 设置 404 没有找到相关链接
		response.setStatus(HttpResponseStatus.NOT_FOUND);
	}
	public static void asyn_back_404() {
		// 创建返回模型
		FullHttpResponse response = response();
		// 设置 404 没有找到相关链接
		response.setStatus(HttpResponseStatus.NOT_FOUND);
	}

	public static FullHttpResponse _500() {
		FullHttpResponse response = response();
		// 设置 500
		response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
		return response;
	}
}
