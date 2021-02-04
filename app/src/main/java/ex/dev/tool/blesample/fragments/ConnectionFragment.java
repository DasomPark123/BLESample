package ex.dev.tool.blesample.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.adapters.RecyclerViewAdapter;
import ex.dev.tool.blesample.entities.BLEDevice;


public class ConnectionFragment extends Fragment
{
    private final String TAG = getClass().getSimpleName();
    private Button btnSearch;
    private ImageButton btnClearAndRefresh;

    private Context context;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter rvAdapter;

    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner leScanner;
    private Handler handler = new Handler();
    private boolean isScanning;

    private final int REQUEST_ENABLE_BT = 0x1001;
    private final int REQUEST_PERMISSIONS = 0x1002;
    private final int REQUEST_ENABLE_LOCATION = 0x1003;

    private static final long SCAN_PERIOD = 10000;

    private ArrayList<BLEDevice> devices = new ArrayList<>();

    public ConnectionFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        checkIsBTEnable();
        checkIsLocationEnable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_connection, container, false);

        btnSearch = rootView.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(onScanClickListener);
        btnClearAndRefresh = rootView.findViewById(R.id.btn_stop);
        btnClearAndRefresh.setOnClickListener(onIconClickListener);

        recyclerView = rootView.findViewById(R.id.rv_device_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        rvAdapter = new RecyclerViewAdapter(devices, context, onItemClickListener);
        recyclerView.setAdapter(rvAdapter);

        requestPermission();
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        leScanner = btAdapter.getBluetoothLeScanner();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private void requestPermission()
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            String[] PERMISSIONS = {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };

            if(!hasPermissions(context, PERMISSIONS))
            {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            }
        }
    }

    private boolean hasPermissions(Context context, String... permissions)
    {
        if(context != null && permissions != null)
        {
            for(String permission : permissions)
            {
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
            return true;
        }
       return false;
    }

    private void checkIsLocationEnable()
    {
        boolean isGpsEnable = false;

        /* Exception will be thrown if provider is not permitted */
        try
        {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception e)
        {
        }

        if(!isGpsEnable)
        {
            requestGPSEnable();
        }
    }

    private void checkIsBTEnable()
    {
        if(btAdapter == null || !btAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void requestGPSEnable()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.dialog_gps_title));
        builder.setMessage(getString(R.string.dialog_gps_msg));
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if(keyCode == KeyEvent.KEYCODE_BACK)
                {
                    dialog.dismiss();
                    getActivity().finish();
                }
                return false;
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableLocationIntent, REQUEST_ENABLE_LOCATION);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                getActivity().finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void scanLEDevice(final boolean enable)
    {
        /* Start */
        if(enable)
        {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d(TAG,"stop enable:true");
                    isScanning = false;
                    leScanner.stopScan(scanCallback);
                    changeUI(isScanning);

                }
            }, SCAN_PERIOD);

            Log.d(TAG,"start");
            rvAdapter.clear();
            isScanning = true;
            leScanner.startScan(scanCallback);
            changeUI(isScanning);
        }
        /* Stop */
        else
        {
            Log.d(TAG,"stop enable:false");
            isScanning = false;
            leScanner.stopScan(scanCallback);
            changeUI(isScanning);
        }
    }

    private void changeUI(boolean isScanning)
    {
        if(isScanning)
        {
            btnSearch.setText(getString(R.string.ble_stop_search_devices));
            btnClearAndRefresh.setImageResource(R.drawable.outline_cached_24);
        }
        else
        {
            btnSearch.setText(getString(R.string.ble_start_search_devices));
            btnClearAndRefresh.setImageResource(R.drawable.outline_delete_24);
        }
    }

    private ScanCallback scanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            super.onScanResult(callbackType, result);
            Log.d(TAG,"device : " + result.getDevice());

            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    /* Remove duplication */
                    boolean isExist = false;
                    for (int i = 0; i < devices.size(); i++)
                    {
                        if(result.getDevice().getAddress().equalsIgnoreCase(devices.get(i).getMacAddress()))
                            isExist = true;
                    }

                    if(!isExist)
                        addDevice(result.getDevice());
                }
            });
        }

        @Override
        public void onScanFailed(int errorCode)
        {
            super.onScanFailed(errorCode);
            Log.d(TAG,"error code : " + errorCode);
        }
    };

    private void addDevice(BluetoothDevice device)
    {
        BLEDevice bleDevice = new BLEDevice();

        String deviceName = device.getName();
        if(deviceName == null || deviceName.isEmpty())
            deviceName = context.getString(R.string.unknown);

        String macAddress = device.getAddress();
        if(macAddress == null || macAddress.isEmpty())
            macAddress = context.getString(R.string.unknown);

        bleDevice.setDeviceName(deviceName);
        bleDevice.setMacAddress(macAddress);

        devices.add(bleDevice);
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ENABLE_BT && requestCode == Activity.RESULT_CANCELED)
        {
            getActivity().finish();
        }
        else if(requestCode == REQUEST_ENABLE_LOCATION && requestCode == Activity.RESULT_CANCELED)
        {
            getActivity().finish();
        }
    }

    private View.OnClickListener onScanClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(!isScanning)
                scanLEDevice(true);
            else
                scanLEDevice(false);
        }
    };

    private View.OnClickListener onIconClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(!isScanning)
            {
                rvAdapter.clear();
                rvAdapter.notifyDataSetChanged();
            }
            else
            {
                rvAdapter.clear();
                scanLEDevice(true);
            }
        }
    };

    private View.OnClickListener onItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

        }
    };
}