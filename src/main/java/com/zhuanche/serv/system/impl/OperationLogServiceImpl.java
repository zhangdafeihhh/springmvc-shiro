package com.zhuanche.serv.system.impl;

import com.mongodb.BasicDBObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.mongo.CustomGroupOperation;
import com.zhuanche.mongo.MenuOperationDTO;
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
import java.util.function.Consumer;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogServiceImpl.class);

    @Resource(name = "userOperationLogMongoTemplate")
    private MongoTemplate template;

    @Autowired
    private CarAdmUserExMapper userExMapper;

    @Override
    public AjaxResponse getUserDailyOperationCount(Date startDate, Date endDate, int top) {
        MatchOperation matchOperation = new MatchOperation(Criteria.where(USER_ID).gt(0)
                .andOperator(Criteria.where(REQUEST_TIME).gt(startDate).lt(endDate)));
        SortOperation sortOperation = new SortOperation(new Sort(Sort.Direction.ASC, ID));
        AggregationOperation groupOperation = new CustomGroupOperation(
                new BasicDBObject(GROUP,
                        new BasicDBObject(ID,
                                new BasicDBObject(DAY, new BasicDBObject("$dayOfMonth", DOLLAR + REQUEST_TIME))
                                        .append(MONTH, new BasicDBObject("$month", DOLLAR + REQUEST_TIME))
                                        .append(YEAR, new BasicDBObject("$year", DOLLAR + REQUEST_TIME))
                        )
                        .append(USERS, new BasicDBObject("$addToSet", DOLLAR + USER_ID))
                        .append(COUNT, new BasicDBObject(SUM, 1))
                ));
        LimitOperation limitOperation = new LimitOperation(top);
        List<UserDailyOperationDTO> results = new ArrayList<>();
        try {
            getResult(getAggregation(matchOperation, groupOperation, sortOperation, limitOperation),
                      basicDBObject -> {
                        UserDailyOperationDTO dailyOperationDTO = new UserDailyOperationDTO();
                        dailyOperationDTO.setCount(basicDBObject.getInt(COUNT));
                        dailyOperationDTO.setDate(basicDBObject.getInt(YEAR) + HYPHEN + basicDBObject.getInt(MONTH) + HYPHEN + basicDBObject.getInt(DAY));
                        Object users = basicDBObject.get(USERS);
                        if (users instanceof List) {
                            dailyOperationDTO.setUserCount(((List) users).size());
                            dailyOperationDTO.setAvgCount(dailyOperationDTO.getCount() / dailyOperationDTO.getUserCount());
                        }
                        results.add(dailyOperationDTO);
                    });
            return AjaxResponse.success(results);
        }catch (Exception e){
            logger.error("查询每日用户使用统计错误", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @Override
    public AjaxResponse getMenuOperationCount(Date startDate, Date endDate, int top) {
        MatchOperation matchOperation = new MatchOperation(
                Criteria.where(USER_ID).gt(0).
                        andOperator(Criteria.where(REQUEST_TIME).gt(startDate).lt(endDate).andOperator(Criteria.where("requestFuncName4").exists(true))));
        SortOperation sortOperation = new SortOperation(new Sort(Sort.Direction.DESC, COUNT));
        AggregationOperation groupOperation = new CustomGroupOperation(
                new BasicDBObject(GROUP, new BasicDBObject(ID, "$requestFuncName4")
                        .append(USERS, new BasicDBObject("$addToSet", DOLLAR + USER_ID))
                        .append(COUNT, new BasicDBObject(SUM, 1))
                ));
        LimitOperation limitOperation = new LimitOperation(top);
        List<MenuOperationDTO> result = new ArrayList<>();
        try {
            getResult(getAggregation(
                    matchOperation,
                    groupOperation,
                    sortOperation,
                    limitOperation
            ), (basicDBObject) -> {
                MenuOperationDTO menuOperationDTO = new MenuOperationDTO();
                menuOperationDTO.setCount(basicDBObject.getInt(COUNT));
                menuOperationDTO.setMenuName(basicDBObject.getString(ID));
                Object users = basicDBObject.get(USERS);
                if (users instanceof List) {
                    menuOperationDTO.setUserCount(((List) users).size());
                }
                result.add(menuOperationDTO);
            });
            return AjaxResponse.success(result);
        }catch (Exception e){
            logger.error("查询功能使用排名统计错误", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @Override
    public AjaxResponse getUserOperationCount(Date startDate, Date endDate, int top) {
        Aggregation aggregation = getAggregation(
                new MatchOperation(Criteria.where(USER_ID).gt(0).andOperator(Criteria.where(REQUEST_TIME).gt(startDate).lt(endDate))),
                new GroupOperation(Fields.fields(USER_ID)).count().as(COUNT),
                new SortOperation(new Sort(Sort.Direction.DESC, COUNT)),
                new LimitOperation(top));
        AggregationResults<UserOperationDTO> userOperationLog = template.aggregate(aggregation, USER_OPERATION_COLLECTION, UserOperationDTO.class);
        List<UserOperationDTO> mappedResults = userOperationLog.getMappedResults();
        int day;
        try {
            day = DateUtil.getDays(startDate, endDate);
            mappedResults.forEach(userOperationDTO -> {
                userOperationDTO.setDailyCount(userOperationDTO.getCount() / day);
                userOperationDTO.setUserName(userExMapper.queryNameById(userOperationDTO.getId()));
            });
            return AjaxResponse.success(mappedResults);
        } catch (Exception e) {
            logger.error("查询用户操作次数统计错误", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    private Aggregation getAggregation(AggregationOperation... aggregationOperations) {
        List<AggregationOperation> operationList = Arrays.asList(aggregationOperations);
        return Aggregation.newAggregation(operationList);
    }

    private void getResult(Aggregation aggregation, Consumer<BasicDBObject> function) {
        AggregationResults<BasicDBObject> operationLog = template.aggregate(aggregation, USER_OPERATION_COLLECTION, BasicDBObject.class);
        operationLog.getMappedResults().forEach(function);
    }
    
    private static final String GROUP = "$group";
    private static final String USER_OPERATION_COLLECTION = "userOperationLog";
    private static final String ID = "_id";
    private static final String COUNT = "count";
    private static final String USER_ID = "userId";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HYPHEN = "-";
    private static final String USERS = "users";
    private static final String REQUEST_TIME = "requestTime";
    private static final String DOLLAR = "$";
    private static final String SUM = "$sum";
}
