package com.zhuanche.controller.carmanage;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.carbizmode.CarBizModelController;
import com.zhuanche.dto.mdbcarmanage.CarDriverFileDto;
import com.zhuanche.entity.driver.DriverVehicle;
import com.zhuanche.entity.mdbcarmanage.CarDriverFile;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.serv.mdbcarmanage.service.CarDriverFileService;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/driverfile")
public class CarDriverFileController {

    private static final Logger logger = LoggerFactory.getLogger(CarDriverFileController.class);

    @Autowired
    private CarDriverFileService carDriverFileService;

    @RequestMapping(value = "/findpage")
    public AjaxResponse findpage(
            @Verify(param = "pageNo", rule = "required|min(1)")Integer pageNo
            , @Verify(param = "pageSize", rule = "required|min(1)")Integer pageSize
            , @Verify(param = "cityId", rule = "required|min(1)")Integer cityId
            , CarDriverFileDto queryParam, HttpServletRequest req
    ) {
        try{
            logger.info("查询司机头像信息异常，参数为：queryParam="+ JSON.toJSONString(queryParam));
            //防止乱码
             String plateNum = req.getParameter("plateNum");
             if(StringUtils.isNotEmpty(plateNum)){
                 String plateNumV1 = new String(plateNum.getBytes("iso-8859-1"), "UTF-8");
                 queryParam.setPlateNum(plateNumV1);
             }
            PageInfo<CarDriverFileDto> pageInfo = carDriverFileService.find4Page(queryParam,pageNo,pageSize);
            PageDTO pageDTO = new PageDTO(pageNo, pageSize, pageInfo.getTotal(), pageInfo.getList());
            pageDTO.setPage(pageNo);
            pageDTO.setPageSize(pageSize);
            pageDTO.setTotal(pageInfo.getTotal());
            pageDTO.setResult(pageInfo.getList());
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            logger.error("查询司机头像信息异常，参数为：queryParam="+ JSON.toJSONString(queryParam),e);
            return AjaxResponse.failMsg(500,"服务端错误");
        }

    }

    @RequestMapping(value = "/exportdriverfile")
    public void exportdriverfile(
            HttpServletRequest request, HttpServletResponse response,
            @Verify(param = "cityId", rule = "required|min(1)")Integer cityId, CarDriverFileDto queryParam
            ,HttpServletRequest req
    ) {
        try{
            logger.info("导出司机头像信息异常，参数为：queryParam="+ JSON.toJSONString(queryParam));
            //防止乱码
            String plateNum = req.getParameter("plateNum");
            if(StringUtils.isNotEmpty(plateNum)){
                String plateNumV1 = new String(plateNum.getBytes("iso-8859-1"), "UTF-8");
                queryParam.setPlateNum(plateNumV1);
            }

            logger.info("导出司机头像信息，参数为：queryParam="+ JSON.toJSONString(queryParam));
            int pageNo = 1;
            int pageSize = 20;
            List<String> headerList = new ArrayList<>();
            headerList.add("序号,城市,司机ID,姓名,手机号,合作商,车牌号,照片提交时间,照片状态");

            PageInfo<CarDriverFileDto> pageInfo = carDriverFileService.find4Page(queryParam,pageNo,pageSize);
            List<String> rowData = new ArrayList<>();
            boolean isFirst = true;
            boolean isLast = false;
            CsvUtils utilEntity = new CsvUtils();
            String fileName = "司机头像信息.csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            if(pageInfo.getTotal() == 0){
                rowData.add("没有查询到符合条件的数据");
                utilEntity.exportCsvV2(response,rowData,headerList,fileName,true,true);
            }else{
                int pages = pageInfo.getPages();
                if(pages == 1){
                    List list = pageInfo.getList();
                    if(list == null || list.size() == 0){
                        rowData.add("没有查询到符合条件的数据");
                    }else{
                        transData2List(list,rowData,0);

                    }
                    utilEntity.exportCsvV2(response,rowData,headerList,fileName,true,true);
                }else{
                    int start = 0;
                    for(pageNo= 1;pageNo<=pages;pageNo ++){
                        if(pageNo != 1){
                            isFirst = false;
                        }
                        if(pageNo == pages){
                            isLast = true;
                        }
                        pageInfo = carDriverFileService.find4Page(queryParam,pageNo,pageSize);
                        List list = pageInfo.getList();
                        rowData.clear();
                        transData2List(list,rowData,start);
                        utilEntity.exportCsvV2(response,rowData,headerList,fileName,isFirst,isLast);
                        //序号自增
                        start = list.size();
                    }
                }
            }
        }catch (Exception e){
            logger.error("导出司机头像信息异常，参数为：queryParam="+ JSON.toJSONString(queryParam),e);

        }

    }
    private void transData2List(List<CarDriverFile> list,List<String> rowData,int start){
        if(list == null || list.size() == 0){
            return ;
        }
//        String headerStr = "序号,城市,司机ID,姓名,手机号,合作商,车牌号,照片提交时间,照片状态";
        int localStart = start;
        for(CarDriverFile item :list){
            rowData.add(
                    (localStart+1)+","
                    + CsvUtils.renderString(item.getCityName())+","
                   + CsvUtils.renderInteger(item.getDriverId())+","
                    + CsvUtils.renderString(item.getDriverName())+","
                   + CsvUtils.renderString(item.getDriverPhone())+","
                   + CsvUtils.renderString(item.getSupplierName())+","
                   + CsvUtils.renderString(item.getPlateNum())+","
                   + CsvUtils.renderDateTime(item.getCreateTime())+","
                            + statusTrans(item.getStatus())
            );
            localStart ++;
        }
    }

//    0-无效,1-审核通过,2-待审核,3-审核未通过)',
    private String statusTrans(Byte status){
        if(status == null){
            return "";
        }else if(status == 0){
            return "无效";
        }else if(status == 1){
            return "审核通过";
        }else if(status == 2){
            return "待审核";
        }else if(status == 3){
            return "审核未通过";
        }else {
            return "";
        }
    }
}
