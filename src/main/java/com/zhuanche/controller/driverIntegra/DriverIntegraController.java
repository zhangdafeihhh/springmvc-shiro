package com.zhuanche.controller.driverIntegra;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.driver.DriverTeamEntity;
import com.zhuanche.dto.driver.DriverTeamRelationEntity;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.serv.rentcar.IDriverService;
import com.zhuanche.serv.rentcar.IDriverTeamRelationService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

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


    protected JSONObject gridJsonFormate(List<?> rows, int total) {
        rows = null == rows ? new ArrayList<>() : rows;
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Common.RESULT_ROWS, rows);
        result.put(Common.RESULT_TOTAL, total);

        JSONObject ret = new JSONObject();
        ret.put("code",0);
        ret.put("msg","成功");
        ret.put("data",result);

        return ret;
    }
    @ResponseBody
    @RequestMapping("/queryDriverIntegralListData")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public Object queryDriverIntegralListData(DriverVoEntity driverEntity) {

        try{
            logger.info("queryDriverAppraisalListData:司机积分数据列表,参数为："+(driverEntity==null?"null": JSON.toJSONString(driverEntity)));
            // 权限
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
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
            if (driverEntity.getServiceCityId() != null && !"".equals(driverEntity.getServiceCityId())) {
                cities = String.valueOf(driverEntity.getServiceCityId());
            }
            if (driverEntity.getSupplierId() != null && !"".equals(driverEntity.getSupplierId())) {
                suppliers = String.valueOf(driverEntity.getSupplierId());
            }
            if (!"".equals(driverEntity.getTeamIds()) && driverEntity.getTeamIds() != null) {
                teamIds = driverEntity.getTeamIds();
            }
            List<DriverVoEntity> rows = new ArrayList<DriverVoEntity>();
            int total = 0;
            driverEntity.setCities(cities);
            driverEntity.setSuppliers(suppliers);
            String driverIdTeam = "";
            if (StringUtils.isNotBlank(teamIds)) {
                String[] teamId = teamIds.split(",");
                DriverTeamRelationEntity params = new DriverTeamRelationEntity();
                params.setTeamId(teamIds);
                DriverTeamEntity paramsTeam = new DriverTeamEntity();
                int j = 0;
                for (int i = 0; i < teamId.length; i++) {
                    paramsTeam.setTeamId(teamId[i]);
                    //不分页查询司机-车队关系
                    List<DriverTeamRelationEntity> driverIdList = this.driverTeamRelationService
                            .selectDriverIdsNoLimit(params);
                    String driverIds = this.driverTeamRelationService.pingDriverIds(driverIdList);
                    if (j == 0) {
                        driverIdTeam = driverIds;
                    } else {
                        driverIdTeam += "," + driverIds;
                    }
                    j++;
                }
                if (driverIdTeam == null || "".equals(driverIdTeam)) {
                    return this.gridJsonFormate(rows, total);
                }
            }else{
                driverIdTeam = driverEntity.getDriverId();
            }
            driverEntity.setDriverId(driverIdTeam);
            total = this.driverService.selectDriverByKeyCountAddCooperation(driverEntity);
            if (total == 0) {
                return this.gridJsonFormate(rows, total);
            }
            //查询司机信息
            rows = this.driverService.selectDriverByKeyAddCooperation(driverEntity);
            List<JSONObject> driverInfoList = new ArrayList<>();
            for (DriverVoEntity driverVoEntity : rows) {
                JSONObject item = new JSONObject();
                item.put("driverId",StringUtils.isNotEmpty(driverVoEntity.getDriverId())?Integer.parseInt(driverVoEntity.getDriverId()):0);
                item.put("cityId",driverVoEntity.getServiceCityId());
                driverInfoList.add(item);
            }
            JSONArray driverIntegralInfoArray = getDriverIntegralInfoList(driverInfoList);
            if(driverIntegralInfoArray != null){
                Map<String,JSONObject> itemMap = new HashMap<>();
                int size = driverIntegralInfoArray.size();
                for(int i=0;i<size;i++ ){
                    JSONObject item = driverIntegralInfoArray.getJSONObject(i);
                    itemMap.put("flag_"+item.getInteger("driverId"),item);
                }
                String driverId = null;
                BigDecimal temp = null;
                for (DriverVoEntity driverVoEntity : rows) {

                    driverId = driverVoEntity.getDriverId() ;
                    JSONObject driverIntegraInfo = itemMap.get("flag_"+driverId);
//				logger.info("调用策略平台批量查询积分-driverId："+driverId+";driverIntegraInfo="+(driverIntegraInfo==null?"null":driverIntegraInfo.toJSONString()));
                    if(driverIntegraInfo != null){
                        //设置司机当月积分
                        if(StringUtils.isNotEmpty(driverIntegraInfo.getString("monthIntegral")) && !("null".equals(driverIntegraInfo.getString("monthIntegral")))) {
                            try{
                            temp = new BigDecimal(driverIntegraInfo.getString("monthIntegral")).setScale(3, BigDecimal.ROUND_HALF_UP);
                            driverVoEntity.setMonthIntegral(temp.toString());
                            }catch (Exception e){

                            }
                        }
                        //设置司机当日积分
                        if(StringUtils.isNotEmpty(driverIntegraInfo.getString("todayIntegral")) && !("null".equals(driverIntegraInfo.getString("todayIntegral")))) {
                            try{
                                temp = new BigDecimal(driverIntegraInfo.getString("todayIntegral")).setScale(3, BigDecimal.ROUND_HALF_UP);
                                driverVoEntity.setDayIntegral(temp.toString());
                            }catch (Exception e){

                            }

                        }
                        //设置司机司机等级
                        if(StringUtils.isNotEmpty(driverIntegraInfo.getString("membershipName"))  && !("null".equals(driverIntegraInfo.getString("membershipName")))){
                            driverVoEntity.setMembershipName(driverIntegraInfo.getString("membershipName"));

                        }

                    }
                }
            }
            return this.gridJsonFormate(rows, total);
        }catch (Exception e){
            logger.error("司机积分数据列表异常,参数为："+(driverEntity==null?"null": JSON.toJSONString(driverEntity)),e);
            JSONObject ret = new JSONObject();
            ret.put("code",1);
            ret.put("msg","失败");
            return  ret;
        }
    }

    private JSONArray getDriverIntegralInfoList(List<JSONObject> driverInfoList){

        JSONObject jsonObj = null;
        JSONArray  retArray = null;
        long start = System.currentTimeMillis();
        try{
            String url = "/integral/batchGetDriverInfo";
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("driverInfo", driverInfoList);

            jsonObj = driverIntegralApiTemplate.postForObject(url,driverInfoList,JSONObject.class);
            logger.info("调用策略平台批量查询积分正常,耗时+"+(System.currentTimeMillis() - start)
                    //+";参数为"+(driverInfoList==null?"": driverInfoList.toString())
                    //+";返回结果为："+ (jsonObj == null ?"null":JSON.toJSONString(jsonObj))
            );
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
            logger.error("调用策略平台批量查询积分异常,耗时"+(System.currentTimeMillis() - start)+";参数为"+(driverInfoList==null?"": driverInfoList.toString())
                    +";返回结果为："+ (jsonObj == null ?"null":jsonObj.toJSONString()),e);
        }
        return retArray;

    }


    @ResponseBody
    @RequestMapping("/queryDriverIntegralListDataDown")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public String queryDriverIntegralListDataDown(DriverVoEntity driverEntity, HttpServletResponse response) {
        driverEntity.setPage(1);
        driverEntity.setPagesize(Integer.MAX_VALUE);

        logger.info("queryDriverIntegralListDataDown:下载司机积分数据列表,参数为："+(driverEntity==null?"null": JSON.toJSONString(driverEntity)));


        try {
            // 权限
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
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
            if (driverEntity.getServiceCityId() != null && !"".equals(driverEntity.getServiceCityId())) {
                cities = String.valueOf(driverEntity.getServiceCityId());
            }
            if (driverEntity.getSupplierId() != null && !"".equals(driverEntity.getSupplierId())) {
                suppliers = String.valueOf(driverEntity.getSupplierId());
            }
            if (!"".equals(driverEntity.getTeamIds()) && driverEntity.getTeamIds() != null) {
                teamIds = driverEntity.getTeamIds();
            }
            List<DriverVoEntity> rows = new ArrayList<DriverVoEntity>();
            int total = 0;
            driverEntity.setCities(cities);
            driverEntity.setSuppliers(suppliers);
            String driverIdTeam = "";
            if (StringUtils.isNotEmpty(teamIds)) {
                String[] teamId = teamIds.split(",");
                DriverTeamRelationEntity params = new DriverTeamRelationEntity();
                params.setTeamId(teamIds);
                DriverTeamEntity paramsTeam = new DriverTeamEntity();
                int j = 0;
                for (int i = 0; i < teamId.length; i++) {
                    paramsTeam.setTeamId(teamId[i]);
                    //根据车队查询车队下所有司机的id
                    List<DriverTeamRelationEntity> driverIdList = this.driverTeamRelationService
                            .selectDriverIdsNoLimit(params);
                    //从list里将司机id拼接成字符串
                    String driverIds = this.driverTeamRelationService.pingDriverIds(driverIdList);
                    if (j == 0) {
                        driverIdTeam = driverIds;
                    } else {
                        driverIdTeam += "," + driverIds;
                    }
                    j++;
                }
                //如果id为空，则只打印头部
                if (StringUtils.isEmpty(driverIdTeam)) {
                    long start = System.currentTimeMillis();
                    try {
                        doDownExcel( rows,   response);
                    } catch (IOException e) {
                        logger.error("下载司机积分数据列表异常,耗时"+(System.currentTimeMillis() - start),e);
                    }
                    return null;
                }
            }else{
                driverIdTeam = driverEntity.getDriverId();
            }
            driverEntity.setDriverId(driverIdTeam);

            total = this.driverService.selectDriverByKeyCountAddCooperation(driverEntity);
            if (total == 0) {
                try {
                    doDownExcel( rows,   response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            rows = this.driverService.selectDriverByKeyAddCooperation(driverEntity);

            List<JSONObject> driverInfoList = new ArrayList<>();
            for (DriverVoEntity driverVoEntity : rows) {
                JSONObject item = new JSONObject();
                item.put("driverId",StringUtils.isNotEmpty(driverVoEntity.getDriverId())?Integer.parseInt(driverVoEntity.getDriverId()):0);
                item.put("cityId",driverVoEntity.getServiceCityId());
                driverInfoList.add(item);
            }
            JSONArray driverIntegralInfoArray = new JSONArray();
            int length = driverInfoList.size();
            int max = 1000;
            if(length <= max){
                driverIntegralInfoArray = getDriverIntegralInfoList(driverInfoList);
            }else {
                //进行分页处理
                List<JSONObject> paramDriverInfoList = new ArrayList<>();

                for(int i=0;i<length;i++){
                    if((i+1) % max == 0){
                        JSONArray newDriverIntegralInfoArray = getDriverIntegralInfoList(paramDriverInfoList);
                        driverIntegralInfoArray.addAll(newDriverIntegralInfoArray);
                        paramDriverInfoList.clear();
                    }else{
                        paramDriverInfoList.add(driverInfoList.get(i));
                    }
                }
                if(paramDriverInfoList.size() >= 1){
                    JSONArray newDriverIntegralInfoArray = getDriverIntegralInfoList(paramDriverInfoList);
                    driverIntegralInfoArray.addAll(newDriverIntegralInfoArray);
                }

            }

            if(driverIntegralInfoArray != null){
                Map<String,JSONObject> itemMap = new HashMap<>();
                int size = driverIntegralInfoArray.size();
                for(int i=0;i<size;i++ ){
                    JSONObject item = driverIntegralInfoArray.getJSONObject(i);
                    itemMap.put("flag_"+item.getInteger("driverId"),item);
                }
                String driverId = null;
                BigDecimal temp;
                for (DriverVoEntity driverVoEntity : rows) {

                    driverId = driverVoEntity.getDriverId() ;
                    JSONObject driverIntegraInfo = itemMap.get("flag_"+driverId);
//				logger.info("调用策略平台批量查询积分-driverId："+driverId+";driverIntegraInfo="+(driverIntegraInfo==null?"null":driverIntegraInfo.toJSONString()));
                    if(driverIntegraInfo != null){
                        //设置司机当月积分
                        if(StringUtils.isNotEmpty(driverIntegraInfo.getString("monthIntegral")) && !("null".equals(driverIntegraInfo.getString("monthIntegral")))) {
                            try{
                                temp = new BigDecimal(driverIntegraInfo.getString("monthIntegral")).setScale(3, BigDecimal.ROUND_HALF_UP);
                                driverVoEntity.setMonthIntegral(temp.toString());
                            }catch (Exception e){
                                logger.error("设置司机当月积分异常，driverId="+driverId+"，monthIntegral="+driverIntegraInfo.getString("monthIntegral"));
                            }
                        }
                        //设置司机当日积分
                        if(StringUtils.isNotEmpty(driverIntegraInfo.getString("todayIntegral")) && !("null".equals(driverIntegraInfo.getString("todayIntegral")))) {
                            try{
                                temp = new BigDecimal(driverIntegraInfo.getString("todayIntegral")).setScale(3, BigDecimal.ROUND_HALF_UP);
                                driverVoEntity.setDayIntegral(temp.toString());
                            }catch (Exception e){
                                logger.error("设置司机当日积分异常，driverId="+driverId+"，todayIntegral="+driverIntegraInfo.getString("todayIntegral"));
                            }

                        }
                        //设置司机司机等级
                        if(StringUtils.isNotEmpty(driverIntegraInfo.getString("membershipName"))  && !("null".equals(driverIntegraInfo.getString("membershipName")))){
                            try{
                                driverVoEntity.setMembershipName(driverIntegraInfo.getString("membershipName"));
                            }catch (Exception e){
                                logger.error("设置司机等级异常，driverId="+driverId+"，membershipName="+driverIntegraInfo.getString("membershipName"));

                            }
                        }
                    }
                }
            }
            doDownExcel( rows,   response);
            return null;
        } catch (Exception e) {
            logger.error("下载司机积分异常",e);
        }
        return "下载失败，请稍后重试";
    }
    private void  doDownExcel(List<DriverVoEntity> rows, HttpServletResponse response) throws IOException {
        //生成一个Excel文件

        String columnNames[] = {"序号", "城市", "司机ID", "司机姓名", "当前司机等级", "手机号", "供应商", "车队", "车牌号", "当月积分", "当日积分"};//列名
        // 创建excel工作簿
        Workbook wb = new HSSFWorkbook();
        // 创建第一个sheet（页），并命名
        Sheet sheet = wb.createSheet("司机等级积分列表");
        // 手动设置列宽。
        // 第一个参数表示要为第几列设；
        // 第二个参数表示列的宽度，n为列高的像素数。
        int columnLength = columnNames.length;
        for (int i = 0; i < columnLength; i++) {
            sheet.setColumnWidth((short) i, (short) (35.7 * 150));
        }
        // 创建第一行
        Row row = sheet.createRow((short) 0);

        //设置列名
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columnNames[i]);
        }
        if(rows == null || rows.size() ==0){
            Row rowItem = sheet.createRow((short) 1);
            Cell cell = rowItem.createCell(0);
            cell.setCellValue("没有查到数据");
        } else {
            int rowIndex = 1;
            for(DriverVoEntity rowEntity:rows){
                Row rowItem = sheet.createRow((short) rowIndex);

                Cell cell = rowItem.createCell(0);
                cell.setCellValue(rowIndex);

                cell = rowItem.createCell(1);
                cell.setCellValue(rowEntity.getServiceCity());

                cell = rowItem.createCell(2);
                cell.setCellValue(rowEntity.getDriverId());

                cell = rowItem.createCell(3);
                cell.setCellValue(rowEntity.getName());

                cell = rowItem.createCell(4);
                cell.setCellValue(rowEntity.getMembershipName());

                cell = rowItem.createCell(5);
                cell.setCellValue(rowEntity.getPhone());

                cell = rowItem.createCell(6);
                cell.setCellValue(rowEntity.getSupplierName());

                cell = rowItem.createCell(7);
                cell.setCellValue(rowEntity.getTeamName());

                cell = rowItem.createCell(8);
                cell.setCellValue(rowEntity.getLicensePlates());

                cell = rowItem.createCell(9);
                cell.setCellValue(rowEntity.getMonthIntegral());

                cell = rowItem.createCell(10);
                cell.setCellValue(rowEntity.getDayIntegral());
                rowIndex ++;
            }
        }
        //同理可以设置数据行
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String("司机等级积分列表.xls".getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

}
