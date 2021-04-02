package ex.dev.tool.blesample.central;

public interface CentralCallback
{
    void requestEnableBLE();
    void requestLocationPermission();
    void requestLocationOn();
    void onStatus(String msg);
    void onToast(String msg);
}
