package com.zhuanche.serv.transportMonitor.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.transportMonitor.IndexMonitorDriverStatisticsDto;
import com.zhuanche.entity.driver.DriverMonitoringLog;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.OutCycleDriverList;
import com.zhuanche.mongo.SaasIndexMonitorDriverStatistics;
import com.zhuanche.serv.transportMonitor.DriverMonitoringService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.driver.DriverMonitoringLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.encrypt.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverMonitoringServiceImpl implements DriverMonitoringService {
    private static final Logger logger = LoggerFactory.getLogger(DriverMonitoringServiceImpl.class);
/*    @Resource(name = "bigDateDriverDBMongoTemplate")
    private MongoTemplate driverMongoTemplate;*/

    @Value("${bigdata.athena.url}")
    private String BIGDATA_ATHENA_URL;

    @Value("${driver.message.send.url}")
    private String driverMessageSendUrl;
    /**
     * @param cityId
     * @param supplierIds
     * @param teamIds
     * @return
     */
    /*@Override
    public IndexMonitorDriverStatisticsDto queryIndexMonitorDriverStatistics(Integer cityId, Set <Integer> supplierIds, Set<Integer> teamIds) {
        Query query = new Query().limit(1);
        query.addCriteria(Criteria.where("cityId").is(cityId));
        if (supplierIds!=null && supplierIds.size()>0){
            query.addCriteria(Criteria.where("driverTeamId").in(supplierIds));
        }
        if (teamIds!=null && teamIds.size()>0){
            query.addCriteria(Criteria.where("supplierId").in(teamIds));
        }
        query.with(new Sort(new Order(Direction.DESC,"minTime")));

        query.skip(0);
        IndexMonitorDriverStatisticsDto dto =null;
        List<SaasIndexMonitorDriverStatistics> list=driverMongoTemplate.find(query, SaasIndexMonitorDriverStatistics.class);
        if (list!=null && list.size()>0){
            SaasIndexMonitorDriverStatistics saasIndexMonitorDriverStatistics = list.get(0);
            String minTime = saasIndexMonitorDriverStatistics.getMinTime();
            List <AggregationOperation> operations = new ArrayList<>();

            Criteria c = Criteria.where("cityId").is(cityId).and("minTime").is(minTime);
            if (supplierIds!=null && supplierIds.size()>0){
                c.and("supplierId").in(supplierIds);
            }
            if (teamIds!=null && teamIds.size()>0){
                c.and("driverTeamId").in(teamIds);
            }
            operations.add(Aggregation.match(c));
            operations.add(Aggregation.group("cityId","minTime")
                    .max("minTime").as("minTime")
                    .sum("inCycleTotalOnlineCnt").as("inCycleTotalOnlineCnt")
                    .sum("inCycleTotalServingCnt").as("inCycleTotalServingCnt")
                    .sum("inCycleTotalFreeCnt").as("inCycleTotalFreeCnt")
                    .sum("inCycleTotalOfflineCnt").as("inCycleTotalOfflineCnt")
                    .sum("outCycleTotalOnlineCnt").as("outCycleTotalOnlineCnt")
                    .sum("outCycleTotalServingCnt").as("outCycleTotalServingCnt")
                    .sum("outCycleTotalFreeCnt").as("outCycleTotalFreeCnt")
                    .sum("outCycleTotalOfflineCnt").as("outCycleTotalOfflineCnt")
                    .sum("totalOnlineCnt").as("totalOnlineCnt")
                    .sum("totalServingCnt").as("totalServingCnt")
                    .sum("totalFreeCnt").as("totalFreeCnt")
                    .sum("totalOfflineCnt").as("totalOfflineCnt")
            );
            operations.add(Aggregation.sort(Sort.Direction.DESC, "minTime"));
            Aggregation aggregation = Aggregation.newAggregation(operations);
            AggregationResults <IndexMonitorDriverStatisticsDto> g = driverMongoTemplate.aggregate(aggregation, SaasIndexMonitorDriverStatistics.class, IndexMonitorDriverStatisticsDto.class);
            List <IndexMonitorDriverStatisticsDto> listIndexMonitorDriverStatistics = g.getMappedResults();
            if (listIndexMonitorDriverStatistics!=null && listIndexMonitorDriverStatistics.size()>0){
                dto = listIndexMonitorDriverStatistics.get(0);
                *//**上线司机 运力在圈率=*//*
                dto.setOnLineInCycleRate(getRate(dto.getInCycleTotalOnlineCnt(),dto.getTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                *//**服务中司机 运力在圈率*//*
                dto.setServingInCycleRate(getRate(dto.getInCycleTotalServingCnt(),dto.getTotalServingCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                *//**空闲司机 运力在圈率*//*
                dto.setFreeInCycleRate(getRate(dto.getInCycleTotalFreeCnt(),dto.getTotalFreeCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                *//**线下听单司机 运力在圈率*//*
                dto.setOffLineInCycleRate(getRate(dto.getInCycleTotalOfflineCnt(),dto.getTotalOfflineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                *//**全城运营率*//*
                dto.setCityOperatingRate(getRate(dto.getInCycleTotalServingCnt(),dto.getTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                *//**圈内运营率*//*
                dto.setWithinCircleOperatingRate(getRate(dto.getTotalServingCnt(),dto.getInCycleTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                *//**圈外运营率*//*
                dto.setOutSideCircleOperatingRate(getRate(dto.getOutCycleTotalServingCnt(),dto.getOutCycleTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
            }
        }
        return dto;
    }*/











    @Override
    public JSONObject getBizdistrict(Integer cityId) {
        String  shangquanApiUrl=BIGDATA_ATHENA_URL+"/api/inside/driverMonitoring/areaNew";
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(shangquanApiUrl+"?cityId="+cityId,1,"");
        return result;
    }

    @Override
    public JSONObject getHotspotDistrict(Integer cityId) {
        String  fengchaoApiUrl=BIGDATA_ATHENA_URL+"/api/inside/driverMonitoring/beehiveNew";
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(fengchaoApiUrl+"?cityId="+cityId,1,"");
        int status = result.getInteger("status");
        if(status==1){
            return result.getJSONObject("info");
        }
        return null;
    }

    @Override
    public JSONArray trajectory(Integer cityId, String supplierIds, String carTeamIds,
                                Integer carType,
                                Integer driverStatus,
                                String licensePlates) {
        String  trajectoryApiUrl=BIGDATA_ATHENA_URL+"/api/inside/saasCenter/trajectory";
        StringBuilder params = new StringBuilder();
        params.append("?cityId=").append(cityId);
        if(StringUtils.isNotEmpty(supplierIds)){
            params.append("&supplierIds=").append(supplierIds);
        }
        if(StringUtils.isNotEmpty(carTeamIds)){
            params.append("&carTeamIds=").append(carTeamIds);
        }
        if(null != carType){
            params.append("&carType=").append(carType);
        }
        if(null != driverStatus){
            params.append("&driverStatus=").append(driverStatus);
        }
        if(StringUtils.isNotEmpty(licensePlates)){
            params.append("&licensePlates=").append(licensePlates);
        }
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(trajectoryApiUrl+params.toString(),1,"");
        int status = result.getInteger("status");
        if(status!=1){
            return null;
        }
        try{
            return result.getJSONArray("info");
        }catch(Exception e){
            return new JSONArray();
        }
    }

    @Override
    public JSONObject driverInfo(Integer driverId) {
        String  driverInfoApiUrl=BIGDATA_ATHENA_URL+"/api/inside/saasCenter/driverInfo";
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(driverInfoApiUrl+"?driverId="+driverId,1,"");
        int status = result.getInteger("status");
        if(status==1){
            return result.getJSONObject("info");
        }
        return null;
    }

    @Override
    public JSONArray efficiency(Integer cityId, String supplierIds, String carTeamIds) {
        String  efficiencyApiUrl=BIGDATA_ATHENA_URL+"/api/inside/saasCenter/efficiency";
        StringBuilder params = new StringBuilder();
        params.append("?cityId=").append(cityId);
        if(StringUtils.isNotEmpty(supplierIds)){
            params.append("&supplierIds=").append(supplierIds);
        }
        if(StringUtils.isNotEmpty(carTeamIds)){
            params.append("&teamIds=").append(carTeamIds);
        }
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(efficiencyApiUrl+params.toString(),1,"");
        if(null == result.get("data")){
            return null;
        }
        try{
            return result.getJSONArray("data");
        }catch(Exception e){
            return new JSONArray();
        }
    }

    @Override
    public JSONArray abnormity(Integer cityId, String supplierIds, String carTeamIds, Integer freeTime, Integer finishedOrder, Integer finishedAmount) {
        String  abnormityApiUrl=BIGDATA_ATHENA_URL+"/api/inside/saasCenter/abnormity";
        StringBuilder params = new StringBuilder();
        params.append("?cityId=").append(cityId);
        if(StringUtils.isNotEmpty(supplierIds)){
            params.append("&supplierIds=").append(supplierIds);
        }
        if(StringUtils.isNotEmpty(carTeamIds)){
            params.append("&teamIds=").append(carTeamIds);
        }
        if(null != freeTime){
            params.append("&freeTime=").append(freeTime);
        }
        if(null != finishedOrder){
            params.append("&finishedOrder=").append(finishedOrder);
        }
        if(null != finishedAmount){
            params.append("&finishedAmount=").append(finishedAmount);
        }
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(abnormityApiUrl+params.toString(),1,"");
        if(null == result.get("data")){
            return null;
        }
        try{
            return result.getJSONArray("data");
        }catch(Exception e){
            return new JSONArray();
        }
    }


    /*
    public BigDecimal getRate(Integer dividend,Integer divisor){
        if (dividend==null || dividend==0 || divisor ==0 ||divisor==null){
            return BigDecimal.ZERO;
        }
        BigDecimal rate = new BigDecimal(dividend/divisor);
        return rate;
    }*/


  /*  @Override
    public boolean outsideDriverSendMsg(Integer cityId, Set < Integer > supplierIds, Set < Integer > teamIds) {

        Query query = new Query().limit(1);
        query.addCriteria(Criteria.where("cityId").is(cityId));
        if (supplierIds!=null && supplierIds.size()>0){
            query.addCriteria(Criteria.where("driverTeamId").in(supplierIds));
        }
        if (teamIds!=null && teamIds.size()>0){
            query.addCriteria(Criteria.where("supplierId").in(teamIds));
        }
        String nowMinTime = DateUtil.beforeHalfHour(new Date(), 10, "yyyyMMddHHmm");
        query.addCriteria(Criteria.where("minTime").gte(nowMinTime));
        query.with(new Sort(new Order(Direction.DESC,"minTime")));
        query.skip(0);
        IndexMonitorDriverStatisticsDto dto =null;
        List<OutCycleDriverList> list=driverMongoTemplate.find(query, OutCycleDriverList.class);
        OutCycleDriverList outCycleDriverList = list.get(0);
        String minTime = outCycleDriverList.getMinTime();

        Query queryParam = new Query().limit(1000);
        query.addCriteria(Criteria.where("cityId").is(cityId));
        if (supplierIds!=null && supplierIds.size()>0){
            queryParam.addCriteria(Criteria.where("driverTeamId").in(supplierIds));
        }
        if (teamIds!=null && teamIds.size()>0){
            queryParam.addCriteria(Criteria.where("supplierId").in(teamIds));
        }
        queryParam.addCriteria(Criteria.where("minTime").is(minTime));

        queryParam.with(new Sort(new Order(Direction.DESC,"minTime")));

        long count = driverMongoTemplate.count(query, OutCycleDriverList.class);

        return false;
    }*/

    /**
     * 司机运力查询
     * @param cityId
     * @param supplierIds
     * @param teamIds
     * @return
     */
    @Override
    public AjaxResponse getTransportStatics(Integer cityId, String supplierIds, String teamIds) {
        Map <String, Object> map = Maps.newHashMap();
        map.put("cityId",cityId);
        if (StringUtils.isNotEmpty(supplierIds)){
            map.put("supplierIds",supplierIds);
        }
        if (StringUtils.isNotEmpty(teamIds)){
            map.put("carTeamIds",teamIds);
        }
        logger.info("----获取司机运力入参：" + JSONObject.toJSONString(map));
        JSONObject jsonObject = MpOkHttpUtil.okHttpGetBackJson(BIGDATA_ATHENA_URL + "/api/inside/saasCenter/getTransportStatics", map, 0, "司机运力查询");
        if(jsonObject!=null && 1 == jsonObject.getInteger("status")){
            JSONObject data = (JSONObject) jsonObject.get("info");
            return AjaxResponse.success(data);
        }
        return AjaxResponse.success(null);
    }

    /**
     * 查询圈外空闲司机并发送socket
     * @param cityId
     * @param supplierIds
     * @param teamIds
     * @return
     */
    @Override
    public boolean sendPushMsg(Integer cityId, String supplierIds, String teamIds) {

        Map <String, Object> map = Maps.newHashMap();
        map.put("cityId",cityId);
        if (StringUtils.isNotEmpty(supplierIds)){
            map.put("supplierIds",supplierIds);
        }
        if (StringUtils.isNotEmpty(teamIds)){
            map.put("carTeamIds",teamIds);
        }
        int pageNum = 0;
        int pageSize=1000;
        //map.put("pageNum",0);
        map.put("pageSize",pageSize);

        List<OutCycleDriverList> sendMsglist=new ArrayList <>();
        List<OutCycleDriverList> list=null;
/*        List<OutCycleDriverList> list=getTrajectory(map);
        if(list!=null && list.size()>0){
            sendMsglist.addAll(list);
            if (list.size()==pageSize){*/
        String minTime="";
        for (; ; ) {
            map.put("pageNum", pageNum++);
            map.put("minTime",minTime);
            list = getTrajectory(map);
            if(list!=null && list.size()>0){
                if (StringUtils.isEmpty(minTime)){
                    minTime=list.get(0).getMinTime();
                }
                sendMsglist.addAll(list);
                if(list.size()!=pageSize){
                    break;
                }
            }else {
                logger.info("============未获取到数据,跳出循环==============");
                break;
            }
        }
         /*   }
        }*/
        /**调用消息中心发送消息*/
        boolean b=sendDriverMessage(sendMsglist);
        return b;
    }

    public List<OutCycleDriverList> getTrajectory(Map<String,Object> map){
        List<OutCycleDriverList> list=null;
        try {
            logger.info("----获取圈外空闲司机入参：" + JSONObject.toJSONString(map));
            JSONObject jsonObject = MpOkHttpUtil.okHttpGetBackJson(BIGDATA_ATHENA_URL + "/api/inside/saasCenter/getOutCycleDriverList", map, 0, "圈外空闲司机查询");
            logger.info("----获取圈外空闲司机返回接口：" + JSONObject.toJSONString(jsonObject));
            if (jsonObject.getInteger("status")==1 && jsonObject.get("info")!=null){
                JSONObject info = jsonObject.getJSONObject("info");
                Object content = info.get("content");
                if(content!=null){
                    list=JSONObject.parseArray(content.toString(),OutCycleDriverList.class);
                }
            }
        } catch (Exception e) {
            logger.error("--获取圈外空闲司机异常--入参--{},--异常--{}",JSONObject.toJSONString(map),e);
        } finally {
            logger.info("--获取圈外空闲司机返回结果数据size--{}",list==null?"":list.size());
            return list;
        }
    }


    private static final String LOGTAG = "[运力监控圈外司机]:";
    private static final String APP_KEY = "82BFBBA6C03247A2A5BCEFB9BD3EBB1F";
    private static final String APP_SECRET = "3D7F14CB41144D6EA5D9C6795CE788CD";

    @Autowired
    private DriverMonitoringLogMapper driverMonitoringLogMapper;
    /**
     * 结果集发送socket
     * @param list
     * @return
     */
    public boolean sendDriverMessage(List<OutCycleDriverList> list){
        if(list==null || list.size()==0){
            return true;
        }
        String  rediskey = "outcycle_drivers_" + APP_KEY;
        int templateId = 2101;
        //第一步获取authToken
        String authToken = RedisCacheUtil.get(rediskey, String.class);
        logger.info(LOGTAG + "发送站内信,查询redis结果authToken={}", authToken);
        if (StringUtils.isEmpty(authToken)) {
            authToken=getToken(rediskey);
            if (StringUtils.isBlank(authToken)) {
                logger.info(LOGTAG + "发送站内信,获取保存消息中心authToken={}失败", authToken);
                return false;
            }
        }
        //第二步获取msgId
        Long msgId=getMsgId(authToken,templateId);
        if (msgId==null||msgId==0){
            logger.info(LOGTAG + "发送站内信,获取msgId={},authToken={}失败",msgId, authToken);
            return false;
        }
        int total=list.size();
        int pageSize=500;
        int pageCount = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        for (int i=1;i<=pageCount;i++){
            List<OutCycleDriverList> listDriver=datepaging(list,i,pageSize);
            List<Integer> listdriverId= listDriver.stream().map(f -> f.getDriverId()).collect(Collectors.toList());
            String driverIds = StringUtils.join(listdriverId, ",");
            //第三步获取msgId
            pushToList(authToken,msgId,driverIds);
        }
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        DriverMonitoringLog driverMonitoringLog=new DriverMonitoringLog();
        driverMonitoringLog.setMsgid(msgId.toString());
        driverMonitoringLog.setOperationAccount(user.getLoginName());
        driverMonitoringLog.setOperator(user.getName());
        driverMonitoringLogMapper.insertSelective(driverMonitoringLog);
        return true;
    }

    /**
     * 获取消息中心token
     * @param rediskey
     * @return
     */
    public String getToken(String rediskey){
        String authToken ="";
        try {
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("appKey", APP_KEY);
            long currentTimeMillis = System.currentTimeMillis();
            map.put("timestamp", currentTimeMillis);
            map.put("sign", MD5Utils.getMD5DigestHex(APP_KEY + currentTimeMillis + APP_SECRET));
            logger.info("--发送消息获取token入参--：" + JSONObject.toJSONString(map));
            Map<String, Object> obj = MpOkHttpUtil.okHttpPostBackMap(driverMessageSendUrl + "/v1/auth", map, 0, "圈外空闲司机消息");
            logger.info(LOGTAG + "发送站内信, 获取保存消息中心authToken={}", obj);
            if (obj != null) {
                authToken = (String) obj.get("authToken");
                if(StringUtils.isNotEmpty(authToken)){
                    RedisCacheUtil.set(rediskey, authToken.toString(), 30 * 60);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("发送消息push异常",e);
        }finally {
            return authToken;
        }

    }

    /**获取msgId
     * @param authToken
     * @param templateId
     * @return
     */
    public long getMsgId(String authToken,long templateId){
        long msgId=0;
        JSONObject customizedParams = new JSONObject();
        customizedParams.put("content", "您当前处于商圈外，商圈内订单较多，建议您前往商圈内接单，请前往首约司机APP—首页—商圈外;看商圈和您所在位置.");
        Map headerParams = new HashMap();
        headerParams.put("authToken", authToken);
        TreeMap<String, Object> map1 = new TreeMap<String, Object>();
        map1.put("templateId", templateId);
        map1.put("customizedParams", customizedParams);
        map1.put("extraMsg", "");
        try {
            logger.info("--发送消息获取msgId入参--：" + JSONObject.toJSONString(map1));
            JSONObject jsonObj = MpOkHttpUtil.okHttpPostBackJson(driverMessageSendUrl + "/v1/message/saveMsgBody",
                    map1, 0, null, headerParams);
            logger.info(LOGTAG + "--发送站内信, 获取msgId返回结果--{}", jsonObj);
            if(jsonObj!=null && "0".equals(jsonObj.getString("code"))){
                msgId=jsonObj.getLong("msgId");
            }
        } catch (Exception e){
            logger.error("--发送消息获取msgId异常--{}",e);
        }
        finally {
            return msgId;
        }
    }

    /**
     * 批量推送接口
     * @param authToken
     * @param msgId
     * @param driverIds
     * @return
     */
    public JSONObject pushToList(String authToken,long msgId,String driverIds){
        Map headerParams = new HashMap();
        headerParams.put("authToken", authToken);
        TreeMap<String, Object> mapFin = new TreeMap<String, Object>();
        mapFin.put("msgId",msgId);
        mapFin.put("driverIds",driverIds);
        JSONObject result =null;
        try {
            logger.info("--批量发送消息入参--：" + JSONObject.toJSONString(mapFin));
            result = MpOkHttpUtil.okHttpPostBackJson(driverMessageSendUrl + "/v1/message/driver/pushToList",
                    mapFin, 0, null, headerParams);
            logger.info(LOGTAG + "--最终发送站内信返回结果--{}", result);
        } catch (Exception e){
            logger.error("--批量推送消息异常--司机ID集合-{}--{}",driverIds,e);
        }
        finally {

            return result;
        }
    }

    /**逻辑分页
     * @param f
     * @param pageNo
     * @param dataSize
     * @param <F>
     * @return
     */
    public <F> List<F> datepaging(List<F> f, int pageNo, int dataSize) {
        /*
         * 经过测试发现当pageNo为0或者小于时，也就是第0页时，程序会报错，所以需要处理一下pageNo的值
         * 先进行空值的判断，避免程序出现null异常
         * 当pageNo的值小于等于0时，我们让它的值为1
         */
        // 参数的校验
        if (f == null) {// 当传入过来的list集合为null时，先进行实例化
            f = new ArrayList<F>();
        }
        if ((Object) pageNo == null) {// 当传入过来的pageNo为null时，先进行赋值操作
            pageNo = 1;
        }
        if ((Object) dataSize == null) {// 当传入过来的dataSize为null时，先进行赋值操作
            dataSize = 1;
        }
        if (pageNo <= 0) {
            pageNo = 1;
        }
        // 记录一下数据一共有多少条
        int totalitems = f.size();
        // 实例化一个接受分页处理之后的数据
        List<F> afterList = new ArrayList<F>();

        for (int i = (pageNo - 1) * dataSize; i < (((pageNo - 1) * dataSize) + dataSize > totalitems ? totalitems
                : ((pageNo - 1) * dataSize) + dataSize); i++) {
            // 然后将数据存入afterList中
            afterList.add(f.get(i));
        }
        // 然后将处理后的数据集合进行返回
        return afterList;
    }
}
