package com.zhuanche.serv.authc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.CarAdmUserDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.SaasRole;
import com.zhuanche.entity.mdbcarmanage.SaasUserRoleRalation;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.RedisSessionDAO;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.PasswordUtil;

import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.SaasRoleExMapper;
import mapper.mdbcarmanage.ex.SaasUserRoleRalationExMapper;

/**用户管理功能**/
@Service
public class UserManagementService{
//	@Autowired
//	private SaasRoleMapper     saasRoleMapper;
	@Autowired
	private SaasRoleExMapper saasRoleExMapper;
	@Autowired
	private SaasUserRoleRalationExMapper saasUserRoleRalationExMapper;
	@Autowired
	private CarAdmUserMapper      carAdmUserMapper;
	@Autowired
	private CarAdmUserExMapper  carAdmUserExMapper;
	@Autowired
	private RedisSessionDAO          redisSessionDAO;
	
	/**一、增加一个用户**/
	public AjaxResponse addUser( CarAdmUser user ) {
		user.setUserId(null);
		//账号已经存在
		CarAdmUser po = carAdmUserExMapper.queryByAccount(user.getAccount());
		if(po!=null) {
			return AjaxResponse.fail(RestErrorCode.ACCOUNT_EXIST );
		}
		if( StringUtils.isEmpty(user.getUserName()) ) {
			user.setUserName(null);
		}
		user.setPassword( PasswordUtil.md5(SaasConst.INITIAL_PASSWORD, user.getAccount())  );
		user.setAccountType(100);
		user.setStatus(200);
		SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
		user.setRemark( ssoLoginUser.getLoginName() );
		user.setCreateUser( ssoLoginUser.getName()  );
		user.setCreateDate(new Date());
		if( StringUtils.isEmpty(user.getCities()) ) {
			user.setCities("");
		}
		if( StringUtils.isEmpty(user.getSuppliers()) ) {
			user.setSuppliers("");
		}
		if( StringUtils.isEmpty(user.getTeamId()) ) {
			user.setTeamId("");
		}
		//保存
		carAdmUserMapper.insertSelective(user);
		return AjaxResponse.success( null );
	}

