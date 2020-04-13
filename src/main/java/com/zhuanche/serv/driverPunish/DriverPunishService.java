package com.zhuanche.serv.driverPunish;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.driver.DriverAppealRecord;
import com.zhuanche.entity.driver.DriverPunishDto;
import mapper.driver.DriverAppealRecordMapper;
import mapper.driver.ex.DriverPunishExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */
@Service
public class DriverPunishService {

    private static Log log =  LogFactory.getLog(DriverPunishService.class);

    @Autowired
    private DriverPunishExMapper driverPunishExMapper;

    @Autowired
    private DriverAppealRecordMapper driverAppealRecordMapper;

    public PageInfo<DriverPunishDto> selectList(DriverPunishDto params) {
        int total = 0;
        List<DriverPunishDto> rows = null;
        if(params.getPage() == null ){
            params.setPage(1);
        }
        if(params.getPagesize() == null){
            params.setPagesize(10);
        }
        Page p = PageHelper.startPage(params.getPage(), params.getPagesize(), true);
        PageInfo<DriverPunishDto> pageInfo=null;
        try {
            rows = driverPunishExMapper.selectList(params);
            //if(rows!=null && rows.size()>0){
                //for(AssetRentOutOrderDto r:rows){
                //    //计算租金，保险金
                //    this.countRentRateInfo(r);
                //    //初始化部分字段
                //    this.setFieldForAssetRentOutOrder(r);
                //}
            //}
            total  = (int)p.getTotal();
        } catch (Exception e) {
            log.error("出租订单信息", e);
        } finally {
            PageHelper.clearPage();
        }
        if (rows==null || rows.size()==0) {
            return  new PageInfo<>(new ArrayList<>());
        }
        pageInfo = new PageInfo<>(rows);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    public DriverPunishDto getDetail(Integer punishId) {
        if(punishId != null){
            return  driverPunishExMapper.getDetail(punishId);
        }
        return null;
    }

    public List<DriverAppealRecord> selectDriverAppealRocordByPunishId(Integer punishId) {
        return driverAppealRecordMapper.selectDriverAppealRocordByPunishId(punishId);
    }


}
