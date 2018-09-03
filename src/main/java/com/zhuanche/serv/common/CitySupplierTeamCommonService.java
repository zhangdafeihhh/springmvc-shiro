package com.zhuanche.serv.common;

import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
  * @description: 城市供应商车队联动查询返回service
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
  * @create: 2018-08-29 18:10
  *
*/
@Service
public class CitySupplierTeamCommonService {

    private static final Logger logger = LoggerFactory.getLogger(CitySupplierTeamCommonService.class);

    @Autowired
    private DataPermissionHelper dataPermissionHelper;

    @Autowired
    private CarBizCityExMapper carBizCityExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Autowired
    private CarDriverTeamExMapper carDriverTeamExMapper;

    /**
     * @Desc: 处理车队关联司机中间表操作
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/8/31
     */
    public  <T> Set<String> dealDriverids(List srcList, Class<T> destClass){
        if(srcList==null){
            return null;
        }
        try{
            Set<String> driverIds = new HashSet<>();
            T param = destClass.newInstance();
            if(param instanceof CarRelateGroup){
                for(int i=0;i<srcList.size();i++ ){
                    Object srcObj = srcList.get(i);
                    CarRelateGroup data = new CarRelateGroup();
                    BeanUtils.copyProperties(srcObj,data);
                    driverIds.add(String.valueOf(data.getDriverId()));
                }
                return driverIds;
            }else if(param instanceof CarRelateTeam){
                for(int i=0;i<srcList.size();i++ ){
                    Object srcObj = srcList.get(i);
                    CarRelateTeam data = new CarRelateTeam();
                    BeanUtils.copyProperties(srcObj,data);
                    driverIds.add(String.valueOf(data.getDriverId()));
                }
                return driverIds;
            }else if(param instanceof CarDriverTeam){
                for(int i=0;i<srcList.size();i++ ){
                    Object srcObj = srcList.get(i);
                    CarDriverTeam data = new CarDriverTeam();
                    BeanUtils.copyProperties(srcObj,data);
                    driverIds.add(String.valueOf(data.getId()));
                }
                return driverIds;
            }else if(param instanceof CarDriverInfoDTO){
                for(int i=0;i<srcList.size();i++ ){
                    Object srcObj = srcList.get(i);
                    CarDriverInfoDTO data = new CarDriverInfoDTO();
                    BeanUtils.copyProperties(srcObj,data);
                    driverIds.add(String.valueOf(data.getDriverId()));
                }
                return driverIds;
            }else{
                return null;
            }
        }catch(Exception e){
            logger.error("关联表分离driverid异常:{}",e);
            return null;
        }
    }

    /** 
    * @Desc: pid查询车队下小组 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/3 
    */ 
    public List<CarDriverTeam> getTeamsByPid(Integer teamId){
        if(Check.NuNObj(teamId)) {
            return null;
        }
        try{
            DriverTeamRequest driverTeamRequest = new DriverTeamRequest();
            driverTeamRequest.setpId(teamId);
            List<CarDriverTeam> carDriverTeams = carDriverTeamExMapper.queryForListByPid(driverTeamRequest);
            return carDriverTeams;
        }catch (Exception e){
            logger.error("查询车队下小组异常:{}",e);
            return null;
        }
    }


    /** 
    * @Desc: 处理包含数据权限和查询条件参数整合处理 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/8/30 
    */ 
    public synchronized CommonRequest paramDeal(CommonRequest paramRequest){
        if(paramRequest == null){
            return null;
        }
        //----------------------------------------------------------------------------------首先，如果是普通管理员，校验数据权限（cityId、supplierId、teamId）
        //TODO 数据权限
        Set<Integer> permOfCity        = new HashSet<Integer>();//普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier = new HashSet<Integer>();//普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam     = new HashSet<Integer>();//普通管理员可以管理的所有车队ID
        if(WebSessionUtil.isSupperAdmin() == false) {//如果是普通管理员
            permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds();
            permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
            permOfTeam     = WebSessionUtil.getCurrentLoginUser().getTeamIds();
            if( permOfCity.size()==0 || (StringUtils.isNotEmpty(paramRequest.getCityId())
                    && permOfCity.contains(Integer.valueOf(paramRequest.getCityId()))==false)  ) {
                return null;
            }
            if( permOfSupplier.size()==0 || (StringUtils.isNotEmpty(paramRequest.getSupplierId())
                    && permOfSupplier.contains(Integer.valueOf(paramRequest.getSupplierId()))==false)   ) {
                return null;
            }
            if( permOfTeam.size()==0 || (paramRequest.getTeamId() != null
                    && permOfTeam.contains(Integer.valueOf(paramRequest.getTeamId())) == false )   ) {
//				return LayUIPage.build("您没有查询此车队的权限！", 0, null);
                return null;
            }
        }
        //A--------------------------------------------------------------------------------设置SQL查询参数
        Set<String> cityIds        = new HashSet<String>();
        Set<String> supplierIds = new HashSet<String>();
        Set<Integer> teamIds    = new HashSet<Integer>();
        //A1城市ID
        if(StringUtils.isNotEmpty(paramRequest.getCityId())) {//当有参数传入时，以传入的参数为准
            cityIds.add(paramRequest.getCityId());
        }else{                                       //当无参数传入时，超级管理员可以查询任何城市; 普通管理员只能查询自己数据权限内的城市
            if( WebSessionUtil.isSupperAdmin() ) {
                cityIds.clear();
            }else {
                for(Integer cityid : permOfCity ) {
                    cityIds.add( String.valueOf(cityid)  );
                }
            }
        }
        //A2供应商ID
        if(StringUtils.isNotEmpty(paramRequest.getSupplierId())) {//当有参数传入时，以传入的参数为准
            supplierIds.add(paramRequest.getSupplierId());
        }else{                                              //当无参数传入时，超级管理员可以查询任何供应商; 普通管理员只能查询自己数据权限内的供应商
            if( WebSessionUtil.isSupperAdmin() ) {
                supplierIds.clear();
            }else {
                for(Integer sid : permOfSupplier) {
                    supplierIds.add( String.valueOf(sid)  );
                }
            }
        }
        //A3车队ID
        if(paramRequest.getTeamId() != null) {         //当有参数传入时，以传入的参数为准
            teamIds.add(paramRequest.getTeamId());
        }else{                         //当无参数传入时，超级管理员可以查询任何车队; 普通管理员只能查询自己数据权限内的车队
            if( WebSessionUtil.isSupperAdmin() ) {
                teamIds.clear();
            }else {
                teamIds.addAll( permOfTeam );
            }
        }
        paramRequest.setCityIds(cityIds);
        paramRequest.setSupplierIds(supplierIds);
        paramRequest.setTeamIds(teamIds);
        return paramRequest;
    }


