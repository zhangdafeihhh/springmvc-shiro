package com.zhuanche.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.dingdingsync.DingdingAnno;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.entity.rentcar.CarBizSupplierQuery;
import com.zhuanche.entity.rentcar.CarBizSupplierVo;
import com.zhuanche.serv.CarBizCooperationTypeService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.*;

@RequestMapping("/supplier")
@Controller
public class SupplierController {

    @Resource
    private CarBizSupplierService supplierService;

    @Resource
    private CarBizCooperationTypeService cooperationTypeService;

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    @RequestMapping("/datalist")
    @ResponseBody
    @RequestFunction(menu = SUPPLIER_LIST)
    public AjaxResponse getSupplierDataList(String supplierFullName, Integer supplierCity,
                                            Integer status, Integer cooperationType, Integer enterpriseType,
                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "30") Integer pageSize){
        CarBizSupplierQuery queryParam = new CarBizSupplierQuery();
        if (supplierCity == null){
            Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
            queryParam.setCityIds(cityIds);
        }else {
            Set<Integer> cityIds = new HashSet<>();
            cityIds.add(supplierCity);
            queryParam.setCityIds(cityIds);
        }
        Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
        queryParam.setSupplierIds(supplierIds);
        queryParam.setSupplierFullName(supplierFullName);
        queryParam.setSupplierCity(supplierCity);
        queryParam.setCooperationType(cooperationType);
        queryParam.setEnterpriseType(enterpriseType);
        queryParam.setStatus(status);
        List<CarBizSupplierVo> list;
        logger.info("????????????????????? ??????:{}", JSON.toJSONString(queryParam));
        int total;
        Page p = PageHelper.startPage(pageNum, pageSize, true);
        try {
            list = supplierService.findSupplierListByPage(queryParam);
            supplierService.addExtInfo(list);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    @RequestMapping("/cooperationType")
    @ResponseBody
    @RequestFunction(menu = SUPPLIER_COOPERATION_TYPE)
    public AjaxResponse getCooperationType(){
        List<CarBizCooperationType> carBizCooperationTypes = cooperationTypeService.queryCarBizCooperationTypeList();
        return AjaxResponse.success(carBizCooperationTypes);
    }

    @RequestMapping("/querySupplierById")
    @ResponseBody
    @RequestFunction(menu = SUPPLIER_DETAIL)
    public AjaxResponse getSupplierInfoById(Integer supplierId){
        return supplierService.querySupplierById(supplierId);
    }

    @RequestMapping("/checkSupplierFullName")
    @ResponseBody
    @RequestFunction(menu = SUPPLIER_CHECK_NAME)
    public AjaxResponse checkSupplierFullName(String supplierFullName){
        return supplierService.checkSupplierFullName(supplierFullName);
    }

    @RequestMapping("/exportSupplierList")
    @ResponseBody
    @RequestFunction(menu = SUPPLIER_LIST_EXPORT)
    public void exportSupplierList(String supplierFullName, Integer supplierCity, Integer status,
                                   Integer cooperationType, Integer enterpriseType,
                                   HttpServletRequest request, HttpServletResponse response) {



        long start=System.currentTimeMillis(); //??????????????????

        CarBizSupplierQuery queryParam = new CarBizSupplierQuery();
        if (supplierCity == null){
            Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
            queryParam.setCityIds(cityIds);
        }else {
            Set<Integer> cityIds = new HashSet<>();
            cityIds.add(supplierCity);
            queryParam.setCityIds(cityIds);
        }
        Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
        queryParam.setSupplierIds(supplierIds);
        queryParam.setSupplierFullName(supplierFullName);
        queryParam.setSupplierCity(supplierCity);
        queryParam.setCooperationType(cooperationType);
        queryParam.setEnterpriseType(enterpriseType);
        queryParam.setStatus(status);
        List<CarBizSupplierVo> list;
        logger.info("????????????????????? ??????:{}", JSON.toJSONString(queryParam));

        try {

            List<String> headerList = new ArrayList<>();
            headerList.add("?????????ID, ?????????, ??????, ??????, ?????????, ????????????, ??????, ???????????????, ??????????????????, ????????????, ??????, " +
                    "??????????????????, ???????????????, ??????????????????, ????????????, ??????????????????, ????????????, ????????????, ?????????, ??????, ????????????, " +
                    "???????????????, ??????????????????, ????????????, ?????????, ?????????");
            String fileName = "???????????????" + DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //???????????????????????????????????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE????????????Edge?????????
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            List<String> csvDataList = new ArrayList<>();
            int pageSize = CsvUtils.downPerSize;
            //????????????
            PageInfo pageInfos  = supplierService.querySupplierPage(queryParam,1,pageSize);
            int pages = pageInfos.getPages();//?????????????????????
            logger.info("???????????????:???"+1+"???/???"+pages+"????????????????????????"+JSON.toJSONString(queryParam));
            List<CarBizSupplierVo> rows = pageInfos.getList();
            if(rows == null || rows.size() == 0){
                csvDataList.add("?????????????????????????????????");
                CsvUtils entity = new CsvUtils();
                entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
                return;
            }

            boolean isFirst = true;
            boolean isLast = false;
            if(pages == 1){
                isLast = true;
            }
            CsvUtils entity = new CsvUtils();

            supplierService.dataTrans( rows,csvDataList);
            entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
            isFirst = false;
            for(int pageNo = 2;pageNo <= pages ; pageNo++){
                logger.info("???????????????:???"+pageNo+"???/???"+pages+"????????????????????????"+JSON.toJSONString(queryParam));
                pageInfos = supplierService.querySupplierPage(queryParam, pageNo, pageSize);
                csvDataList = new ArrayList<>();
                if(pageNo == pages){
                    isLast = true;
                }
                rows =  pageInfos.getList();
                supplierService.dataTrans( rows,csvDataList);

                entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
            }

        } catch (Exception e) {
            logger.error("???????????????:??????????????????????????????"+(JSON.toJSONString(queryParam)),e);
            return ;
        }
        long  end = System.currentTimeMillis();
        logger.info("???????????????:?????????"+(end-start)+"??????");
        return;
    }
}
