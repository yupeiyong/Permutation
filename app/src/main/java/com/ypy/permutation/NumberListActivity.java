package com.ypy.permutation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class NumberListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_list);

        setCustomActionBar();
        List<List<String>>numbers=  (List<List<String>>) getIntent().getSerializableExtra(Constant.NumberList);
        ListView numberView=findViewById(R.id.lv_number);
        NumberAdapter adapter=new NumberAdapter(numbers);
        numberView.setAdapter(adapter);
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
        imageView.setOnClickListener(this);

        TextView tv=mActionBarView.findViewById(R.id.title);
        tv.setText("排列组合");
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.titleBar_back:
                this.finish();
                break;
        }
    }
}
