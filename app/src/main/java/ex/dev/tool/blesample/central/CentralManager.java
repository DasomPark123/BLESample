package ex.dev.tool.blesample.central;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ex.dev.tool.blesample.utils.BluetoothUtils;
import ex.dev.tool.blesample.utils.Constants;

public class CentralManager
{
    private final String TAG = getClass().getSimpleName();

    private static CentralManager centralManager;

    private Context context;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;

    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private BluetoothGatt bluetoothGatt;

    private CentralCallback centralCallback;
    private Map<String, BluetoothDevice> scanResults;

    private Handler scanHandler;

    private BluetoothUtils bluetoothUtils;

    private boolean isConnected = false;
    private boolean isScanning = false;

    private final int SCAN_PERIOD = 10000;

    public CentralManager(Context context) {
        this.context = context;

        if (bluetoothUtils == null)
            bluetoothUtils = new BluetoothUtils();
    }

    public static CentralManager getInstance(Context context) {
        if (centralManager == null)
            centralManager = new CentralManager(context);

        return centralManager;
    }

    public void setCentralCallback(CentralCallback callback) {
        centralCallback = callback;
    }

    /* 블루투스 매니저, 블루투스 어댑터 초기화 */
    public void initBle(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /* BLE 스캐닝 시작 */
    public boolean startScan(Context context) {

        /* 이미 연결되어 있는 경우 스캐닝 안함 */
        if (isConnected)
            return false;

        /* 위치 권한 체크. android9 까지는 ACCESS_COARSE_LOCATION, 그 이후 버전은 ACCESS_FINE_LOCATION 쓰면 됨 */
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            centralCallback.requestLocationPermission();
            centralCallback.onStatus("Requesting location permission");
            return false;
        }

        /* 블루투스 사용 가능한지 체크 */
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            centralCallback.requestEnableBLE();
            centralCallback.onStatus("Requesting enable bluetooth");
            return false;
        }

        /* GPS on 되어있는지 체크 */
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            centralCallback.requestLocationOn();
            centralCallback.onStatus("Requesting enable location on");
            return false;
        }

        /* BLE Scanning 을 위한 BluetoothLEScanner 객체 생성 */
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

