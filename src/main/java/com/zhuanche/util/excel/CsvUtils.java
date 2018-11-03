package com.zhuanche.util.excel;

import com.csvreader.CsvWriter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

public class CsvUtils {
    /**
     * 导出
     * @param dataList 数据
     * @param headdataList 逗号分隔的标题数据
     * @return
     */
//    public static boolean exportCsv(HttpServletResponse response,
//                                    List<String> dataList,
//                                    String headdataList,
//                                    String  fileName) throws IOException {
//
//        response.setContentType("application/csv");
//        response.setHeader("content-disposition", "attachment; filename="+fileName);
//
//        CsvWriter csvWriter = new CsvWriter(response.getOutputStream(),',', Charset.forName("UTF-8"));
//        // 写表头
//        long s= System.currentTimeMillis();
//        System.err.println();
//        String[] headers = {"姓名","年龄","编号","性别"};
//        csvWriter.writeRecord(headers);
//        csvWriter.writeRecord(headers);
////        for (Student stu : ls) {
////            csvWriter.write(stu.getName());
////            csvWriter.write(stu.getAge()+"");
////            csvWriter.write(stu.getScore());
////            csvWriter.write(stu.getSex());
////            csvWriter.endRecord();
////        }
////        csvWriter.close();
//
//        return true;
//    }

    public static boolean exportCsv(HttpServletResponse response,
                                    List<String> dataList,
                                    List<String> headdataList,
                                    String  fileName) throws IOException {
        OutputStreamWriter osw = null;
        boolean isSucess=false;
        BufferedWriter bw=null;
        try {
            osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
            response.reset();
            response.setContentType("multipart/form-data");
            response.setHeader("content-disposition", "attachment; filename="+fileName);
            osw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF })); 
            bw =new BufferedWriter(osw);

            if(headdataList!=null && !headdataList.isEmpty()){
                for(String data : headdataList){
                    bw.write(data+"\r");
                }
            }
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                    bw.write(data+"\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSucess;
    }
}
