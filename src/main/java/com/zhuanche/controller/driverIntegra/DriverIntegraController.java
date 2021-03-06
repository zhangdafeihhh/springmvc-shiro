package com.zhuanche.controller.driverIntegra;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.driver.DriverIntegralDto;
import com.zhuanche.dto.driver.DriverTeamRelationEntity;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.rentcar.IDriverService;
import com.zhuanche.serv.rentcar.IDriverTeamRelationService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MobileOverlayUtil;
import com.zhuanche.util.MyRestTemplate;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_RANK_INTEGRAL_EXPORT;
import static com.zhuanche.common.enums.MenuEnum.DRIVER_RANK_INTEGRAL_LIST;
import static java.util.stream.Collectors.joining;

@Controller
@RequestMapping(value = "/driverIntegra")
public class DriverIntegraController {

    private static Logger logger =  LoggerFactory.getLogger(DriverIntegraController.class);

    @Autowired
    private IDriverTeamRelationService<DriverTeamRelationEntity> driverTeamRelationService;


    @Autowired
    private IDriverService driverService;

    @Autowired
    @Qualifier("driverIntegralApiTemplate")
    private MyRestTemplate driverIntegralApiTemplate;

    @Value("${driver.integral.url}")
    private String DRIVER_INTEGRAL;


