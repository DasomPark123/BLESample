package ex.dev.tool.blesample.holders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ex.dev.tool.blesample.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder
{
    private LinearLayout linearDevice;
    public TextView deviceName;
    public TextView macAddress;

    public RecyclerViewHolder(@NonNull View itemView, View.OnClickListener onClickListener)
    {
        super(itemView);

        linearDevice = itemView.findViewById(R.id.linear_device);
        linearDevice.setOnClickListener(onClickListener);
        deviceName = itemView.findViewById(R.id.tv_device_name);
        macAddress = itemView.findViewById(R.id.tv_mac_address);
    }
}
