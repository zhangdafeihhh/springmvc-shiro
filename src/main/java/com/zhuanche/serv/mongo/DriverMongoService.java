package com.zhuanche.serv.mongo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.mongo.driverwidemongo.DriverWideMongo;
import com.zhuanche.util.Common;
import com.zhuanche.util.Md5Util;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @ClassName: DriverMongoServiceImpl
 * @Description: 司机mongo服务
 */
@Service
public class DriverMongoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
	 * @param driverId
	 * @return
	 */
	public DriverMongo findByDriverId(Integer driverId) {
		DriverMongo driverMongo = new DriverMongo();
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
			driverMongo = JSONObject.toJavaObject(jsonObject, DriverMongo.class);
		}else{
			logger.error(QUERY_DRIVERID+" faild,code={}",code);
		}
		return null;
	}

	/**
	 * 新增司机mongo信息
	 *
	 * @param carBizDriverInfo
	 */
	public void saveDriverMongo(CarBizDriverInfoDTO carBizDriverInfo) {
		DriverMongo driverMongo = new DriverMongo();
		driverMongo.setGroupId(carBizDriverInfo.getGroupId());
		driverMongo.setGroupName(carBizDriverInfo.getCarGroupName());
		driverMongo.setModelId(carBizDriverInfo.getModelId());
		driverMongo.setModelName(carBizDriverInfo.getModelName());
		driverMongo.setPhotoSrc(carBizDriverInfo.getPhotosrct());
		driverMongo.setDriverId(carBizDriverInfo.getDriverId());
		driverMongo.setAge(carBizDriverInfo.getAge());
		driverMongo.setAccountBank(carBizDriverInfo.getAccountBank());
		driverMongo.setArchivesNo(carBizDriverInfo.getArchivesNo());
		driverMongo.setAttachmentAddr(carBizDriverInfo.getAttachmentAddr());
		driverMongo.setAttachmentName(carBizDriverInfo.getAttachmentName());
		driverMongo.setBankAccountNo(carBizDriverInfo.getBankAccountNo());
		driverMongo.setCreateBy(carBizDriverInfo.getCreateBy());
		driverMongo.setDrivingLicenseType(carBizDriverInfo.getDrivingLicenseType());
		driverMongo.setDrivingYears(carBizDriverInfo.getDrivingYears());
		driverMongo.setGender(carBizDriverInfo.getGender());
		driverMongo.setIdCardNo(carBizDriverInfo.getIdCardNo());
		driverMongo.setLicensePlates(carBizDriverInfo.getLicensePlates());
		driverMongo.setName(carBizDriverInfo.getName());
		driverMongo.setPassword(carBizDriverInfo.getPassword());
		driverMongo.setPhone(carBizDriverInfo.getPhone());
		driverMongo.setServiceCityName(carBizDriverInfo.getCityName());
		driverMongo.setServiceCityId(carBizDriverInfo.getServiceCity());
		driverMongo.setSupplierId(carBizDriverInfo.getSupplierId());
		driverMongo.setStatus(carBizDriverInfo.getStatus());
		driverMongo.setDutyStatus(0);
		driverMongo.setServiceStatus(1);
		driverMongo.setCreateDate(new Date());
		driverMongo.setSuperintendNo(carBizDriverInfo.getSuperintendNo());
		driverMongo.setSuperintendUrl(carBizDriverInfo.getSuperintendUrl());
		driverMongo.setCooperationType(carBizDriverInfo.getCooperationType());
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
	 * @param carBizDriverInfo
	 */
	public void updateDriverMongo(CarBizDriverInfoDTO carBizDriverInfo) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessId", Common.DRIVER_MONGO_HTTP_BUSINESSID);
		params.put("transId", Md5Util.md5(UUID.randomUUID().toString()));
		params.put("driverId",carBizDriverInfo.getDriverId());
		params.put("groupId",carBizDriverInfo.getGroupId());
		params.put("groupName",carBizDriverInfo.getCarGroupName());
		params.put("modelId",carBizDriverInfo.getModelId());
		params.put("modelName",carBizDriverInfo.getModelName());
		params.put("serviceCityId",carBizDriverInfo.getServiceCity());
		params.put("serviceCityName",carBizDriverInfo.getCityName());
		params.put("phone",carBizDriverInfo.getPhone());
		params.put("name",carBizDriverInfo.getName());
		params.put("licensePlates",carBizDriverInfo.getLicensePlates());
		params.put("status",carBizDriverInfo.getStatus());
		params.put("supplierId",carBizDriverInfo.getSupplierId());
		params.put("cooperationType",carBizDriverInfo.getCooperationType());
		params.put("isTwoShifts",carBizDriverInfo.getIsTwoShifts());
		params.put("photoSrc",carBizDriverInfo.getPhotosrct());
		logger.info("{},params:{}",UPDATE_MONGODRIVER,params);
		JSONObject jsonObject = MpOkHttpUtil.okHttpPostBackJson(driverMongoUrl + UPDATE_MONGODRIVER, params, 3000,"vehicleManage");
		logger.info("{},result:{}",UPDATE_MONGODRIVER,jsonObject);
		int code = jsonObject.getIntValue("code");
		if(code != 0){
			logger.error(UPDATE_MONGODRIVER+" failed,code={}",code);
		}
	}


	/**
	 * 将mongo中司机信息置为无效
	 * @param driverId
	 */
	public void disableDriverMongo(Integer driverId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", Common.DRIVER_MONGO_HTTP_BUSINESSID);
		params.put("transId", Md5Util.md5(UUID.randomUUID().toString()));
		params.put("driverId", driverId);
		logger.info("{},params:{}", UPDATE_MONGODRIVER_INVALID, params);
		JSONObject jsonObject = MpOkHttpUtil.okHttpPostBackJson(driverMongoUrl + UPDATE_MONGODRIVER_INVALID, params, 3000, "vehicleManage");
		logger.info("{},result:{}", UPDATE_MONGODRIVER_INVALID, jsonObject);
		int code = jsonObject.getIntValue("code");
		if(code != 0){
			logger.error(UPDATE_MONGODRIVER_INVALID+" failed,code={}",code);
		}
	}

	/**
	 * 更新司机信用卡信息  已废弃
	 * @param map
	 */
	public void updateDriverCardInfo (Map<String, Object> map) {
		// 更新mongoDB
//		Query query = new Query(Criteria.where("driverId").is(Integer.parseInt((String)map.get("driverId"))));
//		Update update = new Update();
//		update.set("updateBy", map.get("updateBy"));
//		update.set("creditOpenAccountBank", map.get("creditOpenAccountBank"));
//		update.set("shortCardNo", map.get("shortCardNo"));
//		update.set("isBindingCreditCard", map.get("isBindingCreditCard"));
//		update.set("creditCardNo", map.get("creditCardNo"));
//		update.set("CVN2", map.get("CVN2"));
//		update.set("phone", map.get("phone"));
//		update.set("bindTime", map.get("bindTime"));
//		update.set("expireDate", map.get("expireDate"));
//		driverMongoTemplate.updateFirst(query, update,DriverMongo.class);
	}

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	public void updateDriverCooperationTypeBySupplierId(Integer supplierId, Integer cooperationType) {
		Map<String,Object> map = Maps.newHashMap();
		Integer bigDriverId = 0;
		Integer pageSize = 500;
		c:for (int i = 0;true;i++){
			map.put("pageNo", ((i + 1) - 1) * pageSize);
			map.put("pageSize", pageSize);
			map.put("bigDriverId",bigDriverId);
			map.put("supplierId",supplierId);
			List<Integer> driverIds = carBizDriverInfoExMapper.findAllDriverIdsBySupplierId(map);
			if(driverIds != null && driverIds.size() == 0){
				logger.info("分页完成--页数[{}],每页条数[{}]",i,pageSize);
				break;
			}else{
				//获取每页最大的id进行同步
				bigDriverId = driverIds.get(driverIds.size() -1);
				List<DriverWideMongo> list = Lists.newArrayList();
				Iterator<Integer> iterable =  driverIds.iterator();
				StringBuilder stringBuilder = new StringBuilder();
				while (iterable.hasNext()){
					Integer driverId = iterable.next();
					stringBuilder.append(driverId).append(",");
				}
				String driverIdsStr = stringBuilder.toString();
				if(StringUtils.isNotEmpty(driverIdsStr)){
					driverIdsStr = driverIdsStr.substring(0,driverIds.lastIndexOf(","));
				}
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("businessId", Common.DRIVER_MONGO_HTTP_BUSINESSID);
				params.put("transId", Md5Util.md5(UUID.randomUUID().toString()));
				params.put("driverIds",driverIdsStr);
				params.put("cooperationType",cooperationType);
				logger.info("{},params:{}",UPDATE_COOPERATION,params);
				JSONObject jsonObject = MpOkHttpUtil.okHttpPostBackJson(driverMongoUrl + UPDATE_COOPERATION, params, 3000,"vehicleManage");
				logger.info("{},result:{}",UPDATE_COOPERATION,jsonObject);
				int code = jsonObject.getIntValue("code");
				if(code != 0){
					logger.error(UPDATE_MONGODRIVER+" failed,code={}",code);
				}
				list.clear();
			}
		}
	}
}