    /**
     * @Desc: 查询城市列表（超级管理员可以查询出所有城市、普通管理员只能查询自己数据权限内的城市）
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/8/29
     */
    public List<CarBizCity> queryCityList(){
        if(WebSessionUtil.isSupperAdmin()) {
            return carBizCityExMapper.queryByIds(null);
        }else {
//            Set<Integer> permOfcityids = dataPermissionHelper.havePermOfCityIds("");
            Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
            if(cityIds.size()==0) {
                return new ArrayList<CarBizCity>();
            }
            return carBizCityExMapper.queryByIds(cityIds);
        }
    }

    /**
     * @Desc: 根据一个城市ID，查询供应商列表（超级管理员可以查询出所有供应商、普通管理员只能查询自己数据权限内的供应商）
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/8/29
     */
    public List<CarBizSupplier> querySupplierList(Integer cityId ){
        if(cityId==null || cityId.intValue()<=0) {
            return new ArrayList<CarBizSupplier>();
        }
        //对城市ID进行校验数据权限
        if(WebSessionUtil.isSupperAdmin()==false ) {
//            Set<Integer> permOfcityids = dataPermissionHelper.havePermOfCityIds("");
            Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
            if( cityIds.size()==0 || cityIds.contains(cityId)==false  ) {
                return new ArrayList<CarBizSupplier>();
            }
        }
        //进行查询 (区分 超级管理员 、普通管理员 )
        if( WebSessionUtil.isSupperAdmin() ) {
            return carBizSupplierExMapper.querySuppliers(cityId, null);
        }else {
//            Set<Integer> permOfsupplierIds = dataPermissionHelper.havePermOfSupplierIds("");
            Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
            if(supplierIds.size()==0 ) {
                return new ArrayList<CarBizSupplier>();
            }
            return carBizSupplierExMapper.querySuppliers(cityId, supplierIds);
        }
    }

    /**
     * @Desc: 根据一个城市ID、一个供应商ID，查询车队列表（超级管理员可以查询出所有车队、普通管理员只能查询自己数据权限内的车队）
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/8/29
     */
    public List<CarDriverTeam> queryDriverTeamList(Integer cityId, Integer supplierId){
        if(cityId==null || cityId.intValue()<=0 || supplierId==null || supplierId.intValue()<=0) {
            return new ArrayList<CarDriverTeam>();
        }
        //对城市ID、供应商ID 进行校验数据权限
        if(WebSessionUtil.isSupperAdmin()==false ) {
            Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
            if( cityIds.size()==0 || cityIds.contains(cityId)==false  ) {
                return new ArrayList<CarDriverTeam>();
            }
//            Set<Integer> permOfsupplierIds = dataPermissionHelper.havePermOfSupplierIds("");
            Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
            if( supplierIds.size()==0 || supplierIds.contains(supplierId)==false  ) {
                return new ArrayList<CarDriverTeam>();
            }
        }
        //进行查询 (区分 超级管理员 、普通管理员 )
        Set<String> cityIds = new HashSet<String>(2);
        cityIds.add(String.valueOf(cityId));
        Set<String> supplierIds = new HashSet<String>(2);
        supplierIds.add(String.valueOf(supplierId));
        if( WebSessionUtil.isSupperAdmin() ) {
            return carDriverTeamExMapper.queryDriverTeam(cityIds, supplierIds, null);
        }else {
//            Set<Integer> permOfteamIds = dataPermissionHelper.havePermOfDriverTeamIds("");
            Set<Integer> teamIds = WebSessionUtil.getCurrentLoginUser().getTeamIds();
            if(teamIds.size()==0 ) {
                return new ArrayList<CarDriverTeam>();
            }
            return carDriverTeamExMapper.queryDriverTeam(cityIds, supplierIds, teamIds);
        }
    }

    /** 根据车队id查询小组*/
    public List<CarDriverTeam> queryTeamsById(Integer teamId){
        if(Check.NuNObj(teamId)){
            return new ArrayList<>();
        }
        DriverTeamRequest driverTeamRequest = new DriverTeamRequest();
        driverTeamRequest.setpId(teamId);
        List<CarDriverTeam> carDriverTeams = carDriverTeamExMapper.queryForListByPid(driverTeamRequest);
        return carDriverTeams;
    }

}

