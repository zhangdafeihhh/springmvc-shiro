package com.zhuanche.controller;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.CarBizCityMapper;
import mapper.rentcar.CarBizSupplierMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Component
public class DriverQueryController {

    @Autowired
    private CarRelateTeamExMapper carRelateTeamExMapper;

    @Autowired
    private CarBizSupplierMapper carBizSupplierMapper;

    @Autowired
    private CarBizCityMapper carBizCityMapper;

    /**
     *
     * <p>Title: queryAuthorityDriverIdsByTeamAndGroup</p>
     * <p>Description: 查询该用户权限下的司机id, 如果两个参数都为空，查询该用户绑定的车队的司机，如果传入组id, 则只查询该组的司机id  逗号分隔   例如  '1','2'</p>
     * @param teamIds  车队id
     * @param groupIds  小组id
     * @return
     * return: String
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public String queryAuthorityDriverIdsByTeamAndGroup(String teamIds,String groupIds){
        //车队id如果 为空，将用户的车队id赋值
        if(StringUtils.isEmpty(teamIds)){
            Set<Integer> teamIds2 = WebSessionUtil.getCurrentLoginUser().getTeamIds();
            teamIds = teamIds2 != null ? teamIds2.toString() : "";
        }
        String driverIdTeam = null;
        //如果组不为空，查该组下的司机列表,如果车队不为空，查询车队下的关联关系
        if(StringUtils.isNotEmpty(groupIds) && !"null".equals(groupIds)){
            List<CarRelateTeam> driverIds = carRelateTeamExMapper.queryListByGroupIds(groupIds);
            driverIdTeam = this.pingDriverIds(driverIds);
        }else if(StringUtils.isNotEmpty(teamIds) && !"null".equals(teamIds)){
            //把逗号分隔的字符串改为可以拼接的sql  例如'1','2','3'
            String[] teamId = teamIds.split(Constants.SEPERATER);
            String teams = String.join(Constants.SEPERATER, teamId);
            List<CarRelateTeam> driverIdList = carRelateTeamExMapper.queryListByTeamIds(teams);
            driverIdTeam = this.pingDriverIds(driverIdList);
        }
        return driverIdTeam;
    }

    /**
     * <p>Title: pingSortName</p>
     * <p>Description: 整理排序字段</p>
     * @param sortName
     * @return String
     */
    public String pingSortName(String sortName) {
        StringBuilder builder = new StringBuilder();
        String[] name = sortName.split("");
        int length = sortName.length();
        for (int i = 0; i < length; i++) {
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(name[i]) > 0) {
                name[i] = "_" + name[i].toLowerCase();
            }
        }
        try {
            for (String str: name){
                builder.append(str);
            }
        }catch (Exception e){
        }
        return builder.toString();
    }

    /**
     *
     * <p>Title: pingDriverIds</p>
     * <p>Description: 拼接司机id</p>
     * @param list
     * @return
     * return: String
     */
    public String pingDriverIds(List<CarRelateTeam> list) {
        String driverIds = "";
        if (list != null && !list.isEmpty()){
            driverIds = list.stream().filter((elem) -> elem != null && elem.getDriverId() != null)
                    .map((carRelateTeam) -> "'" + carRelateTeam.getDriverId() + "'")
                    .collect(joining(","));
        }
        return driverIds;
    }

    public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
        if(StringUtils.isEmpty(fileName)) {
            fileName = "exportExcel";
        }
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//指定下载的文件名
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ServletOutputStream os =  response.getOutputStream();
        wb.write(os);
        os.close();
    }

    /**
     * <p>Title: querySupplierName</p>
     * <p>Description: 查询供应商名称和城市名称</p>
     * @param supplierId
     * @return
     * return: Map<String,Object>
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public Map<String,Object> querySupplierName(int supplierId){
        Map<String, Object> result = new HashMap<String, Object>();
        CarBizSupplier supplierEntity = carBizSupplierMapper.selectByPrimaryKey(supplierId);
        if(Objects.nonNull(supplierEntity)){
            result.put("supplierName", supplierEntity.getSupplierFullName());
        }else{
            result.put("supplierName", "");
        }
        return result;
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public Map<String,Object> queryCityName(int cityId){
        Map<String, Object> result = new HashMap<String, Object>();
        CarBizCity cityEntity = carBizCityMapper.selectByPrimaryKey(cityId);
        if(Objects.nonNull(cityEntity)){
            result.put("cityName", cityEntity.getCityName());
        }else{
            result.put("cityName", "");
        }
        return result;
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public Map<String,Object> querySupplierNameAndCityName(int cityId, int supplierId){
        Map<String, Object> result = new HashMap<String, Object>();
        CarBizSupplier supplierEntity = carBizSupplierMapper.selectByPrimaryKey(supplierId);
        if(supplierEntity!=null){
            result.put("supplierName", supplierEntity.getSupplierFullName());
        }else{
            result.put("supplierName", "");
        }
        CarBizCity cityEntity = carBizCityMapper.selectByPrimaryKey(cityId);
        if(cityEntity!=null){
            result.put("cityName", cityEntity.getCityName());
        }else{
            result.put("cityName", "");
        }
        return result;
    }

}
