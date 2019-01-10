package com.zhuanche.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.DriverTelescopeUser;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.*;
import com.zhuanche.serv.authc.UserManagementService;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverUpdateService;
import com.zhuanche.serv.mongo.DriverMongoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Md5Util;
import com.zhuanche.util.PasswordUtil;
import com.zhuanche.util.excel.CsvUtils;
import mapper.mdbcarmanage.*;
import mapper.mdbcarmanage.ex.*;
import mapper.rentcar.CarBizDriverAccountMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    private CarBizDriverInfoMapper carBizDriverInfoMapper;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

    @Autowired
    private CarBizSupplierService carBizSupplierService;

    @Autowired
    private CarBizCarGroupService carBizCarGroupService;

    @Autowired
    private CarDriverTeamMapper carDriverTeamMapper;

    @Autowired
    private CarDriverTeamExMapper carDriverTeamExMapper;

    @Autowired
    private CarRelateTeamMapper carRelateTeamMapper;

    @Autowired
    private CarRelateTeamExMapper carRelateTeamExMapper;

    @Autowired
    private CarRelateGroupMapper carRelateGroupMapper;

    @Autowired
    private CarRelateGroupExMapper carRelateGroupExMapper;

    @Autowired
    private DriverMongoService driverMongoService;

    @Autowired
    private CarDriverTeamService carDriverTeamService;

    @Value("${telescope.supplierId}")
    private Integer telescopeSupplierId ;

    @Autowired
    private DriverTelescopeUserMapper driverTelescopeUserMapper;

    @Autowired
    private DriverTelescopeUserExMapper driverTelescopeUserExMapper;

    @Autowired
    private CarAdmUserMapper carAdmUserMapper;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Value("${user.password.indexOfPhone}")
    private String indexOfPhone; //用户初始密码取自手机号第多少位

    @Value("${transfer.user.fileAddr}")
    private String fileAddr; //文件地址

    private static String ACTION="【千里眼信息导入】";

    @ResponseBody
    @RequestMapping(value = "/changeSupplier", method = { RequestMethod.POST,RequestMethod.GET })
    public void SupplierUser(HttpServletRequest request, HttpServletResponse response)  {
        logger.info(ACTION+"*******************start!**********************");
        changeSupplierDeal(request,response);
        logger.info(ACTION+"********************end!*******************");
    }

    public void changeSupplierDeal(HttpServletRequest request, HttpServletResponse response) {
        //1、查询北京千里眼临时机构下的所有司机
        CarBizDriverInfoDTO param = new CarBizDriverInfoDTO();
        param.setSupplierId(telescopeSupplierId);
        List<CarBizDriverInfoDTO> carBizDriverInfoDTOList = carBizDriverInfoExMapper.queryCarBizDriverList(param);
        //2、修改司机供应商信息（DB+MONGO）
        for(CarBizDriverInfoDTO  carBizDriverInfoDTO : carBizDriverInfoDTOList){
            logger.info(ACTION+"开始操作司机phone={},userName={}",carBizDriverInfoDTO.getPhone(),carBizDriverInfoDTO.getName());
            List<CarAdmUser> users = carAdmUserExMapper.queryUsers( null ,  null, null, carBizDriverInfoDTO.getPhone(), null );
            CarAdmUser user = new CarAdmUser();
            if(null==users || users.size()==0){
                logger.info(ACTION+"根据手机号查询当前用户不存在saas账号需要创建新账号phone={},userName={}",carBizDriverInfoDTO.getPhone(),carBizDriverInfoDTO.getName());
                continue;
            }else{
                user = users.get(0);
            }
            if(StringUtils.isEmpty(user.getCities())){
                logger.info(ACTION+"根据手机号查询当前用户没有城市信息phone={},userName={}",carBizDriverInfoDTO.getPhone(),carBizDriverInfoDTO.getName());
                continue;
            }
            CarBizSupplier carBizSupplierParam = new CarBizSupplier();
            carBizSupplierParam.setSupplierNum("qianliyan");
            carBizSupplierParam.setSupplierCity(Integer.valueOf(user.getCities().split(",")[0]));
            CarBizSupplier carBizSupplier = carBizSupplierService.queryQianLiYanSupplierByCityId(carBizSupplierParam);
            logger.info(ACTION+"该城市下的供应商信息supplierId={},supplierFullName={},supplierCity={}",carBizSupplier.getSupplierId(),carBizSupplier.getSupplierFullName(),carBizSupplier.getSupplierCity());
            carBizDriverInfoDTO.setServiceCity(carBizSupplier.getSupplierCity());
            carBizDriverInfoDTO.setSupplierId(carBizSupplier.getSupplierId());
            carBizDriverInfoExMapper.updateCarBizDriverInfoDTO(carBizDriverInfoDTO);
            // 更新mongoDB
            DriverMongo driverMongo = driverMongoService.findByDriverId(carBizDriverInfoDTO.getDriverId());
            if (driverMongo != null) {
                driverMongoService.updateDriverMongo(carBizDriverInfoDTO);
            } else {
                driverMongoService.saveDriverMongo(carBizDriverInfoDTO);
            }
        }
    }
}