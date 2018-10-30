package com.zhuanche.serv;

import com.zhuanche.dto.driver.DriverVoEntity;

import java.util.List;

public interface IDriverService <DriverEntity> {

    public int selectDriverByKeyCountAddCooperation(DriverVoEntity params);

    public List<DriverVoEntity> selectDriverByKeyAddCooperation(DriverVoEntity params);
}
