package ex.dev.tool.blesample.holders;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.central.CentralCallback;
import ex.dev.tool.blesample.entities.BLEDevice;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = getClass().getSimpleName();

    private LinearLayout linearDevice;
    public TextView deviceName;
    public TextView macAddress;

    private ArrayList<BLEDevice> devices;
    private CentralCallback centralCallback;

    public RecyclerViewHolder(@NonNull View itemView, ArrayList<BLEDevice> devices, CentralCallback centralCallback) {
        super(itemView);

        this.centralCallback = centralCallback;
        this.devices = devices;

        linearDevice = itemView.findViewById(R.id.linear_device);
        linearDevice.setOnClickListener(onItemClickListener);
        deviceName = itemView.findViewById(R.id.tv_device_name);
        macAddress = itemView.findViewById(R.id.tv_mac_address);
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d(TAG, "Selected position : " + position);
            centralCallback.onRequestConnect(devices.get(position).getMacAddress());
        }
    };
}
