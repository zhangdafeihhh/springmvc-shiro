package com.zhuanche.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDTO;
import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.ServiceEntity;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.rentcar.CarBizModelService;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import com.zhuanche.util.Common;

/**
  * @description: 多级联动查询
  *
  * <PRE>
  * <BR>	修改记录
  * <BR>-----------------------------------------------
  * <BR>	修改日期			修改人			修改内容
  * </PRE>
  *
  * @author lunan
  * @version 1.0
  * @since 1.0
  * @create: 2018-09-03 11:05
  *
*/
@Controller
@RequestMapping("/saas/common")
public class CommonController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	@Value("${bigdata.saas.data.url}")
	String  saasBigdataApiUrl;
	
	@Autowired
	private StatisticalAnalysisService statisticalAnalysisService;
	
    @Autowired
    private CitySupplierTeamCommonService citySupplierTeamCommonService;

    @Autowired
    private CarBizModelService carBizModelService;

    @Autowired
    private CarBizCarGroupService carBizCarGroupService;

	@Autowired
	private CarFactOrderInfoService carFactOrderInfoService;
    /**
    * @Desc:  获取城市列表
    * @param:
    * @return:
    * @Author: lunan
    * @Date: 2018/9/3
    */
    @RequestMapping("/citys")
    @ResponseBody
    public AjaxResponse getCities(){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarBizCity> carBizCities = citySupplierTeamCommonService.queryCityList();
            return AjaxResponse.success(carBizCities);
        }catch (Exception e){
            logger.error("查询城市列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
    * @Desc: 查询城市供应列表
    * @param:
    * @return:
    * @Author: lunan
    * @Date: 2018/9/3
    */
    @RequestMapping("/suppliers")
    @ResponseBody
    public AjaxResponse getSuppliers(@Verify(param = "cityId", rule = "required") Integer cityId){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarBizSupplier> carBizSuppliers = citySupplierTeamCommonService.querySupplierList(cityId);
            return AjaxResponse.success(carBizSuppliers);
        }catch (Exception e){
            logger.error("查询城市供应商列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 查询车队列表
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/3
     */
    @RequestMapping("/teams")
    @ResponseBody
    public AjaxResponse getTeams(Integer cityId
                ,@Verify(param = "supplierId", rule = "required") Integer supplierId){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarDriverTeam> carDriverTeams = citySupplierTeamCommonService.queryDriverTeamList(cityId, supplierId);
            return AjaxResponse.success(carDriverTeams);
        }catch (Exception e){
            logger.error("查询城市供应商车队列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 查询车队列表
     * @param:
     * @return:
     * @Author: jd
     * @Date: 2018/9/16
     */
    @RequestMapping("/teamsByCityId")
    @ResponseBody
    public AjaxResponse getTeamsByCityId(@Verify(param = "cityId", rule = "required") Integer cityId){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarDriverTeam> carDriverTeams = citySupplierTeamCommonService.queryDriverTeamList(cityId);
            return AjaxResponse.success(carDriverTeams);
        }catch (Exception e){
            logger.error("查询城市供应商车队列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    
    /**
	    * 查询订单服务类型字典
	    * @return
	  */
    @RequestMapping(value = "/queryServiceEntityData", method = { RequestMethod.POST,RequestMethod.GET })
    @ResponseBody
    public AjaxResponse queryServiceEntityData(){
        try{
         	logger.info("查询订单服务类型字典 :queryServiceEntityData");
         	List<ServiceTypeDTO> list = carFactOrderInfoService.selectServiceEntityList(new ServiceEntity());
            return AjaxResponse.success(list);
        }catch (Exception e){
            logger.error("查询订单服务类型字典异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    
    
    /**
     * @Desc: 查询车队小组列表
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/3
     */
    @RequestMapping("/groups")
    @ResponseBody
    public AjaxResponse getGroups(@Verify(param = "cityId", rule = "required") String cityId
            ,@Verify(param = "supplierId", rule = "required") String supplierId
            ,@Verify(param = "teamId", rule = "required") Integer teamId){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        if(Check.NuNObjs(cityId,supplierId,teamId)){
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        try{
            List<CarDriverTeam> carDriverTeams = citySupplierTeamCommonService.queryTeamsById(teamId);
            return AjaxResponse.success(carDriverTeams);
        }catch (Exception e){
            logger.error("查询城市供应商车队列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     *查询用车型类型列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/queryGroup",method = RequestMethod.GET)
    public AjaxResponse queryGroup() {
        logger.info("queryGroup:查询用车型类型");
        List<CarBizCarGroup> list = carBizCarGroupService.queryCarGroupList(1);
        return AjaxResponse.success(list);
    }
    
    /**
     * @Desc: 查询统计分析 - 完成订单下拉列表查询接口  
     * @param:
     * @return:
     * @Author: jdd
     * @Date: 2018/9/17
     */
    @RequestMapping("/queryListBigDataDropdown")
    @ResponseBody
    public AjaxResponse queryListBigDataDropdown(
    		@Verify(param = "typeName", rule = "required") String typeName,
    		String cityId){
        try{
        	Map<String, Object> paramMap = new HashMap<String, Object>();
        	paramMap.put("typeName", typeName);//
        	 if(StringUtil.isNotEmpty(cityId)){
 	        	paramMap.put("cityId", cityId);//
 	        }
            String url =saasBigdataApiUrl+"/dropdown/queryList";
            // 从大数据仓库获取统计数据
 	        String resultStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap)
 	        			.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
			JSONObject job = JSON.parseObject(resultStr);
			if (job == null) {
				logger.error("调用订单接口" + url + "返回结果为null");
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
			if (!job.getString("code").equals("0")) {
				logger.info("调用订单接口" + url + "返回结果为result"+resultStr);
				return AjaxResponse.failMsg(Integer.parseInt(job.getString("code")), job.getString("message"));
			}
			if (job != null) {
				if("0".equals(job.get("code").toString())){
					List<Map> list =  JSONArray.parseArray(job.get("result").toString(), Map.class);   
					return AjaxResponse.success(list);
				}
			}
        }catch (Exception e){
            logger.error("查询统计分析 - 完成订单下拉列表查询接口异常",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
    }
    
	
    
}

