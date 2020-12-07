package com.zulfikar.aaiplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment {


    TextView button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        button = view.findViewById(R.id.themeBtn);
        button.setOnClickListener(view1 -> openThemeActivity());
        return view;
    }

    public void openThemeActivity() {
        Intent intent = new Intent(getActivity(), ThemeActivity.class);
        startActivity(intent);
    }
}

