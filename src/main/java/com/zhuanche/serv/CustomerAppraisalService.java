package com.zhuanche.serv;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalExtDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import mapper.rentcar.ex.CarBizCustomerAppraisalStatisticsExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.*;

@Service
public class CustomerAppraisalService {

    private static final Logger log =  LoggerFactory.getLogger(CustomerAppraisalService.class);

    @Autowired
    private CarBizCustomerAppraisalExMapper carBizCustomerAppraisalExMapper;

    @Autowired
    private CarBizCustomerAppraisalStatisticsExMapper carBizCustomerAppraisalStatisticsExMapper;

    @Autowired
    private CarDriverTeamService carDriverTeamService;
    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    public List<CarBizCustomerAppraisalDTO> queryCustomerAppraisalList(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
    }

    /**
     * 查询司机评分
     * @param carBizCustomerAppraisalStatisticsDTO
     * @return
     */
    public List<CarBizCustomerAppraisalStatisticsDTO> queryCustomerAppraisalStatisticsList(CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO) {
        return carBizCustomerAppraisalStatisticsExMapper.queryCustomerAppraisalStatisticsList(carBizCustomerAppraisalStatisticsDTO);
    }

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    public List<CarBizCustomerAppraisalDTO> queryDriverAppraisalDetail(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryDriverAppraisalDetail(carBizCustomerAppraisalDTO);
    }

