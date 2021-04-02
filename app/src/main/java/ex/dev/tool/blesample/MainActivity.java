package ex.dev.tool.blesample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.central.CentralActivity;
import ex.dev.tool.blesample.peripheral.PeripheralActivity;
import ex.dev.tool.blesample.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private Button btnCentral;
    private Button btnPeripheral;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        checkBLESupported();
    }

    private void initView() {
        btnCentral = findViewById(R.id.btn_central);
        btnPeripheral = findViewById(R.id.btn_peripheral);

        btnCentral.setOnClickListener(onClickListener);
        btnPeripheral.setOnClickListener(onClickListener);

        utils = new Utils();
    }

    private void checkBLESupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            utils.showDialog(this, getString(R.string.peripheral), getString(R.string.ble_not_supported));
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_central) {
                startActivity(new Intent(MainActivity.this, CentralActivity.class
                ));
            } else if (v.getId() == R.id.btn_peripheral) {
                startActivity(new Intent(MainActivity.this, PeripheralActivity.class));
            }
        }
    };
}