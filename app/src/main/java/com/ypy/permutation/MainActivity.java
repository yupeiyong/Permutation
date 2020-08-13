package com.ypy.permutation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText first;
    private EditText second;
    private EditText third;
    private EditText fourth;
    private EditText fifth;
    private EditText sixth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomActionBar();
        Button btnGenerate=findViewById(R.id.btn_generate);
        btnGenerate.setOnClickListener(this);
        first=findViewById(R.id.txt_first);
        second=findViewById(R.id.txt_second);
        third=findViewById(R.id.txt_third);
        fourth=findViewById(R.id.txt_fourth);
        fifth=findViewById(R.id.txt_fifth);
        sixth=findViewById(R.id.txt_sixth);
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

                List<List<String>>numbers=new ArrayList<>();
                try{
                    List<String> n1=getList(first);
                    List<String> n2=getList(second);
                    List<String> n3=getList(third);
                    List<String> n4=getList(fourth);
                    List<String> n5=getList(fifth);
                    List<String> n6=getList(sixth);

                    numbers.add(n1);
                    numbers.add(n2);
                    numbers.add(n3);
                    numbers.add(n4);
                    numbers.add(n5);
                    numbers.add(n6);
                    NumberGenerateTask task=new NumberGenerateTask(this);

                    task.execute(numbers);
//                    for(int x1=0;x1<n1.size();x1++){
//                        for(int x2=0;x2<n2.size();x2++){
//                            for(int x3=0;x3<n3.size();x3++){
//                                for(int x4=0;x4<n4.size();x4++){
//                                    for(int x5=0;x5<n5.size();x5++){
//                                        for(int x6=0;x6<n6.size();x6++){
//                                            String number1=n1.get(x1);
//                                            String number2=n2.get(x2);
//                                            String number3=n3.get(x3);
//                                            String number4=n4.get(x4);
//                                            String number5=n5.get(x5);
//                                            String number6=n6.get(x6);
//
//                                            List<String>numberList=new ArrayList<>();
//                                            numberList.add(number1);
//                                            numberList.add(number2);
//                                            numberList.add(number3);
//                                            numberList.add(number4);
//                                            numberList.add(number5);
//                                            numberList.add(number6);
//                                            HashSet<String>hs=new HashSet<>(numberList);
//                                            if(hs.size()<6){
//                                                continue;
//                                            }
//                                            String[]numberArray=(String[])numberList.toArray(new String[numberList.size()]);
//                                            String sign=Arrays.toString(numberArray);
//                                            if(!signs.contains(sign)){
//                                                numbers.add(numberList);
//                                            }
//                                            signs.add(sign);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private List<String> getList(EditText textbox) throws Exception {
        String str=textbox.getText().toString().trim();
        if(TextUtils.isEmpty(str)){
            throw new Exception("号码为空！");
        }
        String[] arr=str.split(" ");
        List<String>numbers=new ArrayList<String>();
        for(int i=0;i<arr.length;i++){
            String s=arr[i];
            s=s.trim();
            if(TextUtils.isEmpty(s))
                continue;

            //字符转整数
            Integer.valueOf(s);
            if(numbers.contains(s))
                throw new Exception(s+",号码重复！");

            numbers.add(s);
        }
        if(numbers.size()==0)
            throw new Exception("号码为空！");

        if(numbers.size()>9)
            throw new Exception("号码个数不能超过9个！");

        return numbers;
    }



    private class NumberGenerateTask extends AsyncTask<List<List<String>>, Integer,List<List<String>>> {
        ProgressDialog pdialog;
        private Context mContext;
        public NumberGenerateTask(Context mContext){
            this.mContext=mContext;
        }
        @Override
        protected void onPostExecute(List<List<String>> lists) {
            pdialog.dismiss();
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,NumberListActivity.class);
            intent.putExtra(Constant.NumberList, (Serializable)lists);
            startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
            pdialog = new ProgressDialog(mContext);
            pdialog.setTitle("正在生成号码组合...");
            pdialog.setMessage("任务正在执行中，请等待...");
            pdialog.setCancelable(false);
            pdialog.setMax(202);
            pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdialog.setIndeterminate(false);//设置对话框的进度条是否显示进度
            pdialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pdialog.setProgress(values[0]);
        }

        @Override
        protected List<List<String>> doInBackground(List<List<String>>... numberlists) {
            System.gc();
            List<List<String>> lists=numberlists[0];
            List<List<String>>numbers=new ArrayList<>();

            //记录号码的签名（简单转为字符串）
            List<String>signs=new ArrayList<>();
            int processCount=0;
            int total=lists.get(0).size()*lists.get(1).size()*lists.get(2).size()*lists.get(3).size()*lists.get(4).size()*lists.get(5).size();
            for(int x1=0;x1<lists.get(0).size();x1++){
                for(int x2=0;x2<lists.get(1).size();x2++){
                    for(int x3=0;x3<lists.get(2).size();x3++){
                        for(int x4=0;x4<lists.get(3).size();x4++){
                            for(int x5=0;x5<lists.get(4).size();x5++){
                                for(int x6=0;x6<lists.get(5).size();x6++){
                                    String number1=lists.get(0).get(x1);
                                    String number2=lists.get(1).get(x2);
                                    String number3=lists.get(2).get(x3);
                                    String number4=lists.get(3).get(x4);
                                    String number5=lists.get(4).get(x5);
                                    String number6=lists.get(5).get(x6);

                                    List<String>numberList=new ArrayList<>();
                                    numberList.add(number1);
                                    numberList.add(number2);
                                    numberList.add(number3);
                                    numberList.add(number4);
                                    numberList.add(number5);
                                    numberList.add(number6);
                                    HashSet<String>hs=new HashSet<>(numberList);
                                    if(hs.size()<6){
                                        continue;
                                    }
                                    String[]numberArray=(String[])numberList.toArray(new String[numberList.size()]);
                                    String sign=Arrays.toString(numberArray);
                                    if(!signs.contains(sign)){
                                        numbers.add(numberList);
                                    }
                                    signs.add(sign);
                                    processCount++;
                                    int percent=(int)(100*(double)processCount/total);
                                    publishProgress(percent);
                                }
                            }
                        }
                    }
                }
            }
            return numbers;
        }
    }
}
