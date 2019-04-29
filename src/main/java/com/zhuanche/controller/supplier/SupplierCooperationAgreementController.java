package com.zhuanche.controller.supplier;

import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.enums.FileSizeEnum;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.serv.supplier.SupplierCooperationAgreementService;
import com.zhuanche.util.FileUploadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.zhuanche.common.enums.MenuEnum.*;

@RequestMapping("/supplierCooperationAgreement")
@Controller
public class SupplierCooperationAgreementController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierCooperationAgreementController.class);

    @Resource
    private SupplierCooperationAgreementService supplierCooperationAgreementService;

    /**
     * 根据供应商查询所有申请修改信息
     * @param files
     * @return
     */
    @ResponseBody
    @RequestMapping("/fileUploadAgreement")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = SUPPLIER_UPDATE)
    public AjaxResponse fileUploadAgreement(@RequestParam(value = "files",required = false) MultipartFile[] files){

        List<Map<String, Object>> list = Lists.newArrayList();

        if (files == null || files.length==0) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "files参数未上传");
        }
        //单个文件长度大小不能超过5m
        if (!FileUploadUtils.validateFilesSize(files, FileSizeEnum.MByte.getSize() * 5)){
            return AjaxResponse.fail(RestErrorCode.FILE_SIZE_TOO_BIG);
        }
        //上传附件
        for (MultipartFile file : files) {
            if (null == file || file.isEmpty()) {
                continue;
            }
            Map<String, Object> map = supplierCooperationAgreementService.fileUpload(file);
            logger.info("供应商合作协议上传附件result={}", map);
            list.add(map);
        }

        return AjaxResponse.success(list);
    }
}