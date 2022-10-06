package com.example.navu.chatterbox;

/**
 * Created by Navu on 28-May-18.
 */

public class Request {

    public  String requesttype;

    public Request() {
    }

    public Request(String requesttype) {
        this.requesttype = requesttype;
    }

    public String getRequesttype() {
        return requesttype;
    }

    public void setRequesttype(String requesttype) {
        this.requesttype = requesttype;
    }
}
