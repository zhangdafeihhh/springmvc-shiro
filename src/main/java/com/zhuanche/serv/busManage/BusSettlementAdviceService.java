package com.zhuanche.serv.busManage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.zhuanche.constants.BusConst;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper;
import mapper.rentcar.ex.BusCarBizCarGroupExMapper;
import mapper.rentcar.ex.BusCarBizServiceExMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;

/**
 * @ClassName: BusCommonService
 * @Description: 巴士公用接口服务类
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:25:09
 * 
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BusSettlementAdviceService implements BusConst {

	private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceService.class);

	@Autowired
	private BusBizChangeLogExMapper busBizChangeLogExMapper;

	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	@Autowired
	private BusCarBizCarGroupExMapper busCarBizCarGroupExMapper;

	@Autowired
	private BusCarBizServiceExMapper busCarBizServiceExMapper;

	@Value("${order.pay.url}")
	private String orderPayUrl;

}
