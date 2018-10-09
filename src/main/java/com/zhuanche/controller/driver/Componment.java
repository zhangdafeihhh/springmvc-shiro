package com.zhuanche.controller.driver;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class Componment {

    private static final Logger logger = LoggerFactory.getLogger(Componment.class);

    /**
     *  下载
     * @param response
     * @param wb
     * @param fileName
     */
    public static void fileDownload(HttpServletResponse response, Workbook wb, String fileName) {
        OutputStream outputStream = null;
        try {
            outputStream= response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            wb.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
           logger.error("Componment.fileDownload error fileName={}",fileName,e);
        }finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    logger.error("Componment.fileDownload wb colse error fileName={}",fileName,e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("Componment.fileDownload outputStream colse  error fileName={}",fileName,e);
                }
            }
        }
    }

}
