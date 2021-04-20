package ex.dev.tool.blesample.peripheral;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.utils.Utils;

public class PeripheralActivity extends AppCompatActivity
{
    private TextView tvData;
    private Button btnSend;
    private Button btnClose;
    private Button btnClear;

    private Utils utils;

    private final int REQUEST_BLE = 0x1001;

    private PeripheralManager peripheralManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral);
        initView();
        initServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        peripheralManager.closeServer();
    }

    private void initView()
    {
        tvData = findViewById(R.id.tv_data);
        btnSend = findViewById(R.id.btn_notify);
        btnClose = findViewById(R.id.btn_close);
        btnClear = findViewById(R.id.btn_clear);

        btnSend.setOnClickListener(onClickListener);
        btnClose.setOnClickListener(onClickListener);
        btnClear.setOnClickListener(onClickListener);

        utils = Utils.getInstance();

        peripheralManager = PeripheralManager.getInstance(this);
    }

    private void initServer()
    {
        peripheralManager.setCallback(peripheralCallback);
        peripheralManager.initServer(this);
    }

    private void showStatusMsg(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String oldMsg = tvData.getText().toString();
                tvData.setText(oldMsg + "\n" + message);
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom()
    {
        final ScrollView scrollView = ((ScrollView) findViewById(R.id.sv_data));
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void requestEnableBLE()
    {
        BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        if(btAdapter == null || !btAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_BLE && resultCode != RESULT_OK)
            requestEnableBLE();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_notify)
            {
                Calendar calendar = Calendar.getInstance();
                String data = (calendar.get(Calendar.MONTH) + 1)
                        + "/" + calendar.get(Calendar.DAY_OF_MONTH)
                        + " " + calendar.get(Calendar.HOUR_OF_DAY)
                        + ":" + calendar.get(Calendar.HOUR_OF_DAY)
                        + ":" + calendar.get(Calendar.SECOND);

                peripheralManager.notifyCharacteristic(data);

            }
            else if(v.getId() == R.id.btn_close)
            {
                peripheralManager.close();
            }
            else if(v.getId() == R.id.btn_clear)
            {
                tvData.setText("");
            }
        }
    };

    private PeripheralCallback peripheralCallback = new PeripheralCallback() {
        @Override
        public void requestEnableBLE() {
            requestEnableBLE();
        }

        @Override
        public void onPrintMessage(String message) {
            showStatusMsg(message);
        }
    };
}