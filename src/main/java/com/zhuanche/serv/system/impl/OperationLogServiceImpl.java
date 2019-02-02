package com.zhuanche.serv.system.impl;

import com.mongodb.BasicDBObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.mongo.CustomGroupOperation;
import com.zhuanche.mongo.UserDailyOperationDTO;
import com.zhuanche.mongo.UserOperationDTO;
import com.zhuanche.serv.system.OperationLogService;
import com.zhuanche.util.DateUtil;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogServiceImpl.class);

    @Resource(name = "userOperationLogMongoTemplate")
    private MongoTemplate template;

    @Autowired
    private CarAdmUserExMapper userExMapper;



    @Override
    public AjaxResponse getUserDailyOperationCount(Date startDate, Date endDate) {
        MatchOperation matchOperation = new MatchOperation(Criteria.where("userId").gt(0).andOperator(Criteria.where("requestTime").gt(startDate).lt(endDate)));
        SortOperation sortOperation = new SortOperation(new Sort(Sort.Direction.ASC, "_id"));
        AggregationOperation groupOperation = new CustomGroupOperation(
                new BasicDBObject("$group",
                    new BasicDBObject("_id",
                            new BasicDBObject("day", new BasicDBObject("$dayOfMonth", "$requestTime"))
                            .append("month", new BasicDBObject("$month","$requestTime"))
                            .append("year", new BasicDBObject("$year","$requestTime"))
                    )
                    .append("users", new BasicDBObject("$addToSet", "$userId"))
                    .append("count", new BasicDBObject("$sum", 1))
                ));
        Aggregation aggregation = getAggregation(
                matchOperation,
                groupOperation,
                sortOperation
                );
        AggregationResults<BasicDBObject> userOperationLog = template.aggregate(aggregation, "userOperationLog", BasicDBObject.class);
        List<BasicDBObject> mappedResults = userOperationLog.getMappedResults();
        List<UserDailyOperationDTO> results = new ArrayList<>();
        mappedResults.forEach(basicDBObject -> {
            UserDailyOperationDTO dailyOperationDTO = new UserDailyOperationDTO();
            dailyOperationDTO.setCount(basicDBObject.getInt("count"));
            dailyOperationDTO.setDate(basicDBObject.getInt("year") + "-" + basicDBObject.getInt("month") + "-" + basicDBObject.getInt("day"));
            Object users = basicDBObject.get("users");
            if (users instanceof List){
                dailyOperationDTO.setUserCount(((List) users).size());
                dailyOperationDTO.setAvgCount(dailyOperationDTO.getCount() / dailyOperationDTO.getUserCount());
            }
            results.add(dailyOperationDTO);
        });
        return AjaxResponse.success(results);
    }

    @Override
    public AjaxResponse getMenuOperationCount(Date startDate, Date endDate) {
        MatchOperation matchOperation = new MatchOperation(Criteria.where("userId").gt(0).andOperator(Criteria.where("requestTime").gt(startDate).lt(endDate)));
        SortOperation sortOperation = new SortOperation(new Sort(Sort.Direction.DESC, "count"));
        AggregationOperation groupOperation = new CustomGroupOperation(
                new BasicDBObject("$group",
                        new BasicDBObject("_id",
                                new BasicDBObject("day", new BasicDBObject("$dayOfMonth", "$requestTime"))
                                        .append("month", new BasicDBObject("$month","$requestTime"))
                                        .append("year", new BasicDBObject("$year","$requestTime"))
                        )
                                .append("users", new BasicDBObject("$addToSet", "$userId"))
                                .append("count", new BasicDBObject("$sum", 1))
                ));
        return null;
    }

    @Override
    public AjaxResponse getUserOperationCount(Date startDate, Date endDate) {
        Aggregation aggregation = getAggregation(
                new MatchOperation(Criteria.where("userId").gt(0).andOperator(Criteria.where("requestTime").gt(startDate).lt(endDate))),
                new GroupOperation(Fields.fields("userId")).count().as("count"),
                new SortOperation(new Sort(Sort.Direction.DESC, "count")),
                new LimitOperation(20));
        AggregationResults<UserOperationDTO> userOperationLog = template.aggregate(aggregation, "userOperationLog", UserOperationDTO.class);
        List<UserOperationDTO> mappedResults = userOperationLog.getMappedResults();
        int day;
        try{
            day = DateUtil.daysBetween(startDate, endDate);
            mappedResults.forEach(userOperationDTO -> {
                userOperationDTO.setDailyCount(userOperationDTO.getCount() / day);
                userOperationDTO.setUserName(userExMapper.queryNameById(userOperationDTO.getId()));
            });
            return AjaxResponse.success(mappedResults);
        }catch (Exception e){
            logger.info("parse Date error");
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    private Aggregation getAggregation(AggregationOperation ... aggregationOperations){
        List<AggregationOperation> operationList = Arrays.asList(aggregationOperations);
        return Aggregation.newAggregation(operationList);
    }
}
