//package com.zhuanche.dao;
//
//import com.zhuanche.mongo.DriverMongo;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
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
//    @Test
//    public void streamTest(){
//        List<String> sourceList = new ArrayList<>();
//        fillData(sourceList);
//        StringBuilder sb = new StringBuilder();
//        final int limit = sourceList.size();
//        double a = sourceList.size();
//        double v = a / 100;
//        System.err.println(Math.ceil(v));
//        /*for(int i=0;i<=1000;i+=100){
//            sourceList.stream().skip(i).limit(100).forEach(a->{
//                System.err.print(a);
//                System.err.print(",");
//
//            });
//            System.err.println("");
//        }*/
//
//    }
//    private List<String> fillData(List<String> sourceList){
//        for(int i=1;i<=99;i++){
//            sourceList.add(String.valueOf(i));
//        }
//        return sourceList;
//    }
//
//
//}
