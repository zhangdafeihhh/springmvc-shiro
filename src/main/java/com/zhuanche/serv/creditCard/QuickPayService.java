package com.zhuanche.serv.creditCard;

import com.bill99.mgw.dto.Author;
import com.bill99.mgw.dto.DynDTO;
import com.bill99.mgw.dto.PayResultInfo;
import com.bill99.mgw.dto.QuickPayDTO;
import com.bill99.mgw.facade.PayFacade;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.rentcar.CarBizPaymentDetailDTO;
import mapper.rentcar.ex.CarBizPaymentDetailExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class QuickPayService {

	private static final Logger logger = LoggerFactory.getLogger(QuickPayService.class);

	@Autowired
    private CarBizPaymentDetailExMapper carBizPaymentDetailExMapper;

	// 定义一个变量，所有日志使用这个变量输出，保持日志的连续性
	private String guid = UUID.randomUUID().toString();

	/**
	 * add by zhou 20151118 注册信用卡服务类 封装各业务模块传递的数据，并调用快钱支付包
	 *
	 * @paymentType 支付类型 1:乘客绑定信用卡，2:司机绑定信用卡
	 * @cardNo 信用卡卡号
	 * @storableCardNo 短卡号
	 * @expiredDate 信用卡有效期
	 * @cvv2 信用卡三位数字
	 * @customerId 注册的用户唯一id，可以为电话号码，也可以为系统id或者其它唯一标识
	 * @cardHolderName 持卡人姓名
	 * @cardHolderId 持卡人证件号
	 * @entryTime 商户系统交易日期，可不填
	 * @phone 银行预留手机号
	 * @validCode 银行发送的短信验证码
	 * @token 快钱验证tocken
	 */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER)
	})
	public PayResultInfo firstPayBill99Pay(int paymentType, String cardNo,
			String storableCardNo, String expiredDate, String cvv2,
			String customerId, String cardHolderName, String cardHolderId,
			String entryTime, String phone, String validCode, String token,
			String externalRefNum) {

		logger.info("****************************** " + guid + " 绑定信用卡 增加信用卡刷卡记录表完成");

		int userType = 0;
		if (paymentType == 1) // 乘客绑定信用卡
		{
			userType = 1;
		} else if (paymentType == 3) // 司机绑定信用卡
		{
			userType = 2;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 增加信用卡流水记录
        CarBizPaymentDetailDTO carBizPaymentDetail = new CarBizPaymentDetailDTO();
		carBizPaymentDetail.setRelativeNo(customerId);
		carBizPaymentDetail.setPaymentType(paymentType);
		carBizPaymentDetail.setPaymentUsertype(userType);
		carBizPaymentDetail.setPaymentUser(customerId);
		carBizPaymentDetail.setAmount(1.00);
		carBizPaymentDetail.setExternalNo(externalRefNum);// 块钱外部流水号
		carBizPaymentDetail.setCreatedate(sdf.format(new Date(System.currentTimeMillis())));

        carBizPaymentDetailExMapper.insert(carBizPaymentDetail);
		logger.info("****************************** " + guid + " 绑定信用卡 增加信用卡刷卡记录表完成");
		PayResultInfo payResultInfo = new PayResultInfo();
		QuickPayDTO quickPayDTO = new QuickPayDTO();
		// 外部跟踪编号（externalRefNumber）
		quickPayDTO.setExternalRefNumber(externalRefNum);
		// 信用卡卡号（cardNo）
		quickPayDTO.setCardNo(cardNo);
		// 短卡号（storableCardNo）
		quickPayDTO.setStorableCardNo(storableCardNo);
		// 卡有效期（expiredDate）
		quickPayDTO.setExpiredDate(expiredDate);
		// 安全校验值（cvv2）
		quickPayDTO.setCvv2(cvv2);
		// 客户号（customerId）
		quickPayDTO.setCustomerId(customerId);
		// 客户姓名（cardHolderName）
		quickPayDTO.setCardHolderName(cardHolderName);
		// 客户身份证号（cardHolderId）
		quickPayDTO.setCardHolderId(cardHolderId);
		// 持卡人手机号（phone）
		quickPayDTO.setPhone(phone);
		// 手机验证码（validCode）
		quickPayDTO.setValidCode(validCode);
		// 手机验证码令牌（token）
		quickPayDTO.setToken(token);

		try {
			System.setProperty("jsse.enableSNIExtension", "false");
			payResultInfo = PayFacade.firstPayBill99(quickPayDTO);
			carBizPaymentDetail.setStatus(payResultInfo.getResponseCode());
			carBizPaymentDetail.setPaymentNo(payResultInfo.getRefNumber()); // 快钱系统交易流水号
			carBizPaymentDetail.setErrorMsg(payResultInfo.getResponseTextMessage()); // 快钱返回的错误描述
			carBizPaymentDetail.setAuthorizationCode(payResultInfo.getAuthorizationCode()); // 快钱返回的授权号码
		} catch (Exception e) {
			payResultInfo.setResponseCode("ERROR");
			payResultInfo.setResponseTextMessage("调用快钱失败");
			carBizPaymentDetail.setStatus("ERROR");
			carBizPaymentDetail.setErrorMsg("快钱调用失败");
			e.printStackTrace();
		}
        carBizPaymentDetailExMapper.updateByExternalRefNumber(carBizPaymentDetail);
		logger.info("****************************** " + guid + " 绑定信用卡 更新信用卡刷卡记录表完成");
		return payResultInfo;
	}

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
	 */
	public Author getDyncCode(
			String customerId,
			String externalNo,
			String idCardNo,
			String cardHolderName,
			String expiredDate,
			String cvn2,
			String cardNo,
			String bankPhoneNumber) {
		logger.info("****************************** " + guid + " 发送动态验证码 开始");
		Author author  = null;
		DynDTO dynDTO = new DynDTO();
		dynDTO.setCustomerId(customerId);
		dynDTO.setExternalRefNumber(externalNo);
		dynDTO.setCardHolderId(idCardNo);
		dynDTO.setCardHolderName(cardHolderName);
		dynDTO.setExpiredDate(expiredDate);
		dynDTO.setCvv2(cvn2);
		dynDTO.setPan(cardNo);
		dynDTO.setPhoneNO(bankPhoneNumber);

		try {
			logger.info("****************************** " + guid + " 发送动态验证码 调用快钱开始");
			System.setProperty("jsse.enableSNIExtension", "false");
			author = PayFacade.registerAccount(dynDTO);
			logger.info("****************************** " + guid + " 发送动态验证码 调用快钱结束");
		} catch (Exception e) {
			logger.info("****************************** " + guid + " 发送动态验证码 调用快钱异常"+e.getMessage());
			e.printStackTrace();
		}
		logger.info("****************************** " + guid + " 发送动态验证码 结束");
		return author;
	}
}
