package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.busManage.BusDriverViolatorsQueryDTO;
import com.zhuanche.dto.busManage.BusDriverViolatorsSaveDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizDriverViolators;
import com.zhuanche.serv.busManage.BusBizChangeLogService;
import com.zhuanche.serv.busManage.BusCarBizDriverInfoService;
import com.zhuanche.serv.busManage.BusCarViolatorsService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.vo.busManage.BusBizDriverViolatorsVO;
import com.zhuanche.vo.busManage.BusDriverDetailInfoVO;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


/**
 * 违规司机处理
 */
@RestController
@RequestMapping("/bus/violateDriver")
@Validated
public class BusDriverViolatorsController extends BusBaseController {

	private static final Logger logger = LoggerFactory.getLogger(BusDriverViolatorsController.class);

	@Autowired
	private BusCarBizDriverInfoService busCarBizDriverInfoService;

	@Autowired
	private BusBizChangeLogService busBizChangeLogService;

	@Autowired
	private BusCarViolatorsService busCarViolatorsService;

	@RequestMapping(value = "/queryList",method = RequestMethod.GET)
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse queryList(@Validated BusDriverViolatorsQueryDTO queryDTO) {
		logger.info("【获取违规司机处理列表】start...params:queryDTO="+JSON.toJSONString(queryDTO));
		try{
			SSOLoginUser user=WebSessionUtil.getCurrentLoginUser();
			logger.info("【获取违规司机处理列表】当前登录人信息={}", JSON.toJSONString(user));
			Integer level=user.getLevel();//此用户数据权限等级(巴士包含 1全国 2城市 4供应商)
			if(level==2){queryDTO.setAuthOfCity(user.getCityIds());}
			if(level>=4){queryDTO.setAuthOfSupplier(user.getSupplierIds());}
			List<BusBizDriverViolatorsVO> violatorsList = busCarViolatorsService.queryDriverViolatorsList(queryDTO);
			Page<BusBizDriverViolatorsVO> page = (Page<BusBizDriverViolatorsVO>) violatorsList;
			PageDTO pageDTO = new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), violatorsList);
			return AjaxResponse.success(pageDTO);
		}catch (Exception e){
			logger.error("【获取违规司机处理列表】 error:",e);
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "获取违规司机处理列表程序异常");
		}
	}

	/**
	 * @desc 新增违规司机处罚
	 * @wiki http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31809270
	 * @param saveDTO
	 * @return
	 */
	@RequestMapping(value = "/saveViolateDriver",method = RequestMethod.POST)
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse saveViolateDriver(@Validated BusDriverViolatorsSaveDTO saveDTO) {
		logger.info("【新增违规司机处罚】start...params:saveDTO="+JSON.toJSONString(saveDTO));
		try{
			if(saveDTO.getPunishType()!=1){
				return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "参数处罚状态不正确,目前只支持停运处罚");
			}
			Double punishDuration;
			try{
				punishDuration=Double.parseDouble(saveDTO.getPunishDuration().trim());
			}catch (Exception e){
				logger.error("【新增违规司机处罚】参数停运时长错误",e);
				return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "参数停运时长错误");
			}
			if(punishDuration<=0){
				logger.info("【新增违规司机处罚】参数处罚时长不能为零或负数");
				return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "处罚时长不能为零或负数");
			}
			Date punishStartTime=saveDTO.getPunishStartTime();
			if(punishStartTime.before(new Date())){
				logger.info("【新增违规司机处罚】处罚开始时间不能早于当前时间");
				return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "处罚开始时间不能早于当前时间");
			}
			SSOLoginUser loginUser=WebSessionUtil.getCurrentLoginUser();
			logger.info("【新增违规司机处罚】当前登录人信息={}", JSON.toJSONString(loginUser));
			Integer level=loginUser.getLevel();//此用户数据权限等级(巴士包含 1全国 2城市 4供应商)
			String busDriverPhone=saveDTO.getBusDriverPhone();
			BusDriverDetailInfoVO driverInfo=busCarBizDriverInfoService.selectByBusDriverPhone(String.valueOf(busDriverPhone));
			if(driverInfo==null){
				return AjaxResponse.failMsg(RestErrorCode.DRIVER_NOT_EXIST, "巴士司机不存在");
			}
			if(level==2&&!loginUser.getCityIds().contains(driverInfo.getCityId())){
				logger.info("【新增违规司机处罚】要新增违规处罚的巴士司机所属城市不在当前登陆用户权限[城市权限]所包含的城市范围内，没有违规处罚权限");
				return AjaxResponse.failMsg(RestErrorCode.BUS_CITY_AUTH_FORBIDDEN, "要新增违规处罚的巴士司机所属城市不在当前登陆用户权限所包含的城市范围内，没有违规处罚权限");
			}
			if((level>=4)&&!loginUser.getSupplierIds().contains(driverInfo.getSupplierId())){
				logger.info("【新增违规司机处罚】要新增违规处罚的巴士司机所属供应商不在当前登陆用户权限所包含的供应商范围内");
				return AjaxResponse.failMsg(RestErrorCode.BUS_SUPPLIER_AUTH_FORBIDDEN, "要新增违规处罚的巴士司机所属供应商不在当前登陆用户权限所包含的供应商范围内，没有违规处罚权限");
			}
			Integer id=busCarViolatorsService.insertDriverViolator(saveDTO,driverInfo);
			if(id!=null){
				busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.BUS_PUNISH, String.valueOf(id), "创建处罚信息", new Date());
				return AjaxResponse.success(1);
			}else{
				return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "新增违规巴士司机违规处罚记录失败");
			}
		}catch (Exception e){
			logger.error("【新增违规司机处罚】 error:",e);
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "新增违规司机处罚后台程序异常");
		}
	}

	/**
	 * @desc 通过巴士司机手机号获取司机姓名
	 * @wiki http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31812293
	 * @param busDriverPhone
	 * @return
	 */
	@RequestMapping(value = "/getBusDriverName",method = RequestMethod.GET)
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse getBusDriverNameByPhone(@NotNull(message = "司机手机号不能为空") Long busDriverPhone){
		logger.info("【根据手机号获取巴士司机姓名】start...param:busDriverPhone="+ busDriverPhone);
		try{
			SSOLoginUser loginUser=WebSessionUtil.getCurrentLoginUser();
			logger.info("【根据手机号获取巴士司机姓名】当前登录人信息={}", JSON.toJSONString(loginUser));
			Integer level=loginUser.getLevel();//此用户数据权限等级(巴士包含 1全国 2城市 4供应商)
			BusDriverDetailInfoVO driverInfo=busCarBizDriverInfoService.selectByBusDriverPhone(String.valueOf(busDriverPhone));
			if(driverInfo==null){
				return AjaxResponse.failMsg(RestErrorCode.DRIVER_NOT_EXIST, "巴士司机不存在");
			}
			if(level==2&&!loginUser.getCityIds().contains(driverInfo.getCityId())){
				logger.info("【根据手机号获取巴士司机姓名】查询到的巴士司机所属城市不在当前登陆用户权限[城市权限]所包含的城市范围内");
				return AjaxResponse.failMsg(RestErrorCode.BUS_CITY_AUTH_FORBIDDEN, "查询到的巴士司机所属城市不在当前登陆用户权限所包含的城市范围内");
			}
			if((level>=4)&&!loginUser.getSupplierIds().contains(driverInfo.getSupplierId())){
				logger.info("【根据手机号获取巴士司机姓名】查询到的巴士司机所属供应商不在当前登陆用户权限所包含的供应商范围内");
				return AjaxResponse.failMsg(RestErrorCode.BUS_SUPPLIER_AUTH_FORBIDDEN, "查询到的巴士司机所属供应商不在当前登陆用户权限所包含的供应商范围内");
			}
			logger.info("【根据手机号获取巴士司机姓名】通过巴士司机手机号 {}获取司机姓名是 {}",busDriverPhone,driverInfo.getName());
			return AjaxResponse.success(driverInfo.getName());
		}catch (Exception e){
			logger.error("【根据手机号获取巴士司机姓名】 error:",e);
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "根据手机号获取巴士司机姓名程序异常");
		}
	}

	@RequestMapping(value = "/recover")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER))
	public AjaxResponse recover(Integer id) {
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
		logger.info("【将违约司机恢复正常状态】参数：id=" + id + " 操作人：" + currentLoginUser.getName());
		try {
			BusBizDriverViolators violators = busCarViolatorsService.selectByPrimaryKey(id);
			if (violators == null) {
				logger.error("【将违约司机恢复正常状态】参数：id=" + id + " 操作人：" + currentLoginUser.getName() + " 不存在该记录");
				return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST);
			}
			int result = busCarViolatorsService.recoverDriverStatus(id);
			logger.info("【将违约司机恢复正常状态】参数：id=" + id + " 操作人：" + currentLoginUser.getName() + " 结果：" + result);
			//原始状态
			Short punishType = violators.getPunishType();
			String des = punishType == 1 ? "解除停运" : punishType == 2 ? "解除冻结" : "";
			busBizChangeLogService.insertLog(BusBizChangeLogExMapper.BusinessType.BUS_PUNISH, String.valueOf(violators.getId()), des, new Date());
			return AjaxResponse.success(null);
		} catch (Exception e) {
			logger.error("【将违约司机恢复正常状态】参数：id=" + id + " 操作人：" + currentLoginUser.getName() + " 修改异常：e:{}", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

}