	/**二、禁用一个用户**/
	public AjaxResponse disableUser ( Integer userId ) {
		//用户不存在
		CarAdmUser user = carAdmUserMapper.selectByPrimaryKey(userId);
		if( user==null ) {
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST );
		}
		//执行
		CarAdmUser userForUpdate = new CarAdmUser();
		userForUpdate.setUserId(userId);
		userForUpdate.setStatus(100);
		carAdmUserMapper.updateByPrimaryKeySelective(userForUpdate);
		redisSessionDAO.clearRelativeSession(null, null , userId);//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
	/**三、启用一个用户**/
	public AjaxResponse enableUser ( Integer userId ) {
		//用户不存在
		CarAdmUser user = carAdmUserMapper.selectByPrimaryKey(userId);
		if( user==null ) {
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST );
		}
		//执行
		CarAdmUser userForUpdate = new CarAdmUser();
		userForUpdate.setUserId(userId);
		userForUpdate.setStatus(200);
		carAdmUserMapper.updateByPrimaryKeySelective(userForUpdate);
		redisSessionDAO.clearRelativeSession(null, null , userId);//自动清理用户会话
		return AjaxResponse.success( null );
	}

	/**四、修改一个用户**/
	public 	AjaxResponse changeUser( CarAdmUser newUser ) {
		//用户不存在
		CarAdmUser rawuser = carAdmUserMapper.selectByPrimaryKey(newUser.getUserId());
		if( rawuser==null ) {
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST );
		}
		//可以修改的字段
		if( StringUtils.isEmpty(newUser.getUserName()) ) {
			newUser.setUserName(null);
		}
		if( StringUtils.isEmpty(newUser.getCities()) ) {
			newUser.setCities("");
		}
		if( StringUtils.isEmpty(newUser.getSuppliers()) ) {
			newUser.setSuppliers("");
		}
		if( StringUtils.isEmpty(newUser.getTeamId()) ) {
			newUser.setTeamId("");
		}
		//执行
		carAdmUserMapper.updateByPrimaryKeySelective(newUser);
		redisSessionDAO.clearRelativeSession(null, null , newUser.getUserId() );//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
	/**六、查询一个用户中的角色ID**/
	public List<Integer> getAllRoleIds( Integer userId ){
		List<Integer> roleIds = saasUserRoleRalationExMapper.queryRoleIdsOfUser(userId);
		return roleIds;
	}
	
	/**七、保存一个用户中的角色ID**/
	public AjaxResponse saveRoleIds( Integer userId, List<Integer> roleIds) {
		//先删除
		saasUserRoleRalationExMapper.deleteRoleIdsOfUser(userId);
		//再插入
		if( roleIds!=null && roleIds.size()>0 ) {
			List<SaasUserRoleRalation> records = new ArrayList<SaasUserRoleRalation>(  roleIds.size() );
			for(Integer roleId : roleIds ) {
				SaasUserRoleRalation ralation = new SaasUserRoleRalation();
				ralation.setUserId(userId);	
				ralation.setRoleId(roleId);
				records.add(ralation);
			}
			saasUserRoleRalationExMapper.insertBatch(records);
		}
		redisSessionDAO.clearRelativeSession(null, null , userId );//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
	/**八、查询用户列表**/
	@SuppressWarnings("rawtypes")
	public PageDTO queryUserList( Integer page, Integer pageSize,  Integer roleId , String account , String userName, String phone , Integer status ) {
    	//一、参数修正
		if(page==null || page.intValue()<=0) {
			page = new Integer(1);
		}
		if(pageSize==null || pageSize.intValue()<=0) {
			pageSize = new Integer(20);
		}
		if( StringUtils.isEmpty(account) ) {
			account = null;
		}
		if( StringUtils.isEmpty(userName) ) {
			userName = null;
		}
		if( StringUtils.isNotEmpty(userName) ) {
			userName = userName.replace("/", "//").replace("%", "/%").replace("_", "/_");
			userName = "%"+userName+"%";
		}
		if( StringUtils.isEmpty(phone) ) {
			phone = null;
		}
		if( status!=null && status.intValue()!=100 && status.intValue()!=200 ) {
			status = null;
		}
    	//二、开始查询DB
		//2.1 查询出角色相关联的用户ID
		List<Integer> userIds = new ArrayList<Integer>();
		if(roleId!=null && roleId.intValue()>0) {
			userIds = saasUserRoleRalationExMapper.queryUserIdsOfRole(  Arrays.asList(new Integer[] { roleId})   );
			if( userIds!=null && userIds.size()==0 ) {
				return new PageDTO( page, pageSize, 0 , new ArrayList()  );//肯定查询不到，直接返回
			}
		}
		//2.2 执行SQL 查询
    	int total = 0;
    	List<CarAdmUser> users = null;
    	Page p = PageHelper.startPage( page, pageSize, true );
    	try{
    		users = carAdmUserExMapper.queryUsers( userIds ,  account, userName, phone, status );
        	total    = (int)p.getTotal();
    	}finally {
        	PageHelper.clearPage();
    	}
    	
    	//三、判断返回结果
    	if(users==null|| users.size()==0) {
    		return new PageDTO( page, pageSize, total , new ArrayList()  );
    	}
    	List<CarAdmUserDTO> roledtos = BeanUtil.copyList(users, CarAdmUserDTO.class);
    	//3.1补充上角色名称
    	Map<Integer,String> roleIdNameMappings = this.searchRoleIdNameMappings();
    	for( CarAdmUserDTO admUserDto : roledtos ) {
    		List<Integer> roleIdsOfthisUser = saasRoleExMapper.queryRoleIdsOfUser(admUserDto.getUserId());//根据用户ID，查询其拥有的所有有效的角色ID
    		//拼接角色名称
    		StringBuffer sbRoleNames =  new StringBuffer("");
			for( Integer rid : roleIdsOfthisUser) {
				String rname = roleIdNameMappings.get(rid);
				if( StringUtils.isNotEmpty(rname) ) {
					sbRoleNames.append(rname).append(",");
				}
			}
			String roleNames = sbRoleNames.toString();
			if(roleNames.endsWith(",")) {
				roleNames = roleNames.substring(0, roleNames.length()-1);
			}
			if(StringUtils.isNotEmpty(roleNames)) {
				admUserDto.setRoleNames(roleNames);//设置角色名称
			}
    	}
    	//返回
    	return new PageDTO( page, pageSize, total , roledtos);
	}
	private Map<Integer,String> searchRoleIdNameMappings(){//获得角色ID与角色名称的映射MAP
		List<SaasRole> allRoles =   saasRoleExMapper.queryRoles(null, null, null, null);
		Map<Integer,String> result = new HashMap<Integer,String>( allRoles.size() * 2 );
		for( SaasRole role : allRoles ) {
			result.put(role.getRoleId(), role.getRoleName());
		}
		return result;
	}
	

	/**九、重置密码**/
	public AjaxResponse resetPassword( Integer userId ) {
		//用户不存在
		CarAdmUser rawuser = carAdmUserMapper.selectByPrimaryKey( userId );
		if( rawuser==null ) {
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST );
		}
		//执行
		CarAdmUser userForupdate = new CarAdmUser();
		userForupdate.setUserId(userId);
		userForupdate.setPassword(  PasswordUtil.md5(SaasConst.INITIAL_PASSWORD, rawuser.getAccount()) );
		redisSessionDAO.clearRelativeSession(null, null , userId );//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
}