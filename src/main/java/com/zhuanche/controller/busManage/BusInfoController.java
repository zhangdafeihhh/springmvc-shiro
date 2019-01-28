package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.BusConstant.CarConstant;
import com.zhuanche.constants.busManage.EnumFuel;
import com.zhuanche.dto.busManage.BusCarSaveDTO;
import com.zhuanche.dto.busManage.BusInfoDTO;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.busManage.BusBizChangeLogService;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.serv.busManage.BusInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.excel.ExportExcelUtil;
import com.zhuanche.vo.busManage.BusDetailVO;
import com.zhuanche.vo.busManage.BusInfoVO;
import com.zhuanche.vo.busManage.ErrorReason;
import com.zhuanche.vo.busManage.ImportErrorVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @program: mp-manage
 * @description: 巴士车辆管理
 * @author: niuzilian
 * @create: 2018-11-22 14:22
 **/
@RestController
@RequestMapping("/bus/busInfo")
public class BusInfoController {
    private static Logger logger = LoggerFactory.getLogger(BusInfoController.class);
    private static String LOG_PRE = "【巴士车辆】";
    @Autowired
    private BusInfoService busInfoService;
    @Autowired
    private CarBizCarGroupService groupService;
    @Autowired
    private CarBizSupplierService supplierService;
    @Autowired
    private CarBizCityService cityService;
    @Autowired
    private BusBizChangeLogService busBizChangeLogService;
    @Autowired
    private BusCommonService commonService;


    /**
     * @Description:查询巴士车辆列表
     * @Param: [busDTO]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse queryList(BusInfoDTO busDTO) {
        //从session中获取权限
        busDTO.setAuthOfCity(WebSessionUtil.getCurrentLoginUser().getCityIds());
        busDTO.setAuthOfSupplier(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
        logger.info(LOG_PRE + "查询车辆列表参数=" + JSON.toJSONString(busDTO));
        PageInfo<BusInfoVO> pageInfo = busInfoService.queryList(busDTO);
        return AjaxResponse.success(new PageDTO(busDTO.getPageNum(), busDTO.getPageSize(), Integer.parseInt(pageInfo.getTotal() + ""), pageInfo.getList()));
    }

    /**
     * @Description: 查询车辆详情
     * @Param: [carId]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse getDetail(@Verify(param = "carId", rule = "required") Integer carId) {
        logger.info(LOG_PRE + "查询车辆详情参数carId=" + carId);
        BusDetailVO detail = busInfoService.getDetail(carId);
        if (detail == null) {
            return AjaxResponse.failMsg(RestErrorCode.BUS_NOT_EXIST, "车辆信息不存在");
        }
        String fuelName = EnumFuel.getFuelNameByCode(detail.getFueltype());
        detail.setFuelName(fuelName);
        logger.info(LOG_PRE + "查询车辆详情结果" + JSON.toJSONString(detail));
        return AjaxResponse.success(detail);
    }

    @RequestMapping(value = "/saveCar", method = RequestMethod.POST)
    public AjaxResponse saveCar(@Validated BusCarSaveDTO busCarSaveDTO) {
        if (busCarSaveDTO.getCarId() == null) {
            logger.info(LOG_PRE + "保存车辆信息，参数=" + JSON.toJSONString(busCarSaveDTO));
            return busInfoService.saveCar(busCarSaveDTO);
        } else {
            logger.info(LOG_PRE + "修改车辆信息，参数=" + JSON.toJSONString(busCarSaveDTO));
            return busInfoService.updateCarById(busCarSaveDTO);
        }

    }

    /**
     * @Description: 导出车辆信息
     * @Param: [busDTO, request, response]
     * @return: void
     * @Date: 2018/11/28
     */
    @RequestMapping("/exportBusInfo")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public void exportCarInfo(BusInfoDTO busDTO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //从session中获取权限
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        busDTO.setAuthOfCity(user.getCityIds());
        busDTO.setAuthOfSupplier(user.getSupplierIds());
        //导出信息指定每次查询的页数
        busDTO.setPageSize(CsvUtils.downPerSize);
        logger.info(LOG_PRE + "下载车辆信息参数=" + JSON.toJSONString(busDTO));
        PageInfo<BusInfoVO> pageInfo = busInfoService.queryList(busDTO);
        //文件标题
        List<String> headerList = new ArrayList<>();
        String head = StringUtils.join(CarConstant.TEMPLATE_HEAD, ",");
        headerList.add(head);
        String fileName = CarConstant.FILE_NAME + com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern) + ".csv";
        fileName = BusConstant.buidFileName(request, fileName);
        List<String> csvDataList = new ArrayList<>();
        CsvUtils utilEntity = new CsvUtils();
        long total = pageInfo.getTotal();
        if (total == 0) {
            csvDataList.add("没有符合条件的车辆");
            utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
            return;
        }
        buidCSVdata(pageInfo.getList(), csvDataList);
        //总页数
        int pages = pageInfo.getPages();

