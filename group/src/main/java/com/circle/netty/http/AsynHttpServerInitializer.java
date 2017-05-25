/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.circle.netty.http;


import com.circle.netty.formation.util.AppConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.context.ApplicationContext;

public class AsynHttpServerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	private ApplicationContext context;
	public AsynHttpServerInitializer(SslContext sslCtx, ApplicationContext context) {
		this.sslCtx = sslCtx;
		this.context = context;
	}

	public void initChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		HttpResponseEncoder httpResponseEncoder = new HttpResponseEncoder();
		pipeline.addLast("http-encoder",httpResponseEncoder);
		HttpRequestDecoder httpRequestDecoder = new HttpRequestDecoder();
		pipeline.addLast("http-decoder",httpRequestDecoder);
		HttpObjectAggregator httpObjectAggregator = new HttpObjectAggregator(AppConfig.MAX_CONTENT_LENGTH);
		pipeline.addLast("http-aggregator", httpObjectAggregator);
		ChunkedWriteHandler chunkedWriteHandler =new  ChunkedWriteHandler();
		pipeline.addLast("http-chunked", chunkedWriteHandler);
		//业务逻辑处理handler
		pipeline.addLast("http-serverHandler",new AsynHttpServerHandler(context));
	}
}
