package com.autobutton.autobuttondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoButton autoButton = (AutoButton) findViewById(R.id.autoButton);
        autoButton.setSoftFloorListener(new AutoButton.SoftFloorListener() {
            @Override
            public void softFloor() {
                Toast.makeText(MainActivity.this, "关", Toast.LENGTH_SHORT).show();
            }
        });
        autoButton.setHydropowerListener(new AutoButton.HydropowerListener() {
            @Override
            public void hydropower() {
                Toast.makeText(MainActivity.this, "开", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
