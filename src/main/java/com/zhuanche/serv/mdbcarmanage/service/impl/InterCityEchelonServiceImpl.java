package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.jsonobject.EchelonJsonData;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.mdbcarmanage.InterCityTeamDto;
import com.zhuanche.dto.mdbcarmanage.InterEchelonDto;
import com.zhuanche.dto.rentcar.CarBizSupplierDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.common.SupplierCommonService;
import com.zhuanche.serv.mdbcarmanage.service.InterCityEchelonService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.collectionutil.TransportUtils;
import mapper.mdbcarmanage.InterCityEchelonMapper;
import mapper.mdbcarmanage.InterCityTeamMapper;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.mdbcarmanage.ex.InterCityEchelonExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamDriverRelExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/14 下午5:02
 * @Version 1.0
 */
@Service
public class InterCityEchelonServiceImpl implements InterCityEchelonService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterCityEchelonMapper echelonMapper;


    @Autowired
    private InterCityEchelonExMapper echelonExMapper;


    @Autowired
    private InterCityTeamExMapper teamExMapper;


    @Autowired
    private InterCityTeamMapper teamMapper;

    @Autowired
    private InterCityTeamDriverRelExMapper relExMapper;

    @Autowired
    private DriverInfoInterCityExMapper driverInfoInterCityExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;


    @Autowired
    private SupplierCommonService commonService;

    @Override
    //@Transactional
    public AjaxResponse addOrEdit(EchelonJsonData echelonJsonData) {

        Integer cityId = echelonJsonData.getCityId();
        Integer supplierId = echelonJsonData.getSupplierId();
        Integer teamId = echelonJsonData.getTeamId();
        String echelonMonth = echelonJsonData.getEchelonMonth();
        List<InterCityEchelon> echelonList = echelonJsonData.getEchelonList();

        /**需要验证传的日期是不是有重复的*/
        List<String> repeatDateList = verifyHasRepeatDate(echelonList);
        if(CollectionUtils.isNotEmpty(repeatDateList)){
            logger.info("同一日期仅可存在于一个梯队中");
            return AjaxResponse.fail(RestErrorCode.SAME_ECHELON, JSONObject.toJSON(repeatDateList));
        }

        String repeatDateStr = this.repeat(teamId,echelonMonth,echelonList);
        if(StringUtils.isNotEmpty(repeatDateStr)){
            logger.info("===该日期该梯队已存在车队===",repeatDateStr);
            return AjaxResponse.fail(RestErrorCode.SAME_ECHELON, repeatDateStr);
        }

        String repeateTeamDate = this.repeatTeam(cityId,supplierId,echelonMonth,echelonList);

        if(StringUtils.isNotEmpty(repeateTeamDate)){
            return AjaxResponse.fail(RestErrorCode.ECHELON_HAS_EXIST, repeateTeamDate);
        }
        echelonList.forEach(cityEchelon ->{

            String echelonDate = cityEchelon.getEchelonDate();

            Integer id = cityEchelon.getId();

            Integer sort = cityEchelon.getSort();

            InterCityEchelon echelon = new InterCityEchelon();
            echelon.setCreateTime(new Date());
            echelon.setEchelonDate(echelonDate);
            echelon.setEchelonMonth(echelonMonth);
            echelon.setTeamId(teamId);
            echelon.setEchelonName(String.format(Constants.TEAMNAME,teamId.toString()));
            echelon.setSort(sort);
            if (id != null && id > 0) {
                echelon.setId(id);
                echelonMapper.updateByPrimaryKeySelective(echelon);
            } else {
                echelonMapper.insertSelective(echelon);
            }

        });


        return AjaxResponse.success(null);
    }

    @Override
    public int updateByPrimaryKey(InterCityEchelon record) {

        return echelonMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<InterCityEchelon> detailList(Integer teamId) {

        List<InterCityEchelon> queryTeamIds = echelonExMapper.queryTeamId(teamId,null);



        return queryTeamIds;
    }

    @Override
    public AjaxResponse queryEchelonList(DriverInfoInterCity driverInfoInterCity, String echelonMonth, Integer pageNo, Integer pageSize) {
        try {
            List<Integer> teamIdList = this.teamIdList(echelonMonth);
            /**获取权限*/
            Set<Integer> setCityIds = WebSessionUtil.isSupperAdmin() ? null : WebSessionUtil.getCurrentLoginUser().getCityIds();
            Set<Integer> setSupplierIds = WebSessionUtil.isSupperAdmin() ? null : WebSessionUtil.getCurrentLoginUser().getSupplierIds();

            Page page = PageHelper.startPage(pageNo,pageSize,true);
            /**查询车队*/
            List<InterCityTeam>  cityTeamList = teamExMapper.queryTeamsByParam(driverInfoInterCity.getCityId(),driverInfoInterCity.getSupplierId(),driverInfoInterCity.getTeamId(),teamIdList,
                    setCityIds,setSupplierIds);

            Integer count = cityTeamList.size();

            if(CollectionUtils.isNotEmpty(cityTeamList)){


                List<InterEchelonDto> echelonDtoList = this.echelonDtoList(cityTeamList,echelonMonth);

                PageDTO pageDTO = new PageDTO(pageNo,pageSize,count,echelonDtoList);

                return AjaxResponse.success(pageDTO);
            }
        } catch (Exception e) {
            logger.error("==========查询梯队列表信息异常========",e);
        }

        return AjaxResponse.success(null);
    }

    @Override
    public List<InterCityTeamDto> queryTeam(Integer cityId, Integer supplierId) {
         List<InterCityTeam> teamList = teamExMapper.queryTeam(cityId,supplierId);

         if(CollectionUtils.isNotEmpty(teamList)){
             List<InterCityTeamDto> teamDtoList = new ArrayList<>();
             teamList.forEach(team ->{
                 InterCityTeamDto dto = new InterCityTeamDto();
                 dto.setTeamId(team.getId());
                 dto.setTeamName(String.format(Constants.TEAMNAME,team.getTeamName()));
                 teamDtoList.add(dto);
             });
             return teamDtoList;
         }
        return null;
    }


    /**根据月份获取所有的teamId*/
    private List<Integer> teamIdList(String echelonMonth){
        if(StringUtils.isNotEmpty(echelonMonth)){
            return echelonExMapper.teamIdListByMonth(echelonMonth);
        }
        return null;
    }





    private List<InterEchelonDto> echelonDtoList(List<InterCityTeam>  cityTeamList,String echelonMonth){

        /**拼接名称*/
        Map<Integer, CarBizSupplierDTO> supplierMap = commonService.supplierMap(cityTeamList);


        List<InterEchelonDto> echelonDtoList = new ArrayList<>();

        try {
            cityTeamList.forEach(cityTeam ->{

                InterEchelonDto dto = new InterEchelonDto();

                BeanUtils.copyProperties(cityTeam,dto);
                /**注意id属性*/
                dto.setTeamId(cityTeam.getId());

                try {
                    dto.setCityName(supplierMap == null ? null:supplierMap.get(cityTeam.getSupplierId()).getCityName());
                    dto.setSupplierName(supplierMap == null ? null:supplierMap.get(cityTeam.getSupplierId()).getSupplierFullName());
                } catch (Exception e) {
                    logger.error("异常",e);
                }

                List<InterCityEchelon> queryTeamIds = echelonExMapper.queryTeamId(cityTeam.getId(),echelonMonth);
                dto.setEchelonList(queryTeamIds);
                dto.setEchelonMonth(echelonMonth);
                echelonDtoList.add(dto);
            });
        } catch (Exception e) {
            logger.error("获取列表异常",e);
        }

        return echelonDtoList;
    }


    private String isTrue(List<InterCityEchelon> echelonList, List<String> echelonDateList) {
        final String[] repeatStr = {""};

        if (CollectionUtils.isNotEmpty(echelonList)) {
            echelonList.forEach(list -> {
                String echelon = list.getEchelonDate();
                List<String> hasEchelonList = TransportUtils.listStr(echelon);

                List<String> repeatList = echelonDateList.stream().filter(t -> hasEchelonList.contains(t)).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(repeatList)) {
                    logger.info("==========该日期梯队已经被设置过=====重复的梯队日期:{}", JSONObject.toJSONString(repeatList));
                    repeatStr[0] = JSONObject.toJSONString(repeatList);
                }
            });
        }
        return repeatStr[0];
    }

    /***
     * 判断是否有重复的
     * @param echelonList
     * @return
     */
    private List<String> verifyHasRepeatDate(List<InterCityEchelon> echelonList){
        List<String> repeatDate = new ArrayList<>();
        List<String> list = new ArrayList<>();
        echelonList.forEach(echelon ->{
            String echelonDate = echelon.getEchelonDate();

            List<String> dateList = TransportUtils.listStr(echelonDate);
            dateList.forEach(strDate -> {
                if(list.contains(strDate)){
                    repeatDate.add(echelonDate);
                }
            });
        });
        return repeatDate;
    }



    private String repeat(Integer teamId,String echelonMonth,List<InterCityEchelon> echList){
        final String[] repeatStr = {""};
        echList.forEach(echelon ->{
            String echelonDate = echelon.getEchelonDate();

            List<InterCityEchelon> echelonList = echelonExMapper.queryTeamId(teamId, echelonMonth);
            List<String> echelonDateList = TransportUtils.listStr(echelonDate);

            /**去掉转义符*/
            String repeatDate = StringEscapeUtils.unescapeJava(this.isTrue(echelonList, echelonDateList));
            if (StringUtils.isNotEmpty(repeatDate)) {
                repeatStr[0] = repeatDate;
            }

        });
        return repeatStr[0];
    }


    private String repeatTeam(Integer cityId,Integer supplierId,String echelonMonth, List<InterCityEchelon> echList){

        final String[] repeatTeamDate = {""};
        echList.forEach(echelon ->{

            String echelonDate = echelon.getEchelonDate();

            /**不同车队在同一日期*/
            List<InterCityTeam> teamList = teamExMapper.queryTeam(cityId, supplierId);
            List<Integer> teamIds = new ArrayList<>();
            teamList.forEach(team -> {
                teamIds.add(team.getId());
            });
            List<InterCityEchelon> echelonLists = echelonExMapper.queryTeamIds(teamIds, echelonMonth);

            List<String> echelonDateList = TransportUtils.listStr(echelonDate);


            String repeatDate = StringEscapeUtils.unescapeJava(this.isTrue(echelonLists, echelonDateList));

            if (StringUtils.isNotEmpty(repeatDate)) {
                logger.info("===该日期该梯队已存在车队===",repeatDate);
                repeatTeamDate[0] = repeatDate;
            }

        });
        return repeatTeamDate[0];
    }
}
