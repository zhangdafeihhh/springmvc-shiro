package com.zhuanche.util.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * 导出的excel 需要添加额外的输出
 */
public class SupplierFeeCsvUtils {
    public static final Integer downPerSize = 10000;
	public static final String tab = "\t";
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

    public  boolean exportCsvV2(HttpServletResponse response,
                                    List<String> dataList,
                                    List<String> headdataList,
                                    String  fileName,boolean isFirst,boolean islast,List<String> footerList) throws IOException {

        boolean isSucess=false;
        OutputStreamWriter osw = this.getOsw();
        BufferedWriter bw = this.getBw();
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

                this.setOsw(osw);
            }
            if(bw == null){
                bw = new BufferedWriter(osw);
                this.setBw(bw);
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
            if(footerList != null && !footerList.isEmpty()){
                for(String data : footerList){
                    bw.write(data+"\n");
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
