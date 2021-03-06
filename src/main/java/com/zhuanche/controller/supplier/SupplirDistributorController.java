package com.zhuanche.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.SupplierAllDistributorDTO;
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
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

        List<SupplierDistributorDTO> listDTO = new ArrayList<>();
        try {
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
                BeanUtils.copyProperties(list,dto);

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
        } catch (Exception e) {
            logger.error("查询异常" + e);
        }

        PageInfo<SupplierDistributorDTO> pageInfo = new PageInfo<>(listDTO);

        return AjaxResponse.success(pageInfo);
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
        JSONArray parseArray = null;

        try {
            shortUrl = HttpClientUtil.buildGetRequest(SINA_API + qrUrl).setLimitResult(1).execute();
            parseArray = JSON.parseArray((shortUrl));

        } catch (Exception e) {
            logger.error("生成短连接异常" + e);
            parseArray = new JSONArray();
            Map<String,Object> map = Maps.newHashMap();
            map.put("url_long",qrUrl);
            map.put("type",0);
            parseArray.add(map);
            logger.info("连接地址生成失败，重新生成：" + parseArray);
            return AjaxResponse.success(parseArray);
        }
        logger.info("供应商短链接生成,shortUrl=",parseArray.toJSONString());

        return AjaxResponse.success(parseArray);
    }



    @RequestMapping("/distributorBySupplierId")
    @ResponseBody
    public AjaxResponse distributorBySupplierId(@Verify(param = "supplierId",rule = "required") Integer supplierId){
        logger.info("获取供应商下的分销商入参：supplierId:",supplierId);

        List<SupplierAllDistributorDTO> listDto = new ArrayList<>();
        try {
            SupplierDistributor distributor = new SupplierDistributor();
            distributor.setSupplierId(supplierId);
            List<SupplierDistributor> distributorList = distributorService.distributorList(distributor);
            listDto = new ArrayList<>();

            List<SupplierAllDistributorDTO> finalListDto = listDto;
            distributorList.forEach(o ->{
                SupplierAllDistributorDTO distributorDTO = new SupplierAllDistributorDTO();
                BeanUtils.copyProperties(o,distributorDTO);
                finalListDto.add(distributorDTO);
            });
        } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }

        return AjaxResponse.success(listDto);
    }


    /**
     * 获取当前用户下或者某个城市下的合作商
     * @param cityId
     * @return
     */
    @RequestMapping("/distributorAll")
    @ResponseBody
    public AjaxResponse distributorAll(Integer cityId){

        logger.info(MessageFormat.format("查询分销商列表入参:cityId:",cityId));

        SupplierDistributor distributor = new SupplierDistributor();

        try {
            if(cityId != null ){
                if(cityId != null){
                    distributor.setCityId(cityId);
                }
            }else {
                SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
                distributor.setCityIds(loginUser.getCityIds());
                distributor.setSupplierIds(loginUser.getSupplierIds());
            }

            List<SupplierDistributor> distributorList = distributorService.distributorList(distributor);

            JSONArray jsonArray = new JSONArray();


            distributorList.forEach(disList ->{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",disList.getId());
                jsonObject.put("distributorName",disList.getDistributorName());
                jsonArray.add(jsonObject);
            });

            return AjaxResponse.success(jsonArray);

        } catch (Exception e) {

            logger.error("查询异常" + e);

            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);

        }


    }

}