    /**
     * 导出司机评分
     * @param list
     * @param path
     * @return
     * @throws Exception
     */
    public Workbook exportExcelDriverAppraisal(List<CarBizCustomerAppraisalStatisticsDTO> list, String path) throws Exception {
        FileInputStream io = new FileInputStream(path);
        Workbook wb = new XSSFWorkbook(io);
        if(list != null && list.size()>0){
            Sheet sheet = wb.getSheetAt(0);
            Cell cell = null;
            int i=0;

            Map<Integer, String> teamMap = null;
            try {
                String driverIds = this.pingDriverIds(list);
                teamMap = carDriverTeamService.queryDriverTeamListByDriverId(driverIds);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(CarBizCustomerAppraisalStatisticsDTO s:list){
                Row row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(s.getDriverName());

                cell = row.createCell(1);
                cell.setCellValue(s.getDriverPhone());

                cell = row.createCell(2);
                cell.setCellValue(s.getCreateDate());

                cell = row.createCell(3);
                cell.setCellValue(s.getEvaluateScore());

                cell = row.createCell(4);
                cell.setCellValue(s.getIdCardNo());

                cell = row.createCell(5);
                String teamName = "";
                if(teamMap!=null){
                    teamName = teamMap.get(s.getDriverId());
                }
                cell.setCellValue(teamName);

                i++;
            }
        }
        return wb;
    }

    public static String pingDriverIds(List<CarBizCustomerAppraisalStatisticsDTO> list) {
        String driverId = "";
        if(list!=null&&list.size()>0){
            int j=0;
            for(int i=0;i<list.size();i++){
                if(!"".equals(list.get(i))&&list.get(i)!=null&&!"".equals(list.get(i).getDriverId())&&list.get(i).getDriverId()!=null){
                    if(j==0){
                        driverId = "'"+list.get(i).getDriverId()+"'";
                    }else{
                        driverId +=",'"+list.get(i).getDriverId()+"'";
                    }
                    j++	;
                }
            }
        }
        return driverId;
    }
    //分两种情况，1:除去日期没有其他条件，2：有其他条件还有日期
    public PageInfo<CarBizCustomerAppraisalStatisticsDTO> queryDriverAppraisalDetailV2(
            CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalDTO,int pageNo
            ,int pageSize,String startTime,String endTime) {

        //情况1:
        if(StringUtils.isEmpty(carBizCustomerAppraisalDTO.getDriverPhone()) && StringUtils.isEmpty(carBizCustomerAppraisalDTO.getDriverName())
                && (carBizCustomerAppraisalDTO.getCityIds() == null || carBizCustomerAppraisalDTO.getCityIds().isEmpty())
                && (carBizCustomerAppraisalDTO.getSupplierIds() == null || carBizCustomerAppraisalDTO.getSupplierIds().isEmpty())
                && (carBizCustomerAppraisalDTO.getCityId() == null || carBizCustomerAppraisalDTO.getCityId() == 0)
                && (carBizCustomerAppraisalDTO.getSupplierId() == null || carBizCustomerAppraisalDTO.getSupplierId() == 0)
            ){
            //先查评价信息
            CarBizCustomerAppraisalExtDTO params = new  CarBizCustomerAppraisalExtDTO();
            params.setStartTime(startTime);
            params.setEndTime(endTime);
            PageHelper.startPage(pageNo, pageSize, true);
            List<CarBizCustomerAppraisalStatisticsDTO> list  = carBizCustomerAppraisalExMapper.queryDriverAppraisalDetailByParam(params);
            PageInfo<CarBizCustomerAppraisalStatisticsDTO> pageInfo = new PageInfo<>(list);
            Set<Integer> driverSet = new HashSet<>();
            String driverIds = "";
            if(list != null){
                for(CarBizCustomerAppraisalStatisticsDTO item : list){
                    driverSet.add(item.getDriverId());
                    if(driverIds.length() >= 1){
                        driverIds += ",";
                    }
                    driverIds += (item.getDriverId()+"");
                }
                DutyParamRequest dutyParamRequest = new DutyParamRequest();
                dutyParamRequest.setDriverIds(driverIds);
                List<CarBizDriverInfoDTO> carDriverInfoDTOList = carBizDriverInfoExMapper.queryCarBizDriverList(dutyParamRequest);
                Map<String,CarBizDriverInfoDTO> cacheCarDriverInfoDTO = new HashMap<>();
                if(carDriverInfoDTOList != null){
                    for(CarBizDriverInfoDTO item : carDriverInfoDTOList){
                        cacheCarDriverInfoDTO.put("d_"+item.getDriverId(),item);
                    }
                }
                CarBizDriverInfoDTO temp = null;
                for(CarBizCustomerAppraisalStatisticsDTO item : list){
                    temp = cacheCarDriverInfoDTO.get("d_"+item.getDriverId());
                    if(temp != null){
                        item.setDriverName(temp.getName());
                        item.setDriverPhone(temp.getPhone());
                        item.setIdCardNo(temp.getIdCardNo());
                    }
                }
            }
            return  pageInfo;

        }else{
            //情况2
            /**
             * 先查司机
             */
            DutyParamRequest dutyParamRequest = new DutyParamRequest();
            dutyParamRequest.setPhone(carBizCustomerAppraisalDTO.getDriverPhone());
            dutyParamRequest.setDriverName(carBizCustomerAppraisalDTO.getDriverName());
            dutyParamRequest.setCityId(carBizCustomerAppraisalDTO.getCityId());
            dutyParamRequest.setCityIds(carBizCustomerAppraisalDTO.getCityIds());
            dutyParamRequest.setSupplierId(carBizCustomerAppraisalDTO.getSupplierId());
            dutyParamRequest.setSupplierIds(carBizCustomerAppraisalDTO.getSupplierIds());
            String driverIds = "";
            if(carBizCustomerAppraisalDTO.getDriverIds() != null && !carBizCustomerAppraisalDTO.getDriverIds().isEmpty()){
                for(Integer driverIdTemp : carBizCustomerAppraisalDTO.getDriverIds()){
                    if(driverIds.length() >= 1){
                        driverIds += ",";
                    }
                    driverIds += (driverIdTemp+"");
                }
            }
            dutyParamRequest.setDriverIds(driverIds);
            dutyParamRequest.setDriverId(carBizCustomerAppraisalDTO.getDriverId());

            List<CarBizDriverInfoDTO> carDriverInfoDTOList = carBizDriverInfoExMapper.queryCarBizDriverList(dutyParamRequest);
            if(carDriverInfoDTOList == null){
                log.info("查询评价信息，查询结果为空。参数为："+ JSON.toJSONString(dutyParamRequest));
                return null;
            }
            //再根据司机id、评价开始时间、结束时间 查询评分信息
            Set<Integer> driverIdSet = new HashSet<>();
            Map<String,CarBizDriverInfoDTO> cacheCarDriverInfoDTO = new HashMap<>();
            for(CarBizDriverInfoDTO item : carDriverInfoDTOList){
                driverIdSet.add(item.getDriverId());
                cacheCarDriverInfoDTO.put("d_"+item.getDriverId(),item);
            }

            CarBizCustomerAppraisalExtDTO params = new  CarBizCustomerAppraisalExtDTO();
            params.setDriverIds(driverIdSet);
            params.setStartTime(startTime);
            params.setEndTime(endTime);

            PageHelper.startPage(pageNo, pageSize, true);
            List<CarBizCustomerAppraisalStatisticsDTO> list  = carBizCustomerAppraisalExMapper.queryDriverAppraisalDetailByParam(params);
            PageInfo<CarBizCustomerAppraisalStatisticsDTO> pageInfo = new PageInfo<>(list);
            if(list.size() > 0){
                //遍历赋值，司机姓名
                CarBizDriverInfoDTO temp = null;
                for(CarBizCustomerAppraisalStatisticsDTO item : list){
                    temp = cacheCarDriverInfoDTO.get("d_"+item.getDriverId());
                    if(temp != null){
                        item.setDriverName(temp.getName());
                        item.setDriverPhone(temp.getPhone());
                        item.setIdCardNo(temp.getIdCardNo());
                    }
                }
            }
            return  pageInfo;
        }


    }
}
