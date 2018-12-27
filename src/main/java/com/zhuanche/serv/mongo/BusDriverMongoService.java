package com.zhuanche.serv.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.zhuanche.mongo.DriverMongo;

/**
 * @ClassName: BusDriverMongoService
 * @Description: 巴士司机mongo服务
 */
@Service
public class BusDriverMongoService {

	@Autowired
	private MongoTemplate driverMongoTemplate;

	/**
	 * @Title: queryDriverByName
	 * @Description: 根据司机姓名查询司机信息
	 * @param name
	 * @return 
	 * @return List<DriverMongo>
	 * @throws
	 */
	public List<DriverMongo> queryDriverByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		List<DriverMongo> driverMongos = driverMongoTemplate.find(query, DriverMongo.class);
		return driverMongos;
	}
	
	/**
	 * @Title: queryDriverByPhone
	 * @Description: 根据司机手机号查询司机信息
	 * @param phone
	 * @return 
	 * @return List<DriverMongo>
	 * @throws
	 */
	public List<DriverMongo> queryDriverByPhone(String phone) {
		Query query = new Query(Criteria.where("phone").is(phone));
		List<DriverMongo> driverMongos = driverMongoTemplate.find(query, DriverMongo.class);
		return driverMongos;
	}

}