    protected JSONObject gridJsonFormate(List<?> rows, long total) {
        rows = null == rows ? new ArrayList<>() : rows;
        Map<String, Object> result = new HashMap<>();
        result.put(Common.RESULT_ROWS, rows);
        result.put(Common.RESULT_TOTAL, total);

        JSONObject ret = new JSONObject();
        ret.put("code",0);
        ret.put("msg","??????");
        ret.put("data",result);

        return ret;
    }
    @ResponseBody
    @RequestMapping("/queryDriverIntegralListData")
	@RequiresPermissions(value = { "DriverRankIntegral_look" } )
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = DRIVER_RANK_INTEGRAL_LIST)
    public Object queryDriverIntegralListData(DriverVoEntity driverEntity) {

        try{
            // ??????
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// ??????????????????????????????
            Set<Integer> citieSet = currentLoginUser.getCityIds();
            Set<Integer> supplierSet = currentLoginUser.getSupplierIds();
            Set<Integer> teamIdSet = currentLoginUser.getTeamIds();
            String cities = "";
            String suppliers = "";
            String teamIds = "";
            if (citieSet != null) {
                cities = citieSet.stream().filter(Objects::nonNull)
                        .map(Objects::toString).collect(joining(Constants.SEPERATER));
            }
            if (supplierSet != null){
                suppliers = supplierSet.stream().filter(Objects::nonNull)
                        .map(Objects::toString).collect(joining(Constants.SEPERATER));
            }
            if (teamIdSet != null){
                teamIds = teamIdSet.stream().filter(Objects::nonNull)
                        .map(Objects::toString).collect(joining(Constants.SEPERATER));
            }
            if (!"".equals(driverEntity.getTeamIds()) && driverEntity.getTeamIds() != null) {
                teamIds = driverEntity.getTeamIds();
            }
            List<DriverVoEntity> rows = new ArrayList<DriverVoEntity>();
            long total = 0;
            driverEntity.setCities(cities);
            driverEntity.setSuppliers(suppliers);
            String driverIds = "";
            if (StringUtils.isNotBlank(teamIds)) {
                String[] teamId = teamIds.split(",");
                DriverTeamRelationEntity params = new DriverTeamRelationEntity();
                params.setDriverId(driverEntity.getDriverId());
                for (int i = 0; i < teamId.length; i++) {
                    params.setTeamId(teamId[i]);
                    //?????????????????????-????????????
                    List<DriverTeamRelationEntity> driverIdList = this.driverTeamRelationService
                            .selectDriverIdsNoLimit(params);
                    String driverIdsArray = this.driverTeamRelationService.pingDriverIds(driverIdList);
                    if(StringUtils.isEmpty(driverIds)){
                        driverIds = driverIdsArray;
                    } else {
                        if(StringUtils.isNotEmpty(driverIdsArray)){
                            driverIds += "," + driverIdsArray;
                        }
                    }
                }
                if (StringUtils.isEmpty(driverIds)) {
                    return this.gridJsonFormate(rows, total);
                }
            }
            driverEntity.setDriverIds(driverIds);
            if(driverEntity.getCityId() != 0){
                driverEntity.setServiceCityId(driverEntity.getCityId());
            }
            PageInfo<DriverVoEntity> page = this.driverService.findPageDriver(driverEntity);
            total = page.getTotal();
            if (total == 0) {
                return this.gridJsonFormate(rows, total);
            }
            //??????????????????
            rows = page.getList();
            //?????????????????????
            generatePageData(rows);
            overLayPhone(rows);
            return this.gridJsonFormate(rows, total);
        }catch (Exception e){
            logger.error("??????????????????????????????,????????????"+(driverEntity==null?"null": JSON.toJSONString(driverEntity)),e);
            JSONObject ret = new JSONObject();
            ret.put("code",1);
            ret.put("msg","??????");
            return  ret;
        }
    }

    private void overLayPhone(List<DriverVoEntity> rows) {
        if (Objects.nonNull(rows)){
            for (DriverVoEntity row : rows) {
                row.setPhone(MobileOverlayUtil.doOverlayPhone(row.getPhone()));
            }
        }
    }

    private JSONArray getDriverIntegralInfoList(List<JSONObject> driverInfoList){
        if(driverInfoList == null || driverInfoList.size() == 0){
            return null;
        }
        JSONObject jsonObj = null;
        JSONArray  retArray = null;
        long start = System.currentTimeMillis();
        try{
            String url = "/integral/batchGetDriverInfo";
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("driverInfo", driverInfoList);

            jsonObj = driverIntegralApiTemplate.postForObject(url,driverInfoList,JSONObject.class);
            logger.info("??????????????????????????????????????????,??????+"+(System.currentTimeMillis() - start));
            if (jsonObj != null) {
                int code = jsonObj.getInteger("code");
                if(code == 0) {
                    JSONObject dataJSON = jsonObj.getJSONObject("data");
                    if(dataJSON != null){
                        retArray = dataJSON.getJSONArray("data");
                    }

                }
            }
        }catch (Exception e) {
            logger.error("??????????????????????????????????????????,??????"+(System.currentTimeMillis() - start)+";?????????"+(driverInfoList==null?"": driverInfoList.toString())
                    +";??????????????????"+ (jsonObj == null ?"null":jsonObj.toJSONString()),e);
        }
        return retArray;

    }


    @ResponseBody
    @RequestMapping("/queryDriverIntegralListDataDown")
	@RequiresPermissions(value = { "DriverRankIntegral_export" } )
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = DRIVER_RANK_INTEGRAL_EXPORT)
    public String queryDriverIntegralListDataDown(DriverVoEntity driverEntity, HttpServletRequest request, HttpServletResponse response) {
        /*driverEntity.setPage(1);
        //????????????????????????????????????50
        driverEntity.setPagesize(50);
        logger.info("queryDriverIntegralListDataDown:??????????????????????????????,????????????"+(driverEntity==null?"null": JSON.toJSONString(driverEntity)));
        if(driverEntity.getCityId() == 0){
            return "???????????????";
        }
        if(driverEntity.getSupplierId() == null){
            return "??????????????????";
        }
        try {
            // ?????? ??????????????????????????????
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            Set<Integer> citieSet = currentLoginUser.getCityIds();
            Set<Integer> supplierSet = currentLoginUser.getSupplierIds();
            Set<Integer> teamIdSet = currentLoginUser.getTeamIds();
            String cities = "";
            String suppliers = "";
            String teamIds = "";
            if(citieSet != null){
                for(Integer id : citieSet){
                    if(StringUtils.isNotEmpty(cities)){
                        cities += ",";
                    }
                    cities += id;
                }
            }
            if(supplierSet != null){
                for(Integer id : supplierSet){
                    if(StringUtils.isNotEmpty(suppliers)){
                        suppliers += ",";
                    }
                    suppliers += id;
                }
            }
            if(teamIdSet != null){
                for(Integer id : teamIdSet){
                    if(StringUtils.isNotEmpty(teamIds)){
                        teamIds += ",";
                    }
                    teamIds += id;
                }
            }
            if (!"".equals(driverEntity.getTeamIds()) && driverEntity.getTeamIds() != null) {
                teamIds = driverEntity.getTeamIds();
            }

            List<DriverVoEntity> pageRows;
            long total = 0;
            driverEntity.setCities(cities);
            driverEntity.setSuppliers(suppliers);
            String driverIds = "";

            String fileName = "????????????"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            //???????????????????????????????????????
            String agent = request.getHeader("User-Agent").toUpperCase();
            //IE????????????Edge?????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            List<String> headerList = new ArrayList<>();
            headerList.add("??????,??????ID, ????????????,??????????????????,?????????,?????????,??????,?????????,????????????,????????????,????????????");
            List<String> csvDataList  = new ArrayList<String>();
            if (StringUtils.isNotBlank(teamIds)) {
                String[] teamId = teamIds.split(",");
                DriverTeamRelationEntity params = new DriverTeamRelationEntity();
                params.setDriverId(driverEntity.getDriverId());
                for (int i = 0; i < teamId.length; i++) {
                    params.setTeamId(teamId[i]);
                    //?????????????????????-????????????
                    List<DriverTeamRelationEntity> driverIdList = this.driverTeamRelationService
                            .selectDriverIdsNoLimit(params);
                    String driverIdsArray = this.driverTeamRelationService.pingDriverIds(driverIdList);
                    if(StringUtils.isEmpty(driverIds)){
                        driverIds = driverIdsArray;
                    } else {
                        if(StringUtils.isNotEmpty(driverIdsArray)){
                            driverIds += "," + driverIdsArray;
                        }
                    }
                }
                if (StringUtils.isEmpty(driverIds)) {

                    try {
                        csvDataList.add("?????????????????????????????????");
                        new CsvUtils().exportCsvV2(response,csvDataList,headerList,fileName,true,true);
                    } catch (IOException e) {
                        logger.error("?????????????????????????????????driverEntity="+(driverEntity==null?"null":JSON.toJSONString(driverEntity)));
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            driverEntity.setDriverIds(driverIds);
            if(driverEntity.getCityId() != 0){
                driverEntity.setServiceCityId(driverEntity.getCityId());
            }
            PageInfo<DriverVoEntity> page = this.driverService.findPageDriver(driverEntity);
            total = page.getTotal();
            CsvUtils entity = new CsvUtils();
            if (total == 0) {
                try {
                    csvDataList.add("?????????????????????????????????");
                    entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
                } catch (IOException e) {
                    logger.error("?????????????????????????????????driverEntity="+(driverEntity==null?"null":JSON.toJSONString(driverEntity)));
                    e.printStackTrace();
                }
                return null;
            }
            pageRows = page.getList();
            //?????????????????????
            generatePageData(  pageRows);
            dataTrans(pageRows,csvDataList);

            int pages = page.getPages();

            boolean isFirst = true;
            boolean isLast = false;
            if(pages == 1){
                isLast = true;
            }
            entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
            csvDataList = null;
            isFirst = false;

            //??????????????????
            for(int pageNumber=2 ; pageNumber <= pages ;pageNumber++){
                driverEntity.setPage(pageNumber);
                page = this.driverService.findPageDriver(driverEntity);
                pageRows = page.getList();
                //?????????????????????
                generatePageData(  pageRows);
                csvDataList  = new ArrayList<String>();
                dataTrans(pageRows,csvDataList);
                if(pageNumber == pages){
                    isLast = true;
                }
                entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);

            }

            return "????????????";
        } catch (Exception e) {
            logger.error("????????????????????????,driverEntity="+(driverEntity==null?"null":JSON.toJSONString(driverEntity)),e);
        }*/
        return "?????????????????????????????????";
    }

    /**
     * ???????????????????????????????????????
     * @param pageRows
     */
    private void generatePageData(List<DriverVoEntity> pageRows){
        if (null == pageRows) return;



        if(pageRows != null){
            List<JSONObject> driverInfoList = new ArrayList<>();
            Map<String,JSONObject> itemMap = new HashMap<>();

            for (DriverVoEntity driverVoEntity : pageRows) {
                JSONObject item = new JSONObject();
                item.put("driverId",StringUtils.isNotEmpty(driverVoEntity.getDriverId())?Integer.parseInt(driverVoEntity.getDriverId()):0);
                item.put("cityId",driverVoEntity.getServiceCityId());
                driverInfoList.add(item);
            }
            //????????????????????????
            JSONArray allDriverIntegralInfoArray = getDriverIntegralInfoList(driverInfoList);
            if(allDriverIntegralInfoArray != null){
                int size = allDriverIntegralInfoArray.size();
                for(int i=0;i<size;i++ ){
                    JSONObject item = allDriverIntegralInfoArray.getJSONObject(i);
                    itemMap.put("flag_"+item.getInteger("driverId"),item);
                }
            }
            String driverId;
            DriverIntegralDto dto;
            Map<String, DriverIntegralDto>  driverIntegralDtoMap = getDriverIntegralInfoListNew(pageRows.stream().map(DriverVoEntity::getDriverId).collect(Collectors.toList()));
            for (DriverVoEntity driverVoEntity : pageRows) {
                //???????????????????????????
                if (null != driverIntegralDtoMap){
                    dto = driverIntegralDtoMap.get(driverVoEntity.getDriverId());
                    if (null != dto){
                        //????????????????????????
                        driverVoEntity.setMonthIntegral(dto.getCurrentCycleIntegral());
                        //????????????????????????
                        driverVoEntity.setDayIntegral(dto.getCurrentDayIntegral());
                        //????????????????????????
                        driverVoEntity.setCalcuateCycle(dto.getCalcuateCycle());
                    }
                }
                driverId = driverVoEntity.getDriverId() ;
                JSONObject driverIntegraInfo = itemMap.get("flag_"+driverId);
                if(driverIntegraInfo != null){
                    //????????????????????????
                    if(StringUtils.isNotEmpty(driverIntegraInfo.getString("membershipName"))  && !("null".equals(driverIntegraInfo.getString("membershipName")))){
                        try{
                            driverVoEntity.setMembershipName(driverIntegraInfo.getString("membershipName"));
                        }catch (Exception e){
                            logger.error("???????????????????????????driverId="+driverId+"???membershipName="+driverIntegraInfo.getString("membershipName"));
                        }
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     * wiki http://inside-yapi.01zhuanche.com/project/187/interface/api/13279
     * @param driverIds
     * @return
     */
    private Map<String, DriverIntegralDto> getDriverIntegralInfoListNew(List driverIds) {
        if (null == driverIds || driverIds.size() == 0) {
            return null;
        }
       // String driverInfo = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, String.format(DRIVER_INTEGRAL + "/integral/currentIntegralScore?driverIds=%s", String.join(",", driverIds)), null, null, "UTF-8");
        //?????????okhttp??????
        String driverInfo = MpOkHttpUtil.okHttpGet(String.format(DRIVER_INTEGRAL + "/integral/currentIntegralScore?driverIds=%s", String.join(",", driverIds)),0,null);
        if (StringUtils.isBlank(driverInfo) || driverInfo.equals("true\r\n")) {
            return null;
        }
        RPCResponse orderResponse = RPCResponse.parse(driverInfo);
        if (null == orderResponse || orderResponse.getCode() != 0 || orderResponse.getData() == null) {
            return null;
        }
        List<DriverIntegralDto> list = JSON.parseArray(JSON.toJSONString(orderResponse.getData()), DriverIntegralDto.class);
        Map<String, DriverIntegralDto> map = null;
        if (null != list) {
            map = list.stream().collect(Collectors.toMap(DriverIntegralDto::getDriverId, a -> a, (k1, k2) -> k1));
        }
        return map;
    }

    private void dataTrans(List<DriverVoEntity> list, List<String>  csvDataList ){
        if(null == list){
            return;
        }

        for(DriverVoEntity rowEntity:list){
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(rowEntity.getServiceCity()==null?"":rowEntity.getServiceCity());
            stringBuffer.append(",");

            stringBuffer.append(rowEntity.getDriverId()==null?"":("\t"+rowEntity.getDriverId()));
            stringBuffer.append(",");

            stringBuffer.append(rowEntity.getName()==null?"":rowEntity.getName());
            stringBuffer.append(",");


            stringBuffer.append(rowEntity.getMembershipName()==null?"":rowEntity.getMembershipName());
            stringBuffer.append(",");

            stringBuffer.append(rowEntity.getPhone()==null?"":("\t"+rowEntity.getPhone()));
            stringBuffer.append(",");

            stringBuffer.append(rowEntity.getSupplierName()==null?"":rowEntity.getSupplierName());
            stringBuffer.append(",");


            stringBuffer.append(rowEntity.getTeamName()==null?"":rowEntity.getTeamName());
            stringBuffer.append(",");


            stringBuffer.append(rowEntity.getLicensePlates()==null?"":rowEntity.getLicensePlates());
            stringBuffer.append(",");


            stringBuffer.append(rowEntity.getMonthIntegral()==null?"":rowEntity.getMonthIntegral());
            stringBuffer.append(",");

            stringBuffer.append(rowEntity.getDayIntegral()==null?"":rowEntity.getDayIntegral());
            stringBuffer.append(",");

            if (Objects.nonNull(rowEntity.getCalcuateCycle())){
                stringBuffer.append(rowEntity.getCalcuateCycle()==1?"???":"???");
            }else {
                stringBuffer.append("");
            }

            csvDataList.add(stringBuffer.toString());

        }
    }

}
