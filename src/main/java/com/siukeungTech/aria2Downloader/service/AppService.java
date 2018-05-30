package com.siukeungTech.aria2Downloader.service;

import com.alibaba.fastjson.JSON;
import com.siukeungTech.aria2Downloader.dto.Request;
import com.siukeungTech.aria2Downloader.dto.Response;
import oracle.jrockit.jfr.events.RequestableEventEnvironment;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.naming.event.ObjectChangeListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

/**
 * @author Dylan Wei
 * @date 2018-05-24 21:04
 */
public class AppService {
    private static AppService appService = new AppService();
    private URL url;

    private AppService(){
        try {
            this.url = new URL("http://127.0.0.1:6800/jsonrpc");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static AppService getInstance(){
        return appService;
    }

    private Response sendRequest(Request request){
        request.setId(UUID.randomUUID().toString());
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
        try {
            HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
            connection.setDoOutput(true);

            String jsonRequest = JSON.toJSONString(request);
//            System.out.println("Request: " + jsonRequest);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(jsonRequest);
            writer.flush();
            int responseCode = connection.getResponseCode();
            if(responseCode != 200)
                throw new IOException("请求异常，HTTP Response Code: " + responseCode);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonResponse = "";
            String temp;
            while((temp = reader.readLine()) != null)
                jsonResponse += temp;
//            System.out.println("Response: " + jsonResponse);
            Response response = JSON.parseObject(jsonResponse, Response.class);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response<String> addTask(String url, Map<String, String> options){
        Request request = new Request();
        request.setMethod("aria2.addUri");
        List<Object> params = Arrays.asList(Arrays.asList(url), options);
        request.setParams(params);
        Response response = this.sendRequest(request);
        return response;
    }

    public Response<List<Map<String, Object>>> getActive(){
        Request request = new Request();
        request.setMethod("aria2.tellActive");
        Response response = this.sendRequest(request);
        return response;
    }

    public Response<List<Map<String, Object>>> getWaiting(){
        Request request = new Request();
        request.setMethod("aria2.tellWaiting");
        request.setParams(Arrays.asList(0, 1000));
        Response response = this.sendRequest(request);
        return response;
    }

    public Response<List<Map<String, Object>>> getStopped(){
        Request request = new Request();
        request.setMethod("aria2.tellStopped");
        request.setParams(Arrays.asList(0, 1000));
        Response response = this.sendRequest(request);
        return response;
    }

    public void pause(String gid){
        Request request = new Request();
        request.setMethod("aria2.pause");
        request.setParams(Arrays.asList(gid));
        this.sendRequest(request);
    }

    public void resume(String gid){
        Request request = new Request();
        request.setMethod("aria2.unpause");
        request.setParams(Arrays.asList(gid));
        this.sendRequest(request);
    }


}
