package com.zhuanche.util.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvUtils {
    private OutputStreamWriter osw;
    private BufferedWriter bw = null;

    public OutputStreamWriter getOsw() {
        return osw;
    }

    public void setOsw(OutputStreamWriter osw) {
        this.osw = osw;
    }

    public BufferedWriter getBw() {
        return bw;
    }

    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }

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

    public static boolean exportCsvV2(HttpServletResponse response,
                                    List<String> dataList,
                                    List<String> headdataList,
                                    String  fileName,boolean isFirst,boolean islast,CsvUtils entity) throws IOException {

        boolean isSucess=false;
        OutputStreamWriter osw = entity.getOsw();
        BufferedWriter bw = entity.getBw();
        try {

            if(isFirst){
                response.reset();
                //设置response
                response.setContentType("application/octet-stream;charset=UTF-8");
                response.setHeader("content-disposition", "attachment; filename="+fileName);
            }

            if(osw == null){
                osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
                osw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));

                entity.setOsw(osw);
            }
            if(bw == null){
                bw = new BufferedWriter(osw);
                entity.setBw(bw);
            }


            if(isFirst){
                if(headdataList!=null && !headdataList.isEmpty()){
                    for(String data : headdataList){
                        bw.write(data+"\r\n");
                    }
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
            if(islast){
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
            }else{
                if(bw!=null){
                    try {
                        bw.flush();
                        bw=null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(osw!=null){
                    try {
                        osw.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return isSucess;
    }



}
