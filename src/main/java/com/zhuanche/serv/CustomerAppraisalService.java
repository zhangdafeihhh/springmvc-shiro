package com.zhuanche.serv;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import mapper.rentcar.ex.CarBizCustomerAppraisalStatisticsExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.*;

@Service
public class CustomerAppraisalService {

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
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public PageInfo<CarBizCustomerAppraisalStatisticsDTO> queryCustomerAppraisalStatisticsListV2(CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO
            , int pageNo, int pageSize) {

        //查询司机信息
        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();

        carBizDriverInfoDTO.setPhone(carBizCustomerAppraisalStatisticsDTO.getDriverPhone());
        carBizDriverInfoDTO.setName(carBizCustomerAppraisalStatisticsDTO.getDriverName());

        carBizDriverInfoDTO.setCityIds(carBizCustomerAppraisalStatisticsDTO.getCityIds());
        carBizDriverInfoDTO.setServiceCity(carBizCustomerAppraisalStatisticsDTO.getCityId());
        carBizDriverInfoDTO.setSupplierIds(carBizCustomerAppraisalStatisticsDTO.getSupplierIds());
        carBizDriverInfoDTO.setSupplierId(carBizCustomerAppraisalStatisticsDTO.getSupplierId());
        carBizDriverInfoDTO.setDriverIds(carBizCustomerAppraisalStatisticsDTO.getDriverIds());
        List<CarBizDriverInfoDTO>  driverInfoDTOList = carBizDriverInfoExMapper.queryCarBizDriverList(carBizDriverInfoDTO);
        if(driverInfoDTOList == null){
            return null;
        }
        Set<Integer> driverIdSet = new HashSet<>();
        Map<String,CarBizDriverInfoDTO> cacheItem = new HashMap<>();

        for(CarBizDriverInfoDTO item :driverInfoDTOList){
            driverIdSet.add(item.getDriverId());
            cacheItem.put("d_"+item.getDriverId(),item);
        }
        carBizCustomerAppraisalStatisticsDTO.setDriverIds(driverIdSet);
        PageHelper.startPage(pageNo, pageSize, true);
        List<CarBizCustomerAppraisalStatisticsDTO> list  = carBizCustomerAppraisalStatisticsExMapper.queryCustomerAppraisalStatisticsListV2(carBizCustomerAppraisalStatisticsDTO);
        PageInfo<CarBizCustomerAppraisalStatisticsDTO> pageInfo = new PageInfo<>(list);
        if(list != null && list.size() >= 1){
            CarBizDriverInfoDTO driverInfotemp = null;
            for(CarBizCustomerAppraisalStatisticsDTO item : list){
                driverInfotemp = cacheItem.get("d_"+item.getDriverId());
                if(driverInfotemp != null){
                    item.setIdCardNo(driverInfotemp.getIdCardNo());
                    item.setCityId(driverInfotemp.getServiceCity());
                }
            }
        }
        return pageInfo;
    }
}
