package com.zhuanche.serv.mongo;

import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.mongo.DriverMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DriverMongoServiceImpl
 * @Description: 司机mongo服务
 */
@Service
public class DriverMongoService {

    @Resource(name = "driverMongoTemplate")
    private MongoTemplate driverMongoTemplate;

	/**
	 * 查询司机mongo
	 * @param driverId
	 * @return
	 */
	public DriverMongo findByDriverId(Integer driverId) {
		Query query = new Query(Criteria.where("driverId").is(driverId));
		DriverMongo  driverMongo = driverMongoTemplate.findOne(query, DriverMongo.class);
		return driverMongo;
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
		// 插入司机信息到mongon
		driverMongoTemplate.insert(driverMongo);
	}

	/**
	 * 修改司机mongo信息
	 *
	 * @param carBizDriverInfo
	 */
	public void updateDriverMongo(CarBizDriverInfoDTO carBizDriverInfo) {
		// 更新mongoDB
		Query query = new Query(Criteria.where("driverId").is(carBizDriverInfo.getDriverId()));
		Update update = new Update();
		if (carBizDriverInfo.getModelName() != null) {
			update.set("modelName", carBizDriverInfo.getModelName());
		}
		if (carBizDriverInfo.getAge() != null) {
			update.set("age", carBizDriverInfo.getAge());
		}
		if (carBizDriverInfo.getArchivesNo() != null) {
			update.set("archivesNo", carBizDriverInfo.getArchivesNo());
		}
		update.set("superintendNo", carBizDriverInfo.getSuperintendNo());
		update.set("superintendUrl", carBizDriverInfo.getSuperintendUrl());
		if (carBizDriverInfo.getAttachmentAddr() != null) {
			update.set("attachmentAddr", carBizDriverInfo.getAttachmentAddr());
		}
		if (carBizDriverInfo.getAttachmentName() != null) {
			update.set("attachmentName", carBizDriverInfo.getAttachmentName());
		}
		if (carBizDriverInfo.getUpdateBy() != null) {
			update.set("updateBy", carBizDriverInfo.getUpdateBy());
		}
		if (carBizDriverInfo.getUpdateDate() != null) {
			update.set("updateDate", carBizDriverInfo.getUpdateDate());
		}
		if (carBizDriverInfo.getDrivingLicenseType() != null) {
			update.set("drivingLicenseType", carBizDriverInfo.getDrivingLicenseType());
		}
		if (carBizDriverInfo.getDrivingYears() != null) {
			update.set("drivingYears", carBizDriverInfo.getDrivingYears());
		}
		if (carBizDriverInfo.getExpireDate() != null) {
			update.set("expireDate", carBizDriverInfo.getExpireDate());
		}
		if (carBizDriverInfo.getGender() != null) {
			update.set("gender", carBizDriverInfo.getGender());
		}
		if (carBizDriverInfo.getIdCardNo() != null) {
			update.set("idCardNo", carBizDriverInfo.getIdCardNo());
		}
		if (carBizDriverInfo.getIssueDate() != null) {
			update.set("issueDate", carBizDriverInfo.getIssueDate());
		}
		if(carBizDriverInfo.getGroupId()!=null&&!"".equals(carBizDriverInfo.getGroupId())&&carBizDriverInfo.getGroupId()!=0){
			update.set("groupId", carBizDriverInfo.getGroupId());
			update.set("groupName", carBizDriverInfo.getCarGroupName());
		}
		if (carBizDriverInfo.getLicensePlates() != null) {
			update.set("licensePlates", carBizDriverInfo.getLicensePlates());
			update.set("modelId", carBizDriverInfo.getModelId());
		}
		if (carBizDriverInfo.getName() != null) {
			update.set("name", carBizDriverInfo.getName());
		}
		if (carBizDriverInfo.getPhone() != null) {
			if(carBizDriverInfo.getStatus() != 0){
				update.set("phone", carBizDriverInfo.getPhone());
			}else{
				update.set("phone", "");
			}
		}
		if (carBizDriverInfo.getStatus() != null) {
			update.set("status", carBizDriverInfo.getStatus());
		}
		if (carBizDriverInfo.getPhotosrct() != null) {
			update.set("photoSrc", carBizDriverInfo.getPhotosrct());
		}
		update.set("serviceCityId", carBizDriverInfo.getServiceCity());
		update.set("serviceCityName", carBizDriverInfo.getCityName());
		update.set("superintendNo", carBizDriverInfo.getSuperintendNo());
		update.set("supplierId", carBizDriverInfo.getSupplierId());
		update.set("superintendUrl", carBizDriverInfo.getSuperintendUrl());
		update.set("cooperationType", carBizDriverInfo.getCooperationType());
		driverMongoTemplate.updateFirst(query, update, DriverMongo.class);
	}


	/**
	 * 更新司机
	 * @param driverId
	 * @param status
	 */
	public void updateByDriverId(Integer driverId, Integer status) {
		Query query = new Query(Criteria.where("driverId").is(driverId));
		Update update = new Update();
		update.set("status", status);
		if(status.intValue()==0){
			update.set("licensePlates", null);
			update.set("phone", null);
			update.set("groupId", null);
			update.set("groupName", null);
			update.set("modelId", null);
			update.set("modelName", null);
		}
		driverMongoTemplate.updateFirst(query, update, DriverMongo.class);
	}

	/**
	 * 更新司机信用卡信息
	 * @param map
	 */
	public void updateDriverCardInfo (Map<String, Object> map) {
		// 更新mongoDB
		Query query = new Query(Criteria.where("driverId").is(Integer.parseInt((String)map.get("driverId"))));
		Update update = new Update();
		update.set("updateBy", map.get("updateBy"));
		update.set("creditOpenAccountBank", map.get("creditOpenAccountBank"));
		update.set("shortCardNo", map.get("shortCardNo"));
		update.set("isBindingCreditCard", map.get("isBindingCreditCard"));
		update.set("creditCardNo", map.get("creditCardNo"));
		update.set("CVN2", map.get("CVN2"));
		update.set("phone", map.get("phone"));
		update.set("bindTime", map.get("bindTime"));
		update.set("expireDate", map.get("expireDate"));
		driverMongoTemplate.updateFirst(query, update,DriverMongo.class);
	}

    /**
     * @param supplierId
     * @param cooperationType
     * @return void
     * @throws
     * @Title: updateDriverCooperationTypeBySupplierId
     * @Description: 更新司机的加盟类型
     */
    public void updateDriverCooperationTypeBySupplierId(Integer supplierId, Integer cooperationType) {
        // 更新mongoDB
        Query query = new Query(Criteria.where("supplierId").is(supplierId));
        Update update = new Update();
        update.set("cooperationType", cooperationType);
        driverMongoTemplate.updateFirst(query, update, DriverMongo.class);
    }

    /**
     * 根据司机姓名查询司机信息
     *
     * @param name
     * @return
     */
    public List<DriverMongo> queryDriverByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        List<DriverMongo> driverMongos = driverMongoTemplate.find(query, DriverMongo.class);
        return driverMongos;
    }
}
