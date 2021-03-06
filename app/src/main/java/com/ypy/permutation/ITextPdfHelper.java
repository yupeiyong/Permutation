package com.ypy.permutation;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ITextPdfHelper {
    /**
     * 表格各种属性综合使用
     *
     * @throws IOException
     * @throws DocumentException
     */
    public static void createTablePdf(String filePath, List<String>headers, List<List<Integer>>numberList, IToPdfProcess process) throws IOException, DocumentException {
        Document document = new Document();
        // 创建PdfWriter对象
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        // 打开文档
        document.open();

        // 添加表格，7列
        PdfPTable table = new PdfPTable(7);
        //设置表格宽度比例为%100
        //table.setWidthPercentage(100);
        // 设置表格的宽度
        //table.setTotalWidth(650);
        // 也可以每列分别设置宽度
        table.setTotalWidth(new float[] { 150, 80, 80, 80,80,80,80 });
        // 锁住宽度
        //table.setLockedWidth(true);
        // 设置表格上面空白宽度
        table.setSpacingBefore(10f);
        // 设置表格下面空白宽度
        table.setSpacingAfter(10f);

        //生成表头
        BaseColor headerBackgroudColor=BaseColor.LIGHT_GRAY;
        Font chineseFont=setChineseFont();
        for(int i=0;i<headers.size();i++){
            table.addCell(generateTextCell(headers.get(i),null,headerBackgroudColor,chineseFont));
        }
//        // 设置表格默认为无边框
//        table.getDefaultCell().setBorder(0);
        PdfContentByte cb = writer.getDirectContent();

        for(int r=0;r<numberList.size();r++){
            //行号
            int rowIndex=r+1;
            BaseColor color=null;
            if(rowIndex%5==0){
                color=BaseColor.RED;
            }
            table.addCell(generateTextCell(String.valueOf(rowIndex),null,color,null));

            List<Integer>numbers=numberList.get(r);
            for(int c=0;c<6;c++){
                table.addCell(generateTextCell(String.valueOf(numbers.get(c)),null,color,null));
            }
            if(process!=null){
                process.updateCount(rowIndex);
            }
        }

        if(process!=null){
            process.writeCompleted();
        }
        document.add(table);

        // 关闭文档
        document.close();
    }

    //生成单元格
    private static PdfPCell generateTextCell(String cellValue,BaseColor borderColor,BaseColor backgroundColor,Font font){
        PdfPCell cell =font==null? new PdfPCell(new Paragraph(cellValue)):new PdfPCell(new Paragraph(cellValue,font));
        cell.setPaddingLeft(10);

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        if(borderColor!=null){
            cell.setBorderColor(borderColor);
        }
        if(backgroundColor!=null){
            cell.setBackgroundColor(backgroundColor);
        }
        return cell;
    }


    private static Font setChineseFont() {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            // STSong-Light : Adobe的字体
            // UniGB-UCS2-H : pdf 字体
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, 12, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }

    public interface IToPdfProcess{
        //导入过程中的条数
        public void updateCount(int count);
        //写入完成，document.add(table);之前
        public void writeCompleted();
    }
}
