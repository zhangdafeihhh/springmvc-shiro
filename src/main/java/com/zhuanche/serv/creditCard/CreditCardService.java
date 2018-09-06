package com.zhuanche.serv.creditCard;

import com.bill99.mgw.dto.*;
import com.bill99.mgw.facade.PayFacade;
import com.zhuanche.common.cache.RedisCacheDriverUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.rentcar.CarBizDriverAccountDTO;
import com.zhuanche.entity.creditCard.RegisterCard;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.rentcar.ex.CarBizDriverAccountExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class CreditCardService {

	private static final Logger logger = LoggerFactory.getLogger(CarBizDriverInfoService.class);
	private static final String LOGTAG = "[司机信用卡]: ";

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private QuickPayService quickPayService;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private CarBizDriverAccountExMapper carBizDriverAccountExMapper;

	/**
	 * 解绑信用卡
	 * 返回null 报错。
	 * 返回response.getReturnTextMessage()!'' 解绑错误信息
	 * 返回response.getResponseCode().equals("00") 解绑成功！
	 * @param dto
	 */
	public UnBindResponse relieveCreditCards(UnBindDTO dto) {
		UnBindResponse response  = null;
		try {
			System.setProperty("jsse.enableSNIExtension", "false");
			response = PayFacade.unbind(dto);

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("解绑信用卡，结果="+response);
		return response;
	}

	public boolean getValNum(String cardNo, String validity, String cvn2,
			String name, String idCardNo, String phoneNumber,
			String driverPhoneNumber) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		// 外部流水号，随机码
		Random random = new Random();
		String val = "";
		for (int i = 0; i < 4; i++) {
			val = val + random.nextInt(10);
		}
		RedisCacheDriverUtil.set(driverPhoneNumber.trim(), val, 300);
		RegisterCard dto = new RegisterCard();
		String customerId = driverPhoneNumber.trim();
		dto.setCustomerId(customerId.trim());
		dto.setExternalRefNumber("B_DB_" + driverPhoneNumber + val);
		dto.setCardHolderId(idCardNo.trim());
		dto.setCardHolderName(name.trim());
		dto.setExpiredDate(validity.trim());
		dto.setCvv2(cvn2.trim());
		dto.setPan(cardNo.trim());
		dto.setPhoneNO(phoneNumber.trim());

		Author author = this.registerCreditCard(dto);
		if (author != null) {
			// 将信用卡短卡号及token放入缓存中
			String storablePan = author.getStorablePan();
			// 保存卡信息到redis
			RedisCacheDriverUtil.set("S_CARD" + driverPhoneNumber, storablePan,300);
//			jedisTemplate.set("S_CARD" + driverPhoneNumber, storablePan,300);
//			redisUtil.expire("S_CARD" + driverPhoneNumber.trim(), 300);
			// 保存token到redis
			RedisCacheDriverUtil.set("BIND" + driverPhoneNumber, author.getToken(),300);
//			jedisTemplate.set("BIND" + driverPhoneNumber, author.getToken(),300);
//			redisUtil.expire("BIND" + driverPhoneNumber.trim(), 300);

			logger.info("******************** 司机获取验证码接口处理  author.getToken: " + author.getToken());
			if (!StringUtils.isEmpty(author.getReturnTextMessage())) {
				// return URLEncoder.encode(author.getReturnTextMessage()) ;
				logger.info("******************** 司机获取验证码接口处理【异常】phone"
						+ phoneNumber + "原因" + author.getReturnTextMessage());
				returnMap.put("returnCode", "1");
				return false;
			} else{
				return true;
			}
		} else {
			return false;
		}
	}
	/**
	 * 注册信用卡
	 * @param dto
	 */
	public Author registerCreditCard(RegisterCard dto) {
		Author author  = null;
		DynDTO dynDTO = new DynDTO();
		BeanUtils.copyProperties(dto, dynDTO);
		try {
			System.setProperty("jsse.enableSNIExtension", "false");
			author = PayFacade.registerAccount(dynDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return author;
	}

	/**
	 * 司机手机绑定信用卡
	 * @param cardNo 卡号
	 * @param expireDate 有效期
	 * @param cvn2 cvn2
	 * @param carHolderName 姓名
	 * @param idCardNo 身份证
	 * @param phoneNumber 绑定银行卡手机号
	 * @param valCode 验证码
	 * @param driverPhone 司机手机号
	 * @param driverId 司机ID
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> addDriverCreditCard(String cardNo, String expireDate, String cvn2, String carHolderName,
												   String idCardNo, String phoneNumber, String valCode, String driverPhone,
												   Integer driverId) throws Exception {
			Map<String, Object> returnMap = new HashMap<String, Object>();
			String creditCardNo = cardNo;

			String bandCardphone = phoneNumber;
			String token = RedisCacheDriverUtil.get("BIND" + driverPhone, String.class);
			String valdit = valCode;
			if (token == null || token.equals("")) {
				logger.info(LOGTAG + "司机绑定信用卡接口处理【银行Token过期】phoneNumber={},司机电话={}", phoneNumber, driverPhone);
				returnMap.put("returnCode", "1");
				returnMap.put("returnMsg", "银行Token过期");
				return returnMap;
			}
			// 获取短卡号
			String shortCard = RedisCacheDriverUtil.get("S_CARD" + driverPhone, String.class);
			if (shortCard == null || shortCard.equals("")) {
				logger.info(LOGTAG + "司机绑定信用卡接口处理【银行短卡号过期】phoneNumber={},司机电话={}", phoneNumber, driverPhone);
				returnMap.put("returnCode", "1");
				returnMap.put("returnMsg", "银行短卡号过期");
				return returnMap;
			}
			// 判断验证码是否正确
			String val = RedisCacheDriverUtil.get(driverPhone, String.class);
			if (val == null || val.equals("")) {
				logger.info("******************** 司机绑定信用卡接口处理【验证码过期】phoneNumber={},司机电话={}", phoneNumber, driverPhone);
				returnMap.put("returnCode", "1");
				returnMap.put("returnMsg", "验证码过期");
				return returnMap;
			}

			logger.info("******************** 司机绑定信用卡接口处理 creditCardNo:" + creditCardNo);
			logger.info("******************** 司机绑定信用卡接口处理 shortCard:" + shortCard);
			logger.info("******************** 司机绑定信用卡接口处理 expireDate:" + expireDate);
			logger.info("******************** 司机绑定信用卡接口处理 cvn2:" + cvn2);
			logger.info("******************** 司机绑定信用卡接口处理 driverPhone:" + driverPhone);
			logger.info("******************** 司机绑定信用卡接口处理 carHolderName:" + carHolderName);
			logger.info("******************** 司机绑定信用卡接口处理 idCardNo:" + idCardNo);
			logger.info("******************** 司机绑定信用卡接口处理 bandCardphone:" + bandCardphone);
			logger.info("******************** 司机绑定信用卡接口处理 valdit:" + valdit);
			logger.info("******************** 司机绑定信用卡接口处理 token:" + token);
			logger.info("******************** 司机绑定信用卡接口处理 val:" + val);

		String quickPayCustomerID = carBizDriverInfoService.selectByPrimaryKey(driverId).getQuickpayCustomerid();

		PayResultInfo payResultInfo = quickPayService.firstPayBill99Pay(3,
					creditCardNo.trim(), shortCard.trim(), expireDate,
					cvn2.trim(), quickPayCustomerID, carHolderName.trim(), idCardNo.trim(),
					"", bandCardphone.trim(), valdit.trim(), token.trim(),"B_DB_"+driverPhone + val);

			if (payResultInfo != null){
				//获取返回消息状态
				String msgStatus = payResultInfo.getResponseCode();

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				if("00".equals(msgStatus)){
					logger.info("*********备注：绑定成功;phone："+driverPhone+"");

					// 查询数据库信息
//					DriverMongoEntity driverMongo = driverMongoTemplate.findOne(
//							new Query(Criteria.where("phone").is(driverPhone)),
//							DriverMongoEntity.class);

					// 增加绑定时间
					// 信用卡短卡号绑定至司机信息
					Map<String, Object> cardMap = new HashMap<String, Object>();
					cardMap.put("driverId", driverId);
					cardMap.put("phone", driverPhone);
					cardMap.put("creditCardNo", cardNo.substring(0,4) + " **** **** " + cardNo.substring(12, cardNo.length()));
					cardMap.put("shortCardNo", shortCard);
					Date bindTime = new Date(System.currentTimeMillis());
					String bindStr = sdf.format(bindTime);
					cardMap.put("bindTime", bindStr);
					cardMap.put("isBindingCreditCard", "1");
					cardMap.put("expireDate", expireDate);
					cardMap.put("CVN2", cvn2);
					cardMap.put("creditOpenAccountBank",payResultInfo.getIssuer());
//					cardMap.put("quickpayCustomerid",payResultInfo.getCustomerId());
					cardMap.put("updateBy", WebSessionUtil.getCurrentLoginUser().getId());
					carBizDriverInfoService.updateDriverCardInfo(cardMap);
					// 查询司机账户信息
					CarBizDriverAccountDTO carBizDriverAccount = carBizDriverAccountExMapper.selectDriverAcctount(driverId);

					Double cu = null;
					Double his = null;
					Double cb = null;
					Double cbHis = null;
					Double outCurr = null;
					Double outHis = null;
					if(carBizDriverAccount!=null){
						cu = carBizDriverAccount.getCurrAmount();
						his = carBizDriverAccount.getHistoryAmount();
						cb = carBizDriverAccount.getCreditBalance();
						cbHis = carBizDriverAccount.getCreditBalanceHis();
						outCurr = carBizDriverAccount.getOutCurrAccount();
						if("".equals(outCurr)||outCurr==null){
							outCurr = 0.0;
						}
						outHis = carBizDriverAccount.getOutHisAccount();
					}
					// 更新司机账户，增加司机流水
					this.updateDriverAccountInfo(driverId, cu, cb, outCurr + 1.00);
					this.insertDriverAccountDetil("", 4, driverId, 1.00, cb, cu, his, cbHis, outCurr + 1.00, outHis);

			logger.info("******************** 司机绑定信用卡接口处理 更新司机账户信息完成 phoneNumber" + phoneNumber + " 司机电话" +driverPhone);
			returnMap.put("returnCode", "0");
			returnMap.put("returnMsg", "");
		}else if("C0".equals(msgStatus)||"68".equals(msgStatus)){
			returnMap.put("returnCode", "0");
			returnMap.put("returnMsg", payResultInfo.getResponseTextMessage());
		}else{
			returnMap.put("returnCode", "1");
			returnMap.put("returnMsg", payResultInfo.getResponseTextMessage());
		}
	}else {
		returnMap.put("returnCode", "1");
		returnMap.put("returnMsg", "快钱调用失败");
	}

	return returnMap;
	}

	/**
	 * 获取信用卡验证码及短卡号
	 * @param bindType
	 * @param version
	 * @param driverId 司机ID
	 * @param creditCardNo 卡号
	 * @param expireDate 有效期
	 * @param cvn2 cvn2
	 * @param idCardNo 身份证
	 * @param cardHolderName 姓名
	 * @param driverPhone 司机手机号
	 * @param bandCardphone 绑定银行卡手机号
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateValNumDriver(String bindType, String version, String creditCardNo, String expireDate,
												  String cvn2, String cardHolderName, String idCardNo, String bandCardphone,
												  String driverPhone, Integer driverId) throws Exception {

		logger.info(LOGTAG + "******************** 司机获取验证码接口处理【开始】phone" +driverPhone);
		Map<String, Object> returnMap = new HashMap<String, Object>();

		String quickPayCustomerID = carBizDriverInfoService.selectByPrimaryKey(driverId).getQuickpayCustomerid();
		if(quickPayCustomerID==null || quickPayCustomerID=="" || quickPayCustomerID.length()==0) {
			logger.info("******************** 司机获取验证码接口处理【开始】phone={},开始生成customerID", driverPhone);
//				driverVO =  driverAccountDetailService.getDriverInfo(driverVO);
//				int driverID =driverVO.getDriverId();// 2621;

			String tmpID = "00000000" + driverId;
//				String.valueOf(driverID);
			tmpID = tmpID.substring(tmpID.length()-8, tmpID.length());

			quickPayCustomerID = bindType + tmpID.substring(0, 3) + "D" + tmpID.substring(3,6) + "B" + tmpID.substring(6, 8);

			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("quickpayCustomerID", quickPayCustomerID);
			updateMap.put("driverID", driverId);
			carBizDriverInfoExMapper.updateDriverCustomerId(updateMap);
			logger.info("******************** 司机获取验证码接口处理【开始】phone={},更新customerID完成", driverPhone);
		}
		// 外部流水号，随机码
		Random random = new Random();
		String val = "";
		for (int i = 0; i < 4; i++) {
			val = val + random.nextInt(10);
		}
		RedisCacheDriverUtil.set(driverPhone.trim(), val,300);
//			jedisTemplate.expire(driverPhoneNumber.trim(), 300);

		logger.info("******************** 司机获取验证码接口处理【开始】phone={},调用获取动态码接口开始" , driverPhone);
		/**
		 * add by zhou 20151211
		 * 注册信用卡,获取动态验证码
		 * @param customerId 客户号
		 * @param externalNo 外部流水号
		 * @param idCardNo 身份证号
		 * @param cardHolderName 持卡人姓名
		 * @param expiredDate 失效日期
		 * @param cvn2 卡背面3位数字
		 * @param cardNo 卡号
		 * @param bankPhoneNumber 银行预留手机号
		 * @param dto
		 */
		Author author = quickPayService.getDyncCode(
				quickPayCustomerID,
				"B_DB_"+driverPhone + val,
				idCardNo.trim(),
				cardHolderName.trim(),
				expireDate.trim(),
				cvn2,
				creditCardNo,
				bandCardphone
				);
		logger.info("******************** 司机获取验证码接口处理【开始】phone={},调用获取动态码接口完成", driverPhone);
		if (author != null) {

			logger.info("******************** 司机获取验证码接口处理【开始】phone={},调用获取动态码接口正常返回");
			// 将信用卡短卡号及token放入缓存中
			String storablePan = author.getStorablePan();
			RedisCacheDriverUtil.set("S_CARD" + driverPhone, storablePan,300);
//				jedisTemplate.expire("S_CARD" + driverPhoneNumber.trim(), 300);
			// 保存token到redis
			RedisCacheDriverUtil.set("BIND" + driverPhone, author.getToken(),300);
//				jedisTemplate.expire("BIND" + driverPhoneNumber.trim(), 300);
			logger.info("******************** 司机获取验证码 getValNum 银行预留手机号:" + bandCardphone + " 司机手机号:"+driverPhone + " 快钱返回结果storablePan:"+storablePan + " token:" + author.getToken());

			returnMap.put("token", author.getToken());

			if(!StringUtils.isEmpty(author.getReturnTextMessage())) {
				// return URLEncoder.encode(author.getReturnTextMessage()) ;
				logger.info("******************** 司机获取验证码接口处理【异常】phone"
						+ driverPhone + "原因" + author.getReturnTextMessage());
				returnMap.put("returnCode","1");
				returnMap.put("errormsg", author.getReturnTextMessage());
				return returnMap;
			}else
			{
				returnMap.put("returnCode", "0");
				return returnMap;
			}
		} else {
			logger.info("******************** 司机获取验证码接口处理【开始】phone" +driverPhone +"调用获取动态码接口返回值为NULL");
			returnMap.put("returnCode","1");
			return returnMap;
		}
	}


	/**
	 * 更新司机账户信息
	 * @param driverId
	 * @param currAmount
	 * @param creditAmont
	 * @param outCurr
	 */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
	})
	public void updateDriverAccountInfo(Integer driverId, Double currAmount,
										Double creditAmont, Double outCurr) {
		try {
			CarBizDriverAccountDTO carBizDriverAccount = new CarBizDriverAccountDTO();
			carBizDriverAccount.setDriverId(driverId);
			carBizDriverAccount.setCreditBalance(creditAmont);
			carBizDriverAccount.setCurrAmount(currAmount);
			carBizDriverAccount.setOutCurrAmount(outCurr);
			carBizDriverAccountExMapper.updateDrivetAccount(carBizDriverAccount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加司机提成流水
	 * @param orderNo
	 * @param op
	 * @param driverId
	 * @param amount
	 * @param credit
	 * @param settl
	 * @param settlHis
	 * @param creditHis
	 * @param outCurr
	 * @param outHis
	 */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
	})
	public void insertDriverAccountDetil(String orderNo, Integer op,
										 Integer driverId, Double amount, Double credit, Double settl,
										 Double settlHis, Double creditHis, Double outCurr, Double outHis) {
		try {
			CarBizDriverAccountDTO carBizDriverAccountIncome = new CarBizDriverAccountDTO();
			carBizDriverAccountIncome.setDealNo(orderNo);
			carBizDriverAccountIncome.setOperation(String.valueOf(op));
			carBizDriverAccountIncome.setDriverId(driverId);
			carBizDriverAccountIncome.setAmount(amount);
			carBizDriverAccountIncome.setCreditBalance(credit);
			carBizDriverAccountIncome.setSettleAccount(settl);
			carBizDriverAccountIncome.setSettleAccountHis(settlHis);
			carBizDriverAccountIncome.setCreditBalanceHis(creditHis);
			carBizDriverAccountIncome.setOutCurrAccount(outCurr);
			carBizDriverAccountIncome.setOutHisAccount(outHis);
			carBizDriverAccountExMapper.insertDriverAccountDetil(carBizDriverAccountIncome);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
