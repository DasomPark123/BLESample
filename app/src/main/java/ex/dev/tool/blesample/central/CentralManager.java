package ex.dev.tool.blesample.central;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.util.Log;

public class CentralManager
{
    private final String TAG = getClass().getSimpleName();

    private static CentralManager centralManager;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private BluetoothGatt bluetoothGatt;

    private CentralCallback centralCallback;

    private boolean isConnected = false;

    public static CentralManager getInstance()
    {
        if(centralManager == null)
            centralManager = new CentralManager();

        return centralManager;
    }

    public void setCentralCallback(CentralCallback callback)
    {
        centralCallback = callback;
    }

    /* Initialize bluetooth objects */
    public void initBle(Context context)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    /* Start scanning ble devices */
    public void startScan()
    {

    }

    /* Stop scanning */
    public void stopScan()
    {

    }

    public void connectDevice(BluetoothDevice device)
    {

    }

    public void sendData(String msg)
    {
        if(!isConnected)
        {
            Log.d(TAG,"Failed to sendData due to no connection.");
        }

        BluetoothGattCharacteristic characteristic =
    }






}
