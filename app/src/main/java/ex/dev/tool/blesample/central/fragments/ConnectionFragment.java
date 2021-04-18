package ex.dev.tool.blesample.central.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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

import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.adapters.RecyclerViewAdapter;
import ex.dev.tool.blesample.central.CentralActivity;
import ex.dev.tool.blesample.central.CentralCallback;
import ex.dev.tool.blesample.central.CentralManager;
import ex.dev.tool.blesample.entities.BLEDevice;
import ex.dev.tool.blesample.holders.RecyclerViewHolder;


public class ConnectionFragment extends Fragment
{
    private final String TAG = getClass().getSimpleName();

    private TextView tvDeviceName;
    private TextView tvMacAddress;
    private Button btnStartSearch;
    private Button btnStopSearch;
    private ImageButton btnClearAndRefresh;

    private Context context;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter rvAdapter;

    private CentralManager centralManager;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice selectedDevice;

    private final int REQUEST_ENABLE_BT = 0x1001;
    private final int REQUEST_PERMISSIONS = 0x1002;
    private final int REQUEST_ENABLE_LOCATION = 0x1003;

    private static final long SCAN_PERIOD = 10000;

    private ArrayList<BLEDevice> devices = new ArrayList<>();

    public ConnectionFragment(Context context)
    {
        this.context = context;
    }

    public static ConnectionFragment newInstance(Context context) {
        return new ConnectionFragment(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_connection, container, false);

        tvDeviceName = rootView.findViewById(R.id.tv_connected_device);
        tvMacAddress = rootView.findViewById(R.id.tv_connected_device_mac_address);
        btnStartSearch = rootView.findViewById(R.id.btn_start_search);
        btnStartSearch.setOnClickListener(onScanClickListener);
        btnStopSearch = rootView.findViewById(R.id.btn_stop_search);
        btnStopSearch.setOnClickListener(onScanClickListener);
        btnClearAndRefresh = rootView.findViewById(R.id.btn_remove);
        btnClearAndRefresh.setOnClickListener(onIconClickListener);

        recyclerView = rootView.findViewById(R.id.rv_device_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        rvAdapter = new RecyclerViewAdapter(devices, context, centralCallback);
        recyclerView.setAdapter(rvAdapter);

        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();

        requestEnableBT();
        requestEnableLocation();
        requestPermission();

        initBle();
        startScan();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    /* Request location permissions */
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

    /* Check host device has location permissions */
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

    /* Moving to Settings to enable GPS */
    private void requestEnableLocation()
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
            showEnableGPSRequest();
        }
    }

    /* Request enable BT */
    private void requestEnableBT()
    {
        if(btAdapter == null || !btAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /* Show dialog to enable GPS. if ok pressed moving to Settings */
    public void showEnableGPSRequest()
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

    public void requestConnection(String macAddress)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.dialog_connection_title));
        builder.setMessage(getString(R.string.dialog_connection_msg, macAddress));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selectedDevice = btAdapter.getRemoteDevice(macAddress);
                centralManager.connectDevice(selectedDevice);
                dialog.dismiss();
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

    private void initBle()
    {
        centralManager = ((CentralActivity) getActivity()).getCentralManager();
        centralManager.setCentralCallback(centralCallback);
        centralManager.initBle(context);
    }

    private void startScan()
    {
        stopScan();
        rvAdapter.clear();
        centralManager.startScan(context);
    }

    private void stopScan()
    {
        centralManager.stopScan();
    }

    private void addDeviceToList(BluetoothDevice device)
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
            if(v.getId() == R.id.btn_start_search)
            {
                startScan();
            }
            else if(v.getId() == R.id.btn_stop_search)
            {
                startScan();
            }
        }
    };

    private View.OnClickListener onIconClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(v.getId() == R.id.btn_remove)
            {
                rvAdapter.clear();
            }
        }
    };

    private CentralCallback centralCallback = new CentralCallback()
    {
        @Override
        public void onRequestEnableBLE()
        {
            requestEnableBT();
        }

        @Override
        public void onRequestLocationPermission()
        {
            requestPermission();
        }

        @Override
        public void onRequestLocationOn()
        {
            requestEnableLocation();
        }

        @Override
        public void onPrintMessage(String msg)
        {

        }

        @Override
        public void onScanDeviceResult(BluetoothDevice device)
        {
            addDeviceToList(device);
        }

        @Override
        public void onRequestConnect(String macAddress) {
            requestConnection(macAddress);
        }

        @Override
        public void onConnectionChanged(int state) {
            if(state == CentralManager.CONNECTED)
            {
                tvDeviceName.setText(selectedDevice.getName());
                tvMacAddress.setText(selectedDevice.getAddress());
            }
            else if(state == CentralManager.DISCONNECTED)
            {
                tvDeviceName.setText("");
                tvMacAddress.setText("");
            }
        }
    };
}