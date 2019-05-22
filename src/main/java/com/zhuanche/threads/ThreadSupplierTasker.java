package com.zhuanche.threads;

import com.zhuanche.http.HttpClientUtil;
import mapper.rentcar.ex.CarBizCityExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author :(yangbo)
 * @Date: 2019/5/22 17:54
 * @Description:(供应商修改加盟类型发送MQ)
 */
public class ThreadSupplierTasker implements Callable<String> {

    private static Logger logger = LoggerFactory.getLogger(ThreadSupplierTasker.class);

    /***
     * 根据供应商ID，批量清理司机redis缓存
     */
    public static final String DRIVER_FLASH_REDIS_BY_SUPPLIERID_URL = "/api/v2/driver/flash/driverInfoBySupplierId";

    private Integer supplierId;
    private String supplierName;
    private Integer cityId;
    private Integer cooperationType;
    private CarBizCityExMapper carBizCityExMapper;
    private String url;

    public ThreadSupplierTasker(Integer supplierId, String supplierName, Integer cityId, Integer cooperationType, CarBizCityExMapper carBizCityExMapper, String url) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.cityId = cityId;
        this.cooperationType = cooperationType;
        this.carBizCityExMapper = carBizCityExMapper;
        this.url = url;
    }


    @Override
    public String call() throws Exception {
        try {
            // 根据城市id获取城市名称
            String cityName = carBizCityExMapper.queryNameById(cityId);
            String sendurl = url+DRIVER_FLASH_REDIS_BY_SUPPLIERID_URL
                    + "?supplierId=" + supplierId
                    + "&supplierName=" + supplierName
                    + "&cityId=" + cityId
                    + "&cityName=" + cityName
                    + "&cooperationType=" + cooperationType;
            String result = HttpClientUtil.buildGetRequest(sendurl).execute();
            logger.info("删除司机信息缓存,删除失败不影响业务,调用结果返回={}", result);
        } catch (Exception e) {
            logger.info("供应商修改加盟类型,调用清除接口异常="+e.getMessage());
            return "error";
        }
        return "success";
    }

}
