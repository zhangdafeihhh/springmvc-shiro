package com.zhuanche.serv.rentcar;

import com.zhuanche.dto.driver.DriverVoEntity;

import java.util.List;

public interface IDriverService <DriverEntity> {

    public int selectDriverByKeyCountAddCooperation(DriverVoEntity params);

    public List<DriverVoEntity> selectDriverByKeyAddCooperation(DriverVoEntity params);
}
