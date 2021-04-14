package ex.dev.tool.blesample.central;

import android.bluetooth.BluetoothDevice;

public interface CentralCallback
{
    void onRequestEnableBLE();
    void onRequestLocationPermission();
    void onRequestLocationOn();
    void onStatus(String msg);
    void onScanDeviceResult(BluetoothDevice device);
}
