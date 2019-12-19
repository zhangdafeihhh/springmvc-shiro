package com.zhuanche.controller.supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.SupplierDistributorDTO;
import com.zhuanche.dto.rentcar.CityDto;
import com.zhuanche.entity.mdbcarmanage.SupplierDistributor;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.supplier.SupplierDistributorService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;

/**
 * @Author fanht
 * @Description 供应商下 -> 分销商
 * @Date 2019/12/19 下午4:31
 * @Version 1.0
 */
@Controller
@RequestMapping("/supplierDistributor")
public class SupplirDistributorController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SupplierDistributorService distributorService;

    @Autowired
    private CarBizCityExMapper cityExMapper;

    @Autowired
    private CarBizSupplierExMapper supplierExMapper;

    /**h5跳转到新城际拼车连接地址**/
    private static final String H5_NEW_CITY_URL = "https://img.yun.01zhuanche.com/frontapp/wxp/sc.html?sId=%s&dId=%s";

    /**SINA提供短链接生成服务**/
    private static final String SINA_API = "http://api.t.sina.com.cn/short_url/shorten.json?source=1681459862&url_long=";

    @RequestMapping("/distributorList")
    @ResponseBody
    public AjaxResponse distributorList(Integer cityId,
                                        Integer supplierId,
                                        Integer distributorId,
                                        @Verify(param = "pageNum",rule = "required")Integer pageNum,
                                        @Verify(param = "pageSize",rule = "required")Integer pageSize){
        logger.info(MessageFormat.format("查询分销商列表入参:cityId:{0},supplierId:{1},distributorId:{2}," +
                "pageNum:{3},pageSize:{4}",cityId,supplierId,distributorId,pageNum,pageSize));
        SupplierDistributor distributor = new SupplierDistributor();

        if(cityId != null || supplierId != null || distributorId != null){
            if(cityId != null){
                distributor.setCityId(cityId);
            }

            if(supplierId != null){
                distributor.setSupplierId(supplierId);
            }

            if(distributorId != null){
                distributor.setId(distributorId);
            }
        }else {
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            distributor.setCityIds(loginUser.getCityIds());
            distributor.setSupplierIds(loginUser.getSupplierIds());
         }

        Page page = PageHelper.startPage(pageNum,pageSize);
        List<SupplierDistributor> distributorList = distributorService.distributorList(distributor);
        List<SupplierDistributorDTO> listDTO = new ArrayList<>();

        List<CityDto>  cityDtoList =  cityExMapper.selectAllCity();
        Map<Integer,String> map = Maps.newHashMap();
        cityDtoList.forEach(city -> {
            map.put(city.getCityId(),city.getCityName());
        });
        Set<Integer> setSupplier = new HashSet<>();
        distributorList.forEach(list ->{
            SupplierDistributorDTO dto = new SupplierDistributorDTO();
            dto.setCreateTimeStr(DateUtils.formatDate(list.getCreateTime(),DateUtils.dateTimeFormat_parttern));
            dto.setUpdateTimeStr(DateUtils.formatDate(list.getUpdateTime(),DateUtils.dateTimeFormat_parttern));
            dto.setCityName(map.get(list.getCityId()));
            setSupplier.add(list.getSupplierId());
            try {
                BeanUtils.copyProperties(list,dto);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            listDTO.add(dto);
        });

        List<CarBizSupplier> supplierList = supplierExMapper.queryNamesByIds(setSupplier);
        Map<Integer,String> mapSupplier = Maps.newHashMap();
        supplierList.forEach(supplier ->{
            mapSupplier.put(supplier.getSupplierId(),supplier.getSupplierFullName());
        });

        listDTO.forEach(o ->{
            o.setSupplierName(mapSupplier.get(o.getSupplierId()));
        });
        return AjaxResponse.success(listDTO);
    }



    @RequestMapping(value = "/distributorAdd",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse distributorAdd(@Verify(param = "cityId",rule = "required") Integer cityId,
                                       @Verify(param = "supplierId",rule = "required") Integer supplierId,
                                       @Verify(param = "distributorName",rule = "required") String distributorName,
                                       String remark){

        logger.info(MessageFormat.format("创建分销商入参:cityId:{0},supplierId:{1},distributorName:{2},remark:{3}",cityId,supplierId,distributorName,remark));

        SupplierDistributor distributor = new SupplierDistributor();
        distributor.setCityId(cityId);
        distributor.setSupplierId(supplierId);
        distributor.setDistributorName(distributorName);
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        distributor.setCreateUser(loginUser.getLoginName());
        distributor.setUpdateUser(loginUser.getLoginName());
        distributor.setCreateTime(new Date());
        distributor.setUpdateTime(new Date());
        distributor.setRemark(remark);
        int code = 0;
        try {
            code = distributorService.insert(distributor);
        } catch (Exception e) {
            logger.error("分销商创建异常" + e);
        }
        if(code > 0 ){
            logger.info("分销商创建成功");
        }else {
            logger.info("分销商创建失败");
        }
        return AjaxResponse.success(null);
    }


    @RequestMapping(value = "/distributorDetail")
    @ResponseBody
    public AjaxResponse distributorDetail(@Verify(param = "id",rule = "required") Integer id){

        logger.info(MessageFormat.format("获取分销商详情:id:{0}",id));

        SupplierDistributor distributor = distributorService.queryById(id);

        return AjaxResponse.success(distributor);
    }


    @RequestMapping(value = "/distributorEdit",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse distributorEdit(@Verify(param = "id",rule = "required") Integer id,
                                        @Verify(param = "cityId",rule = "required") Integer cityId,
                                        @Verify(param = "supplierId",rule = "required") Integer supplierId,
                                        @Verify(param = "distributorName",rule = "required") String distributorName,
                                        String remark){

        logger.info(MessageFormat.format("修改分销商入参:cityId:{0},supplierId:{1},distributorName:{2},remark:{3},id:{4}",cityId,supplierId,distributorName,remark,id));

        SupplierDistributor distributor = new SupplierDistributor();
        distributor.setRemark(remark);
        distributor.setSupplierId(supplierId);
        distributor.setCityId(cityId);
        distributor.setDistributorName(distributorName);
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        distributor.setUpdateUser(loginUser.getLoginName());
        distributor.setUpdateTime(new Date());
        distributor.setId(id);
        int code = 0;
        try {
            code = distributorService.update(distributor);
        } catch (Exception e) {
           logger.error("更新分销商异常" + e);
        }
        if(code > 0){
            logger.info("更新数据成功");
        }else {
            logger.info("更新数据失败");
        }
        return AjaxResponse.success(null);
    }



    @RequestMapping("/shortQrCode")
    @ResponseBody
    public AjaxResponse shortQrCode(@Verify(param = "id",rule = "required") Integer id,
                                    @Verify(param = "cityId",rule = "required") Integer cityId,
                                    @Verify(param = "supplierId",rule = "required") Integer supplierId){
        logger.info("分销商生成二维码入参:id:{0},cityId:{1},supplierId:{2}",id,cityId,supplierId);

        String qrUrl =String.format(H5_NEW_CITY_URL,supplierId,id);
        logger.info("生成短连接地址："+ qrUrl);
        String shortUrl = null;
        try {
            shortUrl = HttpClientUtil.buildGetRequest(SINA_API + qrUrl).setLimitResult(1).execute();
        } catch (HttpException e) {
            logger.error("生成短连接异常" + e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        logger.info("供应商短链接生成,supplierId={},result={}",supplierId,shortUrl);

        return AjaxResponse.success(shortUrl);
    }
}
