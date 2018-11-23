package com.zhuanche.serv.busManage;

import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.rentcar.BusInfoDTO;
import com.zhuanche.vo.rentcar.BusDetailVO;
import com.zhuanche.vo.rentcar.BusInfoVO;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2018-11-22 15:51
 **/
public interface BusInfoService {
    PageInfo<BusInfoVO> queryList(BusInfoDTO infoDTO);
    BusDetailVO getDetail(Integer carId);
    Integer saveCar(BusInfoDTO busInfoDTO);
    /*存在 true 不存在 false*/
    boolean licensePlatesIfExist(String licensePlates);
    String getLicensePlatesByCarId(Integer carId);
}
