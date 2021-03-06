package com.zhuanche.controller.user;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.common.CitySupplierTeamService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/** 
 * 查询当前登录用户可见的城市、供应商、车队信息
 * ClassName: CitySupplierTeamController.java 
 * Date: 2018年9月3日 
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */
@Controller
@RequestMapping("/user")
public class CitySupplierTeamController {

    private static final Logger logger = LoggerFactory.getLogger(CitySupplierTeamController.class);

	@Autowired
    private CitySupplierTeamService citySupplierTeamService;

	/** 查询当前登录用户可见的供应商信息 **/
	@RequestMapping("/suppliers")
    @ResponseBody
    public AjaxResponse getSuppliers(){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarBizSupplier> carBizSuppliers = citySupplierTeamService.querySupplierList();
            return AjaxResponse.success(carBizSuppliers);
        }catch (Exception e){
            logger.error("查询城市供应商列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
	
	/** 查询供应商下可见的车队信息 **/
	@RequestMapping("/teamsForSupplier")
    @ResponseBody
    public AjaxResponse getTeamsForSupplier(@Verify(param = "supplierId", rule = "required") Integer supplierId){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarDriverTeam> carDriverTeams = citySupplierTeamService.queryDriverTeamListForSupplier(supplierId);
            return AjaxResponse.success(carDriverTeams);
        }catch (Exception e){
            logger.error("查询城市供应商车队列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
	
	/** 查询当前登录用户可见的车队信息 **/
	@RequestMapping("/teams")
    @ResponseBody
    public AjaxResponse getTeams(){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarDriverTeam> carDriverTeams = citySupplierTeamService.queryDriverTeamList();
            return AjaxResponse.success(carDriverTeams);
        }catch (Exception e){
            logger.error("查询城市供应商车队列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /** 查询当前登录用户可见的供应商信息,包括无效 **/
    @RequestMapping("/suppliersAll")
    @ResponseBody
    public AjaxResponse getSuppliersAll(){
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(currentLoginUser)){
            return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
        }
        try{
            List<CarBizSupplier> carBizSuppliers = citySupplierTeamService.querySupplierAllList();
            return AjaxResponse.success(carBizSuppliers);
        }catch (Exception e){
            logger.error("查询城市供应商列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
}
