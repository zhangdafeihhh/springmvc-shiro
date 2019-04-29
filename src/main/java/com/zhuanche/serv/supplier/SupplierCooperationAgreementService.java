package com.zhuanche.serv.supplier;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.driver.SupplierCooperationAgreement;
import mapper.driver.ex.SupplierCooperationAgreementExMapper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SupplierCooperationAgreementService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierCooperationAgreementService.class);

    @Autowired
    private SupplierCooperationAgreementExMapper supplierCooperationAgreementExMapper;

    /**
     * 根据供应商ID查询最新一条合作协议
     * @param supplierId
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public SupplierCooperationAgreement selectLastAgreementBySupplierId(Integer supplierId){
        //根据供应商ID查询最新一条合作协议
        return supplierCooperationAgreementExMapper.selectLastAgreementBySupplierId(supplierId);
    }










    /**
     * 文件服务器上传
     * @param file
     * @return
     */
    public Map<String,Object> fileUpload(MultipartFile file){
        Map<String,Object> map = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String uuid = UUID.randomUUID().toString();
                String timeStamp = System.currentTimeMillis() + "";
                // 文件存放服务端的位置
                String rootPath = this.getRemoteFileDir();
                File filePath = new File(rootPath);

                logger.info("文件路径:" +rootPath);
                if (!filePath.exists())
                    filePath.mkdirs();
                // 写文件到服务器
                String  absoluteUrl = rootPath + uuid + "_" + timeStamp + "." + extension;
                File serverFile = new File(absoluteUrl);
                file.transferTo(serverFile);
                logger.info("消息附件上传地址：" + absoluteUrl);
                map.put("ok",true);
                map.put("url",absoluteUrl);
                map.put("fileName",file.getOriginalFilename());
            } catch (Exception e) {
                logger.info("文件上传失败");
                map.put("ok",false);
                map.put("url","");
                map.put("fileName","");
            }
        } else {
            logger.info("文件上传失败");
            map.put("ok",false);
            map.put("url","");
            map.put("fileName","");
        }
        return map;
    }

    private String getRemoteFileDir() {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator).append("u01").append(File.separator).append("upload").append(File.separator).append("message")
                .append(File.separator).append(now.get(Calendar.YEAR)).append(File.separator).append(now.get(Calendar.MONTH))
                .append(File.separator);
        return sb.toString();
    }

}