package com.zhuanche.serv.driverwide;

import com.mongodb.WriteResult;
import com.zhuanche.mongo.driverwidemongo.DriverWideMongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @author (yangbo)
 * @Date: 2019/6/25 14:35
 * @Description:(司机宽表mongodb服务)
 */
@Service
public class DriverWideMongoService {

    private static final Logger logger = LoggerFactory.getLogger(DriverWideMongoService.class);


    @Autowired
    @Qualifier("driverWideMongoTemplate")
    private MongoTemplate driverWideMongoTemplate;

    /***
     * 根据车队id批量修改车队的名称
     * @param teamId        车队id
     * @param newTeamName   新的车队名称
     * @param oldTeamName   旧的车队名称
     */
    public void updateTeamNameByTeamId(Integer teamId, String newTeamName, String oldTeamName) {
        try {
            Query query = new Query().addCriteria(Criteria.where("teamId").is(teamId));
            Update update = new Update().set("info.$.teamName", newTeamName);
            WriteResult writeResult = driverWideMongoTemplate.updateMulti(query, update, DriverWideMongo.class);
            logger.info("根据车队id修改车队名称,teamId:{},newTeamName：{},oldTeamName:{},条数:{}", teamId, newTeamName, oldTeamName, writeResult.getN());
        } catch (Exception e) {
            logger.error("根据车队id修改车队名称异常teamId:{},newTeamName：{},oldTeamName:{},error:{}", teamId, newTeamName, oldTeamName, e);
        }
    }

    /***
     * 根据小组id修改小组名称
     * @param teamGroupId       小组id
     * @param newTeamGroupName  新的小组名称
     * @param oldTeamGroupName  旧的小组名称
     */
    public void updateTeamGroupNameByTeamGroupId(Integer teamGroupId, String newTeamGroupName, String oldTeamGroupName) {
        try {
            Query query = new Query().addCriteria(Criteria.where("teamGroupId").is(teamGroupId));
            Update update = new Update().set("info.$.teamGroupName", newTeamGroupName);
            WriteResult writeResult = driverWideMongoTemplate.updateMulti(query, update, DriverWideMongo.class);
            logger.info("根据车队id修改小组名称,teamGroupId:{},newTeamGroupName：{},oldTeamGroupName:{},条数:{}", teamGroupId, newTeamGroupName, oldTeamGroupName, writeResult.getN());
        } catch (Exception e) {
            logger.info("根据车队id修改小组名称异常teamGroupId:{},newTeamGroupName：{},oldTeamGroupName:{},error:{}", teamGroupId, newTeamGroupName, oldTeamGroupName, e);
        }
    }
}
