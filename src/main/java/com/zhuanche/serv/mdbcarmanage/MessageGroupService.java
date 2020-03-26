package com.zhuanche.serv.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarMessageGroup;
import mapper.mdbcarmanage.ex.CarMessageGroupExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/2/10 上午11:08
 * @Version 1.0
 */
@Service
public class MessageGroupService {

    @Autowired
    private CarMessageGroupExMapper carMessageGroupExMapper;



    public int createGroup(CarMessageGroup group){
      return   carMessageGroupExMapper.insert(group);
    }

    public CarMessageGroup detailGroup(Integer id){
        return carMessageGroupExMapper.selectByPrimaryKey(Long.valueOf(id));
    }

    public int editGroup(CarMessageGroup group){
        return   carMessageGroupExMapper.updateByPrimaryKeySelective(group);
    }

    public List<CarMessageGroup> searchGroup(CarMessageGroup group){
        return carMessageGroupExMapper.searchGroup(group);
    }

    public int isRepeatGroupName(String  groupName){
        try {
            return   carMessageGroupExMapper.isRepeatGroupName(groupName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
