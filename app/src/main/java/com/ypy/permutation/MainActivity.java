package com.ypy.permutation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomActionBar();
        Button btnGenerate=findViewById(R.id.btn_generate);
        btnGenerate.setOnClickListener(this);
    }

    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        ImageView imageView=mActionBarView.findViewById(R.id.titleBar_back);
        imageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.btn_generate:
                Intent intent=new Intent();
                intent.setClass(this,NumberListActivity.class);
                List<List<String>>numbers=new ArrayList<>();
                List<String>num1=new ArrayList<>();
                num1.add("111");
                num1.add("111");
                num1.add("111");
                numbers.add(num1);
                intent.putExtra("111", (Serializable)numbers);
                startActivity(intent);
                break;
        }
    }
}
