package com.example.xvso;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.xvso.databinding.ActivityVsBinding;

public class VSActivity extends AppCompatActivity {

    private ActivityVsBinding vsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vs);

        vsBinding = DataBindingUtil.setContentView(this, R.layout.activity_vs);
        vsBinding.setLifecycleOwner(this);

    }
}
