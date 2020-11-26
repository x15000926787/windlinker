package cn.tellsea.utils;


import org.apache.http.Header;

import java.util.Arrays;


public class HttpResponse {
    private int statusCode;

    private String protocol;

    private String message;

    private Object body;

    private Header[] headers;

    private String reasonPhrase;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", protocol='" + protocol + '\'' +
                ", message='" + message + '\'' +
                ", body='" + body + '\'' +
                ", headers=" + Arrays.toString(headers) +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                '}';
    }
}

