package com.zhuanche.serv.mdbcarmanage.service;

import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.mdbcarmanage.CarDriverFile;

public interface CarDriverFileService {
    PageInfo<CarDriverFile> find4Page(CarDriverFile queryParam, int pageNo, int pageSize);
}
