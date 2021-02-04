package ex.dev.tool.blesample;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ex.dev.tool.blesample.adapters.ViewpagerAdapter;
import ex.dev.tool.blesample.fragments.ConnectionFragment;

public class MainActivity extends FragmentActivity
{
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private BluetoothGatt bluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new ViewpagerAdapter(this, new ConnectionFragment(this));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        String[] tabsName = getResources().getStringArray(R.array.tabs_names);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabsName[position])).attach();
    }

//    private BluetoothGatt getBluetoothGatt()
//    {
//        if(bluetoothGatt == null)
//            bluetoothGatt = new
//        return bluetoothGatt
//    }
}