package cn.tellsea.utils;



import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpClientUtils {

    /**
     * 发送http get请求
     */
    public HttpResponse httpGetBasic(String url, List<Header> headersList, String encode, int mode){
        HttpResponse response = new HttpResponse();
        if(encode == null){
            encode = "utf-8";
        }
        Object content = null;
        //since 4.3 不再使用 DefaultHttpClient
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);

        //设置header
        if (headersList != null && headersList.size() > 0) {
            for (Header header : headersList) {
                httpGet.setHeader(header.getName(), header.getValue());

            }
        }
        httpGet.setHeader("Content-Type","application/json");
        httpGet.setHeader("Trackingmore-Api-Key","2744bdc9-956d-43e4-83f4-7af90f7dc629");

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpGet);
            response.setHeaders(httpResponse.getAllHeaders());
            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());

            HttpEntity entity = httpResponse.getEntity();
            if (1 == mode) {//返回体为字符串
                content = EntityUtils.toString(entity, encode);
            } else if (2 == mode) {
                content = EntityUtils.toByteArray(entity);
            }
            response.setBody(content);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse httpGet(String url, List<Header> headersList, String encode){
        return httpGetBasic(url, headersList, encode, 1);
    }

    public HttpResponse httpGetByte(String url, List<Header> headersList, String encode){
        return httpGetBasic(url, headersList, encode, 2);
    }

    /**
     * 发送 http post 请求，参数以form表单键值对的形式提交。
     */
    public HttpResponse httpPostForm(String url, Map<String,String> params, List<Header> headersList, String encode){
        HttpResponse response = new HttpResponse();
        if(encode == null){
            encode = "utf-8";
        }
        //HttpClients.createDefault()等价于 HttpClientBuilder.create().build();
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        HttpPost httpost = new HttpPost(url);

        //设置header
        if (headersList != null && headersList.size() > 0) {
            for (Header header : headersList) {
                httpost.setHeader(header.getName(), header.getValue());
            }
        }
//        httpost.addHeader("Host","117.131.48.166:8084");
//        httpost.setHeader("Connection: ","keep-alive");
//        httpost.setHeader("Pragma","no-cache");
//        httpost.setHeader("User-Agent","webkit;Resolution(PAL,720P,1080P)");
//        httpost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        httpost.setHeader("Referer","http://117.131.48.114:8084/iptvepg/function/funcportalauth.jsp");
//        httpost.addHeader("Accept-Encoding","gzip, deflate");
//        httpost.addHeader("Accept-Language","zh-CN,en-US;q=0.8");
//        httpost.addHeader("X-Requested-With","com.android.smart.terminal.iptv");

        //组织请求参数
        List<NameValuePair> paramList = new ArrayList <NameValuePair>();
        if(params != null && params.size() > 0){
            Set<String> keySet = params.keySet();
            for(String key : keySet) {
                paramList.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        try {


            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(60000).setConnectionRequestTimeout(60000)
                    .setSocketTimeout(60000).build();

            httpost.setConfig(requestConfig);

            httpost.setEntity(new UrlEncodedFormEntity(paramList, encode));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String content = null;
        CloseableHttpResponse  httpResponse = null;
        try {

            httpResponse = closeableHttpClient.execute(httpost);
            HttpEntity entity = httpResponse.getEntity();

            content = EntityUtils.toString(entity, encode);
            response.setBody(content);
            response.setHeaders(httpResponse.getAllHeaders());
            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());


        } catch (Exception e) {
            e.printStackTrace();

        }finally{
            try {
                httpResponse.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return response;
    }

    /**
     * 发送 http post 请求，参数以form表单键值对的形式提交。
     */
    public HttpResponse httpPostXml(String url, String params, List<Header> headersList, String encode){
        HttpResponse response = new HttpResponse();
        if(encode == null){
            encode = "utf-8";
        }
        //HttpClients.createDefault()等价于 HttpClientBuilder.create().build();
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost httpost = new HttpPost(url);

        //设置header
        if (headersList != null && headersList.size() > 0) {
            for (Header header : headersList) {
                httpost.setHeader(header.getName(), header.getValue());
            }
        }
        //组织请求参数
//        List<NameValuePair> paramList = new ArrayList <NameValuePair>();
//        if(params != null && params.size() > 0){
//            Set<String> keySet = params.keySet();
//            for(String key : keySet) {
//                paramList.add(new BasicNameValuePair(key, params.get(key)));
//            }
//        }

        try {
            httpost.setEntity(new StringEntity(params,"text/xml",  encode));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String content = null;
        CloseableHttpResponse  httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpost);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
            response.setBody(content);
            response.setHeaders(httpResponse.getAllHeaders());
            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 发送 http post 请求，参数以原生字符串进行提交
     * @param url
     * @param encode
     * @return
     */
    public HttpResponse httpPostJSON(String url, String stringJson, List<Header> headersList, String encode){
        HttpResponse response = new HttpResponse();
        if(encode == null){
            encode = "utf-8";
        }
        //HttpClients.createDefault()等价于 HttpClientBuilder.create().build();
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost httpost = new HttpPost(url);

        //设置header
        httpost.setHeader("Content-type", "application/json");
        if (headersList != null && headersList.size() > 0) {
            for (Header header : headersList) {
                httpost.setHeader(header.getName(), header.getValue());
            }
        }
        //组织请求参数
        StringEntity stringEntity = new StringEntity(stringJson, encode);
        httpost.setEntity(stringEntity);
        String content = null;
        CloseableHttpResponse  httpResponse = null;
        try {
            //响应信息
            httpResponse = closeableHttpClient.execute(httpost);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
            response.setBody(content);
            response.setHeaders(httpResponse.getAllHeaders());
            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 发送 http put 请求，参数以原生字符串进行提交
     * @param url
     * @param encode
     * @return
     */
    public HttpResponse httpPutRaw(String url, String stringJson, List<Header> headersList, String encode){
        HttpResponse response = new HttpResponse();
        if(encode == null){
            encode = "utf-8";
        }
        //HttpClients.createDefault()等价于 HttpClientBuilder.create().build();
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(url);

        //设置header
        httpput.setHeader("Content-type", "application/json");
        if (headersList != null && headersList.size() > 0) {
            for (Header header : headersList) {
                httpput.setHeader(header.getName(), header.getValue());
            }
        }
        //组织请求参数
        StringEntity stringEntity = new StringEntity(stringJson, encode);
        httpput.setEntity(stringEntity);
        String content = null;
        CloseableHttpResponse  httpResponse = null;
        try {
            //响应信息
            httpResponse = closeableHttpClient.execute(httpput);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
            response.setBody(content);
            response.setHeaders(httpResponse.getAllHeaders());
            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            closeableHttpClient.close();  //关闭连接、释放资源
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 发送http delete请求
     */
    public HttpResponse httpDelete(String url, List<Header> headersList, String encode){
        HttpResponse response = new HttpResponse();
        if(encode == null){
            encode = "utf-8";
        }
        String content = null;
        //since 4.3 不再使用 DefaultHttpClient
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        HttpDelete httpdelete = new HttpDelete(url);
        //设置header
        if (headersList != null && headersList.size() > 0) {
            for (Header header : headersList) {
                httpdelete.setHeader(header.getName(), header.getValue());
            }
        }
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpdelete);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
            response.setBody(content);
            response.setHeaders(httpResponse.getAllHeaders());
            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {   //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse uploadFile(String url, Map<String,String> params, File file,
                                   List<Header> headersList, String encode) {
        List<File> list = new ArrayList();
        list.add(file);
        return httpPostFormMultipart(url,params,list,headersList,encode);
    }

    public HttpResponse uploadMultiFile(String url, Map<String,String> params, List<File> files,
                                        List<Header> headersList, String encode) {
        return httpPostFormMultipart(url,params,files,headersList,encode);
    }


    /**
     * 发送 http post 请求，支持文件上传
     */
    public HttpResponse httpPostFormMultipart(String url, Map<String,String> params, List<File> files,
                                              List<Header> headersList, String encode){
        HttpResponse response = new HttpResponse();
        if(encode == null){
            encode = "utf-8";
        }
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost httpost = new HttpPost(url);

        //设置header
        if (headersList != null && headersList.size() > 0) {
            for (Header header : headersList) {
                httpost.setHeader(header.getName(), header.getValue());
            }
        }
        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setCharset(Charset.forName(encode));

        // 普通参数
        ContentType contentType = ContentType.create("text/plain",Charset.forName(encode));//解决中文乱码
        if (params != null && params.size() > 0) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                mEntityBuilder.addTextBody(key, params.get(key),contentType);
            }
        }
        //二进制参数
        if (files != null && files.size() > 0) {
            for (File file : files) {
                mEntityBuilder.addBinaryBody("file", file);
            }
        }
        httpost.setEntity(mEntityBuilder.build());
        String content = null;
        CloseableHttpResponse  httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpost);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
            response.setBody(content);
            response.setHeaders(httpResponse.getAllHeaders());
            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
