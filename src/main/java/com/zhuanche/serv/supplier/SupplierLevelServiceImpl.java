package com.zhuanche.serv.supplier;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.bigdata.BiSaasSupplierRankData;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;
import com.zhuanche.serv.bigdata.BiSaasSupplierRankDataService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.bigdata.BiSaasSupplierRankDataMapper;
import mapper.bigdata.ex.BiSaasSupplierRankDataExMapper;
import mapper.driver.SupplierLevelAdditionalMapper;
import mapper.driver.SupplierLevelMapper;
import mapper.driver.ex.SupplierLevelAdditionalExMapper;
import mapper.driver.ex.SupplierLevelExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    public void doGenerateByDate(Integer supplierId,Date settleStartTime,Date settleEndTime,int cityId,String cityNmae,String supplierName) {


        BiSaasSupplierRankData  rankData = biSaasSupplierRankDataService.findByParam(supplierId,settleStartTime,settleEndTime);
        if(rankData == null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 24小时制
            logger.info("根据参数查询大数据组数据，没有查到对应数据，参数为:supplierId="+supplierId+",settleStartTime="+sdf.format(settleStartTime)+",settleEndTime="+sdf.format(settleEndTime));
        }else {

            SupplierLevel supplierLevel = new SupplierLevel();
            supplierLevel.setMonth(rankData.getDataMonth());
            supplierLevel.setCityId(cityId);
            supplierLevel.setCityName(cityNmae);
            supplierLevel.setSupplierName(supplierName);
            supplierLevel.setStartTime(settleStartTime);
            supplierLevel.setEndTime(settleEndTime);
            supplierLevel.setScaleScore(new BigDecimal(rankData.getScaleScore()));
            supplierLevel.setEfficiencyScore(new BigDecimal(rankData.getEfficiencyScore()));
            supplierLevel.setServiceScore(new BigDecimal(rankData.getServiceScore()));
            supplierLevel.setAdditionalScore(BigDecimal.ZERO);

            //（规模分*35%+效率分*30%+服务分*35%）+附加分
            BigDecimal levelScore = calculationLevelScore(supplierLevel.getScaleScore(),supplierLevel.getEfficiencyScore(),supplierLevel.getServiceScore(),supplierLevel.getAdditionalScore());
            supplierLevel.setGradeScore(levelScore);
            supplierLevel.setStates();
        }


    }

    private BigDecimal calculationLevelScore(BigDecimal scaleScore,BigDecimal efficiencyScore,BigDecimal serviceScore,BigDecimal additionScore){
//        （规模分*35%+效率分*30%+服务分*35%）+附加分
        BigDecimal levelScore = new BigDecimal("0.35").multiply(scaleScore)
                .add(   new BigDecimal("0.30").multiply(efficiencyScore ))
                .add(   new BigDecimal("0.35").multiply(serviceScore )).add(additionScore);
        return levelScore;
    }
}
