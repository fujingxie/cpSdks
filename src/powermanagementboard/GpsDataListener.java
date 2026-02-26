package powermanagementboard;

public interface GpsDataListener {
    void onGpsDataReceived(GpgllParser.GpgllData data);
    void onError(String errorMessage);
}
