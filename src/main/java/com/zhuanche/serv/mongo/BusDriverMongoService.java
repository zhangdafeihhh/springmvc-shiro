package com.zhuanche.serv.mongo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private MongoTemplate driverMongoTemplate;

	/**
	 * 查询司机mongo
	 * 
	 * @param driverId
	 * @return
	 */
	public DriverMongo findByDriverId(Integer driverId) {
		Query query = new Query(Criteria.where("driverId").is(driverId));
		DriverMongo driverMongo = driverMongoTemplate.findOne(query, DriverMongo.class);
		return driverMongo;
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
		driverMongo.setDrivingLicenseType(saveDTO.getDrivingLicenseType());
		driverMongo.setDrivingYears(saveDTO.getDrivingYears());
		driverMongo.setGender(saveDTO.getGender());
		driverMongo.setIdCardNo(saveDTO.getIdCardNo());
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
		driverMongo.setCreateDate(new Date());
		driverMongo.setSuperintendNo(saveDTO.getSuperintendNo());
		driverMongo.setSuperintendUrl(saveDTO.getSuperintendUrl());
		driverMongo.setCooperationType(saveDTO.getCooperationType());
		driverMongo.setOutageStatus(2);
		// 插入司机信息到mongon
		driverMongoTemplate.insert(driverMongo);
	}

	/**
	 * 修改司机mongo信息
	 *
	 * @param saveDTO
	 */
	public void updateDriverMongo(BusDriverSaveDTO saveDTO) {
		// 更新mongoDB
		Query query = new Query(Criteria.where("driverId").is(saveDTO.getDriverId()));
		Update update = new Update();
		if (saveDTO.getModelName() != null) {
			update.set("modelName", saveDTO.getModelName());
		}
		if (saveDTO.getAge() != null) {
			update.set("age", saveDTO.getAge());
		}
		if (saveDTO.getArchivesNo() != null) {
			update.set("archivesNo", saveDTO.getArchivesNo());
		}
		update.set("superintendNo", saveDTO.getSuperintendNo());
		update.set("superintendUrl", saveDTO.getSuperintendUrl());
		if (saveDTO.getAttachmentAddr() != null) {
			update.set("attachmentAddr", saveDTO.getAttachmentAddr());
		}
		if (saveDTO.getAttachmentName() != null) {
			update.set("attachmentName", saveDTO.getAttachmentName());
		}
		if (saveDTO.getUpdateBy() != null) {
			update.set("updateBy", saveDTO.getUpdateBy());
		}
		if (saveDTO.getUpdateDate() != null) {
			update.set("updateDate", saveDTO.getUpdateDate());
		}
		if (saveDTO.getDrivingLicenseType() != null) {
			update.set("drivingLicenseType", saveDTO.getDrivingLicenseType());
		}
		if (saveDTO.getDrivingYears() != null) {
			update.set("drivingYears", saveDTO.getDrivingYears());
		}
		if (saveDTO.getExpireDate() != null) {
			update.set("expireDate", saveDTO.getExpireDate());
		}
		if (saveDTO.getGender() != null) {
			update.set("gender", saveDTO.getGender());
		}
		if (saveDTO.getIdCardNo() != null) {
			update.set("idCardNo", saveDTO.getIdCardNo());
		}
		if (saveDTO.getIssueDate() != null) {
			update.set("issueDate", saveDTO.getIssueDate());
		}
		if (saveDTO.getGroupId() != null && saveDTO.getGroupId() != 0) {
			update.set("groupId", saveDTO.getGroupId());
			update.set("groupName", saveDTO.getGroupName());
		}
		if (saveDTO.getLicensePlates() != null) {
			update.set("licensePlates", saveDTO.getLicensePlates());
			update.set("modelId", saveDTO.getModelId());
		}
		if (saveDTO.getName() != null) {
			update.set("name", saveDTO.getName());
		}
		if (saveDTO.getPhone() != null) {
			if (saveDTO.getStatus() != 0) {
				update.set("phone", saveDTO.getPhone());
			} else {
				update.set("phone", "");
			}
		}
		if (saveDTO.getStatus() != null) {
			update.set("status", saveDTO.getStatus());
		}
		if (saveDTO.getPhotosrct() != null) {
			update.set("photoSrc", saveDTO.getPhotosrct());
		}
		update.set("serviceCityId", saveDTO.getServiceCity());
		update.set("serviceCityName", saveDTO.getCityName());
		update.set("superintendNo", saveDTO.getSuperintendNo());
		update.set("supplierId", saveDTO.getSupplierId());
		update.set("superintendUrl", saveDTO.getSuperintendUrl());
		update.set("cooperationType", saveDTO.getCooperationType());
		driverMongoTemplate.updateFirst(query, update, DriverMongo.class);
	}

}
