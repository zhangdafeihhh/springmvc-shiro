package com.zhuanche.controller;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.ServiceEntity;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.rentcar.CarBizModelService;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    public AjaxResponse getTeams(@Verify(param = "cityId", rule = "required") Integer cityId
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
}