//        /* 스캔 필터를 리스트에 추가함 */
//        List<ScanFilter> filters = new ArrayList<>();
//        ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("00:24:06:f7:4f:d9").build();
//        filters.add(scanFilter);

        /* 저전력 스캔 모드를 셋팅함 */
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        /* 결과 값을 전달 받기 위한 Callback 생성 */
        scanResults = new HashMap<>();
        scanCallback = new BLEScanCallback(scanResults);

        /* 스캔할 준비가됨. 스캔 시작 */
        bluetoothLeScanner.startScan(null, settings, scanCallback);
        isScanning = true;

        /* 일정 시간 scanning 이후 Scanning stop */
        scanHandler = new Handler();
        scanHandler.postDelayed(runnable, SCAN_PERIOD);

        return true;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isScanning)
                stopScan();
        }
    };

    /* 스캔 정지 */
    private void stopScan() {
        if (isScanning && bluetoothAdapter != null && bluetoothAdapter.isEnabled() && bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
            scanComplete();
        }

        if (scanCallback != null)
            scanCallback = null;
        if (scanHandler != null)
            scanHandler = null;
        isScanning = false;

        centralCallback.onStatus("scanning stopped");
    }

    /* 스캔을 마쳤을때 동작 */
    private void scanComplete() {
        /* 스캔 결과가 아무것도 없는 경우*/
        if (scanResults.isEmpty()) {
            centralCallback.onStatus("scan results is empty");
            Log.d(TAG, "scan result is empty");
            return;
        }

//        String selectedDevice = "";
//        for (String deviceAddr : scanResults.keySet()) {
//            Log.d(TAG, "Found device : " + deviceAddr);
//            if (selectedDevice.equalsIgnoreCase(deviceAddr)) {
//                Log.d(TAG, "selectedDevice : " + deviceAddr);
//                BluetoothDevice device = scanResults.get(deviceAddr);
//                connectDevice(device);
//            }
//        }
    }

    /* 디바이스 연결 */
    private void connectDevice(BluetoothDevice device) {
        centralCallback.onStatus("Connecting to " + device.getAddress());
        GattClientCallback gattClientCallback = new GattClientCallback();
        bluetoothGatt = device.connectGatt(context, false, gattClientCallback);
        if(bluetoothGatt == null)
        {
            centralCallback.onStatus("Failed create bluetoothGatt");
        }
        else
        {
            centralCallback.onStatus("Successfully create bluetoothGatt");
        }
    }


    /* 디바이스 연결 해제 */
    public void disconnectGattServer() {
        Log.d(TAG, "Closing Gatt connection");
        centralCallback.onStatus("Closing Gatt connection");

        isConnected = false;

        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
    }

    /* 데이터 보냄. 20Byte 까지만 보낼 수 있음 */
    public void writeCharacteristic(String message) {
        if (!isConnected) {
            Log.d(TAG, "Failed to sendData due to no connection");
            return;
        }

        /* Service에 존재하는 Characteristic을 찾는다 */
        BluetoothGattCharacteristic characteristic = bluetoothUtils.findCharacteristic(bluetoothGatt, Constants.CHARACTERISTIC_UUID);

        if (characteristic == null) {
            Log.e(TAG, "Unable to find cmd characteristic");
            disconnectGattServer();
            return;
        }

        characteristic.setValue(message.getBytes()); // 20 byte limit
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

        boolean isSuccess = bluetoothGatt.writeCharacteristic(characteristic);

        if (isSuccess) {
            centralCallback.onStatus("Success to write command : " + message);
        } else {
            centralCallback.onStatus("Failed to write command");
            disconnectGattServer();
        }
    }

    public void readCharacteristic()
    {
        if (!isConnected) {
            Log.d(TAG, "Failed to sendData due to no connection");
            return;
        }

        /* GATT 서버에서 command 속성을 찾음 */
        BluetoothGattCharacteristic characteristic = bluetoothUtils.findCharacteristic(bluetoothGatt, Constants.CHARACTERISTIC_UUID);

        if (characteristic == null) {
            Log.e(TAG, "Unable to find cmd characteristic");
            disconnectGattServer();
            return;
        }

        boolean isSuccess = bluetoothGatt.readCharacteristic(characteristic);

        if(isSuccess)
        {
            centralCallback.onStatus("Success to read command");
        }
        else
        {
            centralCallback.onStatus("Failed to read command");
        }
    }

    private class BLEScanCallback extends ScanCallback {
        private Map<String, BluetoothDevice> scanResults;

        private BLEScanCallback(Map<String, BluetoothDevice> scanResult) {
            this.scanResults = scanResult;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult");
            addScanResult(result);
        }

//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//            Log.d(TAG, "onBatchScanResults");
//            for (ScanResult result : results) {
//                addScanResult(result);
//            }
//        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "BLE scan failed with code : " + errorCode);
        }


        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            Log.e(TAG, "scanned device : " + device.getName() + ", " + device.getAddress());
            if(!scanResults.containsKey(device.getAddress())) {
                centralCallback.onStatus("scanned device : " + device.getName() + ": " + device.getAddress());
                scanResults.put(deviceAddress, device);

                if(device.getName() != null && device.getName().equalsIgnoreCase("SN2033910131"))
                {
                    connectDevice(device);
                }
            }
        }
    }

    /* Gatt client callback class */
    private class GattClientCallback extends BluetoothGattCallback
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            centralCallback.onStatus("onConnectionStateChange : " + status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                disconnectGattServer();
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stopScan();
                centralCallback.onStatus("Connected");
                isConnected = true;
                Log.d(TAG, "Connected to the GATT server");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                disconnectGattServer();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            centralCallback.onStatus("onServicesDiscovered : " + status);

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Device service discovery failed, status:" + status);
                return;
            }

            List<BluetoothGattCharacteristic> matchingCharacteristics = bluetoothUtils.findBLECharacteristics(gatt);
            for (BluetoothGattCharacteristic characteristic : matchingCharacteristics) {
                Log.d(TAG, "characteristic: " + characteristic.getUuid());
            }

            if (matchingCharacteristics.isEmpty()) {
                Log.d(TAG, "Unable to find characteristics");
                return;
            }

            Log.d(TAG, " Service discovery is successful");

            /* 이걸 설정해 줘야 onCharacteristicChanged callback 을 받을 수 있음 */
            BluetoothGattCharacteristic characteristic = bluetoothUtils.findCharacteristic(bluetoothGatt, Constants.CHARACTERISTIC_UUID);
            gatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(Constants.DESCRIPTOR_STRING));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean success = gatt.writeDescriptor(descriptor);
            if (success)
                Log.e(TAG, "writeCharacteristic success");
            else
                Log.d(TAG, "writeCharacteristic fail");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "characteristic changed : " + characteristic.getUuid().toString());

            String message = new String(characteristic.getValue());
            centralCallback.onStatus("onCharacteristicChanged() : " + message);
            //printData(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "Characteristic written successfully");
            } else {
                Log.e(TAG, "Characteristic write unsuccessful, status: " + status);
                disconnectGattServer();
            }

            String message = new String(characteristic.getValue());
            centralCallback.onStatus("onCharacteristicWrite() : " + message);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic read successfully");

                String message = new String(characteristic.getValue());
                centralCallback.onStatus("onCharacteristicRead() : " + message);

            } else {
                Log.e(TAG, "Characteristic read unsuccessful, status: " + status);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            centralCallback.onStatus("onDescriptorWrite() status : " + status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);

            centralCallback.onStatus("onDescriptorRead() status : " + status);
        }

        //        private void printData(BluetoothGattCharacteristic characteristic) {
//            byte[] msg = characteristic.getValue();
//            String message = new String(msg);
//            Log.d(TAG, "read:" + message);
//            centralCallback.onStatus("read : " + message);
//            centralCallback.onToast("read : " + message);
//        }
    }
}
