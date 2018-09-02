package com.zhuanche.serv;

import com.zhuanche.entity.rentcar.CarBizChatUser;
import com.zhuanche.entity.rentcar.CarBizUserAction;
import mapper.rentcar.CarBizChatUserMapper;
import mapper.rentcar.CarBizUserActionMapper;
import mapper.rentcar.ex.CarBizUserActionExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CarBizChatUserService {

    @Autowired
    private CarBizChatUserMapper carBizChatUserMapper;

    @Autowired
    private CarBizUserActionExMapper carBizUserActionExMapper;

    /**
     * 根据userId、usertype
     * @param userId
     * @throws Exception
     */
    public int insertChat(int userId) throws Exception {

        CarBizChatUser carBizChatUser = new CarBizChatUser();
        carBizChatUser.setUserId(userId);
        carBizChatUser.setUserType(true);//1,司机
        //保存用户关系
        carBizChatUserMapper.insert(carBizChatUser);

        Integer chatUserId = carBizChatUser.getChatUserId();
        if (chatUserId != null) {
            CarBizUserAction userAction = new CarBizUserAction();
            userAction.setChatUserId(chatUserId);
            userAction.setRegisterDate(new Date(System.currentTimeMillis()));
            //保存动态数据-注册时间
            carBizUserActionExMapper.insertCarBizUserAction(userAction);
        }
        return 0;
    }
}
