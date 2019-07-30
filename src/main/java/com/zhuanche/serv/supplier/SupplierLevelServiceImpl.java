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
import java.util.*;

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
            Map<String,BigDecimal> supplierLevelCache = new HashMap<>();
            for(SupplierLevelAdditional item : list){
                item.setCreateTime(now);
                item.setUpdateTime(now);
                item.setCreateBy(userName);
                item.setUpdateBy(userName);
                supplierLevelAdditionalMapper.insertSelective(item);

                if(supplierLevelCache.containsKey(""+item.getSupplierLevelId())){
                    supplierLevelCache.put(""+item.getSupplierLevelId(),supplierLevelCache.get(""+item.getSupplierLevelId()).add(item.getItemValue()));
                }else {
                    supplierLevelCache.put(""+item.getSupplierLevelId(), item.getItemValue());
                }
            }
            Set<String> keySet = supplierLevelCache.keySet();
            Iterator<String> is = keySet.iterator();
            String supplierLevelIdString = null;
            SupplierLevel supplierLevel = null;
            BigDecimal additionalScore = null;
            while(is.hasNext()){
                supplierLevelIdString = is.next();
                additionalScore = supplierLevelCache.get(supplierLevelIdString);
                supplierLevel = supplierLevelMapper.selectByPrimaryKey(new Integer(supplierLevelIdString));
                BigDecimal levelScore = calculationLevelScore(supplierLevel.getScaleScore(),supplierLevel.getEfficiencyScore(),supplierLevel.getServiceScore(),additionalScore);
                //等级分
                supplierLevel.setGradeScore(levelScore);
                //附加分
                supplierLevel.setAdditionalScore(additionalScore);
                supplierLevel.setUpdateTime(now);
                supplierLevelMapper.updateByPrimaryKeySelective(supplierLevel);

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
        //迁移到mp-job里通过传参可完成触发

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
        BigDecimal gradeScore = calculationLevelScore(supplierLevel.getScaleScore(),supplierLevel.getEfficiencyScore(),supplierLevel.getServiceScore(),  additionScore);
        //修改等级分
        supplierLevel.setGradeScore(gradeScore);
        //修改附加分
        supplierLevel.setAdditionalScore(additionScore);
        supplierLevel.setUpdateTime(now);
        supplierLevelMapper.updateByPrimaryKeySelective(supplierLevel);
    }

    /**
     * 排名并发布
     * @param month
     */
    @Override
    public void doSeqence(String month) {
        //迁移到mp-job里通过传参可完成触发
    }

    @Override
    public SupplierLevel findSupplierLevelScoreBySupplierId(int supplierId) {
        return supplierLevelexMapper.findSupplierLevelScoreBySupplierId(supplierId);
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
