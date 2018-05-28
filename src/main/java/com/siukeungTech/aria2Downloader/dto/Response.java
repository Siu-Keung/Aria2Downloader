package com.siukeungTech.aria2Downloader.dto;

/**
 * @author Dylan Wei
 * @date 2018-05-25 09:57
 */
public class Response<T> {
    private String id;
    private String jsonrpc;
    private T result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id='" + id + '\'' +
                ", jsonrpc='" + jsonrpc + '\'' +
                ", result=" + result +
                '}';
    }
}
