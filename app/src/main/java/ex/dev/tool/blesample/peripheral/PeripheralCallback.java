package ex.dev.tool.blesample.peripheral;

public interface PeripheralCallback
{
    void requestEnableBLE();
    void onStatusMsg(String message);
    void onToast(String message);
}
