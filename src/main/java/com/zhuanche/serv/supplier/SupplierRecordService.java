package com.zhuanche.serv.supplier;

import com.zhuanche.entity.driver.SupplierExtDto;
import mapper.driver.ex.SupplierExtExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/12/3 下午3:57
 * @Version 1.0
 */
public interface SupplierRecordService {

    List<SupplierExtDto> extDtoList(SupplierExtDto dto);
}
