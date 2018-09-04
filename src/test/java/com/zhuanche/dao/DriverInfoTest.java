//package com.zhuanche.dao;
//
//import com.zhuanche.mongo.DriverMongo;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//
//public class DriverInfoTest extends BaseFunctionalTestCase {
//
//
//    @Autowired
//    private MongoTemplate driverMongoTemplate;
//
//    @Test
//    public void test(){
//        Query query = new Query();
//        query.addCriteria(new Criteria().where("phone").is("15011571341"));
//        DriverMongo driverMongos = driverMongoTemplate.findOne(query, DriverMongo.class);
//        System.out.print("==============================================sadas===========================================================");
//    }
//
//}
