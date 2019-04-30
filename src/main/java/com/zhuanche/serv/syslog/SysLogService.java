package com.zhuanche.serv.syslog;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.syslog.SysLogAnn;
import com.zhuanche.common.util.CompareObjUtil;
import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;
import com.zhuanche.entity.driver.SysLog;
import com.zhuanche.entity.driver.SysLogDTO;
import com.zhuanche.util.BeanUtil;

import mapper.driver.SysLogMapper;

/**  
 * ClassName:SysLogService <br/>  
 * Date:     2019年4月17日 下午7:34:52 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class SysLogService {
    private static final Logger logger = LoggerFactory.getLogger(SysLogService.class);
    @Autowired
    private SysLogMapper sysLogMapper;
    
    @Async
	public void save(SysLog sysLog) {
		logger.info(sysLog.getMethod());
		
	}
    
    public SysLog querySysLog(Integer sysLogId) {
    	SysLog sysLog=sysLogMapper.selectByPrimaryKey(sysLogId);
    	return sysLog;
	}
    
	public void saveLog(SysLog sysLog) {
		sysLogMapper.insertSelective(sysLog);
	}
	/*@SysLogAnn(module="系统日志",methods="系统日志更新",
		    serviceClass="sysLogService",queryMethod="querySysLog",parameterType="Integer",parameterKey="sysLogId",parameterObj="sysLog")
*/	public void updateSysLog(SysLog sysLog) {
		sysLogMapper.updateByPrimaryKeySelective(sysLog);
	}

	public PageDTO querySysLogList(Integer page, Integer pageSize) {

		if(page==null || page.intValue()<=0) {
			page = new Integer(1);
		}
		if(pageSize==null || pageSize.intValue()<=0) {
			pageSize = new Integer(10);
		}
		//执行SQL查询
    	int total = 0;
    	List<SysLog> sysLogs=null;
    	Page p = PageHelper.startPage( page, pageSize, true );
    	try{
    		sysLogs=sysLogMapper.selectByPrimaryList();
        	total  = (int)p.getTotal();
    	}catch (Exception e) {
    		logger.error("查询一级白名单信息异常",e);
		}finally {
        	PageHelper.clearPage();
    	}
    	//判断返回结果
    	if(sysLogs==null || sysLogs.size()==0) {
    		return new PageDTO(page, pageSize, total, new ArrayList());
    	}
    	List<SysLogDTO> sysLogDTOs = BeanUtil.copyList(sysLogs, SysLogDTO.class);
    	
    	for (SysLogDTO sysLogDTO : sysLogDTOs) {
    		sysLogDTO.setRemarks(CompareObjUtil.getRemarks(FinancialBasicsVehiclesDTO.class, sysLogDTO.getBeforeParams(), sysLogDTO.getOperateParams()));
		}
    	
    	//返回
    	return new PageDTO( page,pageSize,total,sysLogDTOs);
	
	}
}
  
