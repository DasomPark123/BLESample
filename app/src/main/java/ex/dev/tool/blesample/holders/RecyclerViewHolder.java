package ex.dev.tool.blesample.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ex.dev.tool.blesample.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder
{
    public TextView deviceName;
    public TextView macAddress;

    public RecyclerViewHolder(@NonNull View itemView)
    {
        super(itemView);

        deviceName = itemView.findViewById(R.id.tv_device_name);
        macAddress = itemView.findViewById(R.id.tv_mac_address);
    }
}
