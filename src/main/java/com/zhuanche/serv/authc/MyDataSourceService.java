package com.zhuanche.serv.authc;


import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.shiro.realm.SSOLoginUser;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author
 * 用于设置方法数据源**/
@Service
public class MyDataSourceService {

    private static final Logger logger = LoggerFactory.getLogger(MyDataSourceService.class);
    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;


    /**
     * 获取用户的身份认证信息
     * @param loginName
     * @return
     */
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.MASTER )
    } )
    public SSOLoginUser getSsoLoginUser(String loginName){
        logger.info( "[WebSessionUtil获取用户的身份认证信息开始]loginName={}"+loginName);
        try {
            CarAdmUser adMuser = carAdmUserExMapper.queryByAccount( loginName );
            //当前登录的用户
            SSOLoginUser loginUser = new SSOLoginUser();
            //用户ID
            loginUser.setId( adMuser.getUserId() );
            //登录名
            loginUser.setLoginName( adMuser.getAccount() );
            //手机号码
            loginUser.setMobile( adMuser.getPhone() );
            //真实姓名
            loginUser.setName( adMuser.getUserName() );
            //邮箱地址
            loginUser.setEmail(adMuser.getEmail());
            loginUser.setType( null );
            //状态
            loginUser.setStatus( adMuser.getStatus() );
            //自有的帐号类型：[100 普通用户]、[900 管理员]
            loginUser.setAccountType( adMuser.getAccountType() );
            loginUser.setLevel(adMuser.getLevel());
            //---------------------------------------------------------------------------------------------------------数据权限BEGIN
            /**此用户可以管理的城市ID**/
            if( StringUtils.isNotEmpty(adMuser.getCities()) ) {
                String[] idStrs = adMuser.getCities().trim().split(",");
                Set<Integer> ids = new HashSet<Integer>( idStrs.length *2 +2 );
                for(String id : idStrs) {
                    if( StringUtils.isNotEmpty(id) ) {
                        try { ids.add(  Integer.valueOf(id.trim()) ); }catch(Exception e) {}
                    }
                }
                loginUser.setCityIds(ids);
            }
            /**此用户可以管理的供应商ID**/
            if( StringUtils.isNotEmpty(adMuser.getSuppliers()) ) {
                String[] idStrs = adMuser.getSuppliers().trim().split(",");
                Set<Integer> ids = new HashSet<Integer>( idStrs.length *2 +2 );
                for(String id : idStrs) {
                    if( StringUtils.isNotEmpty(id) ) {
                        try { ids.add(  Integer.valueOf(id.trim()) ); }catch(Exception e) {}
                    }
                }
                loginUser.setSupplierIds(ids);
            }
            /**此用户可以管理的车队ID**/
            if( StringUtils.isNotEmpty(adMuser.getTeamId()) ) {
                String[] idStrs = adMuser.getTeamId().trim().split(",");
                Set<Integer> ids = new HashSet<Integer>( idStrs.length *2 +2 );
                for(String id : idStrs) {
                    if( StringUtils.isNotEmpty(id) ) {
                        try { ids.add(  Integer.valueOf(id.trim()) ); }catch(Exception e) {}
                    }
                }
                loginUser.setTeamIds(ids);
            }
            /**此用户可以管理的班组ID**/
            if(StringUtils.isNotEmpty(adMuser.getGroupIds())){
                String[] idStrs = adMuser.getGroupIds().trim().split(",");
                Set<Integer> ids = new HashSet<>(idStrs.length * 2 + 2);
                for(String id : idStrs){
                    if(StringUtils.isNotEmpty(id)){
                        try {
                            ids.add(Integer.valueOf(id.trim()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                loginUser.setGroupIds(ids);
            }
            //---------------------------------------------------------------------------------------------------------数据权限END
            logger.info( "[WebSessionUtil获取用户的身份认证信息]="+loginUser);
            return loginUser;
        } catch (Exception e) {
            logger.error("WebSessionUtil获取用户的身份认证信息异常",e);
            return null;
        }
    }


}
