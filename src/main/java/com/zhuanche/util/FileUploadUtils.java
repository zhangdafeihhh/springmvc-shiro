package com.zhuanche.util;

import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class FileUploadUtils {

    public static boolean validateFileSize(MultipartFile multipartFile, long maxSize) throws Exception{
        if (null == multipartFile){
            return true;
        }
        if (maxSize < multipartFile.getSize()){
            return false;
        }
        return true;
    }

    public static boolean validateFilesSize(MultipartFile[] multipartFiles,long maxSize){
        for (MultipartFile multipartFile : multipartFiles) {
            if (null == multipartFile){
                continue;
            }
            if (maxSize < multipartFile.getSize()){
                return false;
            }
        }
        return true;
    }

    public static Map<String, Object> fileUpload(MultipartFile file) throws Exception {
        Map<String, Object> map = Maps.newHashMap();
        if (null == file || file.isEmpty()) {
            return null;
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String timeStamp = System.currentTimeMillis() + "";
        // 文件存放服务端的位置
        String rootPath = getRemoteFileDir();
        File filePath = new File(rootPath);

        if (!filePath.exists())
            filePath.mkdirs();
        // 写文件到服务器
        String absoluteUrl = rootPath + uuid + "_" + timeStamp + "." + extension;
        File serverFile = new File(absoluteUrl);
        file.transferTo(serverFile);
        map.put("ok", true);
        map.put("fileUrl", absoluteUrl);
        map.put("fileName", file.getOriginalFilename());
        return map;
    }


    private static String getRemoteFileDir() {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator)
                .append("u01")
                .append(File.separator)
                .append("upload")
                .append(File.separator)
                .append("message")
                .append(File.separator)
                .append(now.get(Calendar.YEAR))
                .append(File.separator)
                //now.get(Calendar.MONTH)获取的月份比当前少一个月
                .append(now.get(Calendar.MONTH) + 1)
                .append(File.separator);
        return sb.toString();
    }

}
