package com.zhuanche.serv.subscription;

import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.driver.SubscriptionReportConfigureDTO;
import com.zhuanche.dto.rentcar.CarBizSupplierDTO;
import com.zhuanche.entity.driver.SubscriptionReport;
import com.zhuanche.entity.driver.SubscriptionReportConfigure;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.driver.SubscriptionReportConfigureMapper;
import mapper.driver.SubscriptionReportMapper;
import mapper.driver.ex.SubscriptionReportConfigureExMapper;
import mapper.driver.ex.SubscriptionReportExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SubscriptionReportConfigureService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionReportConfigureService.class);
    private static final String LOGTAG = "[数据报表订阅]: ";

    @Autowired
    private SubscriptionReportConfigureExMapper subscriptionReportConfigureExMapper;

    @Autowired
    private SubscriptionReportConfigureMapper subscriptionReportConfigureMapper;

    @Autowired
    private SubscriptionReportExMapper subscriptionReportExMapper;

    @Autowired
    private SubscriptionReportMapper subscriptionReportMapper;

    @Autowired
    private CarBizCityExMapper carBizCityExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Autowired
    private CarDriverTeamExMapper carDriverTeamExMapper;

    /**
     * 根据订阅周期查询数据报表订阅配置
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @return
     */
    public List<SubscriptionReportConfigureDTO> selectBySubscriptionCycle (Integer subscriptionCycle){
        return subscriptionReportConfigureExMapper.selectBySubscriptionCycle(subscriptionCycle);
    }

    /**
     * 数据报表下载列表
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖;
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @return
     */
    public List<SubscriptionReport> querySubscriptionList(Integer reportId, Integer subscriptionCycle,
                                                          Integer cityId, Integer supplierId, Integer teamId) {
        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        return subscriptionReportExMapper.querySubscriptionList(reportId, subscriptionCycle,
                cityId, supplierId, teamId,
                permOfCity, permOfSupplier, permOfTeam);
    }

    /**
     * 根据id查询配置信息
     * @param id
     * @return
     */
    public SubscriptionReportConfigure selectByPrimaryKey (Long id){
        return subscriptionReportConfigureMapper.selectByPrimaryKey(id);
    }

    /**
     * 数据报表下载地址保存
     * @param record
     * @return
     */
    public int saveSubscriptionUrl (SubscriptionReport record){
        return subscriptionReportMapper.insert(record);
    }

    /**
     * 保存
     * bussinessNumber存在更新状态status=1,不存在插入一条新数据
     * @param record
     * @return
     */
    public int saveSubscriptionReportConfigure(SubscriptionReportConfigure record){
        record.setCreateId(WebSessionUtil.getCurrentLoginUser().getId());
        record.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
        return subscriptionReportConfigureExMapper.saveSubscriptionReportConfigure(record);
    }

    /**
     * 数据报表订阅
     * @param reportId 报表ID,1-工资明细;2-完单详情;3-积分;4-数单奖;
     * @param reportName 报表名称:1-工资明细,2-完单详情,3-积分,4-数单奖
     * @param subscriptionCycle 订阅周期,1-周;2-月;
     * @param level 级别,1-全国;2-城市;4-加盟商;8-车队;16-班组（多个ID用英文逗号分隔）
     * @param cities 城市ID（多个ID用英文逗号分隔）
     * @param supplierIds 供应商ID（多个ID用英文逗号分隔）
     * @param teamIds 车队ID（多个ID用英文逗号分隔）
     * @param isWhole 是否有全国级别
     * @param isCity 是否有城市级别
     * @param isSupplier 是否有供应商级别
     * @param isTeam 是否有车队级别
     * @return
     */
    public AjaxResponse saveSubscription(Integer reportId, String reportName,
                                         Integer subscriptionCycle, String level,
                                         String cities, String supplierIds, String teamIds,
                                         Boolean isWhole, Boolean isCity,
                                         Boolean isSupplier, Boolean isTeam) {

        //TODO 更新订阅配置的状态status=0
        subscriptionReportConfigureExMapper.updateConfigureStatus(subscriptionCycle);

        if(isWhole) { //全国,需要单独生成一个配置
            String bussinessNumber = reportId + "" + subscriptionCycle + "" + level + "" + "000";
            String md5DigestBase64 = null;
            try {
                md5DigestBase64 = MD5Utils.getMD5DigestBase64(bussinessNumber);
                logger.info(LOGTAG + "加密,md5DigestBase64={}", md5DigestBase64);
            } catch (NoSuchAlgorithmException e) {
                logger.info(LOGTAG + "加密,error:", e);
            }
            SubscriptionReportConfigure configure = new SubscriptionReportConfigure();
            configure.setBussinessNumber(md5DigestBase64);
            configure.setStatus(1);
            configure.setLevel(1);
            configure.setReportId(reportId);
            configure.setReportName(reportName);
            configure.setSubscriptionCycle(subscriptionCycle);
            //TODO 存在
            int had = this.saveSubscriptionReportConfigure(configure);
            logger.info(LOGTAG + "配置, 全国, result={}", had);
        }
        if(isCity){ //城市
            //查询城市名称，一次查询出来避免多次读库
            List<CarBizCity> cityList = carBizCityExMapper.queryNameByCityIds(cities);
            if(cityList==null || cityList.size()==0){
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "选择城市不存在");
            }
            Map<String,CarBizCity> byCityIds = new HashMap();
            if(cityList != null){
                for(CarBizCity city :cityList){
                    byCityIds.put("c_"+city.getCityId(),city);
                }
            }
            String[] citiesStr = cities.split(",");
            CarBizCity carBizCity = null;
            for (int j = 0; j < citiesStr.length; j++) {
                Integer cityId = Integer.parseInt(citiesStr[j]);
                carBizCity = byCityIds.get("c_"+cityId);
                if(carBizCity!=null){
                    String bussinessNumber = reportId + "" + subscriptionCycle + "" + level + "" + cityId + "00";
                    String md5DigestBase64 = null;
                    try {
                        md5DigestBase64 = MD5Utils.getMD5DigestBase64(bussinessNumber);
                        logger.info(LOGTAG + "加密,md5DigestBase64={}", md5DigestBase64);
                    } catch (NoSuchAlgorithmException e) {
                        logger.info(LOGTAG + "加密,error:", e);
                    }
                    SubscriptionReportConfigure configure = new SubscriptionReportConfigure();
                    configure.setBussinessNumber(md5DigestBase64);
                    configure.setStatus(1);
                    configure.setLevel(2);
                    configure.setReportId(reportId);
                    configure.setReportName(reportName);
                    configure.setSubscriptionCycle(subscriptionCycle);
                    configure.setCityId(cityId);
                    configure.setCityName(carBizCity.getCityName());
                    //TODO 存在
                    int had = this.saveSubscriptionReportConfigure(configure);
                    logger.info(LOGTAG + "配置, 城市, cityId={}, result={}", had);
                }
            }
        }
        if(isSupplier) { //供应商
            //查询供应商名称，一次查询出来避免多次读库
            List<CarBizSupplierDTO> supplierList = carBizSupplierExMapper.queryNameBySupplierIds(supplierIds);
            if(supplierList==null || supplierList.size()==0){
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "选择供应商不存在");
            }
            Map<String, CarBizSupplierDTO> bySuppliers = Maps.newHashMap();
            if(supplierList != null){
                for(CarBizSupplierDTO supplierDTO :supplierList){
                    bySuppliers.put("s_"+supplierDTO.getSupplierId(), supplierDTO);
                }
            }
            CarBizSupplierDTO temp = null;
            String[] supplierStr = supplierIds.split(",");
            for (int j = 0; j < supplierStr.length; j++) {
                Integer supplierId = Integer.parseInt(supplierStr[j]);
                temp = bySuppliers.get("s_"+supplierId);
                if(temp != null){
                    SubscriptionReportConfigure configure = new SubscriptionReportConfigure();
                    String bussinessNumber = reportId + "" + subscriptionCycle + "" + level + ""
                            + temp.getSupplierCity() + "" + supplierId + "0";
                    String md5DigestBase64 = null;
                    try {
                        md5DigestBase64 = MD5Utils.getMD5DigestBase64(bussinessNumber);
                        logger.info(LOGTAG + "加密,md5DigestBase64={}", md5DigestBase64);
                    } catch (NoSuchAlgorithmException e) {
                        logger.info(LOGTAG + "加密,error:", e);
                    }
                    configure.setBussinessNumber(md5DigestBase64);
                    configure.setStatus(1);
                    configure.setLevel(4);
                    configure.setReportId(reportId);
                    configure.setReportName(reportName);
                    configure.setSubscriptionCycle(subscriptionCycle);
                    configure.setCityId(temp.getSupplierCity());
                    configure.setCityName(temp.getCityName());
                    configure.setSupplierId(supplierId);
                    configure.setSupplierName(temp.getSupplierFullName());
                    //TODO 存在
                    int had = this.saveSubscriptionReportConfigure(configure);
                    logger.info(LOGTAG + "配置, 供应商, cityId={}, result={}", had);
                }
            }
        }
        if(isTeam) { //车队
            //查询车队名称，一次查询出来避免多次读库
            List<CarDriverTeam> teamList = carDriverTeamExMapper.queryTeamNameByTemIds(teamIds);
            if(teamList==null || teamList.size()==0){
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "选择车队不存在");
            }
            String suppliers = "";
            Map<String, CarDriverTeam> byTeams = Maps.newHashMap();
            if(teamList!=null) {
                for (CarDriverTeam carDriverTeam : teamList) {
                    byTeams.put("t_" + carDriverTeam.getId(), carDriverTeam);
                    suppliers += "," + carDriverTeam.getSupplier();
                }
            }
            if(suppliers.length()>1){
                suppliers = suppliers.substring(1,suppliers.length());
            }
            List<CarBizSupplierDTO> supplierList = carBizSupplierExMapper.queryNameBySupplierIds(suppliers);
            if(supplierList==null || supplierList.size()==0){
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "选择供应商不存在");
            }
            Map<String, CarBizSupplierDTO> bySuppliers = Maps.newHashMap();
            if(supplierList != null){
                for(CarBizSupplierDTO supplierDTO :supplierList){
                    bySuppliers.put("s_"+supplierDTO.getSupplierId(), supplierDTO);
                }
            }
            CarDriverTeam teamTemp = null;
            CarBizSupplierDTO supplierTemp = null;
            String[] teamStr = teamIds.split(",");
            for (int j = 0; j < teamStr.length; j++) {
                Integer teamId = Integer.parseInt(teamStr[j]);
                teamTemp = byTeams.get("t_"+teamId);
                if(teamTemp!=null) {
                    supplierTemp = bySuppliers.get("s_" + teamTemp.getSupplier());
                    if (supplierTemp != null) {
                        SubscriptionReportConfigure configure = new SubscriptionReportConfigure();
                        String bussinessNumber = reportId + "" + subscriptionCycle + "" + level + ""
                                + supplierTemp.getSupplierCity() + "" + supplierTemp.getSupplierId()
                                + "" + teamTemp.getId();
                        String md5DigestBase64 = null;
                        try {
                            md5DigestBase64 = MD5Utils.getMD5DigestBase64(bussinessNumber);
                            logger.info(LOGTAG + "加密,md5DigestBase64={}", md5DigestBase64);
                        } catch (NoSuchAlgorithmException e) {
                            logger.info(LOGTAG + "加密,error:", e);
                        }
                        configure.setBussinessNumber(md5DigestBase64);
                        configure.setStatus(1);
                        configure.setLevel(8);
                        configure.setReportId(reportId);
                        configure.setReportName(reportName);
                        configure.setSubscriptionCycle(subscriptionCycle);
                        configure.setCityId(supplierTemp.getSupplierCity());
                        configure.setCityName(supplierTemp.getCityName());
                        configure.setSupplierId(supplierTemp.getSupplierId());
                        configure.setSupplierName(supplierTemp.getSupplierFullName());
                        configure.setTeamId(teamTemp.getId());
                        configure.setTeamName(teamTemp.getTeamName());
                        //TODO 存在
                        int had = this.saveSubscriptionReportConfigure(configure);
                        logger.info(LOGTAG + "配置, 供应商, cityId={}, result={}", had);
                    }
                }
            }
        }
        return AjaxResponse.success(null);
    }


    /**
     * 根据id查询历史数据报表信息
     * @param id
     * @return
     */
    public SubscriptionReport selectSubscriptionReportByPrimaryKey (Long id){
        return subscriptionReportMapper.selectByPrimaryKey(id);
    }

}
