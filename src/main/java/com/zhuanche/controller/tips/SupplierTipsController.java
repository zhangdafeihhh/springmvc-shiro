package com.zhuanche.controller.tips;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.mdbcarmanage.CarBizSupplierTipsDetail;
import com.zhuanche.entity.mdbcarmanage.CarBizTipsDoc;
import com.zhuanche.exception.MessageException;
import com.zhuanche.serv.tips.SupplierTipsService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.HtmlFilterUtil;
import mapper.mdbcarmanage.ex.CarBizTipsDocExMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * @Author fanht
 * @Description 供应商小贴士
 * @Date 2019/3/24 下午2:14
 * @Version 1.0
 */
@Controller
@RequestMapping("/supplier-tips")
public class SupplierTipsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SupplierTipsService tipsService;

    @Autowired
    private CarBizTipsDocExMapper docExMapper;



    @RequestMapping(value = "/createTips",method = RequestMethod.POST)
    @RequiresPermissions(value = {"SupplierTipsCreate"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse createTips(@RequestParam(value = "tipsTitle",required = false)String tipsTitle,
                                   @RequestParam(value = "tipsContent",required = false)String tipsContent,
                                   @RequestParam(value = "file",required = false) MultipartFile file,
                                   HttpServletRequest request){
        logger.info(MessageFormat.format("createTips入参：tipsTitle:{0},tipsContent:{1}",tipsTitle,tipsContent));

        if(StringUtils.isEmpty(tipsContent) || StringUtils.isEmpty(tipsTitle)){
            logger.info("createTips参数缺失.");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        String  clearCss = HtmlFilterUtil.HTMLTagSpirit(tipsContent);
        if (clearCss.length() > 2000){
            logger.info("字体长度大于2000");
            return AjaxResponse.fail(RestErrorCode.TIPS_CONTENT_MAX_LIMIT);
        }
        if (tipsContent.length() - clearCss.length() > 4000-clearCss.length()){
            logger.info("样式长度过于复杂");
            return AjaxResponse.fail(RestErrorCode.TIPS_CONTENT_COMPEX);
        }

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        Integer userId = loginUser.getId();

        AjaxResponse response = AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);

        try {
            int code = tipsService.createTips(null,tipsTitle,tipsContent,userId,file,request);

            if(code > 0){
                logger.info("小贴士添加成功");
                response = AjaxResponse.success(null);
            }else {
                logger.info("小贴士添加失败");
            }
        } catch (MessageException e) {
            logger.info("小贴士添加异常" + e.getMessage());
        }

        return response;
    }



    @RequestMapping(value = "/editTips",method = RequestMethod.POST)
    @RequiresPermissions(value = {"SupplierTipsEdit"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse editTips(@RequestParam(value = "id")Integer id,
                                   @RequestParam(value = "tipsTitle",required = false)String tipsTitle,
                                   @RequestParam(value = "tipsContent",required = false)String tipsContent,
                                   @RequestParam(value = "file",required = false) MultipartFile file,
                                   HttpServletRequest request){
        logger.info(MessageFormat.format("editTips入参：tipsTitle:{0},tipsContent:{1},id:{2}",tipsTitle,tipsContent,id));

        if(id == null ||  StringUtils.isEmpty(tipsContent) || StringUtils.isEmpty(tipsTitle)){
            logger.info("editTips参数缺失.");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        String  clearCss = HtmlFilterUtil.HTMLTagSpirit(tipsContent);
        if (clearCss.length() > 2000){
            logger.info("字体长度大于2000");
            return AjaxResponse.fail(RestErrorCode.TIPS_CONTENT_MAX_LIMIT);
        }
        if (tipsContent.length() - clearCss.length() > 4000-clearCss.length()){
            logger.info("样式长度过于复杂");
            return AjaxResponse.fail(RestErrorCode.TIPS_CONTENT_COMPEX);
        }

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        Integer userId = loginUser.getId();

        AjaxResponse response = AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);

        try {
            int code = tipsService.createTips(id,tipsTitle,tipsContent,userId,file,request);

            if(code > 0){
                logger.info("小贴士修改成功");
                response = AjaxResponse.success(null);
            }else {
                logger.info("小贴士修改失败,ID:" + id);
                response = AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }
        } catch (MessageException e) {
            logger.info("小贴士修改异常" + e.getMessage());
        }

        return response;
    }



    @RequestMapping(value = "/deleteTips",method = RequestMethod.POST)
    @RequiresPermissions(value = {"SupplierTipsDel"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse deleteTips(@RequestParam(value = "id")Integer id){
        logger.info(MessageFormat.format("deleteTips入参：id:{0}",id));

        if(id == null){
            logger.info("deleteTips参数缺失.");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }

        AjaxResponse response = AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);

        try {
            int code = tipsService.deleteTipsById(id);

            if(code > 0){
                logger.info("小贴士删除成功");
                response = AjaxResponse.success(null);
            }else {
                logger.info("小贴士删除失败");
            }
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }

        return response;
    }



    @RequestMapping(value = "/listTips")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse listTips(@RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                 @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum){
        logger.info(MessageFormat.format("listTips入参：pageSize:{0},pageNum:{1}",pageSize,pageNum));

        AjaxResponse response = AjaxResponse.success(null);

        try {
            PageDTO pageDTO = tipsService.supplierlistDto(pageNum,pageSize);

            response.setData(pageDTO);
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }

        return response;
    }


    @RequestMapping(value = "/detailTips")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse detailTips(@RequestParam(value = "id")Integer id){
        logger.info(MessageFormat.format("detailTips入参：id:{0}",id));

        AjaxResponse response = AjaxResponse.success(null);

        try {
            CarBizSupplierTipsDetail detail = tipsService.tipsDetail(id);

            response.setData(detail);
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }

        return response;
    }


    @RequestMapping(value = "/readCountTips",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse readCountTips(@RequestParam(value = "id")Integer id){
        logger.info(MessageFormat.format("detailTips入参：id:{0}",id));

        AjaxResponse response = AjaxResponse.success(null);

        try {
            int code  = tipsService.addReadCount(id);

            if(code > 0){
                logger.info("阅读次数添加成功，ID：" + id);
            }else {
                logger.info("阅读次数添加失败：ID:" + id);
            }
            return response;
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }
    }




    @RequestMapping(value="/downLoadTips")
    public ResponseEntity<byte[]> download(@RequestParam("tipsDocId") Integer tipsDocId)
            throws Exception {
        //下载文件路径
        try {
            CarBizTipsDoc doc = docExMapper.selectByPrimaryKey(tipsDocId);
            if(doc != null){
                String fileUrl = doc.getDocUrl();
                String fileName = doc.getDocName();
                if(StringUtils.isEmpty(fileName)){
                    fileName = UUID.randomUUID().toString();
                }
                String path = "";  //服务器
                //String path = "/Users/fan/upload";  //本地
                logger.info("path:" + path);
                File file = new File(path + File.separator + fileUrl);
                HttpHeaders headers = new HttpHeaders();
                //下载显示的文件名，解决中文名称乱码问题
                String downloadFielName = new String(fileName.getBytes("UTF-8"));
                String docName = downloadFielName.substring(downloadFielName.lastIndexOf(File.separator)+1);
                logger.info(docName);
                headers.add("Content-Disposition", "attchement;filename="+ URLEncoder.encode(fileName,"UTF-8"));

                //通知浏览器以attachment（下载方式）打开图片
                //headers.setContentDispositionFormData("attachment", docName);
                //application/octet-stream ： 二进制流数据（最常见的文件下载）。
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                        headers, HttpStatus.CREATED);
            }

        } catch (IOException e) {
            logger.info("下载错误：" + e.getMessage());
        }
        return null;
    }


    @RequestMapping(value = "/searchTips")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse searchTips(@RequestParam(value = "keyword",required = false)String keyword,
                                   @RequestParam(value = "range",required = false)String range,
                                   @RequestParam(value = "timeRange",required = false)String timeRange,
                                   @RequestParam(value = "userName",required = false)String userName,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                   @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum){
        logger.info(MessageFormat.format("searchTips入参：keyword:{0},range:{1},timeRange:{2},userName:{3}",keyword,
                range,timeRange,userName));

        AjaxResponse response = AjaxResponse.success(null);

        try {
            if (StringUtils.isEmpty(range)){
                range = Constants.ALL_RANGE;
            }else {
                if (!range.equals(Constants.TITLE) && !range.equals(Constants.ATTACHMENT) &&
                        !range.equals(Constants.ALL_RANGE) && !range.equals(Constants.ALL_RANGE_REVERSE)){
                    return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR, "range is invalid");
                }
            }
            String startDate = null;
            String endDate = null;
            if (StringUtils.isNotEmpty(timeRange)){
                String[] split = timeRange.split(Constants.TILDE);
                startDate = split[0];
                endDate = split[1];
            }

            PageDTO pageDTO = tipsService.tipsSearch(range, keyword,
                    startDate, endDate, userName, pageSize, pageNum);
            response.setData(pageDTO);
            return response;
        } catch (MessageException e) {
            return AjaxResponse.fail(e.getCode());
        }
    }

}
