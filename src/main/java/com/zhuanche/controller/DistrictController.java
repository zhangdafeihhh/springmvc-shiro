package com.zhuanche.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.zhuanche.entity.driver.DriverBigdataDistrict;
import com.zhuanche.entity.driver.DriverCityWhite;
import com.zhuanche.entity.rentcar.District;
import com.zhuanche.service.driver.DriverBigdataDistrictService;
import com.zhuanche.service.driver.DriverCityWhiteService;
import com.zhuanche.service.rentcar.DistrictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/district")
public class DistrictController {

    private static final Logger logger = LoggerFactory.getLogger(DistrictController.class);

    @Autowired
    private DriverCityWhiteService driverCityWhiteService;

    @Autowired
    private DriverBigdataDistrictService driverBigdataDistrictService;

    @Autowired
    private DistrictService districtService;

    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model){
        try {
            List<DriverCityWhite> cityList = driverCityWhiteService.findList();
            model.addAttribute("cityList",cityList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/district/index";
    }

    @ResponseBody
    @RequestMapping(value = "/queryDistrictData", method = RequestMethod.POST)
    public Object hiveMapData(HttpServletRequest request, Integer cityId) {
        Map<String, Object> result = Maps.newHashMap();
        /*String city = request.getParameter("cityId");
        if(StringUtils.isEmpty(city)){
            result.put("isSuccess",false);
            return result;
        }
        Integer cityId = Integer.valueOf(city);*/
        if(cityId!=null){
            List<DriverBigdataDistrict> listByCityId = driverBigdataDistrictService.findListByCityId(cityId);
            List<District> districtList = districtService.findListByCityId(cityId);
            result.put("result1",listByCityId);//大数据商圈
            result.put("result2",districtList);//默认商圈
            result.put("isSuccess",true);
        }
        return JSON.toJSON(result);
    }
}