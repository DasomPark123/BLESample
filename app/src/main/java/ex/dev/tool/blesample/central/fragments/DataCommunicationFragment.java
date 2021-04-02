package ex.dev.tool.blesample.central.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ex.dev.tool.blesample.R;


public class DataCommunicationFragment extends Fragment
{
    private Context context;

    public DataCommunicationFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_data_communication, container, false);
        return rootView;
    }
}