package com.zhuanche.controller.disinfect;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Stopwatch;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.disinfect.DisinfectParamDTO;
import com.zhuanche.dto.disinfect.DisinfectResultDTO;
import com.zhuanche.serv.bigdata.BigDataDriverInfoService;
import com.zhuanche.serv.disinfect.DisinfectService;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 消毒
 *
 * @author admin
 */
@RestController
@Slf4j
@RequestMapping("/disinfect")
public class DisinfectController {
    @Autowired
    private DisinfectService disinfectService;

    @Autowired
    private BigDataDriverInfoService bigDataDriverInfoService;

    private static final String LOGTAG = "[司机消毒信息]: ";

    /**
     * 消毒列表
     *
     * @param disinfectParamDTO
     * @return
     */
    @RequestMapping("/list")
    public AjaxResponse list(@Validated DisinfectParamDTO disinfectParamDTO) {
        PageInfo<DisinfectResultDTO> pageInfo = PageHelper.startPage(disinfectParamDTO.getPage(), disinfectParamDTO.getPagesize(), true).doSelectPageInfo(() ->
                bigDataDriverInfoService.list(disinfectParamDTO));
        PageDTO pageDTO = new PageDTO();
        pageDTO.setResult(pageInfo.getList());
        pageDTO.setTotal(pageInfo.getTotal());
        pageDTO.setPage(pageInfo.getPageNum());
        pageDTO.setPageSize(pageInfo.getPageSize());
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 导出消毒列表
     *
     * @param disinfectParamDTO
     */
    @RequestMapping("/exportDisinfectList")
    public void exportDisinfectList(@Validated DisinfectParamDTO disinfectParamDTO, HttpServletRequest request, HttpServletResponse response) {
        List<String> headerList = new ArrayList<>();
        headerList.add("城市,合作商,司机ID,司机姓名,司机手机号,车牌号,消毒状态,消毒时间");
        Stopwatch watch = Stopwatch.createStarted();
        try {
            String fileName = getFileName(request);
            List<String> csvDataList = new ArrayList<>();
            CsvUtils utilEntity = new CsvUtils();
            PageInfo<DisinfectResultDTO> pageInfo = PageHelper.startPage(1, CsvUtils.downPerSize, true).doSelectPageInfo(() ->
                    bigDataDriverInfoService.list(disinfectParamDTO));
            if (CollectionUtils.isEmpty(pageInfo.getList())) {
                csvDataList.add("没有查到符合条件的数据");
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
                return;
            }
            //临时计算总页数
            int pages = pageInfo.getPages();
            boolean isFirst = true;
            boolean isLast = false;
            if (pages == 1 || pages == 0) {
                isLast = true;
            }
            List<DisinfectResultDTO> rows = pageInfo.getList();
            disinfectService.batchGetBaseStatis(rows, csvDataList);
            utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isLast);
            isFirst = false;
            for (int pageNo = 2; pageNo <= pages; pageNo++) {
                csvDataList = new ArrayList<>();
                pageInfo = PageHelper.startPage(pageNo, CsvUtils.downPerSize, true).doSelectPageInfo(() -> bigDataDriverInfoService.list(disinfectParamDTO));
                if (pageNo == pages) {
                    isLast = true;
                }
                rows = pageInfo.getList();
                disinfectService.batchGetBaseStatis(rows, csvDataList);
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isLast);
            }
            long stop = watch.stop().elapsed(TimeUnit.MILLISECONDS);
            log.info(LOGTAG + "导出成功,参数为:{},耗时:{} ms", JSON.toJSONString(disinfectParamDTO), stop);
        } catch (Exception e) {
            log.error("导出消毒信息exception:", e);
            e.printStackTrace();
        }
    }

    private String getFileName(HttpServletRequest request) throws UnsupportedEncodingException {
        String fileName = "司机消毒信息" + DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern) + ".csv";
        String agent = request.getHeader("User-Agent").toUpperCase();
        if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {  //其他浏览器
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
        }
        return fileName;
    }

}
