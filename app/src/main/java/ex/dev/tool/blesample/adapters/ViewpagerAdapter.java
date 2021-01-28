package ex.dev.tool.blesample.adapters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ex.dev.tool.blesample.fragments.ConnectionFragment;
import ex.dev.tool.blesample.fragments.DataCommunicationFragment;

public class ViewpagerAdapter extends FragmentStateAdapter
{
    private Context context;
    private Fragment connectionFragment;
    private Fragment dataCommunicationFragment;

    private final int TABS_COUNT = 2;

    public ViewpagerAdapter(@NonNull FragmentActivity fragmentActivity, Fragment connectionFragment)
    {
        super(fragmentActivity);
        this.connectionFragment = connectionFragment;
        context = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        Log.d("ViewpagerAdapter","current position : " + position);
        switch(position)
        {
            case 0 :
                if(connectionFragment == null)
                    connectionFragment = new ConnectionFragment(context);
                return connectionFragment;
            case 1 :
                if(dataCommunicationFragment == null)
                    dataCommunicationFragment = new DataCommunicationFragment(context);
                return dataCommunicationFragment;
        }
        return new ConnectionFragment(context);
    }

    @Override
    public int getItemCount()
    {
        return TABS_COUNT;
    }
}
