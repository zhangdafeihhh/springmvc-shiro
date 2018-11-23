package com.zhuanche.serv.busManage.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.rentcar.BusInfoDTO;
import com.zhuanche.serv.busManage.BusInfoService;
import com.zhuanche.vo.rentcar.BusDetailVO;
import com.zhuanche.vo.rentcar.BusInfoVO;
import mapper.rentcar.ex.BusInfoExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: mp-manage
 * @description: 巴士车辆管理
 * @author: niuzilian
 * @create: 2018-11-22 16:06
 **/
@Service
public class BusInfoServiceImpl implements BusInfoService {

    @Autowired
    private BusInfoExMapper busInfoExMapper;

    @Override
    public PageInfo<BusInfoVO> queryList(BusInfoDTO infoDTO) {
        PageInfo<BusInfoVO> pageInfo = PageHelper.startPage(infoDTO.getPageNum(), infoDTO.getPageSize(), true).doSelectPageInfo(() -> busInfoExMapper.selectList(infoDTO));
        return pageInfo;
    }

    @Override
    public BusDetailVO getDetail(Integer carId) {
        return busInfoExMapper.selectCarByCarId(carId);
    }

    @Override
    public Integer saveCar(BusInfoDTO busInfoDTO) {

        return null;
    }

    @Override
    public boolean licensePlatesIfExist(String licensePlates) {
        int result = busInfoExMapper.countLicensePlates(licensePlates);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String getLicensePlatesByCarId(Integer carId) {
        return busInfoExMapper.getLicensePlatesByCarId(carId);
    }
}
