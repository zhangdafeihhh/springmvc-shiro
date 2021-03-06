package com.zhuanche.controller.user;

import com.le.config.dict.Dicts;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.constants.SmsTempleConstants;
import com.zhuanche.dto.AjaxLoginUserDTO;
import com.zhuanche.dto.SaasPermissionDTO;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.SaasPermission;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.realm.UsernamePasswordRealm;
import com.zhuanche.shiro.session.RedisSessionDAO;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.NumberUtil;
import com.zhuanche.util.PasswordUtil;
import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.SaasPermissionExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**???????????????????????????**/
@Controller
public class LoginController{
	private static final Logger log        =  LoggerFactory.getLogger(LoginController.class);
	private static final String CACHE_PREFIX_MSGCODE_CONTROL = "mp_login_cache_msgcode_control_";
	private static final String CACHE_PREFIX_MSGCODE                   = "mp_login_cache_msgcode_";
    @Value(value="${loginpage.url}")
    private String loginpageUrl;  //??????UI????????????
	@Value("${homepage.url}")
	private String homepageUrl; //??????UI????????????
	@Value("${login.checkMsgCode.switch}")
	private String loginCheckMsgCodeSwitch = "ON";//??????????????????????????????????????????
	
	private int msgcodeTimeoutMinutes = 2;
	
	@Autowired
	private CarAdmUserMapper carAdmUserMapper;
	@Autowired
	private CarAdmUserExMapper carAdmUserExMapper;
	@Autowired
	private SaasPermissionExMapper saasPermissionExMapper;
	@Autowired
	private UsernamePasswordRealm usernamePasswordRealm;
	@Autowired
	private RedisSessionDAO redisSessionDAO;

	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;
	
	/**????????????????????????????????????????????????**/
	@RequestMapping(value = "/getMsgCode" )
	@ResponseBody
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode=DataSourceMode.SLAVE )
	} )
    public AjaxResponse getMsgCode( @Verify(param="username",rule="required") String username,
									@Verify(param="password",rule="required") String password ){
		//A: ???????????? ,
		String redis_getmsgcode_key = "mp_manager_getmsgcode_key_"+username;
		long score = System.currentTimeMillis();
		//zset???????????????????????????????????????????????????????????????
		redisTemplate.opsForZSet().add(redis_getmsgcode_key, String.valueOf(score), score);
		//??????30??????????????????????????????
		int statistics = 30;
		redisTemplate.expire(redis_getmsgcode_key, statistics, TimeUnit.MINUTES);

		//????????????30??????????????????????????????
		long max = score;
		long min = max - (statistics * 60 * 1000);
		long count = redisTemplate.opsForZSet().count(redis_getmsgcode_key, min, max);

		int countLimit = 5;
		log.info("???????????????-??????"+username+"???"+statistics+"????????????"+count+"??????????????????????????????");
		if(count  > countLimit) {
			log.info("???????????????-??????"+username+"???"+statistics+"??????????????????????????????"+count+"???,????????????"+countLimit+",????????????"+statistics+"??????");
			return AjaxResponse.fail(RestErrorCode.GET_MSGCODE_EXCEED,statistics);
		}

//		String flag = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE_CONTROL+username, String.class);
//		if(flag!=null ) {
//			return AjaxResponse.fail(RestErrorCode.GET_MSGCODE_EXCEED);
//		}
		//B:??????????????????
		CarAdmUser user = carAdmUserExMapper.queryByAccount(username);
		if(user==null){
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST) ;
		}
		//C:???????????????
		String enc_pwd = PasswordUtil.md5(password, user.getAccount());
		if(!enc_pwd.equalsIgnoreCase(user.getPassword())) {
			return AjaxResponse.fail(RestErrorCode.USER_PASSWORD_WRONG) ;
		}
		//D: ?????????????????????????????????????????????????????????????????????
		String  msgcode = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE+username, String.class);
		if(msgcode==null) {
			msgcode = NumberUtil.genRandomCode(6);
		}
		String mobile   = user.getPhone();
		List list = new ArrayList();
		list.add(msgcode);
		SmsSendUtil.sendTemplate(mobile, SmsTempleConstants.loginTemple,list);
		//E: ????????????