        if (pages == 1) {
            utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
            return;
        }
        utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, false);
        for (int i = 2; i <= pages; i++) {
            csvDataList.clear();
            busDTO.setPageNum(i);
            List<BusInfoVO> list = busInfoService.queryList(busDTO).getList();
            buidCSVdata(list, csvDataList);
            if (i == pages) {
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, false, true);
            } else {
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, false, false);
            }
        }
    }

    private void buidCSVdata(List<BusInfoVO> infoVOS, List<String> csvData) {
        for (BusInfoVO info : infoVOS) {
            String split = ",";
            StringBuffer sb = new StringBuffer();
            //        String[] TEMPLATE_HEAD={"城市","供应商","车牌号","车型类别名称","车辆颜色","燃料类别","运输证字号","车辆厂牌","具体车型（选填）","下次车检时间（选填）","下次维保时间（选填）","下次运营证检测时间（选填）","购买时间（选填）"};

            String fuelName = EnumFuel.getFuelNameByCode(info.getFueltype());
            sb.append(info.getCityName()).append(split).append(info.getSupplierName()).append(split).append(info.getLicensePlates()).append(split)
                    .append(info.getGroupName()).append(split).append(StringUtils.defaultString(info.getColor())).append(split).append(StringUtils.defaultString(fuelName)).append(split)
                    .append(StringUtils.defaultString(info.getTransportnumber())).append(split)
                    .append(StringUtils.defaultString(info.getVehicleBrand())).append(split).append(info.getModelDetail()).append(split)
                    .append(info.getNextInspectDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getNextInspectDate())).append(split)
                    .append(info.getNextMaintenanceDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getNextMaintenanceDate())).append(split)
                    .append(info.getNextOperationDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getNextOperationDate())).append(split)
                    .append(info.getCarPurchaseDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getCarPurchaseDate()));
            csvData.add(sb.toString());
        }
    }

    /**
     * @Description: 下载导入模板
     * @Param: [request, response]
     * @return: void
     * @Date: 2018/11/28
     */
    @RequestMapping("/exportTemplate")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = "巴士车辆导入模板";
        List<Map<Object, Object>> maps = commonService.queryGroups();
        String[] groupNames = new String[maps.size()];
        for (int i = 0; i < maps.size(); i++) {
            groupNames[i] = (String) maps.get(i).get("groupName");
        }
        String allFuelName = EnumFuel.getAllFuelName();
        String[] fuelNames = allFuelName.split(",");
        List<String[]> downdata = new ArrayList<>();
        downdata.add(groupNames);
        downdata.add(fuelNames);
        //判断是否是运营角色
        boolean roleBoolean = commonService.ifOperate();
        //文件表头
        String[] head = null;
        //下拉框所在位置
        String[] downRows = new String[2];
        //如果是巴士运营角色,下载的模板包括城市和供应商
        if (roleBoolean) {
            head = CarConstant.TEMPLATE_HEAD;
            downRows[0] = "3";
            downRows[1] = "5";
        } else {
            //非运营角色下载的模板不包括城市和供应商
            head = new String[CarConstant.TEMPLATE_HEAD.length - 2];
            System.arraycopy(CarConstant.TEMPLATE_HEAD, 2, head, 0, CarConstant.TEMPLATE_HEAD.length - 2);
            downRows[0] = "1";
            downRows[1] = "3";
        }
        //表示生成的下拉框在第三列和第五列
        ExportExcelUtil.exportExcel(fileName, head, downdata, downRows, request, response);
    }


    /**
     * @Description: 导入巴士车辆信息
     * @Param: [cityId, supplierId, file]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/28
     */
    @RequestMapping(value = "/importBusInfo", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse importBusInfo(Integer cityId,
                                      Integer supplierId,
                                      MultipartFile file) throws Exception {
        if (file.isEmpty() || cityId == null || supplierId == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "有参数为空");
        }
        String supplierName = supplierService.getSupplierNameById(supplierId);
        if (StringUtils.isBlank(supplierName)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "供应商不存在");
        }
        String cityName = cityService.queryNameById(cityId);
        if (StringUtils.isBlank(cityName)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "城市不存在");
        }

        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        logger.info(LOG_PRE + "上传的文件名为:{},上传的后缀名为:{}", fileName, suffixName);
        InputStream is = file.getInputStream();
        Workbook workbook = null;
        if (suffixName.equals(".xls")) {
            workbook = new HSSFWorkbook(is);
        } else if (suffixName.equals(".xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            logger.error(LOG_PRE + "巴士导入车辆文件后缀名错误");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        //默认值导入第一个sheet;
        Sheet sheet = workbook.getSheetAt(0);
        //判断是否是运营角色
        boolean roleBoolean = commonService.ifOperate();
        //校验模板表头
        Row rowFirst = sheet.getRow(0);

        String[] heads = null;
        if (roleBoolean) {
            heads = CarConstant.TEMPLATE_HEAD;
        } else {
            heads = new String[CarConstant.TEMPLATE_HEAD.length - 2];
            System.arraycopy(CarConstant.TEMPLATE_HEAD, 2, heads, 0, CarConstant.TEMPLATE_HEAD.length - 2);
        }
        if (rowFirst == null || rowFirst.getLastCellNum() != heads.length) {
            logger.error(LOG_PRE + "巴士导入车辆文件表头为空或者长度错误");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        boolean templateFlag = checkTableHead(rowFirst, heads);
        if (!templateFlag) {
            logger.error(LOG_PRE + "巴士导入车辆文件表头内容错误");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        // 过滤掉标题，从第一行开始导入数据
        int start = 1;
        // 要导入数据的总条数
        int total = sheet.getLastRowNum();
        if (total == 0) {
            logger.error(LOG_PRE + "巴士导入车辆文件内容为空");
            return AjaxResponse.fail(RestErrorCode.BUS_NOT_EXIST);
        }
        // 成功导入条数
        int successCount = 0;
        List<ErrorReason> errList = new ArrayList();
        logger.info(LOG_PRE + "巴士导入车辆，总条数=" + total);
        //循环行
        for (int rowIdx = start; rowIdx <= total; rowIdx++) {
            // 获取行对象
            Row row = sheet.getRow(rowIdx);
            if (row == null) {
                saveErrorMsg(errList, rowIdx, 0, "数据为空");
                continue;
            }
            // BusCarInfo carInfo = new BusCarInfo();
            BusCarSaveDTO saveDTO = new BusCarSaveDTO();
            boolean validFlag = true;
            // 循环列
            for (int colIdx = 0; colIdx < row.getLastCellNum(); colIdx++) {
                Cell cell = row.getCell(colIdx);
                String value = readCellValue(cell);
                DateFormat df = new SimpleDateFormat("yyyy-M-d");
                //获取表头信息
                String head = heads[colIdx];
                switch (head) {
                    //城市：
                    case "城市":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        if (!value.equals(cityName)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 城市名称和页面选择的不一致");
                            validFlag = false;
                            break;
                        }
                        break;
                    //供应商：
                    case "供应商":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                        }
                        if (!value.equals(supplierName)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 供应商名称和页面选择的不一致");
                            validFlag = false;
                        }
                        break;
                    //车牌号
                    case "车牌号":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        boolean checkResult = busInfoService.licensePlatesIfExist(value);
                        if (checkResult) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 已经存在");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setLicensePlates(value);
                        break;
                    // 车型类别
                    case "车型类别名称":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        CarBizCarGroup group = groupService.queryGroupByGroupName(value);
                        if (group == null) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 不存在");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setGroupId(group.getGroupId());
                        break;
                    //车辆颜色
                    case "车辆颜色":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setColor(value);
                        break;
                    //燃料类型
                    case "燃料类别":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        String code = EnumFuel.getFuelCodeByName(value);
                        if (code == null) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 不存在");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setFueltype(code);
                        break;
                    //运输证字号
                    case "运输证字号":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setTransportnumber(value);
                        break;
                    //车厂厂牌
                    case "车辆厂牌":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setVehicleBrand(value);
                        break;
                    //具体车型
                    case "具体车型(选填)":
                        saveDTO.setModelDetail(value);
                        break;
                    //下次车检时间
                    case "下次车检时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextInspectDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    //下次维保时间
                    case "下次维保时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextMaintenanceDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    //下次运营检测时间
                    case "下次运营证检测时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextOperationDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    //购买时间
                    case "购买时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setCarPurchaseDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    default:
                        break;
                }// switch end

            }// 列循环结束
            if (validFlag) {
                saveDTO.setCityId(cityId);
                saveDTO.setSupplierId(supplierId);
                AjaxResponse response = busInfoService.saveCar(saveDTO);
                if (response.isSuccess()) {
                    successCount++;
                }
            }
        }
        ImportErrorVO errorVO = new ImportErrorVO(total, successCount, (total - successCount), errList);
        //如果有错误信息将信息放到redis中以便下载
        if (errList.size() > 0) {
            String errMsgKey = BusConstant.ERROR_CAR_KEY + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            saveErrorMsg2Redis(errMsgKey, errList);
            errorVO.setErrorMsgKey(errMsgKey);
        }
        return AjaxResponse.success(errorVO);
    }

    private String transfTime(String data) {
        if (data.contains("/")) {
            data = data.replace("/", "-");
        }
        return data;
    }

    private void saveErrorMsg2Redis(String key, List<ErrorReason> errorList) {
        List<String> array = new ArrayList();
        errorList.forEach(o -> {
            array.add("第" + o.getRow() + "行，" + "第" + o.getCol() + "列  " + o.getReason());
        });
        RedisCacheUtil.set(key, array, BusConstant.ERROR_IMPORT_KEY_EXPIRE);
    }

    /**
     * 获取所有的燃料类型
     *
     * @Param: []
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/12
     */
    @RequestMapping(value = "/queryFuel", method = RequestMethod.GET)
    public AjaxResponse queryFuel() {
        List<Map<String, String>> allFuel = EnumFuel.getAllFuel();
        return AjaxResponse.success(allFuel);
    }


    private String readCellValue(Cell cell) {
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        int cellType = cell.getCellType();
        String value = null;
        switch (cellType) {
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    //  如果是date类型则 ，获取该cell的date值
                    Date val = cell.getDateCellValue();
                    DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                    value = formater.format(val);
                } else { // 纯数字 只保留整数部分
                    DecimalFormat df = new DecimalFormat("########");
                    value = df.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            default:
                value = StringUtils.EMPTY;
                break;
        }
        return value;
    }

    private void saveErrorMsg(List<ErrorReason> reasons, int row, int col, String reason) {
        ErrorReason errorReason = new ErrorReason();
        errorReason.setRow(row);
        errorReason.setCol(col);
        errorReason.setReason(reason);
        reasons.add(errorReason);
    }

    private boolean checkTableHead(Row row, String[] head) {
        boolean templateFlag = true;
        for (int colIdx = 0; colIdx < row.getLastCellNum(); colIdx++) {
            // 获取列对象
            Cell cell = row.getCell(colIdx);
            if (cell == null) {
                templateFlag = false;
                break;
            } else {
                String stringValue = cell.getStringCellValue();
                if (!stringValue.equals(head[colIdx])) {
                    templateFlag = false;
                    break;
                }
            }
        }
        return templateFlag;
    }
}
