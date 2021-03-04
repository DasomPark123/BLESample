package ex.dev.tool.blesample.central;

public interface CentralCallback
{
    void requestEnableBLE();
    void requestLocationPermission();
    void onStatusMsg(String msg);
    void onToast(String msg);
}
