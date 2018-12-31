package com.kong.alex.sharkfeed.network;

public class NetworkState {

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    private final Status status;
    private final String msg;

    public static final NetworkState LOADED;
    public static final NetworkState LOADING;

    private NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static NetworkState error(String msg) {
        return new NetworkState(Status.FAILED, msg);
    }

    static {
        LOADED = new NetworkState(Status.SUCCESS, "Success");
        LOADING = new NetworkState(Status.RUNNING, "Running");
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}