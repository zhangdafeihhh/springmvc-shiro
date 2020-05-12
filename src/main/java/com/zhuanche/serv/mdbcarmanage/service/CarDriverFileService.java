package com.zhuanche.serv.mdbcarmanage.service;

import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.mdbcarmanage.CarDriverFileDto;

public interface CarDriverFileService {
    PageInfo<CarDriverFileDto> find4Page(CarDriverFileDto queryParam, int pageNo, int pageSize);
}
