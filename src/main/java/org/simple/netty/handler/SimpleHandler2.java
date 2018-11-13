package org.simple.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import org.simple.core.generator.SnowFlakeIDGenerator;

import java.util.List;

/**
 * Created by arebya on 18/4/8.
 */
public class SimpleHandler2 extends SimpleChannelInboundHandler<HttpRequest> {

    private SnowFlakeIDGenerator generator;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        if (!msg.getDecoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        FullHttpResponse response = null;
        long datacenterId = -1L;
        String dataCenterparam = System.getProperty("datacenterId");
        if (!StringUtil.isNullOrEmpty(dataCenterparam)) {
            // sys property
            datacenterId = Long.valueOf(dataCenterparam);
        }
        long workerId = -1L;
        String workerparam = System.getProperty("workerId");
        if (!StringUtil.isNullOrEmpty(workerparam)) {
            workerId = Long.valueOf(workerparam);
        }
        // 这里需要在server的handler pipeline中加入HttpObjectAggregator,来获取FullHttpRequest,否则得到的是DefaultHttpRequest
        if (!(msg instanceof FullHttpRequest)) {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer("OK"
                    .getBytes()));
            sendResponse(ctx, response);
            return;
        }
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        // get方法处理
        if (msg.getMethod() == HttpMethod.GET) {
            String url = msg.getUri();
            // 解析get请求中的params
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
            if (workerId <= 0) {
                List<String> workIdList = queryStringDecoder.parameters().get("workerId");
                workerId = Long.valueOf(workIdList.get(0));
            }
            if (datacenterId <= 0) {
                List<String> dataCenterIdList = queryStringDecoder.parameters().get("datacenterId");
                datacenterId = Long.valueOf(dataCenterIdList.get(0));
            }
        } else if (msg.getMethod() == HttpMethod.POST) {
            // post方法处理
//            ByteBuf body = httpRequest.content();
            // 解析post请求的body数据
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), msg);
            if (workerId <= 0) {
                InterfaceHttpData workerIdData = decoder.getBodyHttpData("workerId");
                workerId = Long.valueOf(((MemoryAttribute) workerIdData).getValue());
            }
            if (datacenterId <= 0) {
                InterfaceHttpData datacenterIdData = decoder.getBodyHttpData("datacenterId");
                datacenterId = Long.valueOf(((MemoryAttribute) datacenterIdData).getValue());
            }
        }
        // TODO
        generator = new SnowFlakeIDGenerator(workerId, datacenterId);
        long nextId = generator.nextId();
        ByteBuf output = Unpooled.wrappedBuffer(String.valueOf(nextId).getBytes());
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, output);
        sendResponse(ctx, response);
    }


    /**
     * 处理异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 返回错误码给客户端
     *
     * @param ctx
     * @param status
     */
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
//        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
//        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        sendResponse(ctx, response);
    }

    /**
     * 响应数据发送
     *
     * @param ctx
     * @param httpResponse
     */
    private void sendResponse(ChannelHandlerContext ctx, FullHttpResponse httpResponse) {
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain;charset=UTF-8");
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                httpResponse.content().readableBytes());
        httpResponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        ctx.write(httpResponse);
        ctx.flush();
    }
}
