package com.zhuanche.serv.driverPunish;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.bigdata.MaxAndMinId;
import com.zhuanche.entity.driver.DriverAppealRecord;
import com.zhuanche.entity.driver.DriverPunishDto;
import com.zhuanche.util.DateUtils;
import mapper.driver.DriverAppealRecordMapper;
import mapper.driver.ex.DriverPunishExMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */
@Service
public class DriverPunishService {

    private static Log log =  LogFactory.getLog(DriverPunishService.class);

    @Autowired
    private DriverPunishExMapper driverPunishExMapper;

    @Autowired
    private DriverAppealRecordMapper driverAppealRecordMapper;

    public PageInfo<DriverPunishDto> selectList(DriverPunishDto params) {
        return selectList(params, true);
    }

    public PageInfo<DriverPunishDto> selectList(DriverPunishDto params, Boolean isCount) {
        int total = 0;
        List<DriverPunishDto> rows = null;
        if(params.getPage() == null ){
            params.setPage(1);
        }
        if(params.getPagesize() == null){
            params.setPagesize(10);
        }
        Page p = PageHelper.startPage(params.getPage(), params.getPagesize(), isCount);
        PageInfo<DriverPunishDto> pageInfo=null;
        try {
            rows = driverPunishExMapper.selectList(params);
            //if(rows!=null && rows.size()>0){
                //for(AssetRentOutOrderDto r:rows){
                //    //计算租金，保险金
                //    this.countRentRateInfo(r);
                //    //初始化部分字段
                //    this.setFieldForAssetRentOutOrder(r);
                //}
            //}
            total  = (int)p.getTotal();
        } catch (Exception e) {
            log.error("出租订单信息", e);
        } finally {
            PageHelper.clearPage();
        }
        if (rows==null || rows.size()==0) {
            return  new PageInfo<>(new ArrayList<>());
        }
        pageInfo = new PageInfo<>(rows);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    public DriverPunishDto getDetail(Integer punishId) {
        if(punishId != null){
            DriverPunishDto driverPunishDto= driverPunishExMapper.getDetail(punishId);
            if(driverPunishDto.getCreateDate() != null){
                driverPunishDto.setCreateDateStr(DateUtils.formatDateTime(driverPunishDto.getCreateDate()));
            }
            return driverPunishDto;
        }
        return null;
    }

    public List<DriverAppealRecord> selectDriverAppealRocordByPunishId(Integer punishId) {
        return driverAppealRecordMapper.selectDriverAppealRocordByPunishId(punishId);
    }

    public Workbook exportExcel(List<DriverPunishDto> list , String path) throws Exception {

        FileInputStream io = new FileInputStream(path);
        // 创建 excel
        Workbook wb = new XSSFWorkbook(io);

        if(list != null && list.size()>0){
            Sheet sheet = null;
            try {
                sheet = wb.getSheetAt(0);
            } catch (Exception e) {
                log.error("exportExcel error", e);
            }
            Cell cell = null;
            int i=0;
            for(DriverPunishDto s:list){
                Row row = sheet.createRow(i + 1);
                // 处罚id
                cell = row.createCell(0);
                cell.setCellValue(s.getBusinessId()!=null?""+s.getBusinessId()+"":"");
                //订单号
                cell = row.createCell(1);
                cell.setCellValue(s.getOrderId()!=null?""+s.getOrderId()+"":"");
                //处罚类型
                cell = row.createCell(2);
                cell.setCellValue(s.getPunishTypeName()!=null?""+s.getPunishTypeName()+"":"");
                //处罚原因
                cell = row.createCell(3);
                cell.setCellValue(s.getPunishReason()!=null?""+s.getPunishReason()+"":"");
                //停运天数
                cell = row.createCell(4);
                cell.setCellValue(s.getStopDay()!=null?""+s.getStopDay()+"":"");
                //处罚金额
                cell = row.createCell(5);
                cell.setCellValue(s.getPunishPrice()!=null?""+s.getPunishPrice()+"":"");
                //扣除积分
                cell = row.createCell(6);
                cell.setCellValue(s.getPunishIntegral()!=null?""+s.getPunishIntegral()+"":"");
                //扣除流水
                cell = row.createCell(7);
                cell.setCellValue(s.getPunishFlow()!=null?""+s.getPunishFlow()+"":"");
                //处罚时间
                cell = row.createCell(8);
                cell.setCellValue(s.getCreateDateStr()!=null?""+s.getCreateDateStr()+"":"");
                //申诉时间
                cell = row.createCell(9);
                cell.setCellValue(s.getAppealDateStr()!=null?""+s.getAppealDateStr()+"":"");
                //司机手机号
                cell = row.createCell(10);
                cell.setCellValue(s.getPhone()!=null?""+s.getPhone()+"":"");
                //司机姓名
                cell = row.createCell(11);
                cell.setCellValue(s.getName()!=null?""+s.getName()+"":"");
                //车牌号
                cell = row.createCell(12);
                cell.setCellValue(s.getLicensePlates()!=null?""+s.getLicensePlates()+"":"");
                //合作类型
//                String cooperationName = "";
//                if(s.getCooperationType()!=null){
//                    CooperationTypeEntity cooperationType = new CooperationTypeEntity();
//                    cooperationType.setId(s.getCooperationType());
//                    CooperationTypeEntity cooperationTypeEntity = cooperationTypeDao.queryForObject(cooperationType);
//                    if(cooperationTypeEntity!=null){
//                        cooperationName = cooperationTypeEntity.getCooperationName();
//                    }
//                }
                cell = row.createCell(13);
                cell.setCellValue(s.getCooperationTypeName()!=null?""+s.getCooperationTypeName()+"":"");
                //城市
                cell = row.createCell(14);
                cell.setCellValue(s.getCityName()!=null?""+s.getCityName()+"":"");
                //合作商
                cell = row.createCell(15);
                cell.setCellValue(s.getSupplierName()!=null?""+s.getSupplierName()+"":"");
                //车队
                cell = row.createCell(16);
                cell.setCellValue(s.getTeamName()!=null?""+s.getTeamName()+"":"");
                //状态
                int status = s.getStatus();
                String statusName = "";
                if(status==15){
                    statusName = "待服务";
                }else if(status==20){
                    statusName = "已出发";
                }else if(status==30){
                    statusName = "服务中";
                }else if(status==50){
                    statusName = "已完成";
                }else if(status==60){
                    statusName = "取消";
                }
                cell = row.createCell(17);
                cell.setCellValue(statusName);
                //审核节点
                cell = row.createCell(18);
                cell.setCellValue(s.getAuditNode()!=null?""+s.getAuditNode()+"":"");

                i++;
            }
        }
        return wb;
    }

    public MaxAndMinId queryMaxAndMin(String startDate, String endDate){
        return driverPunishExMapper.queryMaxAndMin(startDate,endDate);
    }

}
