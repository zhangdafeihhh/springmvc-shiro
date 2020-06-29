package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.util.LbsSignUtil;
import com.zhuanche.common.util.StaticRpcUrl;
import com.zhuanche.constant.Constants;
import com.zhuanche.http.MpOkHttpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/31 下午8:58
 * @Version 1.0
 */
@Component
public class InterCityUtils {


    private Logger logger = LoggerFactory.getLogger(this.getClass());





    private static final String SYSMOL = "&";

    private static final String SPLIT = ",";



    /**
     * 判断上车点 是否在围栏区域 wiki:http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=11178396
     * @param boardingCityId
     * @param boardingGetOnX
     * @param boardingGetOnY
     * @return
     */
    public List<String> hasBoardRoutRights(Integer boardingCityId, String boardingGetOnX, String boardingGetOnY){
        List<String> strList = new ArrayList<>();
        //根据横纵坐标获取围栏，根据围栏获取路线
        Map<String,Object> mapX = Maps.newHashMap();
        mapX.put("token", StaticRpcUrl.lbsToken);
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cityId",boardingCityId);
        jsonObject.put("x",boardingGetOnX);
        jsonObject.put("y",boardingGetOnY);
        array.add(jsonObject);
        mapX.put("coordinateList",array.toString());
        String lbsSign = LbsSignUtil.sign(mapX,StaticRpcUrl.lbsToken);
        mapX.put("sign",lbsSign);
        String lbsResult = MpOkHttpUtil.okHttpPost(StaticRpcUrl.lbsUrl + "/area/getAreaByCoordinate",mapX,0,null);
        String getOnId = "";
        if(StringUtils.isNotEmpty(lbsResult)){
            JSONObject jsonResult = JSONObject.parseObject(lbsResult);
            if(jsonResult.get(Constants.CODE) == null || jsonResult.getInteger(Constants.CODE) != 0 ||
                    jsonResult.get("data") == null) {
                logger.info("获取上车点失败");
                return null;
            }

            JSONArray arrayData = jsonResult.getJSONArray("data");
            if(arrayData == null){
                logger.info("获取上车区域失败");
                return null;
            }

            for(int i  =0;i<arrayData.size();i++){
                JSONObject lbsRes = arrayData.getJSONObject(0);
                if(lbsRes.get("areaId") != null){
                    getOnId = lbsRes.getString("areaId");
                    strList.add(getOnId);
                    //break;
                }

            }
        }

        if(CollectionUtils.isEmpty(strList)){
            logger.info("上下车点不再围栏区域");
            return null;
        }

        return strList;
    }



    /**
     * 获取路线 wiki:http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=11178396
     * @param boardingGetOffCityId
     * @param boardingGetOffX
     * @param boardingGetOffY
     * @return
     */
    public List<String> hasBoardOffRoutRights(Integer boardingGetOffCityId, String boardingGetOffX, String boardingGetOffY){

        List<String> strList = new ArrayList<>();
        Map<String,Object> mapY = Maps.newHashMap();

        mapY.put("token",StaticRpcUrl.lbsToken);
        JSONArray arrayY = new JSONArray();
        JSONObject jsonY = new JSONObject();
        jsonY.put("cityId",boardingGetOffCityId);
        jsonY.put("x",boardingGetOffX);
        jsonY.put("y",boardingGetOffY);
        arrayY.add(jsonY);
        mapY.put("coordinateList",arrayY.toString());
        String lbsSignY = LbsSignUtil.sign(mapY,StaticRpcUrl.lbsToken);
        mapY.put("sign",lbsSignY);

        String resultY = MpOkHttpUtil.okHttpGet(StaticRpcUrl.lbsUrl+"/area/getAreaByCoordinate",mapY,0,null);

        String getOffId = "";

        if(resultY != null ){
            JSONObject jsonResultY = JSONObject.parseObject(resultY);
            if(jsonResultY.get(Constants.CODE) == null || jsonResultY.getInteger(Constants.CODE) != 0 ||
                    jsonResultY.get("data") == null){
                logger.info("获取下车点失败");
                return null;
            }

            JSONArray jsonArray = jsonResultY.getJSONArray(Constants.DATA);
            if(jsonArray.size() == 0){
                logger.info("获取下车点失败");
                return null;
            }


            for(int i  =0;i<jsonArray.size();i++){
                JSONObject lbsRes = jsonArray.getJSONObject(0);
                if(lbsRes.get("areaId") != null){
                    getOffId = lbsRes.getString("areaId");
                    strList.add(getOffId);
                    //break;
                }
            }
        }

        if(CollectionUtils.isEmpty(strList)){
            logger.info("上下车点不再围栏区域");
            return null;
        }
        return strList;
    }

