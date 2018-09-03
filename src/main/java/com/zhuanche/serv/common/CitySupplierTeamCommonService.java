package com.zhuanche.serv.common;

import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            Set<Integer> permOfcityids = dataPermissionHelper.havePermOfCityIds("");
            if(permOfcityids.size()==0) {
                return new ArrayList<CarBizCity>();
            }
            return carBizCityExMapper.queryByIds(permOfcityids);
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
            Set<Integer> permOfcityids = dataPermissionHelper.havePermOfCityIds("");
            if( permOfcityids.size()==0 || permOfcityids.contains(cityId)==false  ) {
                return new ArrayList<CarBizSupplier>();
            }
        }
        //进行查询 (区分 超级管理员 、普通管理员 )
        if( WebSessionUtil.isSupperAdmin() ) {
            return carBizSupplierExMapper.querySuppliers(cityId, null);
        }else {
            Set<Integer> permOfsupplierIds = dataPermissionHelper.havePermOfSupplierIds("");
            if(permOfsupplierIds.size()==0 ) {
                return new ArrayList<CarBizSupplier>();
            }
            return carBizSupplierExMapper.querySuppliers(cityId, permOfsupplierIds);
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
            Set<Integer> permOfcityids = dataPermissionHelper.havePermOfCityIds("");
            if( permOfcityids.size()==0 || permOfcityids.contains(cityId)==false  ) {
                return new ArrayList<CarDriverTeam>();
            }
            Set<Integer> permOfsupplierIds = dataPermissionHelper.havePermOfSupplierIds("");
            if( permOfsupplierIds.size()==0 || permOfsupplierIds.contains(supplierId)==false  ) {
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
            Set<Integer> permOfteamIds = dataPermissionHelper.havePermOfDriverTeamIds("");
            if(permOfteamIds.size()==0 ) {
                return new ArrayList<CarDriverTeam>();
            }
            return carDriverTeamExMapper.queryDriverTeam(cityIds, supplierIds, permOfteamIds);
        }
    }

}
