package com.zhuanche.util.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvUtils {


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
            //设置response 
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("content-disposition", "attachment; filename="+fileName);
            osw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF })); 
            bw =new BufferedWriter(osw);

            if(headdataList!=null && !headdataList.isEmpty()){
                for(String data : headdataList){
                    bw.write(data+"\r\n");
                }
            }
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                    bw.write(data+"\r\n");
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
