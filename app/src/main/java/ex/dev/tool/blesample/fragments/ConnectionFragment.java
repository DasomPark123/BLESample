package ex.dev.tool.blesample.fragments;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.adapters.RecyclerViewAdapter;
import ex.dev.tool.blesample.entities.BLEDevice;


public class ConnectionFragment extends Fragment
{
    private final String TAG = getClass().getSimpleName();
    private Button btnSearch;
    private Button btnStop;

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
        btnSearch.setOnClickListener(onClickListener);
        btnStop = rootView.findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(onClickListener);

        recyclerView = rootView.findViewById(R.id.rv_device_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        rvAdapter = new RecyclerViewAdapter(devices, context);
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
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
            Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(enableLocationIntent, REQUEST_ENABLE_LOCATION);
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
                }
            }, SCAN_PERIOD);

            Log.d(TAG,"start");
            isScanning = true;
            leScanner.startScan(scanCallback);
        }
        /* Stop */
        else
        {
            Log.d(TAG,"stop enable:false");
            isScanning = false;
            leScanner.stopScan(scanCallback);
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
                    rvAdapter.addDevice(result.getDevice());
                    rvAdapter.notifyDataSetChanged();
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

    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId()){
                case R.id.btn_search :
                    scanLEDevice(true);
                    break;
                case R.id.btn_stop :
                    scanLEDevice(false);
                default:
                    break;
            }
        }
    };
}