package com.siukeungTech.aria2Downloader.dto;

import java.util.List;

/**
 * @author Dylan Wei
 * @date 2018-05-24 21:17
 */
public class Request {
    private String id;
    private String method;
    private String jsonrpc = "2.0";
    private List<Object> params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", method='" + method + '\'' +
                ", jsonrpc='" + jsonrpc + '\'' +
                ", params=" + params +
                '}';
    }
}
