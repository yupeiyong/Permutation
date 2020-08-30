package com.ypy.permutation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ypy.filelib.FileUtils;
import com.ypy.filelib.OpenDirecroty;
import com.ypy.filelib.OpenFileUtils;
import com.ypy.filelib.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NumberListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NumberListActivity";
    private ListView numberListView;

    //界面上用于显示的数据，当筛选后此集合会改变
    private List<List<Integer>>numbers;

    //显示条数
    private TextView txtTotal;
    //原始数据
    private List<List<Integer>> originalNumbers;

    private String mFileName;

    //导出pdf的文件夹
    private String mPDFPath="download/files";
    private String mFileType="application/pdf";

    //奇数最小和最大数量
    private int minCount=1;
    private int maxCount=5;
    private EditText etOddCount;

    //数据适配器
    private NumberAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_list);

        setCustomActionBar();
        txtTotal=findViewById(R.id.txt_number_total);
        numbers=(List<List<Integer>>)ModelStorage.getInstance().getModel(Constant.NumberListKey);
        originalNumbers=new ArrayList<>(numbers);
        numberListView=findViewById(R.id.lv_number);
        adapter=new NumberAdapter(numbers);
        numberListView.setAdapter(adapter);
        txtTotal.setText("共有"+ String.valueOf(numbers.size())+"条数据");

        Button btnExport=findViewById(R.id.btn_export);
        btnExport.setOnClickListener(this);

        Button btnSearch=findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        etOddCount=findViewById(R.id.et_odd_Count);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.titleBar_back:
                this.finish();
                break;
            case R.id.btn_export:
                createPDF();
                break;
            case R.id.btn_search:
                queryNumbers();
                break;
        }
    }

    //筛选号码
    private void queryNumbers(){
        String str=etOddCount.getText().toString();

        try{
            //条件是否为空
            boolean isEmpty=false;
            int count=0;
            if(TextUtils.isEmpty(str)){
                isEmpty=true;
            }else{
                if(!StringUtils.isNumeric(str))
                    throw new Exception("请输入整数！");

                count=Integer.valueOf(str);
                if(count<minCount||count>maxCount)
                    throw new Exception("奇数数量范围1-5！");
            }

            //先清除旧数据
            numbers.clear();
            for(int i=0;i<originalNumbers.size();i++){
                List<Integer>row=originalNumbers.get(i);
                if(isEmpty){
                    //条件为空直接写入集合
                    numbers.add(row);
                }else{
                    int oCount=0;
                    for(int c=0;c<row.size();c++){
                        oCount+=row.get(c) %2;
                    }
                    if(oCount==count){
                        numbers.add(row);
                    }
                }
            }
            txtTotal.setText("共有"+ String.valueOf(numbers.size())+"条数据");
            adapter.notifyDataSetChanged();
        }catch (Exception ex){
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
            etOddCount.setFocusable(true);
            return;
        }
    }

    //导出pdf
    public void createPDF()
    {
        try {
            // 储存下载文件的目录
            String savePath = isExistDir(mPDFPath);
            mFileName = "number.pdf";
            File file = new File(savePath, mFileName);
            if (file.exists()) {
                file.delete();
            }

            List<String>headers=new ArrayList<>();
            headers.add("序号");
            headers.add("第1");
            headers.add("第2");
            headers.add("第3");
            headers.add("第4");
            headers.add("第5");
            headers.add("第6");
            ToPdfTask task=new ToPdfTask(this,file.getPath(),headers,numbers.size());
            task.execute(numbers);
        }
        catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory().getPath() , saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        Log.w(TAG,"下载目录："+savePath);
        return savePath;
    }

    class ToPdfTask extends AsyncTask<List<List<Integer>>, Integer, String> {
        private Context _mContext;
        private ProgressDialog progressDialog;
        private List<String>_headers;
        private String _pdfFilePath;
        private int total;
        public ToPdfTask(Context mContext,String pdfTargetPath, List<String>headers,int total){
            _mContext=mContext;
            _headers=headers;
            _pdfFilePath=pdfTargetPath;
            this.total=total;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Toast.makeText(_mContext,result+"，文件位置：Download/files文件夹。",Toast.LENGTH_LONG).show();
            File file=new File(_pdfFilePath);
            //file=new File(file.getParent());
            try{
                //打开导出文件所在文件夹
                OpenFileUtils.openFile(_mContext, file);
//                OpenDirecroty.open(_mContext,file,mFileType);
            }catch (Exception ex){
                Toast.makeText(_mContext,ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(_mContext);
            progressDialog.setTitle("正在导出Pdf...");
            progressDialog.setMessage("正在写入数据，请等待...");
            progressDialog.setCancelable(false);
            progressDialog.setMax(total);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);//设置对话框的进度条是否显示进度
            progressDialog.show();
        }

        @Override
        protected String doInBackground(List<List<Integer>>... lists) {
            String result="";
            try{
                ITextPdfHelper.createTablePdf(_pdfFilePath, _headers, lists[0], new ITextPdfHelper.IToPdfProcess() {
                    @Override
                    public void updateCount(int count) {
                        progressDialog.setProgress(count);
                    }

                    @Override
                    public void writeCompleted() {
                        progressDialog.setMessage("正在生成PDF文件，请等待...");
                    }
                });
                result="导出成功！";
            }catch (Exception ex){
                result="导出错误："+ex.getMessage();
            }

            return result;
        }
    }
}
