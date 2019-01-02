package com.zhuanche.serv;

import com.google.common.collect.Sets;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.driver.SupplierExtDto;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.CarBizSupplierQuery;
import com.zhuanche.entity.rentcar.CarBizSupplierVo;
import com.zhuanche.serv.deiver.CarBizCarInfoTempService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.driver.SupplierExtDtoMapper;
import mapper.driver.ex.SupplierExtDtoExMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**供应商信息 的 基本服务层**/
@Service
public class CarBizSupplierService{

	private static final Logger logger = LoggerFactory.getLogger(CarBizSupplierService.class);

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

	@Autowired
	private SupplierExtDtoMapper supplierExtDtoMapper;

	@Autowired
	private SupplierExtDtoExMapper supplierExtDtoExMapper;

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private CarBizCarInfoTempService carBizCarInfoTempService;

	/**查询供应商信息**/
	public Map<Integer, CarBizSupplier> querySupplier( Integer cityId,  Set<Integer> supplierids ){
        Set<Integer> cityIds = Sets.newHashSet();
        cityIds.add(cityId);
		List<CarBizSupplier> list = carBizSupplierExMapper.querySuppliers(cityIds, supplierids);
		if(list==null||list.size()==0) {
			return new HashMap<Integer, CarBizSupplier>(4);
		}
		Map<Integer, CarBizSupplier> result = new HashMap<Integer, CarBizSupplier>();
		for(CarBizSupplier c : list) {
			result.put(c.getSupplierId(),  c);
		}
		return result;
	}

    public CarBizSupplier queryForObject(CarBizSupplier carBizSupplier){
	    return carBizSupplierExMapper.queryForObject(carBizSupplier);
    }

	/**
	 * 根据供应商ID查询供应商信息
	 * @param supplierId
	 * @return
	 */
	public CarBizSupplier selectByPrimaryKey(Integer supplierId){
		return carBizSupplierMapper.selectByPrimaryKey(supplierId);
	}

    public List<CarBizSupplier> findByIdSet(Set<Integer> supplierIdSet) {
		return carBizSupplierExMapper.findByIdSet(supplierIdSet);
    }

	public List<CarBizSupplierVo> findSupplierListByPage(CarBizSupplierQuery queryParam) {
		return carBizSupplierExMapper.findByParams(queryParam);
	}

    public AjaxResponse saveSupplierInfo(CarBizSupplierVo supplier) {
		try{
			String method = Constants.UPDATE;
			Integer id = WebSessionUtil.getCurrentLoginUser().getId();
			supplier.setUpdateBy(id);
			supplier.setUpdateDate(new Date());
			if (supplier.getSupplierId() == null || supplier.getSupplierId() == 0){
				method = Constants.CREATE;
				supplier.setCreateBy(id);
				supplier.setCreateDate(new Date());
				carBizSupplierMapper.insertSelective(supplier);
				SupplierExtDto extDto = new SupplierExtDto();
				extDto.setEmail(supplier.getEmail());
				extDto.setSupplierShortName(supplier.getSupplierShortName());
				extDto.setSupplierId(supplier.getSupplierId());
				extDto.setCreateDate(new Date());
				extDto.setUpdateDate(new Date());
				supplierExtDtoMapper.insertSelective(extDto);
			}else {
				carBizSupplierMapper.updateByPrimaryKeySelective(supplier);
				SupplierExtDto extDto = new SupplierExtDto();
				extDto.setEmail(supplier.getEmail());
				extDto.setSupplierShortName(supplier.getSupplierShortName());
				extDto.setSupplierId(supplier.getSupplierId());
				extDto.setStatus(supplier.getStatus().byteValue());
				extDto.setUpdateDate(new Date());
				SupplierExtDto supplierExtDto = supplierExtDtoExMapper.selectBySupplierId(supplier.getSupplierId());
				if (supplierExtDto == null){
					extDto.setCreateDate(new Date());
					supplierExtDtoMapper.insertSelective(extDto);
				}else {
					supplierExtDtoExMapper.updateBySupplierId(extDto);
				}
			}
			try{
				Map<String, Object> messageMap = new HashMap<String, Object>();
				messageMap.put("method",method);
				JSONObject json = JSONObject.fromObject(supplier);
				messageMap.put("data", json);
				String messageStr = JSONObject.fromObject(messageMap).toString();
				logger.info("专车供应商，同步发送数据：" + messageStr);
				CommonRocketProducer.publishMessage(Constants.SUPPLIER_TOPIC, method, String.valueOf(supplier.getSupplierId()), messageMap);
			}catch (Exception e){
				logger.error(Constants.SUPPLIER_MQ_SEND_FAILED, e);
			}
			if (Constants.UPDATE.equals(method)){
				carBizDriverInfoService.updateDriverCooperationTypeBySupplierId(supplier.getSupplierId(), supplier.getCooperationType());
				carBizCarInfoTempService.updateDriverCooperationTypeBySupplierId(supplier.getSupplierId(), supplier.getCooperationType());
			}
			logger.info(Constants.SAVE_SUPPLIER_SUCCESS);
			return AjaxResponse.success(Constants.SAVE_SUPPLIER_SUCCESS);
		}catch (Exception e){
			logger.error(Constants.SAVE_SUPPLIER_ERROR, e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
    }

	public void addExtInfo(List<CarBizSupplierVo> list) {
		List<Integer> idList = new ArrayList<>(list.size());
		list.forEach( carBizSupplierVo -> idList.add(carBizSupplierVo.getSupplierId()));
		if (idList.size() == 0){
			return;
		}
		List<SupplierExtDto> extDtos = supplierExtDtoExMapper.queryExtDtoByIdList(idList);
		for (SupplierExtDto supplierExtDto : extDtos){
			for (CarBizSupplierVo vo : list){
				if (vo.getSupplierId().equals(supplierExtDto.getSupplierId())){
					vo.setSupplierShortName(supplierExtDto.getSupplierShortName());
					vo.setEmail(supplierExtDto.getEmail());
					break;
				}
			}
		}
	}
}