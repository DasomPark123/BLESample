package ex.dev.tool.blesample.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.entities.BLEDevice;
import ex.dev.tool.blesample.holders.RecyclerViewHolder;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
{
    private Context context;
    private ArrayList<BLEDevice> devices;

    public RecyclerViewAdapter(ArrayList<BLEDevice> devices, Context context)
    {
        this.devices = devices;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_device, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)
    {
        BLEDevice device = devices.get(position);
        holder.deviceName.setText(device.getDeviceName());
        holder.macAddress.setText(device.getMacAddress());
    }

    @Override
    public int getItemCount()
    {
        return devices.size();
    }


    public void addDevice(BluetoothDevice device)
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
    }
}
