package ex.dev.tool.blesample.central;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.provider.SyncStateContract;

import java.util.ArrayList;
import java.util.List;

import ex.dev.tool.blesample.Constants;

public class BluetoothUtils
{
    private static boolean matchUUIDs(String uuidString, String match)
    {
        if(uuidString.equalsIgnoreCase(match))
            return true;

        return false;
    }

    public static boolean matchServiceUUIDString(String serviceUUIDString)
    {
        return matchUUIDs(serviceUUIDString, Constants.SERVICE_STRING);
    }


    /* Find Gatt service matches with the server's service */
    public static BluetoothGattService findGattService(List<BluetoothGattService> serviceList)
    {
        for(BluetoothGattService service : serviceList)
        {
            String serviceUUIDString = service.getUuid().toString();
            if(matchServiceUUIDString(serviceUUIDString))
                return service;
        }
        return null;
    }

    /* Find characteristic of BLE */
    public static List<BluetoothGattCharacteristic> findBLECharacteristics(BluetoothGatt bluetoothGatt)
    {
        List<BluetoothGattCharacteristic> matchedCharacteristics = new ArrayList<>();
        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
        BluetoothGattService service = findGattService(serviceList);
        if(service == null)
            return matchedCharacteristics;

//        List<BluetoothGattCharacteristic>
    }
}
