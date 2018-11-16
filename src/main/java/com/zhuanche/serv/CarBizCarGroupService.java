package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import mapper.rentcar.CarBizCarGroupMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CarBizCarGroupService {
    private static final Logger logger = LoggerFactory.getLogger(CarBizCarGroupService.class);
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
     * 查询服务类型
     * @param type 1:专车、2:大巴车
     * @return
     */
    public List<CarBizCarGroup> queryCarGroupList(Integer type){
        return carBizCarGroupExMapper.queryCarGroupList(type);
    }

    /**
     * 查询所有服务类型
     * @return
     */
    public Map<Integer, String> queryGroupNameMap() {
        List<CarBizCarGroup> list = carBizCarGroupExMapper.queryGroupNameList();
        if(list==null||list.size()==0) {
            return new HashMap<Integer, String>(4);
        }
        Map<Integer, String> result = Maps.newHashMap();
        for(CarBizCarGroup c : list) {
            result.put(c.getGroupId(),  c.getGroupName());
        }
        return result;
    }

    public List<CarBizCarGroup> queryCarGroupByIdSet(Set<Integer> carBizCarGroupSet) {
        if(carBizCarGroupSet == null || carBizCarGroupSet.isEmpty()){
            logger.info("参数为空，carBizCarGroupSet="+(carBizCarGroupSet==null?"null": JSON.toJSONString(carBizCarGroupSet)));
            return null;
        }
         return carBizCarGroupExMapper.queryCarGroupByIdSet(carBizCarGroupSet);
    }
}
