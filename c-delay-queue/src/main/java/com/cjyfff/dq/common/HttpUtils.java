package com.cjyfff.dq.common;

import java.io.IOException;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiashen on 18-10-10.
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static RequestConfig requestConfig;

    private static CloseableHttpClient httpClient;

    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(
                builder.build());

            // 配置同时支持 HTTP 和 HTTPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(
                    "http", PlainConnectionSocketFactory.getSocketFactory())
                .register(
                    "https", sslFactory).build();

            PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            pcm.setMaxTotal(100);
            pcm.setDefaultMaxPerRoute(20);

            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000).setConnectTimeout(10000).build();

            ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
                HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && "timeout".equalsIgnoreCase(param)) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return 30 * 1000;
            };

            httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(pcm)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                // 设置长连接策略
                .setKeepAliveStrategy(keepAliveStrategy)
                .build();
        } catch (Exception e) {
            logger.error("HttpUtils init get error: ", e);
        }
    }


    public static String doPost(String apiUrl, String param) {
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(requestConfig);
            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(param, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            logger.error(httpPost.toString(), e);
        } finally {
            if (response != null) {
                try {
                    // 调用consume方法就会关闭流
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error(httpPost.toString(), e);
                }
            }
        }
        logger.debug("url:" + apiUrl + "-----result:" + httpStr);
        return httpStr;
    }

    public static void main(String[] args) throws InterruptedException {
        String url = "127.0.0.1:12001/test";
        String param = "";

        System.out.println(HttpUtils.doPost(url, param));
    }
}
