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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

    private static String ACTION="【千里眼信息导入】";

    @Autowired
    private CarAdmUserMapper carAdmUserMapper;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Value("${user.password.indexOfPhone}")
    private String indexOfPhone; //用户初始密码取自手机号第多少位

    @Value("${transfer.user.fileAddr}")
    private String fileAddr; //用户初始密码取自手机号第多少位


    @ResponseBody
    @RequestMapping(value = "/transferUser", method = { RequestMethod.POST,RequestMethod.GET })
    public void transferUser(HttpServletRequest request, HttpServletResponse response)  {
        logger.info(ACTION+"*******************start!**********************");
        operate();
        logger.info(ACTION+"********************end!*******************");
    }

    public void operate() {
        //获取Excel对象
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(new FileInputStream("E:/shouqi/20181125_test.xlsx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取一个Sheet对象
        XSSFSheet sheet = wb.getSheetAt(0);
        for(int i =1 ; i<sheet.getLastRowNum();i++){
            try{
                XSSFRow row = sheet.getRow(i);
                String phone = row.getCell(1).getStringCellValue();
                String userName = row.getCell(2).getStringCellValue();
                String city = row.getCell(3).getStringCellValue();
                String supplier = row.getCell(4).getStringCellValue();
                String team = row.getCell(5).getStringCellValue();
                String group = row.getCell(6).getStringCellValue();
                logger.info(ACTION+"开始执行导入记录信息phone={},userName={}",phone,userName);
                CarAdmUser user = new CarAdmUser();
                user.setPhone(phone);
                user.setUserName(userName);
                user.setCities(city.replaceAll("\\[","").replaceAll("\\]",""));
                user.setSuppliers(supplier.replaceAll("\\[","").replaceAll("\\]",""));
                user.setTeamId(team.replaceAll("\\[","").replaceAll("\\]",""));
                user.setGroupIds(group.replaceAll("\\[","").replaceAll("\\]",""));
                user.setAccount(phone);
                String initPassword = null;
                String indexOfPhone="9,5,3,7,1,4,8,2";
                if(indexOfPhone!=null && indexOfPhone.length()>0) {
                    List<Integer> indexes = Stream.of(indexOfPhone.split(",")).mapToInt(s -> Integer.parseInt(s) ).boxed().collect(Collectors.toList());
                    StringBuffer password = new StringBuffer();
                    for( Integer index : indexes ) {
                        password.append(user.getPhone().charAt((index)));
                    }
                    initPassword = password.toString();
                }else {
                    initPassword = SaasConst.INITIAL_PASSWORD;
                }
                user.setPassword( PasswordUtil.md5( initPassword, user.getAccount())  );
                user.setRoleId(0);
                user.setAccountType(100);
                user.setStatus(200);
                //根据手机号查询当前用户是不是已经有saas账号了(如果不存在创建新账号)比较权限是否相同如果不同则输出日志
                List<CarAdmUser> users = carAdmUserExMapper.queryUsers( null ,  null, null, phone, null );
                if(null==users || users.size()==0){
                    logger.info(ACTION+"根据手机号查询当前用户不存在saas账号需要创建新账号phone={},userName={}",phone,userName);
                    addUser(user);
                }else{
                    logger.info(ACTION+"用户已存在SAAS账号phone={},userName={}",phone,userName);
                    user = users.get(0);
                }
                CarBizDriverInfoDTO driverInfoDTO = carBizDriverInfoExMapper.selectByPhone(phone);
                if(driverInfoDTO==null){
                    //根据手机号查询是不是已经有司机账号了(如果不存在创建新账号)
                    logger.info(ACTION+"根据手机号查询不存在司机账号需要创建phone={},userName={}",phone,userName);
                    CarBizDriverInfo carBizDriverInfo = addDriver(user);
                    logger.info(ACTION+"创建司机账号成功phone={},userName={},driverId={}",phone,userName,carBizDriverInfo.getDriverId());
                }else{
                    logger.info(ACTION+"根据手机号查询存在司机账号phone={},userName={},driverId={}",phone,userName,driverInfoDTO.getDriverId());
                }
                logger.info(ACTION+"开始操作关联关系phone={},userName={}",phone,userName);
                oprateRelation(user,driverInfoDTO);
                logger.info(ACTION+"导入记录信息phone={},userName={}结束",phone,userName);
            }catch(Exception e){
               logger.error(ACTION+"导入记录异常：",e);
               continue;
            }

        }
    }

    /**创建SAAS账号权限**/
    public CarAdmUser addUser( CarAdmUser user ) {
        user.setUserId(null);
        if( StringUtils.isEmpty(user.getUserName()) ) {
            user.setUserName("");
        }
        //用户初始密码
        String initPassword = null;
        if(indexOfPhone!=null && indexOfPhone.length()>0) {
            List<Integer> indexes = Stream.of(indexOfPhone.split(",")).mapToInt( s -> Integer.parseInt(s) ).boxed().collect(Collectors.toList());
            StringBuffer password = new StringBuffer();
            for( Integer index : indexes ) {
                password.append(user.getPhone().charAt((index)));
            }
            initPassword = password.toString();
        }else {
            initPassword = SaasConst.INITIAL_PASSWORD;
        }
        user.setPassword( PasswordUtil.md5( initPassword, user.getAccount())  );
        user.setRoleId(0);
        user.setAccountType(100);
        user.setStatus(200);
        SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
        user.setRemark( ssoLoginUser.getLoginName() );
        user.setCreateUser( ssoLoginUser.getName()  );
        user.setCreateDate(new Date());
        if( StringUtils.isEmpty(user.getCities()) ) {
            user.setCities("");
        }
        if( StringUtils.isEmpty(user.getSuppliers()) ) {
            user.setSuppliers("");
        }
        if( StringUtils.isEmpty(user.getTeamId()) ) {
            user.setTeamId("");
        }
        //保存
        carAdmUserMapper.insertSelective(user);

        //短信通知
        String text = user.getUserName() + "，您好！已为您成功开通“首约加盟商服务平台”管理员账号。登录账号为："+user.getAccount()+"，初始密码为："+initPassword+"（为保障账户安全，请您登录后进行密码修改）";
        SmsSendUtil.send( user.getPhone() , text);

        return user;
    }



    /**创建司机账号**/
    public CarBizDriverInfo addDriver( CarAdmUser user ) {
        String initPwd = String.valueOf((int)((Math.random()*9+1)*100000));
        CarBizDriverInfo carBizDriverInfo = new CarBizDriverInfo();
        CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(telescopeSupplierId);
        carBizDriverInfo.setServiceCity(carBizSupplier.getSupplierCity());
        carBizDriverInfo.setSupplierId(carBizSupplier.getSupplierId());
        carBizDriverInfo.setCooperationType(Byte.valueOf(carBizSupplier.getCooperationType().toString()));
        carBizDriverInfo.setPhone(user.getPhone());
        carBizDriverInfo.setName(user.getUserName());
        carBizDriverInfo.setStatus(1);
        carBizDriverInfo.setPassword(Md5Util.md5(initPwd));
        carBizDriverInfo.setGroupId(34);
        carBizDriverInfo.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
        carBizDriverInfo.setCreateDate(new Date());
        carBizDriverInfo.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
        carBizDriverInfo.setUpdateDate(new Date());
        int insertResult = carBizDriverInfoMapper.insertSelective(carBizDriverInfo);
        if(insertResult>0){
            CarBizDriverInfoDTO driverInfoDTO = new CarBizDriverInfoDTO();
            driverInfoDTO.setDriverId(carBizDriverInfo.getDriverId());
            driverInfoDTO.setServiceCity(carBizSupplier.getSupplierCity());
            driverInfoDTO.setSupplierId(carBizSupplier.getSupplierId());
            driverInfoDTO.setPhone(user.getPhone());
            driverInfoDTO.setName(user.getUserName());
            driverInfoDTO.setStatus(1);
            driverInfoDTO.setPassword(Md5Util.md5(initPwd));
            driverInfoDTO.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
            driverInfoDTO.setCreateDate(new Date());
            driverInfoDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            driverInfoDTO.setUpdateDate(new Date());
            driverInfoDTO.setCooperationType(carBizSupplier.getCooperationType());
            driverInfoDTO.setGroupId(34);
            // 更新mongoDB
            DriverMongo driverMongo = driverMongoService.findByDriverId(carBizDriverInfo.getDriverId());
            if (driverMongo != null) {
                driverMongoService.updateDriverMongo(driverInfoDTO);
            } else {
                driverMongoService.saveDriverMongo(driverInfoDTO);
            }
        }
        return carBizDriverInfo;
    }


    /**操作关联关系**/
    public AjaxResponse oprateRelation(CarAdmUser user , CarBizDriverInfoDTO driverInfoDTO) {
        boolean result = false;
        String initPwd = String.valueOf((int)((Math.random()*9+1)*100000));
        DriverTelescopeUser driverTelescopeUser = driverTelescopeUserExMapper.selectTelescopeUserByUserId(user.getUserId());
        //关联关系不存在
        if(null == driverTelescopeUser){
            driverTelescopeUser = new DriverTelescopeUser();
            driverTelescopeUser.setUserId(user.getUserId());
            driverTelescopeUser.setDriverId(driverInfoDTO.getDriverId());
            driverTelescopeUser.setStatus(1);
            result = driverTelescopeUserMapper.insertSelective(driverTelescopeUser)>0;
            if(result){
                try{
                    //短信通知
                    String text = user.getUserName() + "，您好！已为您成功开通“首汽约车司机端”千里眼管理账号。登录账号为："+user.getPhone()+"，初始密码为："+initPwd+"（为保障账户安全，请您登录后进行密码修改）";
                    SmsSendUtil.send(user.getPhone() , text);
                }catch (Exception e){
                    logger.error("开通千里眼账号短信通知异常：",e);
                }
            }
        }else{
            //关联关系存在
            if(driverTelescopeUser.getStatus()==0){
                result = driverTelescopeUserExMapper.enableDriverTelescopeUser(driverTelescopeUser.getUserId())>0;
            }else{
                result = true;
            }
        }
        return AjaxResponse.success(result);
    }

}