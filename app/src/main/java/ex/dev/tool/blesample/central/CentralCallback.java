package ex.dev.tool.blesample.central;

import android.bluetooth.BluetoothDevice;

public interface CentralCallback
{
    void onRequestEnableBLE();
    void onRequestLocationPermission();
    void onRequestLocationOn();
    void onScanDeviceResult(BluetoothDevice device);
    void onRequestConnect(String macAddress);
    void onConnectionChanged(int state);
    void onPrintMessage(String msg);
}
