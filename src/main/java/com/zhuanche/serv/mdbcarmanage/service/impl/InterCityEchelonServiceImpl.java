package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
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
    public AjaxResponse addOrEdit(EchelonJsonData echelonJsonData) {

        Integer cityId = echelonJsonData.getCityId();
        Integer supplierId = echelonJsonData.getSupplierId();
        Integer teamId = echelonJsonData.getTeamId();
        String echelonMonth = echelonJsonData.getEchelonMonth();

        JSONArray array = JSONArray.parseArray(echelonJsonData.getJsonArray());

        List<InterCityEchelon> echelonList = new ArrayList<>();
        array.forEach(arr ->{
            JSONObject object = (JSONObject) arr;
            InterCityEchelon interCityEchelon = new InterCityEchelon();
            interCityEchelon.setId(object.get("id") == null ? null : object.getInteger("id"));
            interCityEchelon.setEchelonDate(object.get("echelonDate") == null ? null : object.getString("echelonDate"));
            interCityEchelon.setSort(object.get("sort") == null ? null : object.getInteger("sort"));
            echelonList.add(interCityEchelon);
        });

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

        String repeateTeamDate = this.repeatTeam(cityId,supplierId,teamId,echelonMonth,echelonList);

        if(StringUtils.isNotEmpty(repeateTeamDate)){
            logger.info("===该日期该梯队已存在车队===");
            return AjaxResponse.fail(RestErrorCode.ECHELON_HAS_EXIST, repeateTeamDate);
        }

        this.addEchelonData(echelonList,echelonMonth,teamId);

        return AjaxResponse.success(null);
    }

    @Override
    public int updateByPrimaryKey(InterCityEchelon record) {

        return echelonMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<InterCityEchelon> detailList(Integer teamId,String echelonMonth) {

        List<InterCityEchelon> queryTeamIds = echelonExMapper.queryTeamId(teamId,echelonMonth);

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

                /**求集合并集*/
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

                if(StringUtils.isNotEmpty(echelonDate)){
                    List<String> dateList = TransportUtils.listStr(echelonDate);
                    dateList.forEach(strDate -> {
                        if(list.contains(strDate)){
                            repeatDate.add(strDate);
                        }
                        list.add(strDate);
                    });
                }

        });
        return repeatDate;
    }


    /***
     * 校验日期是否已在其他车队里面
     * @param teamId
     * @param echelonMonth
     * @param echList
     * @return
     */
    private String repeat(Integer teamId,String echelonMonth,List<InterCityEchelon> echList){
        final String[] repeatStr = {""};
        echList.forEach(echelon ->{
            if(echelon.getId() == null || echelon.getId() == 0){
                if(StringUtils.isNotEmpty(echelon.getEchelonDate())){
                    String echelonDate = echelon.getEchelonDate();

                    List<InterCityEchelon> echelonList = echelonExMapper.queryTeamId(teamId, echelonMonth);
                    List<String> echelonDateList = TransportUtils.listStr(echelonDate);

                    /**去掉转义符*/
                    String repeatDate = StringEscapeUtils.unescapeJava(this.isTrue(echelonList, echelonDateList));
                    if (StringUtils.isNotEmpty(repeatDate)) {
                        repeatStr[0] = repeatDate;
                    }
                }
            }

        });
        return repeatStr[0];
    }


    /**
     * 校验不同车队在同一日期不能存在一个梯队
     * 例如：第1车队在6月1日至6月7日，为第一梯队。第2车队在6月1日至6月7日之间的任意一天均不能为第一梯队
     * @param cityId
     * @param supplierId
     * @param echelonMonth
     * @param echList
     * @return
     */
    private String repeatTeam(Integer cityId,Integer supplierId,Integer teamId,String echelonMonth, List<InterCityEchelon> echList){

        final String[] repeatTeamDate = {""};
        echList.forEach(echelon ->{

            String echelonDate = echelon.getEchelonDate();
            List<Integer> teamIds = new ArrayList<>();

            /**查询当前合作商下的所有车队*/
            List<InterCityTeam> teamList = teamExMapper.queryTeam(cityId, supplierId);
            teamList.forEach(team -> {

                /**如果是编辑时候，去掉当前车队的校验。因为上次和这边的车队id一样*/
                if(echelon.getId() != null && team.getId().equals(teamId)){
                    /**如果传的id和查询出来的id相同 则不做校验*/
                }else {
                    teamIds.add(team.getId());
                }

            });

            /**如果编辑的是同一个车队 则没必要校验*/
            if(CollectionUtils.isNotEmpty(teamIds)){
                List<InterCityEchelon> echelonLists = echelonExMapper.queryTeamIds(teamIds, echelonMonth);

                List<String> echelonDateList = TransportUtils.listStr(echelonDate);


                String repeatDate = StringEscapeUtils.unescapeJava(this.isTrue(echelonLists, echelonDateList));

                if (StringUtils.isNotEmpty(repeatDate)) {
                    /**如果仍有相同的，比较相同梯队是否有相同的日期。如果有不允许创建*/
                    Map<Integer,String> map = Maps.newHashMap();
                   echList.forEach(ech ->{
                       map.put(ech.getSort(),ech.getEchelonDate());
                   });

                   echelonLists.forEach(echs ->{
                       if(map.get(echs.getSort()) != null){
                          String curDate = map.get(echs.getSort());
                          String beforeDate = echs.getEchelonDate();

                          List<String> curList = TransportUtils.listStr(curDate);
                           List<String> beforeList = TransportUtils.listStr(beforeDate);

                           List<String>  sameList = curList.stream().filter(t -> beforeList.contains(t)).collect(Collectors.toList());

                           if(CollectionUtils.isNotEmpty(sameList)){
                               logger.info("===该日期该梯队已存在车队===",JSONObject.toJSONString(sameList));
                               repeatTeamDate[0] = JSONObject.toJSONString(sameList);
                          }
                       }
                   });

                }
            }




        });
        return repeatTeamDate[0];
    }


    /**
     * 添加梯队规则数据
     * @param echelonList
     * @param echelonMonth
     * @param teamId
     */
    private void addEchelonData(List<InterCityEchelon> echelonList,String echelonMonth,Integer teamId){
        echelonExMapper.batchDeleteEchelon(teamId,echelonMonth);
        echelonList.forEach(cityEchelon ->{
            String echelonDate = cityEchelon.getEchelonDate();
            Integer sort = cityEchelon.getSort();

            InterCityEchelon echelon = new InterCityEchelon();
            echelon.setCreateTime(new Date());
            echelon.setEchelonDate(echelonDate);
            echelon.setEchelonMonth(echelonMonth);
            echelon.setTeamId(teamId);
            echelon.setEchelonName(String.format(Constants.TEAMNAME,teamId.toString()));
            echelon.setSort(sort);

            echelonMapper.insertSelective(echelon);
        });
    }
}
