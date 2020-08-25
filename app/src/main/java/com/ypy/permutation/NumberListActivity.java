package com.ypy.permutation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/numberList";

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
            ITextPdfHelper.createTablePdf(file.getPath(),headers,numbers);
            Toast.makeText(this, "导出为pdf成功！", Toast.LENGTH_SHORT).show();
        } catch (IOException | DocumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally
        {
            //doc.close();
        }
    }

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
