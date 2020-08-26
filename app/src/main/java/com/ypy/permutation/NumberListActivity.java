package com.ypy.permutation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NumberListActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView numberListView;
    private List<List<Integer>>numbers;
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
            String path = Environment.getExternalStorageDirectory() + "/numberList";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "number.pdf");
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

    public void openAssignFolder(String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(getUriForFile(this,file), "file/*");
        try {
            startActivity(intent);
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;

        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Toast.makeText(_mContext,result,Toast.LENGTH_LONG).show();
            File file=new File(_pdfFilePath);
            try{
                //打开导出文件所在文件夹
                openAssignFolder(file.getPath());
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
    //原生导出pdf方法
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void createPDF()
//    {
//        try {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/numberList";
//
//            File dir = new File(path);
//            if(!dir.exists())
//                dir.mkdirs();
//
//            File file = new File(dir, "number.pdf");
//            //file.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(file);
//
//            // create a new document
//            PdfDocument document = new PdfDocument();
//
//
//            // crate a page description
////            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(numberListView.getWidth(), numberListView.getHeight()*10, 10).create();
////
////            // start a page
////            PdfDocument.Page page = document.startPage(pageInfo);
//
//            // draw something on the page
//            View content = numberListView;
//
//            for (int i = 0; i < 10; i++) {
//                int webMarginTop = i * numberListView.getHeight();
//
//                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(numberListView.getWidth(), numberListView.getHeight(), i+1).create();
//
//                // start a page
//                PdfDocument.Page page = document.startPage(pageInfo);
//                page.getCanvas().translate(0, -webMarginTop);
//                content.draw(page.getCanvas());
//
//                document.finishPage(page);
//            }
//
//            //content.draw(page.getCanvas());
//
//            // finish the page
//            //document.finishPage(page);
//
//            // write the document content
//            document.writeTo(fOut);
//
//            //close the document
//            document.close();
//            Toast.makeText(this, "导出为pdf成功！", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        finally
//        {
//            //doc.close();
//        }
//    }
}
