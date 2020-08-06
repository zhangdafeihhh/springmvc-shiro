package com.zhuanche.serv.punish;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.driver.DriverPunishDto;
import lombok.extern.slf4j.Slf4j;
import mapper.driver.ex.DriverPunishExMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */
@Slf4j
public class DriverPunishService {


    @Resource
    private DriverPunishExMapper driverPunishExMapper;



    public PageInfo<DriverPunishDto> selectList(DriverPunishDto params) {
        return selectList(params, true);
    }

    public PageInfo<DriverPunishDto> selectList(DriverPunishDto params, Boolean isCount) {
        int total = 0;
        List<DriverPunishDto> rows = null;
        if (params.getPage() == null) {
            params.setPage(1);
        }
        if (params.getPagesize() == null) {
            params.setPagesize(10);
        }
        Page p = PageHelper.startPage(params.getPage(), params.getPagesize(), isCount);
        PageInfo<DriverPunishDto> pageInfo = null;
        try {
            rows = driverPunishExMapper.selectList(params);
            total = (int) p.getTotal();
        } catch (Exception e) {
            log.error("出租订单信息", e);
        } finally {
            PageHelper.clearPage();
        }
        if (rows == null || rows.size() == 0) {
            return new PageInfo<>(new ArrayList<>());
        }
        pageInfo = new PageInfo<>(rows);
        pageInfo.setTotal(total);
        return pageInfo;
    }





}
