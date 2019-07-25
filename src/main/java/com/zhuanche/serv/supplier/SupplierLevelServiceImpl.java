package com.zhuanche.serv.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.bigdata.BiSaasSupplierRankData;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.bigdata.BiSaasSupplierRankDataService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.bigdata.BiSaasSupplierRankDataMapper;
import mapper.bigdata.ex.BiSaasSupplierRankDataExMapper;
import mapper.driver.SupplierLevelAdditionalMapper;
import mapper.driver.SupplierLevelMapper;
import mapper.driver.ex.SupplierLevelAdditionalExMapper;
import mapper.driver.ex.SupplierLevelExMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupplierLevelServiceImpl  implements  SupplierLevelService{

    private static final Logger logger = LoggerFactory.getLogger(SupplierLevelServiceImpl.class);

    @Autowired
    private SupplierLevelMapper supplierLevelMapper;

    @Autowired
    private SupplierLevelExMapper supplierLevelexMapper;

    @Autowired
    private SupplierLevelAdditionalMapper supplierLevelAdditionalMapper;

    @Autowired
    public SupplierLevelAdditionalExMapper supplierLevelAdditionalExMapper;

    @Autowired
    public BiSaasSupplierRankDataMapper biSaasSupplierRankDataMapper;

    @Autowired
    public BiSaasSupplierRankDataService biSaasSupplierRankDataService;
    @Autowired
    private CarBizSupplierMapper carBizSupplierMapper ;

    @Autowired
    private CarBizCityExMapper carBizCityExMapper;

    @Override
    public PageInfo<SupplierLevel> findPage(int pageNo, int pageSize, SupplierLevel params) {

        PageHelper.startPage(pageNo, pageSize, true);
        PageInfo<SupplierLevel> pageInfo = null;
        try{
            List<SupplierLevel> list =  supplierLevelexMapper.findPage(params);
            pageInfo = new PageInfo<>(list);

        }finally {
            PageHelper.clearPage();
        }
        return pageInfo;
    }

    /**
     * 发布供应商等级积分
     * @param idList
     */
    @Override
    public void doPublishSupplierLevel(List<Integer> idList) {
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        String userName = currentLoginUser.getName();

        StringBuffer stringBuffer = new StringBuffer();
        if(idList != null){
            int size = idList.size();
            for (int i=0;i<size;i++) {
                stringBuffer.append(idList.get(i));
                if(i != size-1){
                    stringBuffer.append(",");
                }
            }
        }

        if(idList != null && idList.size() >= 1){
            logger.info("用户"+userName+"["+userId+"]发布供应商的等级，其中供应商的id为"+stringBuffer.toString());
            supplierLevelexMapper.doPublishSupplierLevel(idList);
        }else{
            logger.info("用户"+userName+"["+userId+"]发布供应商的等级，但是供应商的id为空");
        }


    }

    @Override
    public void doUnPublishSupplierLevel(List<Integer> idList) {
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        String userName = currentLoginUser.getName();

        StringBuffer stringBuffer = new StringBuffer();
        if(idList != null){
            int size = idList.size();
            for (int i=0;i<size;i++) {
                stringBuffer.append(idList.get(i));
                if(i != size-1){
                    stringBuffer.append(",");
                }
            }
        }

        if(idList != null && idList.size() >= 1){
            logger.info("用户"+userName+"["+userId+"]取消发布供应商的等级，其中供应商的id为"+stringBuffer.toString());
            supplierLevelexMapper.doUnPublishSupplierLevel(idList);
        }else{
            logger.info("用户"+userName+"["+userId+"]取消发布供应商的等级，但是供应商的id为空");
        }
    }

    @Override
    public List<SupplierLevelAdditional> findSupplierLevelAdditionalBySupplierLevelId(Integer supplierLevelId) {
        logger.info("查询供应商的附加信息，supplierLevelId="+supplierLevelId);
        return supplierLevelAdditionalExMapper.findbySupplierLevelId(supplierLevelId);
    }

    @Override
    public void doDeleteBySupplierLevelAdditionalId(Integer supplierLevelAdditionalId) {
        logger.info("删除供应商的附加信息，supplierLevelAdditionalId="+supplierLevelAdditionalId);
        supplierLevelAdditionalMapper.deleteByPrimaryKey(supplierLevelAdditionalId);
    }

    @Override
    public void doImportSupplierLevelAdditional(List<SupplierLevelAdditional> list) {

        if(list != null){
            logger.info("导入供应商附加分，条目数量为"+list.size());
            Date now = new Date();
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            Integer userId = currentLoginUser.getId();
            String userName = currentLoginUser.getName();
            for(SupplierLevelAdditional item : list){
                item.setCreateTime(now);
                item.setUpdateTime(now);
                item.setCreateBy(userName);
                item.setUpdateBy(userName);
                supplierLevelAdditionalMapper.insertSelective(item);
            }
        }else{
            logger.info("导入供应商附加分，但是list为null");
        }

    }

    @Override
    public SupplierLevel findByMonthAndSupplierName(String month, String supplierName) {
        SupplierLevel entity = supplierLevelexMapper.findByMonthAndSupplierName(month,supplierName);
        return entity;
    }

    @Override
    public SupplierLevelAdditional findBySupplierLevelIdAndSupplierLevelAdditionalName(Integer supplierLevelId, String supplierLevelAdditionalName) {
        return supplierLevelAdditionalExMapper.findBySupplierLevelIdAndSupplierLevelAdditionalName(supplierLevelId,supplierLevelAdditionalName);
    }

    @Override
    public void doGenerateByDate(String month) throws ParseException {

        int pageNo  = 1;
        int pageSize = 20;
        PageInfo<BiSaasSupplierRankData> pageInfo =  biSaasSupplierRankDataService.findPage(pageNo,pageSize,month);
        if(pageInfo != null){
            int pages = pageInfo.getPages();
            List<BiSaasSupplierRankData>  list = null;

            Map<String,CarBizSupplier> carBizSupplierCache = new HashMap<>();
            Map<String,String> citynameCache = new HashMap<>();

            SupplierLevel dbSupplierLevel = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 24小时制
            for(pageNo = 1; pageNo<=pages;pageNo++){
                pageInfo =  biSaasSupplierRankDataService.findPage(pageNo,pageSize,month);
                list = pageInfo.getList();
                if(list != null  && list.size() >= 1){
                    CarBizSupplier supplier = null;
                    String cityname = null;
                    for(BiSaasSupplierRankData rankData : list){
                        SupplierLevel supplierLevel = new SupplierLevel();
                        supplierLevel.setMonth(rankData.getDataMonth());
                        if(carBizSupplierCache.containsKey(""+supplierLevel.getSupplierId())){
                            supplier = carBizSupplierCache.get(""+supplierLevel.getSupplierId());
                        }else{
                            supplier =  carBizSupplierMapper.selectByPrimaryKey(supplierLevel.getSupplierId());
                            carBizSupplierCache.put(""+supplierLevel.getSupplierId(),supplier);
                        }

                        if(citynameCache.containsKey(""+supplier.getSupplierCity())){
                            cityname = citynameCache.get(""+supplier.getSupplierCity());
                        }else {
                            cityname = carBizCityExMapper.queryNameById(supplier.getSupplierCity());
                            citynameCache.put(""+supplier.getSupplierCity(),cityname);
                        }

                        supplierLevel.setCityId(supplierLevel.getSupplierId());
                        supplierLevel.setCityName(cityname);
                        supplierLevel.setSupplierName(supplier.getSupplierFullName());
                        supplierLevel.setStartTime(sdf.parse(rankData.getStartTime()));
                        supplierLevel.setEndTime(sdf.parse(rankData.getEndTime()));
                        supplierLevel.setScaleScore(new BigDecimal(rankData.getScaleScore()));
                        supplierLevel.setEfficiencyScore(new BigDecimal(rankData.getEfficiencyScore()));
                        supplierLevel.setServiceScore(new BigDecimal(rankData.getServiceScore()));
                        supplierLevel.setAdditionalScore(BigDecimal.ZERO);

                        //（规模分*35%+效率分*30%+服务分*35%）+附加分
                        BigDecimal levelScore = calculationLevelScore(supplierLevel.getScaleScore(),supplierLevel.getEfficiencyScore(),supplierLevel.getServiceScore(),supplierLevel.getAdditionalScore());
                        supplierLevel.setGradeScore(levelScore);
                        supplierLevel.setStates(1);

                        Date now = new Date();

                        supplierLevel.setUpdateTime(now);

                        dbSupplierLevel = supplierLevelexMapper.findByMonthAndSupplierName(month,supplierLevel.getSupplierName());
                        if(dbSupplierLevel == null){
                            supplierLevel.setCreateTime(now);
                            supplierLevelMapper.insertSelective(supplierLevel);

                        }else{
                            supplierLevelMapper.updateByPrimaryKeySelective(supplierLevel);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void doSaveSupplierLevelAdditionScore(Integer supplierLevelId,String delIds, String saveJson) {

        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Integer userId = currentLoginUser.getId();
        String userName = currentLoginUser.getName();
        if(StringUtils.isNoneEmpty(delIds)){
          String[] delIdArray =  delIds.split(",");
          for(String id:delIdArray){
              supplierLevelAdditionalMapper.deleteByPrimaryKey(new Integer(id));
          }
        }
        Date now = new Date();
        BigDecimal additionScore = BigDecimal.ZERO;
        if(StringUtils.isNoneEmpty(saveJson)){
            JSONArray jsonArray = JSONArray.parseArray(saveJson);
            int length = jsonArray.size();
            JSONObject item = null;

            for(int i=0;i<length;i++){
                item = jsonArray.getJSONObject(i);
                SupplierLevelAdditional supplierLevelAdditional = JSON.parseObject(item.toJSONString(),SupplierLevelAdditional.class);
                supplierLevelAdditional.setUpdateTime(now);
                supplierLevelAdditional.setUpdateBy(userName);
                supplierLevelAdditionalMapper.updateByPrimaryKeySelective(supplierLevelAdditional);

                additionScore = additionScore.add(supplierLevelAdditional.getItemValue());

            }
        }
        //重新计算该供应商等级的等级分
        //（规模分*35%+效率分*30%+服务分*35%）+附加分
        SupplierLevel supplierLevel = supplierLevelMapper.selectByPrimaryKey(supplierLevelId);
        BigDecimal additionalScore = calculationLevelScore(supplierLevel.getScaleScore(),supplierLevel.getEfficiencyScore(),supplierLevel.getServiceScore(),  additionScore);
        supplierLevel.setAdditionalScore(additionalScore);
        supplierLevel.setUpdateTime(now);
        supplierLevelMapper.updateByPrimaryKeySelective(supplierLevel);
    }

    private BigDecimal calculationLevelScore(BigDecimal scaleScore,BigDecimal efficiencyScore,BigDecimal serviceScore,BigDecimal additionScore){
//        （规模分*35%+效率分*30%+服务分*35%）+附加分
        BigDecimal levelScore = new BigDecimal("0.35").multiply(scaleScore)
                .add(   new BigDecimal("0.30").multiply(efficiencyScore ))
                .add(   new BigDecimal("0.35").multiply(serviceScore ))
                .add(additionScore);
        return levelScore;
    }
}
