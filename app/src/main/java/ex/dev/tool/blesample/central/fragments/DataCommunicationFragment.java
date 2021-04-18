package ex.dev.tool.blesample.central.fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.central.CentralActivity;
import ex.dev.tool.blesample.central.CentralCallback;
import ex.dev.tool.blesample.central.CentralManager;


public class DataCommunicationFragment extends Fragment
{
    private Context context;

    private CentralManager centralManager;
    private boolean isConnected;

    private Button btnWrite;
    private Button btnRead;
    private Button btnDisconnect;
    private Button btnClear;

    private TextView tvData;

    public DataCommunicationFragment(Context context)
    {
        this.context = context;
    }

    public DataCommunicationFragment newInstance(Context context)
    {
        return new DataCommunicationFragment(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_data_communication, container, false);
        btnWrite = rootView.findViewById(R.id.btn_write);
        btnWrite.setOnClickListener(onClickListener);
        btnRead = rootView.findViewById(R.id.btn_read);
        btnRead.setOnClickListener(onClickListener);
        btnDisconnect = rootView.findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(onClickListener);
        btnClear = rootView.findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(onClickListener);

        tvData = rootView.findViewById(R.id.tv_data);

        if(!isConnected)
            moveToConnectionFragment();

        initBle();

        return rootView;
    }

    private void initBle()
    {
        centralManager = ((CentralActivity)getActivity()).getCentralManager();
        centralManager.setCentralCallback(centralCallback);
        centralManager.initBle(context);
        isConnected =  centralManager.isConnected;
    }

    public void moveToConnectionFragment()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.dialog_connection_title));
        builder.setMessage(getString(R.string.dialog_not_connected));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //((CentralActivity) getActivity()).replaceFragment(ConnectionFragment.newInstance(context));
                dialog.dismiss();
                ((CentralActivity) getActivity()).finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_write)
            {
                Calendar calendar = Calendar.getInstance();
                String todayTime = (calendar.get(Calendar.MONTH))
                        + "월" + calendar.get(Calendar.DAY_OF_MONTH)
                        + "일" + calendar.get(Calendar.HOUR_OF_DAY)
                        + ":" + calendar.get(Calendar.MINUTE)
                        + ":" + calendar.get((Calendar.SECOND));
                centralManager.writeCharacteristic(todayTime);
            }
            else if(v.getId() == R.id.btn_read)
            {
                centralManager.readCharacteristic();
            }
            else if(v.getId()== R.id.btn_disconnect)
            {
                centralManager.disconnectGattServer();
            }
            else if(v.getId() == R.id.btn_clear)
            {

            }
        }
    };

    private CentralCallback centralCallback = new CentralCallback() {
        @Override
        public void onRequestEnableBLE() {

        }

        @Override
        public void onRequestLocationPermission() {

        }

        @Override
        public void onRequestLocationOn() {

        }

        @Override
        public void onScanDeviceResult(BluetoothDevice device) {

        }

        @Override
        public void onRequestConnect(String macAddress) {

        }

        @Override
        public void onConnectionChanged(int state) {
            if(state == CentralManager.DISCONNECTED)
            {
                ((CentralActivity) getActivity()).finish();
            }
        }

        @Override
        public void onPrintMessage(String msg) {
            tvData.setText(msg);
        }
    };
}