package com.zhuanche.controller.telescope;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.vo.telescope.TelescopeUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**用户登录相关的功能**/
@Controller
@RequestMapping("/telescope")
public class TelescopeController {
	private static final Logger log        =  LoggerFactory.getLogger(TelescopeController.class);
	
	/**查询千里眼用户列表**/
	@RequestMapping(value = "/queryTelescopeUserForList" )
	@ResponseBody
    public AjaxResponse queryTelescopeUserForList( Integer cityId, Integer supplierId, Integer teamId, Integer teamGroupId , Integer status, String phone, Integer page, Integer pageSize){
		int total = 0;
		List<TelescopeUserVo> list =  Lists.newArrayList();
		TelescopeUserVo telescopeUserVo = new TelescopeUserVo();
		telescopeUserVo.setDriverId(1000000);
		telescopeUserVo.setName("于师傅");
		telescopeUserVo.setPhone("13666666666");
		telescopeUserVo.setDriverStatus(1);
		telescopeUserVo.setStatus(1);
		telescopeUserVo.setCityId(44);
		telescopeUserVo.setCityName("北京");
		telescopeUserVo.setSupplierId(111);
		telescopeUserVo.setSupplierName("测试供应商");
		telescopeUserVo.setTeamId(111);
		telescopeUserVo.setTeamName("测试车队");
		telescopeUserVo.setTeamGroupId(111);
		telescopeUserVo.setTeamGroupName("测试小组");
		list.add(telescopeUserVo);
		PageInfo<TelescopeUserVo> p = new PageInfo<TelescopeUserVo>(list);
		if(p != null){
			list = p.getList();
			total = (int)p.getTotal();
		}
		PageDTO pageDTO = new PageDTO(1, 1, total, list);
		return AjaxResponse.success( pageDTO );
	}
	
	/**修改千里眼权限状态**/
	@RequestMapping(value = "/updateTelescopeStatus" )
	@ResponseBody
    public AjaxResponse updateTelescopeStatus(Integer driverId, Integer status) throws IOException{
		return AjaxResponse.success(null);
    }
	
	/**新增千里眼用户权限**/
	@RequestMapping("/addTelescopeUser")
	@ResponseBody
    public AjaxResponse dologout( HttpServletRequest request , HttpServletResponse response ) throws Exception{
		return AjaxResponse.success(null);
	}
	
	/**修改千里眼用户权限**/
	@RequestMapping("/updateTelescopeUser")
	@ResponseBody
    public AjaxResponse updateTelescopeUser(){
		return AjaxResponse.success( null );
	}

	@RequestMapping("/queryTelescopeUser")
	@ResponseBody
    public AjaxResponse queryTelescopeUser(){
		TelescopeUserVo telescopeUserVo = new TelescopeUserVo();
		telescopeUserVo.setDriverId(1000000);
		telescopeUserVo.setName("于师傅");
		telescopeUserVo.setPhone("13666666666");
		telescopeUserVo.setDriverStatus(1);
		telescopeUserVo.setStatus(1);
		telescopeUserVo.setCityId(44);
		telescopeUserVo.setCityName("北京");
		telescopeUserVo.setSupplierId(111);
		telescopeUserVo.setSupplierName("测试供应商");
		telescopeUserVo.setTeamId(111);
		telescopeUserVo.setTeamName("测试车队");
		telescopeUserVo.setTeamGroupId(111);
		telescopeUserVo.setTeamGroupName("测试小组");
		telescopeUserVo.setPermissionCityIds("44");
		telescopeUserVo.setPermissionSupplierIds("111");
		telescopeUserVo.setPermissionTeamIds("111");
		telescopeUserVo.setPermissionTeamGroupIds("111");
		return AjaxResponse.success(telescopeUserVo);
	}

	
}