    /**
     * 是否有配置的线路 http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=40172935
     * @param getOnId
     * @param getOffId
     * @return
     */
    public String hasRoute(String getOnId,String getOffId) {
        JSONObject jsonRoute = new JSONObject();
        try {
            Integer areaStartRangId = Integer.valueOf(getOnId);
            Integer areaEndRangId = Integer.valueOf(getOffId);
            String areaUrl = StaticRpcUrl.configUrl + "/intercityCarUse/getLineSupplier?areaStartRangeId=" + areaStartRangId + "&areaEndRangeId=" + areaEndRangId;

            String areaResult = MpOkHttpUtil.okHttpGet(areaUrl, null, 0, null);
            if (StringUtils.isNotEmpty(areaResult)) {
                JSONObject jsonResult = JSONObject.parseObject(areaResult);

                if (jsonResult.get(Constants.DATA) != null && jsonResult.get(Constants.DATA) != "") {

                    JSONObject jsonData = jsonResult.getJSONObject("data");
                    jsonRoute.put("supplierId", jsonData.getString("supplierId"));

                    logger.info("该坐标含有线路");
                } else {
                    logger.info("对应坐标没有线路");
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            logger.error("获取线路异常" + e.getMessage());
            return null;
        }
        return jsonRoute.toString();

    }



    public static String testHasBoardOffRoutRights(Integer boardingGetOffCityId, String boardingGetOffX, String boardingGetOffY){
        Map<String,Object> mapY = Maps.newHashMap();

        mapY.put("token","fadca09abaac445e38bb76d86466181a");
        JSONArray arrayY = new JSONArray();
        JSONObject jsonY = new JSONObject();
        jsonY.put("cityId",boardingGetOffCityId);
        jsonY.put("x",boardingGetOffX);
        jsonY.put("y",boardingGetOffY);
        arrayY.add(jsonY);
        mapY.put("coordinateList",arrayY.toString());
        String lbsSignY = LbsSignUtil.sign(mapY,"fadca09abaac445e38bb76d86466181a");
        mapY.put("sign",lbsSignY);

        String resultY = MpOkHttpUtil.okHttpGet("http://test-inside-area-service.01zhuanche.com/area/getAreaByCoordinate",mapY,0,null);
        System.out.println("========" + resultY);
        String getOffId = "";

        if(resultY != null ){
            JSONObject jsonResultY = JSONObject.parseObject(resultY);
            if(jsonResultY.get(Constants.CODE) == null || jsonResultY.getInteger(Constants.CODE) != 0 ||
                    jsonResultY.get("data") == null){
                return null;
            }

            JSONArray jsonArray = jsonResultY.getJSONArray("data");
            if(jsonArray.size() == 0){
                return null;
            }


            for(int i  =0;i<jsonArray.size();i++){
                JSONObject lbsRes = jsonArray.getJSONObject(0);
                if(lbsRes.get("areaId") != null){
                    getOffId = lbsRes.getString("areaId");
                    break;
                }
            }
        }

        if(StringUtils.isEmpty(getOffId)){
            return null;
        }
        System.out.println("=======getOffId:" + getOffId);
        return getOffId;
    }

    public static void main(String[] args) {
        //116.46269989013672,39.943695068359375;116.469304,39.949344
        String bookingStartPoint = "116.46269989013672,39.943695068359375;116.469304,39.949344";
        String[] on = bookingStartPoint.split(",");
        System.out.println("=========获取坐标做的数据=====" + JSONObject.toJSONString(on));
        System.out.println(testHasBoardOffRoutRights(44,"116.46269989013672","39.943695068359375"));
    }

}
