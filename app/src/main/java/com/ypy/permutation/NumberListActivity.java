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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ypy.permutation.utils.FileUtils;
import com.ypy.permutation.utils.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NumberListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NumberListActivity";
    private ListView numberListView;
    private List<List<Integer>>numbers;

    private String mFilePath;
    private String mFileName;
    private String mContentType = "application/pdf";//文件类型：pdf
    private String mPDFPath;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_list);

        setCustomActionBar();
        //List<List<String>>numbers=  (List<List<String>>) getIntent().getParcelableExtra(Constant.NumberList);
        TextView txtTotal=findViewById(R.id.txt_number_total);
        numbers=(List<List<Integer>>)ModelStorage.getInstance().getModel(Constant.NumberListKey);
        numberListView=findViewById(R.id.lv_number);
        NumberAdapter adapter=new NumberAdapter(numbers);
        numberListView.setAdapter(adapter);
        txtTotal.setText("共有"+ String.valueOf(numbers.size())+"条数据");

        Button btnExport=findViewById(R.id.btn_export);
        btnExport.setOnClickListener(this);

        FileUtils.init();
        mFilePath = FileUtils.getFileDir() + File.separator;
        mPDFPath = FileUtils.getFileDir() + File.separator + "numbers.pdf";
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
        }
    }

    public void createPDF()
    {
        try {
            //文件在手机内存存储的路径
//            final String saveurl="/download/";
            final String saveurl="/numberList/files/";
            // 储存下载文件的目录
            String savePath = isExistDir(saveurl);
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
//        File downloadFile = new File(Environment.getExternalStorageDirectory().getPath() + "/download/", saveDir);
        File downloadFile = new File(Environment.getExternalStorageDirectory().getPath() , saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        Log.w(TAG,"下载目录："+savePath);
        return savePath;
    }

//    private Uri getUriForFile(Context context, File file) {
//        if (context == null || file == null) {
//            throw new NullPointerException();
//        }
//        Uri uri;
//        if (Build.VERSION.SDK_INT >= 24) {
//            uri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID +".fileprovider", file);
//            uri=Uri.parse("file:"+file.getParent());
//        } else {
//            uri = Uri.fromFile(file);
//        }
//        return uri;
//
////        if (!file.getParentFile().exists()) {
////            file.getParentFile().mkdirs();
////        }
////        Uri imageUri;
////        String path = file.getPath();
////        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
////            imageUri = Uri.fromFile(file);
////        } else {
////            //兼容android7.0 使用共享文件的形式
////            ContentValues contentValues = new ContentValues(1);
////            contentValues.put(MediaStore.Images.Media.DATA, path);
////            imageUri = this.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
////        }
////        return imageUri;
//    }
//
//    public void openAssignFolder(String path){
//        File file = new File(path);
//        if(null==file || !file.exists()){
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setDataAndType(getUriForFile(this,file), "file/*");
//        try {
////            startActivity(intent);
//            startActivity(Intent.createChooser(intent,"打开文件夹"));
////            startActivity(Intent.createChooser(intent,"选择浏览工具"));
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

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
            Toast.makeText(_mContext,result,Toast.LENGTH_LONG).show();
            File file=new File(_pdfFilePath);
            try{
                //打开导出文件所在文件夹
                //openAssignFolder(file.getPath());
                FileUtils.startActionFile(getApplicationContext(),file,mContentType);
                //OpenFileUtils.openFile(_mContext, file);
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
