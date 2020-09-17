package com.zhuanche.common.util;

import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author fanht
 * @Description 首页看板添加城市选项
 * @Date 2020/9/7 上午11:18
 * @Version 1.0
 */
@Service
public class CurrentSystemUtils {

    @Autowired
    private CarBizSupplierExMapper supplierExMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取首页数据
     * @param cityId
     * @param supplierId
     * @return
     */
    public  List<String> supplierIds(Integer cityId,String supplierId){
        try {
            if(StringUtils.isNotEmpty(supplierId)){
                return TransportUtils.strToList(supplierId,Constants.SEPERATER);
            }

            if(cityId != null && cityId > 0){
                Set<Integer> setCity = new HashSet<>();
                setCity.add(cityId);
                List<CarBizSupplier> supplierList = supplierExMapper.queryByCityOrSupplierName(setCity,null);
                String supplis = supplierList.stream().map(e -> String.valueOf(e.getSupplierId())).collect(Collectors.joining(Constants.SEPERATER));

                if(WebSessionUtil.isSupperAdmin()){
                    return TransportUtils.strToList(supplis,Constants.SEPERATER);
                }else {
                    Integer level = WebSessionUtil.getCurrentLoginUser().getLevel();
                    if(level.equals(PermissionLevelEnum.CITY.getCode()) ||
                         level.equals(PermissionLevelEnum.ALL.getCode())){
                        logger.info("=======合作商id集合：" + supplis + "=======");
                        return TransportUtils.strToList(supplis,Constants.SEPERATER);
                    }else {
                        Set<Integer> setSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
                        String supp = StringUtils.join(setSupplier.toArray(),Constants.SEPERATER);
                        return TransportUtils.strToList(supp,Constants.SEPERATER);
                    }
                }

            }

            if(cityId == null && StringUtil.isEmpty(supplierId)){
                if(WebSessionUtil.isSupperAdmin()){
                    return null;
                }else {
                    Integer level = WebSessionUtil.getCurrentLoginUser().getLevel();
                    if(level.equals(PermissionLevelEnum.ALL.getCode())){
                        return null;
                    }
                    if(level.equals(PermissionLevelEnum.CITY.getCode())){
                        List<CarBizSupplier> supplierList = supplierExMapper.queryByCityOrSupplierName(WebSessionUtil.getCurrentLoginUser().getCityIds(),null);
                        String supplis = supplierList.stream().map(e -> String.valueOf(e.getSupplierId())).collect(Collectors.joining(Constants.SEPERATER));
                        logger.info("=======合作商id集合：" + supplis + "=======");
                        return TransportUtils.strToList(supplis,Constants.SEPERATER);
                    }else {
                        Set<Integer> setSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
                        String supp = StringUtils.join(setSupplier.toArray(),Constants.SEPERATER);
                        return TransportUtils.strToList(supp,Constants.SEPERATER);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("查询权限异常",e);
            throw new RuntimeException("获取合作商数据异常",e);
        }
        return null;
    }
}
