package com.zhuanche.controller.driver;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.dto.rentcar.DriverDutyStatisticDTO;
import com.zhuanche.serv.CustomerAppraisalService;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.threads.CustomerAppraisalExportHelper;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Controller
@RequestMapping("/customerAppraisal")
public class CustomerAppraisalController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAppraisalController.class);
    private static final String LOGTAG = "[订单评分]: ";

    @Autowired
    private CustomerAppraisalService customerAppraisalService;

    @Autowired
    private CarDriverTeamService carDriverTeamService;



    /**
     * 订单评分
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param orderNo 订单号
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param teamGroupId 车队下小组ID
     * @param createDateBegin 开始时间
     * @param createDateEnd 结束时间
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCustomerAppraisalList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCustomerAppraisalList(String name, String phone, String orderNo, Integer cityId, Integer supplierId,
                                       Integer teamId, Integer teamGroupId, String createDateBegin, String createDateEnd,
                                       @RequestParam(value="page", defaultValue="0")Integer page,
                                                   @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        int total = 0;
        List<CarBizCustomerAppraisalDTO> list =  Lists.newArrayList();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if(teamGroupId!=null || teamId!=null || (permOfTeam!=null && permOfTeam.size()>0)){
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(teamGroupId, teamId, permOfTeam);
        }
        if(had && (driverIds==null || driverIds.size()==0)){
            logger.info(LOGTAG + "查询teamId={},teamGroupId={},permOfTeam={}没有司机信息", teamId, teamGroupId, permOfTeam);
            PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
            return AjaxResponse.success(pageDTO);
        }

        CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO = new CarBizCustomerAppraisalDTO();
        carBizCustomerAppraisalDTO.setName(name);
        carBizCustomerAppraisalDTO.setPhone(phone);
        carBizCustomerAppraisalDTO.setOrderNo(orderNo);
        carBizCustomerAppraisalDTO.setCityId(cityId);
        carBizCustomerAppraisalDTO.setSupplierId(supplierId);
        carBizCustomerAppraisalDTO.setTeamId(teamId);
        carBizCustomerAppraisalDTO.setTeamGroupId(teamGroupId);
        carBizCustomerAppraisalDTO.setCreateDateBegin(createDateBegin);
        carBizCustomerAppraisalDTO.setCreateDateEnd(createDateEnd);
        //数据权限
        carBizCustomerAppraisalDTO.setCityIds(permOfCity);
        carBizCustomerAppraisalDTO.setSupplierIds(permOfSupplier);
        carBizCustomerAppraisalDTO.setTeamIds(permOfTeam);
        carBizCustomerAppraisalDTO.setDriverIds(driverIds);

        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = customerAppraisalService.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 司机评分
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param teamGroupId 车队下小组ID
     * @param month 月份 yyyy-M
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCustomerAppraisalStatisticsList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCustomerAppraisalStatisticsList(String name, String phone,Integer cityId, Integer supplierId,
                                                   Integer teamId, Integer teamGroupId,
                                                   @Verify(param = "month", rule = "required") String month,
                                                   @RequestParam(value="page", defaultValue="0")Integer page,
                                                   @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        int total = 0;
        List<CarBizCustomerAppraisalStatisticsDTO> list =  Lists.newArrayList();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if(teamGroupId!=null || teamId!=null || (permOfTeam!=null && permOfTeam.size()>0)){
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(teamGroupId, teamId, permOfTeam);
        }
        if(had && (driverIds==null || driverIds.size()==0)){
            logger.info(LOGTAG + "查询teamId={},teamGroupId={},permOfTeam={}没有司机信息", teamId, teamGroupId, permOfTeam);
            PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
            return AjaxResponse.success(pageDTO);
        }

        CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO = new CarBizCustomerAppraisalStatisticsDTO();
        carBizCustomerAppraisalStatisticsDTO.setDriverName(name);
        carBizCustomerAppraisalStatisticsDTO.setDriverPhone(phone);
        carBizCustomerAppraisalStatisticsDTO.setCreateDate(month);
        carBizCustomerAppraisalStatisticsDTO.setCityId(cityId);
        carBizCustomerAppraisalStatisticsDTO.setSupplierId(supplierId);
        //数据权限
        carBizCustomerAppraisalStatisticsDTO.setCityIds(permOfCity);
        carBizCustomerAppraisalStatisticsDTO.setSupplierIds(permOfSupplier);
        carBizCustomerAppraisalStatisticsDTO.setTeamIds(permOfTeam);
        carBizCustomerAppraisalStatisticsDTO.setDriverIds(driverIds);

        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = customerAppraisalService.queryCustomerAppraisalStatisticsList(carBizCustomerAppraisalStatisticsDTO);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 司机评分导出
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param teamGroupId 车队下小组ID
     * @param month 月份 yyyy-M
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exportCustomerAppraisalStatistics")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public String queryCustomerAppraisalStatisticsList(String name, String phone, Integer cityId, Integer supplierId,
                                                     Integer teamId, Integer teamGroupId,
                                                     @Verify(param = "month", rule = "required") String month,
                                                     HttpServletRequest request, HttpServletResponse response) {

        long start = System.currentTimeMillis();

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        Vector<CarBizCustomerAppraisalStatisticsDTO> list =  new Vector();
        Set<Integer> driverIds = null;
        Boolean had = false;
        if(teamGroupId!=null || teamId!=null || (permOfTeam!=null && permOfTeam.size()>0)){
            had = true;
            driverIds = carDriverTeamService.selectDriverIdsByTeamIdAndGroupId(teamGroupId, teamId, permOfTeam);
        }
        if(had && (driverIds==null || driverIds.size()==0)){
            logger.info(LOGTAG + "查询teamId={},teamGroupId={},permOfTeam={}没有司机评分信息", teamId, teamGroupId, permOfTeam);
            list =   new Vector();
        }else {

            CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO = new CarBizCustomerAppraisalStatisticsDTO();
            carBizCustomerAppraisalStatisticsDTO.setDriverName(name);
            carBizCustomerAppraisalStatisticsDTO.setDriverPhone(phone);
            carBizCustomerAppraisalStatisticsDTO.setCreateDate(month);
            carBizCustomerAppraisalStatisticsDTO.setCityId(cityId);
            carBizCustomerAppraisalStatisticsDTO.setSupplierId(supplierId);

            //数据权限
            carBizCustomerAppraisalStatisticsDTO.setCityIds(permOfCity);
            carBizCustomerAppraisalStatisticsDTO.setSupplierIds(permOfSupplier);
            carBizCustomerAppraisalStatisticsDTO.setTeamIds(permOfTeam);
            carBizCustomerAppraisalStatisticsDTO.setDriverIds(driverIds);

            int pageSize = 50;

            PageInfo<CarBizCustomerAppraisalStatisticsDTO> pageInfo = customerAppraisalService.queryCustomerAppraisalStatisticsListV2(carBizCustomerAppraisalStatisticsDTO,1
                    ,  pageSize  );
            list.addAll(pageInfo.getList());

            int pages = pageInfo.getPages();

            Hashtable<String,PageInfo<CarBizCustomerAppraisalStatisticsDTO> > hashtable = new Hashtable<>();

            if(pages >= 2){
                CountDownLatch endGate = new CountDownLatch(pages-1);
                //循环加载其他页数据
                for(int i = 2 ;i <= pages ; i++){
                    CustomerAppraisalExportHelper
                             helper = new CustomerAppraisalExportHelper(customerAppraisalService,i,pageSize,hashtable,carBizCustomerAppraisalStatisticsDTO,endGate);
                    helper.start();
                }
                try {
                    logger.info("所有的线程在等待中。。。"+JSON.toJSONString(carBizCustomerAppraisalStatisticsDTO));
                    //主线程阻塞,等待其他所有 worker 线程完成后再执行
                    endGate.await();
                } catch (InterruptedException e) {
                   logger.error("所有的线程在等待中。。异常，"+JSON.toJSONString(carBizCustomerAppraisalStatisticsDTO),e);
                }
                for(int i = 2 ;i <= pages ; i++){
                    PageInfo<CarBizCustomerAppraisalStatisticsDTO> pageInfoX = hashtable.get("page_"+i);
                    if(pageInfoX != null){
                        list.addAll(pageInfoX.getList());
                    }
                }

            }
        }
        try {
            List<String> headerList = new ArrayList<>();
            headerList.add("司机姓名,手机号,评价月份,本月得分,身份证号,车队");

            List<String> csvDataList  = new ArrayList<>(list.size());
            dataTrans(list,csvDataList);

            String fileName = "司机评分"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            CsvUtils.exportCsv(response,csvDataList,headerList,fileName);
            long end = System.currentTimeMillis();
            logger.info("司机评分导出完成，耗时"+(end -start)+"毫秒");


        } catch (Exception e) {
            if(list != null){
                list.clear();
            }
            logger.error("司机信息列表查询导出error",e);

        }
        return null;
    }
    private void dataTrans(List<CarBizCustomerAppraisalStatisticsDTO> list, List<String>  csvDataList ){
        if(null == list){
            return;
        }
        Map<Integer, String> teamMap = null;
        try {
            String driverIds = customerAppraisalService.pingDriverIds(list);
            teamMap = carDriverTeamService.queryDriverTeamListByDriverId(driverIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(CarBizCustomerAppraisalStatisticsDTO s:list){
            StringBuffer stringBuffer = new StringBuffer();


            stringBuffer.append(s.getDriverName());
            stringBuffer.append(",");

            stringBuffer.append(s.getDriverPhone());
            stringBuffer.append(",");

            stringBuffer.append(s.getCreateDate());
            stringBuffer.append(",");

            stringBuffer.append(s.getEvaluateScore());
            stringBuffer.append(",");

            stringBuffer.append(s.getIdCardNo());
            stringBuffer.append(",");

            String teamName = "";
            if(teamMap!=null){
                teamName = teamMap.get(s.getDriverId());
            }
            stringBuffer.append(teamName);

            csvDataList.add(stringBuffer.toString());
        }

    }

    /**
     * 司机评分一个月详情
     * @param driverId 司机ID
     * @param month 月份 yyyy-MM
     * @param orderNo 订单号
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCustomerAppraisalStatisticsDetail")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCustomerAppraisalStatisticsDetail(@Verify(param = "driverId", rule = "required") Integer driverId,
                                                               @Verify(param = "month", rule = "required") String month,
                                                               String orderNo,
                                                               @RequestParam(value="page", defaultValue="0")Integer page,
                                                               @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO = new CarBizCustomerAppraisalDTO();
        carBizCustomerAppraisalDTO.setDriverId(driverId);
        carBizCustomerAppraisalDTO.setOrderNo(orderNo);
        carBizCustomerAppraisalDTO.setCreateDateBegin(month);

        List<CarBizCustomerAppraisalDTO> list = Lists.newArrayList();
        int total = 0;

        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = customerAppraisalService.queryDriverAppraisalDetail(carBizCustomerAppraisalDTO);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }
}