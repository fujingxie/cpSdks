package fourg.interfaces;

public interface ServerConnectListerner {

    void onSuccess();

    void onFail(String errorMsg);
}
