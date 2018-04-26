package com.zhuanche.service.impl.rentcar;

import com.zhuanche.dao.rentcar.DistrictMapper;
import com.zhuanche.entity.rentcar.District;
import com.zhuanche.service.rentcar.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("districtService")
public class DistrictServiceImpl implements DistrictService {

    @Autowired
    private DistrictMapper districtMapper;

    @Override
    public List<District> findListByCityId(Integer cityId) {
        return districtMapper.findListByCityId(cityId);
    }
}
