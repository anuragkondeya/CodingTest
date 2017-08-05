package anuragkondeya.com.anuragkondeya;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import anuragkondeya.com.anuragkondeya.Fragments.HeadlineSummary;

public class MainActivity extends AppCompatActivity {

    private HeadlineSummary mHeadlineSummary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHeadlineSummary = new HeadlineSummary();
        if (findViewById(R.id.headlines_frame_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.headlines_frame_container, mHeadlineSummary)
                    .commit();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}
