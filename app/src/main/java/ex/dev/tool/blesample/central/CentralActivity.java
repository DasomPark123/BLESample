package ex.dev.tool.blesample.central;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ex.dev.tool.blesample.R;
import ex.dev.tool.blesample.adapters.ViewpagerAdapter;
import ex.dev.tool.blesample.central.fragments.ConnectionFragment;

public class CentralActivity extends FragmentActivity
{
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private CentralManager centralManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        centralManager = CentralManager.getInstance(this);

        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new ViewpagerAdapter(this, new ConnectionFragment(this));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        String[] tabsName = getResources().getStringArray(R.array.tabs_names);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabsName[position])).attach();
    }

    public CentralManager getCentralManager()
    {
        if(centralManager == null)
            centralManager = CentralManager.getInstance(this);

        return centralManager;
    }
}