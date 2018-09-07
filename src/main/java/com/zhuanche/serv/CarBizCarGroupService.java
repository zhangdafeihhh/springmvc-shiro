package com.zhuanche.serv;

import com.zhuanche.entity.rentcar.CarBizCarGroup;
import mapper.rentcar.CarBizCarGroupMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarBizCarGroupService {

    @Autowired
    private CarBizCarGroupMapper carBizCarGroupMapper;

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;

    public CarBizCarGroup selectByPrimaryKey(Integer groupId){
        return carBizCarGroupMapper.selectByPrimaryKey(groupId);
    }

    /**
     * 查询手机号是否存在
     * @param groupName 服务类型名称
     * @return
     */
    public CarBizCarGroup queryGroupByGroupName(String groupName){
        return carBizCarGroupExMapper.queryGroupByGroupName(groupName);
    }

    /**
     * 根据groupId查询
     * @param carBizCarGroup
     * @return
     */
    public CarBizCarGroup queryForObject(CarBizCarGroup carBizCarGroup){
        return carBizCarGroupExMapper.queryForObject(carBizCarGroup);
    }

    /**
     * 查询列表
     * @param carBizCarGroup
     * @return
     */
    public List<CarBizCarGroup> queryForListObject(CarBizCarGroup carBizCarGroup){
        return carBizCarGroupExMapper.queryForListObject(carBizCarGroup);
    }
}
