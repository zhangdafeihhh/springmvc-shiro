package com.zhuanche.serv.deiver;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constant.OrderConstants;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.entity.driver.CustomerAppraisal;
import com.zhuanche.entity.driver.MpCarBizCustomerAppraisal;
import com.zhuanche.entity.driver.MpCustomerAppraisalParams;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;
import com.zhuanche.serv.impl.CarBizCustomerAppraisalExServiceImpl;
import mapper.driver.CustomerAppraisalMapper;
import mapper.driver.ex.CustomerAppraisalExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MpDriverCustomerAppraisalService {

    private static final Logger logger = LoggerFactory.getLogger(MpDriverCustomerAppraisalService.class);

    @Autowired
    CustomerAppraisalExMapper customerAppraisalExMapper;

    @Autowired
    CustomerAppraisalMapper customerAppraisalMapper;

    @Value("${index}")
    private String env;

    public PageInfo<MpCarBizCustomerAppraisal> findPageByparam(MpCustomerAppraisalParams params) {
        logger.info("查询订单评分，参数为："+(params==null?"null": JSON.toJSONString(params)));
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        String createDateBegin = params.getCreateDateBegin();
        String createDateEnd = params.getCreateDateEnd();
        String orderFinishTimeBegin = params.getOrderFinishTimeBegin();
        String orderFinishTimeEnd = params.getOrderFinishTimeEnd();


        if (StringUtils.isNotEmpty(createDateBegin)){
            params.setCreateDateBegin(createDateBegin + " 00:00:00");



        }
        if (StringUtils.isNotEmpty(createDateEnd)){
            params.setCreateDateEnd(createDateEnd + " 23:59:59");

        }
        if (StringUtils.isNotEmpty(orderFinishTimeBegin)){
            params.setOrderFinishTimeBegin(orderFinishTimeBegin + " 00:00:00");
            //如果是线上环境
            //如果是2018以及之后
            if(env.equals("pre") || env.equals("online")){
                params = this.getMinId(params);
            }
        }
        if (StringUtils.isNotEmpty(orderFinishTimeEnd)){
            params.setOrderFinishTimeEnd(orderFinishTimeEnd + " 23:59:59");
            if(env.equals("pre") || env.equals("online")){
                params = this.getMaxId(params);
            }
        }


        List<MpCarBizCustomerAppraisal> list  = customerAppraisalExMapper.queryForListObject(params);
        PageInfo<MpCarBizCustomerAppraisal> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }


    //根据开始时间获取数据库里面的id最小值minId
    private MpCustomerAppraisalParams getMinId(MpCustomerAppraisalParams params){
        String orderFinishTimeBegin = params.getOrderFinishTimeBegin();
        Integer minId = null;
        String createDateSub = orderFinishTimeBegin.substring(0,4);
        if(Integer.valueOf(createDateSub)>2018){
            minId =  OrderConstants.getOrderMap().get(orderFinishTimeBegin.substring(0,7));
            if(minId == null){
                minId = OrderConstants.getOrderMap().get("2020-04");
            }
        }else {
            //考虑到可能会选择2018-12,多加一个月的判断
            minId = OrderConstants.getOrderMap().get(orderFinishTimeBegin.substring(0,7));
        }
        logger.info("【min】获取的月份：" + orderFinishTimeBegin.substring(0,7) +",value值" + minId);
        params.setMinId(minId);
        return params;
    }

    //根据结束时间获取数据库里面的id最大值maxId
    private MpCustomerAppraisalParams getMaxId(MpCustomerAppraisalParams params){
        String orderFinishTimeEnd = params.getOrderFinishTimeEnd();
        Integer maxId = null;
        String createDeteSub = orderFinishTimeEnd.substring(0,4);
        //如果是2018以及之前的
        if(Integer.valueOf(createDeteSub)<=2018){
            maxId = OrderConstants.getOrderMap().get("2018-12");
        }
        logger.info("【max】获取的月份：" + orderFinishTimeEnd.substring(0,7) +",value值" + maxId);
        params.setMaxId(maxId);
        return params;
    }

    public List<Integer> queryIds(MpCustomerAppraisalParams params){
        logger.info("查询订单评分，参数为："+(params==null?"null": JSON.toJSONString(params)));
        String createDateBegin = params.getCreateDateBegin();
        String createDateEnd = params.getCreateDateEnd();
        String orderFinishTimeBegin = params.getOrderFinishTimeBegin();
        String orderFinishTimeEnd = params.getOrderFinishTimeEnd();
        if (StringUtils.isNotEmpty(createDateBegin)){
            params.setCreateDateBegin(createDateBegin + " 00:00:00");
            params = this.getMinId(params);
        }
        if (StringUtils.isNotEmpty(createDateEnd)){
            params.setCreateDateEnd(createDateEnd + " 23:59:59");
            params = this.getMaxId(params);
        }
        if (StringUtils.isNotEmpty(orderFinishTimeBegin)){
            params.setOrderFinishTimeBegin(orderFinishTimeBegin + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderFinishTimeEnd)){
            params.setOrderFinishTimeEnd(orderFinishTimeEnd + " 23:59:59");
        }
        return customerAppraisalExMapper.queryIds(params);
    }
    @MasterSlaveConfigs(configs =
    @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE))
    public List<MpCarBizCustomerAppraisal> queryByIds(List<Integer> ids) {
        return customerAppraisalExMapper.queryByIds(ids);
    }

    @MasterSlaveConfigs(configs =
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE))
    public CustomerAppraisal selectByPrimaryKey(Integer appraisal){
         return customerAppraisalMapper.selectByPrimaryKey(appraisal);
    }

    @MasterSlaveConfigs(configs =
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    )
    public int updateBySelective(CustomerAppraisal customerAppraisal){
        int result=0;
        try {
            result = customerAppraisalMapper.updateByPrimaryKeySelective(customerAppraisal);
        } catch (Exception e) {
            logger.error("修改乘客对司机的评分信息失败,e:{}",e);
        }
        return result;
    }

}
