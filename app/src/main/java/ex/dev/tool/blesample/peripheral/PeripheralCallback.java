package ex.dev.tool.blesample.peripheral;

public interface PeripheralCallback
{
    void requestEnableBLE();
    void onPrintMessage(String message);
}
