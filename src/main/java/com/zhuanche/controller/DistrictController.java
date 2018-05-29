package com.zhuanche.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.driver.DriverBigdataDistrict;
import com.zhuanche.entity.driver.DriverCityWhite;
import com.zhuanche.entity.rentcar.CarBizDistrict;
import com.zhuanche.serv.DemoService;

import mapper.driver.ex.DriverBigdataDistrictExMapper;
import mapper.driver.ex.DriverCityWhiteExMapper;
import mapper.rentcar.ex.CarBizDistrictExMapper;

@Controller
@RequestMapping("/district")
public class DistrictController {

    private static final Logger logger = LoggerFactory.getLogger(DistrictController.class);

    @Autowired
    private DriverCityWhiteExMapper driverCityWhiteExMapper;

    @Autowired
    private DriverBigdataDistrictExMapper driverBigdataDistrictExMapper;

    @Autowired
    private CarBizDistrictExMapper carBizDistrictExMapper;
    
    @Autowired
    DemoService demoService;
    
    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model){
        try{
            List<DriverCityWhite> cityList = driverCityWhiteExMapper.findList();
            model.addAttribute("cityList",cityList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/district/index";
    }

   @ResponseBody
   @RequestMapping(value = "/queryDistrictData")
   @MasterSlaveConfigs(configs={ 
		  @MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER ),
		  @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public Object hiveMapData(HttpServletRequest request, Integer cityId) {
        Map<String, Object> result = Maps.newHashMap();
        
        String a = request.getRequestURI();
    	System.out.println(  a );
    	
    	demoService.sayhello();
    	System.out.println( System.getenv("env.name") );

    	String key = "ADSFSFSFSFSDFS090SF0000000SSS";
    	
    	RedisCacheUtil.set(key, "YALI", 20);
    	String value = RedisCacheUtil.get(key, String.class);
        System.out.println( value );
        
        CarBizDistrict obj = new CarBizDistrict();
        obj.setCityId(10086);
        obj.setCityName("中山市");
        RedisCacheUtil.set(key,   obj );
        CarBizDistrict value2 = RedisCacheUtil.get(key, CarBizDistrict.class);
        System.out.println( value2 );
        
        RedisCacheUtil.delete(key);
        
        Long cnt = RedisCacheUtil.incr(key);
         cnt = RedisCacheUtil.incr(key);
         cnt = RedisCacheUtil.incr(key);
         cnt = RedisCacheUtil.incr(key);
         System.out.println( cnt );
         cnt = RedisCacheUtil.decr(key);
         System.out.println( cnt );
         
         RedisCacheUtil.delete(key);
        
        
        /*String city = request.getParameter("cityId");
        if(StringUtils.isEmpty(city)){
            result.put("isSuccess",false);
            return result;
        }
        Integer cityId = Integer.valueOf(city);*/
        if(cityId!=null){
            SimpleDateFormat df_hh = new SimpleDateFormat("HH");//获取当前时间
            List<DriverBigdataDistrict> listByCityId = driverBigdataDistrictExMapper.findListByCityId(cityId, new Date(), df_hh.format(new Date()) );
        	
            List<CarBizDistrict> districtList = carBizDistrictExMapper.queryCarBizDistrict(cityId);
            result.put("result1",listByCityId);//大数据商圈
            result.put("result2",districtList);//默认商圈
            result.put("isSuccess",true);
        }
        return result;
    }
}