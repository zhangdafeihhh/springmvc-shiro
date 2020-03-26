package com.zhuanche.serv.mongo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.le.config.dict.Dicts;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.zhuanche.dto.busManage.BusDriverSaveDTO;
import com.zhuanche.mongo.DriverMongo;

/**
 * @ClassName: BusDriverMongoService
 * @Description: 巴士司机mongo服务
 */
@Service
public class BusDriverMongoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Value("${driver.service.url}")
	private String driverMongoUrl;

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31810731
	private static final String QUERY_DRIVERID="/v1/driver/query/driverId";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31810733
	private static final String QUERY_PHONE="/v1/driver/query/phone";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31810725
	private static final String UNBIND_CAR="/v1/driver/remove/car";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31810729
	private static final String INSERT_MONGODRIVER="/v1/driver/driverInfo/insert";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31817237
	private static final String UPDATE_MONGODRIVER="/v1/driver/attribute/update";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31817237
	private static final String UPDATE_MONGODRIVER_BY_LICENSE="/v1/driver/attribute/update/license";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31816433
	private static final String UPDATE_MONGODRIVER_INVALID="/v1/driver/set/invalid";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31810734
	private static final String QUERY_LICENSEPLATES="/v1/driver/query/licensePlates";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=40170708
	private static final String UPDATE_GRABNOTICE="/v1/driver/update/grabNotice";

	//	http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=43156150
	private static final String UPDATE_COOPERATION="/v1/driver/update/cooperationType/supplierId";

	/**
	 * 查询司机mongo
	 * 
	 * @param driverId
	 * @return
	 */
	public DriverMongo findByDriverId(Integer driverId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessId", Common.DRIVER_MONGO_HTTP_BUSINESSID);
		params.put("transId", Md5Util.md5(UUID.randomUUID().toString()));
		params.put("driverId",driverId);
		logger.info("{},params:{}",QUERY_DRIVERID,params);
		JSONObject jsonObject = MpOkHttpUtil.okHttpGetBackJson(driverMongoUrl + QUERY_DRIVERID, params, 3000,"vehicleManage");
		logger.info("{},result:{}",QUERY_DRIVERID,jsonObject);
		int code = jsonObject.getIntValue("code");
		if(code == 0){
			jsonObject.getJSONObject("data");
			return JSONObject.toJavaObject(jsonObject, DriverMongo.class);
		}else{
			logger.error("{} fail,code:{}",QUERY_DRIVERID,code);
			return null;
		}
	}

	/**
	 * 新增司机mongo信息
	 *
	 * @param saveDTO
	 */
	public void saveDriverMongo(BusDriverSaveDTO saveDTO) {
		DriverMongo driverMongo = new DriverMongo();
		driverMongo.setGroupId(saveDTO.getGroupId());
		driverMongo.setGroupName(saveDTO.getGroupName());
		driverMongo.setModelId(saveDTO.getModelId());
		driverMongo.setModelName(saveDTO.getModelName());
		driverMongo.setPhotoSrc(saveDTO.getPhotosrct());
		driverMongo.setDriverId(saveDTO.getDriverId());
		driverMongo.setAge(saveDTO.getAge());
		driverMongo.setAccountBank(saveDTO.getAccountBank());
		driverMongo.setArchivesNo(saveDTO.getArchivesNo());
		driverMongo.setAttachmentAddr(saveDTO.getAttachmentAddr());
		driverMongo.setAttachmentName(saveDTO.getAttachmentName());
		driverMongo.setBankAccountNo(saveDTO.getBankAccountNo());
		driverMongo.setCreateBy(saveDTO.getCreateBy());
		driverMongo.setCreateDate(new Date());
		driverMongo.setDrivingLicenseType(saveDTO.getDrivingLicenseType());
		driverMongo.setDrivingYears(saveDTO.getDrivingYears());
		if (saveDTO.getExpireDate() != null) {
			String expireDate = formatter.format(LocalDateTime.ofInstant(saveDTO.getExpireDate().toInstant(), ZoneId.systemDefault()));
			driverMongo.setExpireDate(expireDate);
		}
		driverMongo.setGender(saveDTO.getGender());
		driverMongo.setIdCardNo(saveDTO.getIdCardNo());
		if (saveDTO.getIssueDate() != null) {
			String issueDate = formatter.format(LocalDateTime.ofInstant(saveDTO.getIssueDate().toInstant(), ZoneId.systemDefault()));
			driverMongo.setIssueDate(issueDate);
		}
		driverMongo.setLicensePlates(saveDTO.getLicensePlates());
		driverMongo.setName(saveDTO.getName());
		driverMongo.setPassword(saveDTO.getPassword());
		driverMongo.setPhone(saveDTO.getPhone());
		driverMongo.setServiceCityName(saveDTO.getCityName());
		driverMongo.setServiceCityId(saveDTO.getServiceCity());
		driverMongo.setSupplierId(saveDTO.getSupplierId());
		driverMongo.setStatus(saveDTO.getStatus());
		driverMongo.setDutyStatus(0);
		driverMongo.setServiceStatus(1);
		driverMongo.setSuperintendNo(saveDTO.getSuperintendNo());
		driverMongo.setSuperintendUrl(saveDTO.getSuperintendUrl());
		driverMongo.setCooperationType(saveDTO.getCooperationType());
		driverMongo.setOutageStatus(2);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessId", Common.DRIVER_MONGO_HTTP_BUSINESSID);
		params.put("transId", Md5Util.md5(UUID.randomUUID().toString()));
		params.put("phone",driverMongo.getPhone());
		params.put("supplierId",driverMongo.getSupplierId());
		params.put("status",driverMongo.getStatus());
		params.put("serviceCityName",driverMongo.getServiceCityName());
		params.put("serviceCityId",driverMongo.getServiceCityId());
		params.put("name",driverMongo.getName());
		params.put("modelName",driverMongo.getModelName());
		params.put("modelId",driverMongo.getModelId());
		params.put("licensePlates",driverMongo.getLicensePlates());
		params.put("isTwoShifts",driverMongo.getIsTwoShifts());
		params.put("groupName",driverMongo.getGroupName());
		params.put("groupId",driverMongo.getGroupId());
		params.put("driverId",driverMongo.getDriverId());
		params.put("cooperationType",driverMongo.getCooperationType());
		params.put("photoSrc",driverMongo.getPhotoSrc());
		params.put("serviceStatus",driverMongo.getServiceStatus());
		params.put("dutyStatus",driverMongo.getDutyStatus());
		params.put("outageStatus",driverMongo.getOutageStatus());
		logger.info("{},params:{}",INSERT_MONGODRIVER,params);
		JSONObject jsonObject = MpOkHttpUtil.okHttpPostBackJson(driverMongoUrl + INSERT_MONGODRIVER, params, 3000,"vehicleManage");
		logger.info("{},result:{}",INSERT_MONGODRIVER,jsonObject);
		int code = jsonObject.getIntValue("code");
		if(code != 0 ){
			logger.error("{} fail,code:{}",INSERT_MONGODRIVER,code);
		}
	}

	/**
	 * 修改司机mongo信息
	 *
	 * @param saveDTO
	 */
	public void updateDriverMongo(BusDriverSaveDTO saveDTO) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessId", Common.DRIVER_MONGO_HTTP_BUSINESSID);
		params.put("transId", Md5Util.md5(UUID.randomUUID().toString()));
		params.put("driverId",saveDTO.getDriverId());
		params.put("groupId",saveDTO.getGroupId());
		params.put("groupName",saveDTO.getGroupName());
		params.put("modelId",saveDTO.getModelId());
		params.put("modelName",saveDTO.getModelName());
		params.put("serviceCityId",saveDTO.getServiceCity());
		params.put("serviceCityName",saveDTO.getCityName());
		params.put("phone",saveDTO.getPhone());
		params.put("name",saveDTO.getName());
		params.put("licensePlates",saveDTO.getLicensePlates());
		params.put("status",saveDTO.getStatus());
		params.put("supplierId",saveDTO.getSupplierId());
		params.put("cooperationType",saveDTO.getCooperationType());
		params.put("isTwoShifts",saveDTO.getIsTwoShifts());
		params.put("photoSrc",saveDTO.getPhotosrct());
		logger.info("{},params:{}",UPDATE_MONGODRIVER,params);
		JSONObject jsonObject = MpOkHttpUtil.okHttpPostBackJson(driverMongoUrl + UPDATE_MONGODRIVER, params, 3000,"vehicleManage");
		logger.info("{},result:{}",UPDATE_MONGODRIVER,jsonObject);
		int code = jsonObject.getIntValue("code");
		if(code != 0 ){
			logger.error("{} fail,code:{}",UPDATE_MONGODRIVER,code);
		}
	}

	/**
	 * @Title: queryDriverByName
	 * @Description: 根据司机姓名查询司机信息
	 * @param name
	 * @return 
	 * @return List<DriverMongo>
	 * @throws
	 */
//	public List<DriverMongo> queryDriverByName(String name) {
//		Query query = new Query(Criteria.where("name").is(name));
//		List<DriverMongo> driverMongos = driverMongoTemplate.find(query, DriverMongo.class);
//		return driverMongos;
//	}
	
	/**
	 * @Title: queryDriverByPhone
	 * @Description: 根据司机手机号查询司机信息
	 * @param phone
	 * @return 
	 * @return List<DriverMongo>
	 * @throws
	 */
//	public List<DriverMongo> queryDriverByPhone(String phone) {
//		Query query = new Query(Criteria.where("phone").is(phone));
//		List<DriverMongo> driverMongos = driverMongoTemplate.find(query, DriverMongo.class);
//		return driverMongos;
//	}

}
