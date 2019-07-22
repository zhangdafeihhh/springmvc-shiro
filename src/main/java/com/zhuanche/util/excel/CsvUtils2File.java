package com.zhuanche.util.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class CsvUtils2File {
    /**
          * 读取 
          * @param file csv文件(路径+文件名)，csv文件不存在会自动创建
          * @param dataList 数据
          * @return
          */
    public static boolean exportCsv(File file, List<String> dataList) {
        boolean isSucess = false;
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()){
                String dirFile = file.getParent();
                File fileDir = new File(dirFile);
                if(!fileDir.exists()){
                    fileDir.mkdirs();
                }
                file.createNewFile();
            }
           out = new FileOutputStream(file);
           osw = new OutputStreamWriter(out);
           bw = new BufferedWriter(osw);
           if (dataList != null && !dataList.isEmpty()) {
               for (String data : dataList) {
                   bw.append(data).append("\r");
               }
           }
           isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                    try {
                        out.close();
                        out = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return isSucess;
    }




}
