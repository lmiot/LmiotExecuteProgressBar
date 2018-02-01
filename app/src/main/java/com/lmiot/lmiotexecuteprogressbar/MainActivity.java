package com.lmiot.lmiotexecuteprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lmiot.lmiotexecuteprogresslibrary.LmiotExecuteProgressBar;

public class MainActivity extends AppCompatActivity {

    private LmiotExecuteProgressBar mLmiotExecuteProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView=findViewById(R.id.id_test);
        TextView textView1=findViewById(R.id.id_test_1);
        TextView textView2=findViewById(R.id.id_test_2);
        mLmiotExecuteProgressBar = findViewById(R.id.id_lm);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLmiotExecuteProgressBar.setProgress(80);
            }
        });

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLmiotExecuteProgressBar.setProgress(100);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLmiotExecuteProgressBar.setProgress(-1);
            }
        });
    }
}
