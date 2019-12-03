package com.zhuanche.serv.supplier;

import com.zhuanche.entity.driver.SupplierExtDto;
import mapper.driver.ex.SupplierExtExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/12/3 下午4:00
 * @Version 1.0
 */
@Service
public class SupplierRecordServiceImpl implements SupplierRecordService{

    @Autowired
    private SupplierExtExMapper extExMapper;


    @Override
    public List<SupplierExtDto> extDtoList(SupplierExtDto dto) {
        return extExMapper.extDtoList(dto);
    }

    @Override
    public SupplierExtDto extDtoDetail(Integer supplierId) {
        return extExMapper.extDtoDetail(supplierId);
    }

    @Override
    public int editExtDto(SupplierExtDto dto) {
        return extExMapper.editExtDto(dto);
    }

    @Override
    public SupplierExtDto recordDetail(Integer id) {
        return extExMapper.recordDetail(id);
    }
}
