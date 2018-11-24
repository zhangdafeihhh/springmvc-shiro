package com.zhuanche.serv.message;

import com.google.common.collect.Lists;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.mdbcarmanage.CarMessagePost;
import com.zhuanche.entity.mdbcarmanage.CarMessageReceiver;
import com.zhuanche.exception.MessageException;
import mapper.mdbcarmanage.CarMessageReceiverMapper;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.CarMessageReceiverExMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @Author fanht
 * @Description
 * @Date 2018/11/22 下午8:12
 * @Version 1.0
 */
@Service
public class MessageReceiveService {


    @Autowired
    private CarMessageReceiverExMapper receiverMapper;

    @Autowired
    private CarAdmUserExMapper exMapper;

    public int sendMessage(Integer messageId,Integer level,String cities,
                           String suppliers,String teamId) throws MessageException{


        switch (level){
            case Constants.CONTRY:
                break;
            case Constants.CITY:
                this.insertByLevel(Constants.CITY,messageId,cities);
                break;
            case Constants.SUPPY:
                this.insertByLevel(Constants.SUPPY,messageId,suppliers);
                break;
            case Constants.CITYANDSUPPY:
                this.insertByLevel(Constants.CITY,messageId,cities);
                this.insertByLevel(Constants.SUPPY,messageId,suppliers);
                break;
            case Constants.TEAM:
                this.insertByLevel(Constants.TEAM,messageId,teamId);
                break;
            case Constants.CITYANDTEAM:
                this.insertByLevel(Constants.CITY,messageId,cities);
                this.insertByLevel(Constants.TEAM,messageId,teamId);
                break;
            case Constants.SUPPYANDTEAM:
                this.insertByLevel(Constants.SUPPY,messageId,suppliers);
                this.insertByLevel(Constants.TEAM,messageId,teamId);
                break;
            case Constants.CITYANDSUPPYANDTEAM:
                this.insertByLevel(Constants.CITY,messageId,cities);
                this.insertByLevel(Constants.SUPPY,messageId,suppliers);
                this.insertByLevel(Constants.TEAM,messageId,teamId);
                break;
            default:
                ;



        }


        return 0;
    }


    private int insertByLevel(Integer level,Integer messageId,String cities){
        Set<CarAdmUser> sendList = new HashSet<>();
        //查询出所有符合等级的用户
        List<CarAdmUser> carAdmUserList = exMapper.selectUsersByLevel(level);
        //如果选择的是城市用户 则查询该用户选择的城市 有没有 城市等级下选择的城市
        String[] sendCity = cities.split(Constants.SEPERATER);
        List<String> list = Arrays.asList(sendCity);
        for(CarAdmUser admUser : carAdmUserList){
            String levelName = "";
            switch (level){
                case Constants.CITY:
                    levelName = admUser.getCities();
                    break;
                case Constants.SUPPY:
                    levelName = admUser.getSuppliers();
                    break;
                case Constants.TEAM:
                    levelName = admUser.getTeamId();
                    break;
                default:
                    levelName = admUser.getCities();
            }
            HashSet<String> hashSet = new HashSet<>(list);
            if (StringUtils.isNotBlank(levelName)){
                String[] city = levelName.split(Constants.SEPERATER);
                List<String>  userHasCity= Arrays.asList(city);
                Iterator it = userHasCity.iterator();
                while (it.hasNext()){
                    if (hashSet.contains(it.next())){
                        sendList.add(admUser);
                        break;
                    }
                }
            }

        }

        for (CarAdmUser admUser : sendList){
            CarMessageReceiver receiver = new CarMessageReceiver();
            receiver.setCreateTime(new Date());
            receiver.setMessageId(messageId);
            receiver.setStatus(1);
            receiver.setReceiveUserId(admUser.getUserId());
            receiver.setUpdateTime(new Date());
            receiverMapper.insert(receiver);
        }

        return 0;
    }

}