//		RedisCacheUtil.set(CACHE_PREFIX_MSGCODE_CONTROL+username, "Y",  60 );
		RedisCacheUtil.set(CACHE_PREFIX_MSGCODE+username, msgcode,  msgcodeTimeoutMinutes * 60 );
		//????????????
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("timeout",  60 );//????????????????????????
		result.put("tipText", "??????????????????????????????????????????"+mobile.substring(7)+"???????????????" );//????????????
		return AjaxResponse.success( result );
	}
	
	/**????????????**/
	@RequestMapping(value = "/dologin" )
	@ResponseBody
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode=DataSourceMode.SLAVE )
	} )
    public AjaxResponse dologin(HttpServletRequest request , HttpServletResponse response, 
    	@Verify(param="username",rule="required") String username, 
    	@Verify(param="password",rule="required") String password, 
    	@Verify(param="msgcode",rule="required") String msgcode ) throws IOException{

		String redis_login_key = "mp_manager_login_key_"+username;
		String redis_getmsgcode_key = "mp_manager_getmsgcode_key_"+username;

		Subject currentLoginUser = SecurityUtils.getSubject();
		//A:??????????????????
		if(currentLoginUser.isAuthenticated()) {
			Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
			if(  isAjax  ) {
				return AjaxResponse.success( null );
			}else {
				response.sendRedirect(homepageUrl);
				return null;
			}
		}
		//B:??????????????????
		CarAdmUser user = carAdmUserExMapper.queryByAccount(username);
		if(user==null){
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST) ;
		}
		//C:???????????????
		String enc_pwd = PasswordUtil.md5(password, user.getAccount());
		if(!enc_pwd.equalsIgnoreCase(user.getPassword())) {
			return AjaxResponse.fail(RestErrorCode.USER_PASSWORD_WRONG) ;
		}
		//D: ???????????????????????????????????????
		if("ON".equalsIgnoreCase(loginCheckMsgCodeSwitch)) {


			long score = System.currentTimeMillis();
			//zset???????????????????????????????????????????????????????????????
			redisTemplate.opsForZSet().add(redis_login_key, String.valueOf(score), score);
			//??????30???????????????????????????
			int statistics = 30;
			redisTemplate.expire(redis_login_key, statistics, TimeUnit.MINUTES);

			//????????????30????????????????????????
			long max = score;
			long min = max - (statistics * 60 * 1000);
			long count = redisTemplate.opsForZSet().count(redis_login_key, min, max);

			int countLimit = 5;
			log.info("??????-??????"+username+"???"+statistics+"????????????"+count+"?????????");
			if(count  > countLimit) {
				log.info("??????-??????"+username+"???"+statistics+"???????????????"+count+"???,????????????"+countLimit+",????????????"+statistics+"??????");
				return AjaxResponse.fail(RestErrorCode.DO_LOGIN_FREQUENTLY,statistics);
			}
			//???????????????????????????
			String  msgcodeInCache = RedisCacheUtil.get(CACHE_PREFIX_MSGCODE+username, String.class);
			if(msgcodeInCache==null) {
				return AjaxResponse.fail(RestErrorCode.MSG_CODE_INVALID) ;
			}
			if(!msgcodeInCache.equals(msgcode)) {
				return AjaxResponse.fail(RestErrorCode.MSG_CODE_WRONG) ;
			}

		}
		//E: ????????????
		if(user.getStatus()!=null && user.getStatus().intValue()==100 ){
			return AjaxResponse.fail(RestErrorCode.USER_INVALID) ;
		}
		//F: ????????????
		try {
			//shiro??????
			UsernamePasswordToken token = new UsernamePasswordToken( username, password.toCharArray() );
			currentLoginUser.login(token);
			//?????????????????????????????????ID????????????????????????????????????????????????????????????
			String sessionId =  (String)currentLoginUser.getSession().getId() ;
			redisSessionDAO.saveSessionIdOfLoginUser(username, sessionId);

			redisTemplate.delete(redis_login_key);
			redisTemplate.delete(redis_getmsgcode_key);

		}catch(AuthenticationException aex) {
			return AjaxResponse.fail(RestErrorCode.USER_LOGIN_FAILED) ;
		}
		//??????????????????
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
		if(  isAjax  ) {
			return AjaxResponse.success( null );
		}else {
			response.sendRedirect(homepageUrl);
			return null;
		}
    }
	
	/**???????????? **/
	@RequestMapping("/dologout")
	@ResponseBody
    public AjaxResponse dologout( HttpServletRequest request , HttpServletResponse response ) throws Exception{
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()) {
			subject.logout();
		}
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
		if(  isAjax  ) {
			return AjaxResponse.success( null );
		}else {
			response.sendRedirect(loginpageUrl);
			return null;
		}
	}
	
	/**????????????**/
	@RequestMapping("/changePassword")
	@ResponseBody
    public AjaxResponse changePassword( @Verify(param="oldPassword",rule="required") String oldPassword, @Verify(param="newPassword",rule="required") String newPassword ){
		SSOLoginUser ssoLoginUser  =  WebSessionUtil.getCurrentLoginUser();
		CarAdmUser   carAdmUser    =  carAdmUserMapper.selectByPrimaryKey( ssoLoginUser.getId()  );
		//A:???????????????
		if(carAdmUser==null){
			return AjaxResponse.fail(RestErrorCode.USER_NOT_EXIST) ;
		}
		//B:???????????????
		String enc_pwd = PasswordUtil.md5(oldPassword, carAdmUser.getAccount());
		if(!enc_pwd.equalsIgnoreCase(carAdmUser.getPassword())) {
			return AjaxResponse.fail(RestErrorCode.USER_PASSWORD_WRONG) ;
		}
		//C:??????
		String new_enc_pwd = PasswordUtil.md5(newPassword, carAdmUser.getAccount());
		CarAdmUser   carAdmUserForUpdate = new  CarAdmUser();
		carAdmUserForUpdate.setUserId(carAdmUser.getUserId());
		carAdmUserForUpdate.setPassword(new_enc_pwd);
		carAdmUserMapper.updateByPrimaryKeySelective(carAdmUserForUpdate);
		return AjaxResponse.success( null );
	}
	
 
	//-------------------------------------------------------------------------------------------------------------------------------------????????????????????????BEGIN
	@RequestMapping("/currentLoginUserInfo")
	@ResponseBody
	@SuppressWarnings("unchecked")
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode=DataSourceMode.SLAVE )
	} )
    public AjaxResponse currentLoginUserInfo( String menuDataFormat ){
		SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
		
		AjaxLoginUserDTO ajaxLoginUserDTO = new AjaxLoginUserDTO();
		//????????????????????????
		ajaxLoginUserDTO.setId(ssoLoginUser.getId());
		ajaxLoginUserDTO.setLoginName(ssoLoginUser.getLoginName());
		ajaxLoginUserDTO.setMobile(ssoLoginUser.getMobile());
		ajaxLoginUserDTO.setName(ssoLoginUser.getName());
		ajaxLoginUserDTO.setEmail(ssoLoginUser.getEmail());
		ajaxLoginUserDTO.setStatus(ssoLoginUser.getStatus());
		//???????????????????????????      (  ??????Session???????????? ??????????????????   )
		if( !SaasConst.PermissionDataFormat.TREE.equalsIgnoreCase(menuDataFormat) && !SaasConst.PermissionDataFormat.LIST.equalsIgnoreCase(menuDataFormat) ) {
			menuDataFormat = SaasConst.PermissionDataFormat.TREE;//???????????????
		}
		List<SaasPermissionDTO>  menuPerms = (List<SaasPermissionDTO>)WebSessionUtil.getAttribute("xxx_menu_"+menuDataFormat);
		if( menuPerms ==null ) {
			List<Byte> permissionTypes =  Arrays.asList( new Byte[] { SaasConst.PermissionType.MENU });
			menuPerms = this.getAllPermissions( ssoLoginUser.getId()  , permissionTypes, menuDataFormat);
			if(menuPerms!=null && menuPerms.size()>0) {
				WebSessionUtil.setAttribute("xxx_menu_"+menuDataFormat, menuPerms);
			}
		}
		ajaxLoginUserDTO.setMenus( menuPerms );
		
		//??????????????????????????? ( ??????shiro?????????????????? )
		Subject subject = SecurityUtils.getSubject();
		subject.isPermitted("XXXX-XXXXXXXXX-XXXXXXXX-123456");//?????????????????????????????????shiro????????????????????????????????????
		PrincipalCollection principalCollection =subject.getPrincipals();
		if(principalCollection!=null) {
			Cache<Object, AuthorizationInfo> cache = usernamePasswordRealm.getAuthorizationCache();
			if(cache!=null) {
				AuthorizationInfo info = cache.get(  usernamePasswordRealm.getAuthorizationCacheKey(principalCollection)  );
				if(info!=null) {
					Collection<String> pemissionStrings = info.getStringPermissions();
					Collection<String> roles = info.getRoles();
					if(pemissionStrings!=null && pemissionStrings.size()>0 ) {
						ajaxLoginUserDTO.setHoldPerms( new  HashSet<String>( pemissionStrings )  );
					}
					if (roles != null && roles.size() > 0) {
						ajaxLoginUserDTO.setHoldRoles(new HashSet<String>(roles));
					}
				}
			}
		}
		//???????????????????????????
		ajaxLoginUserDTO.setCityIds( ssoLoginUser.getCityIds()  );
		ajaxLoginUserDTO.setSupplierIds( ssoLoginUser.getSupplierIds() );
		ajaxLoginUserDTO.setTeamIds( ssoLoginUser.getTeamIds() );
		
		//??????????????????
		Map<String, Object > configs = new HashMap<String,Object>();
		configs.put("mobileRegex",  SaasConst.MOBILE_REGEX);       //?????????????????????
		configs.put("accountRegex", SaasConst.ACCOUNT_REGEX);  //????????????????????????
		configs.put("emailRegex",    SaasConst.EMAIL_REGEX);         //??????????????????????????????
		ajaxLoginUserDTO.setConfigs(configs);
		return AjaxResponse.success( ajaxLoginUserDTO );
	}
	
	/**??????????????????????????????????????????????????????????????????????????????**/
	private List<SaasPermissionDTO> getAllPermissions( Integer userId,  List<Byte> permissionTypes,  String dataFormat ){
		List<Integer> validPermissionIdsOfCurrentLoginUser =  saasPermissionExMapper.queryPermissionIdsOfUser( userId ); //?????????????????????????????????????????????ID
		if(validPermissionIdsOfCurrentLoginUser==null || validPermissionIdsOfCurrentLoginUser.size()==0) {
			return null;
		}
		if(  SaasConst.PermissionDataFormat.LIST.equalsIgnoreCase(dataFormat) ) {
			return this.getAllPermissions_list( validPermissionIdsOfCurrentLoginUser, permissionTypes );
		}else if( SaasConst.PermissionDataFormat.TREE.equalsIgnoreCase(dataFormat) ) {
			return this.getAllPermissions_tree( validPermissionIdsOfCurrentLoginUser, permissionTypes );
		}
		return null;
	}
	/**??????????????????????????????**/
	private List<SaasPermissionDTO> getAllPermissions_list( List<Integer> permissionIds,  List<Byte> permissionTypes ){
		List<SaasPermission> allPos = saasPermissionExMapper.queryPermissions(permissionIds, null, null, permissionTypes, null, null);
		List<SaasPermissionDTO> allDtos = BeanUtil.copyList(allPos, SaasPermissionDTO.class);
		return allDtos;
	}
	/**??????????????????????????????**/
	private List<SaasPermissionDTO> getAllPermissions_tree( List<Integer> permissionIds,  List<Byte> permissionTypes ){
		return this.getChildren(permissionIds,  0 , permissionTypes);
	}
	private List<SaasPermissionDTO> getChildren( List<Integer> permissionIds,  Integer parentPermissionId,  List<Byte> permissionTypes ){
		List<SaasPermission> childrenPos = saasPermissionExMapper.queryPermissions(permissionIds, parentPermissionId, null, permissionTypes, null, null);
		if(childrenPos==null || childrenPos.size()==0) {
			return null;
		}
		//??????
		List<SaasPermissionDTO> childrenDtos = BeanUtil.copyList(childrenPos, SaasPermissionDTO.class);

		/**?????????????????? ????????????**/
        Iterator<SaasPermissionDTO> iterator = childrenDtos.iterator();
        while (iterator.hasNext()) {
            SaasPermissionDTO childrenDto = iterator.next();
            if (childrenDto.getPermissionCode().equals("Capacity") && !authCapacity()) {
                iterator.remove();
				continue;
            }
            List<SaasPermissionDTO> childs = this.getChildren( permissionIds, childrenDto.getPermissionId() ,  permissionTypes );
            childrenDto.setChildPermissions(childs);
        }
		return childrenDtos;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------????????????????????????END




    /**
     * @return
     */
    public boolean authCapacity(){
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Set<Integer> userCityIds = user.getCityIds();
        if(userCityIds.isEmpty()){
            return true;
        }
        Set<String> authCityIdSet = getAuthCityId();
        for (String cityId : authCityIdSet) {
            if(userCityIds.contains(Integer.valueOf(cityId))){
				return true;
            }
        }
        return false;
    }


    public Set<String> getAuthCityId(){
        String authCityIdStr = Dicts.getString("driverMonitoring_authCityIdStr", Constants.MONITOR_CITY);
        String[] strArray = authCityIdStr.split(",");
        List<String> strList =  java.util.Arrays.asList(strArray);
        Set<String> authCityIdSet = new HashSet<>(strList);
        return authCityIdSet;
    }
	
}