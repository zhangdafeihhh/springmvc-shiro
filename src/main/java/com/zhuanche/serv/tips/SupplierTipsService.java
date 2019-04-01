package com.zhuanche.serv.tips;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.mdbcarmanage.*;
import com.zhuanche.exception.MessageException;
import com.zhuanche.util.Common;
import com.zhuanche.util.dateUtil.DateUtil;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.CarBizSupplierTipsDtoExMapper;
import mapper.mdbcarmanage.ex.CarBizSupplierTipsExMapper;
import mapper.mdbcarmanage.ex.CarBizTipsDocExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * @Author fanht
 * @Description
 * @Date 2019/3/24 下午2:32
 * @Version 1.0
 */
@Service
public class SupplierTipsService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CarBizSupplierTipsExMapper supplierTipsExMapper;

    @Autowired
    private CarBizTipsDocExMapper docExMapper;

    @Autowired
    private CarBizSupplierTipsDtoExMapper dtoExMapper;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Autowired
    private CarAdmUserExMapper admUserExMapper;

    /**
     * 添加
     * @param id
     * @param tipsTitle
     * @param tipsContent
     * @param userId
     * @param file
     * @param docIdList   原来的文档idList
     * @param request
     * @return
     * @throws MessageException
     */
    @Transactional
    public int createTips(Integer id,
                          String tipsTitle,
                          String tipsContent,
                          Integer userId,
                          MultipartFile file,String docIdList,
                          HttpServletRequest request) throws MessageException{


        CarBizSupplierTips supplierTips = new CarBizSupplierTips();
        supplierTips.setTipsTitle(tipsTitle);
        supplierTips.setTipsContent(tipsContent);
        supplierTips.setUserId(userId);
        int tipsId = 0;
        boolean isUpdate = false;
        if(id == null){
           int code =  supplierTipsExMapper.createTips(supplierTips);
           if(code > 0){
               tipsId  =  supplierTips.getId();
           }
        }else {
            supplierTips.setId(id);
            int code =  supplierTipsExMapper.updateTips(supplierTips);
            if(code > 0){
                tipsId = id;
                isUpdate = true;
            }
        }
        if(tipsId > 0){

          logger.info("小贴士主表数据成功");

          if(file != null && !file.isEmpty()){

              List<CarBizTipsDoc> tipsDocList = null;
              List<Integer> docIds = new ArrayList();
              //获取上次文档====
              if(isUpdate){
                  tipsDocList = docExMapper.listTipsDoc(Long.valueOf(tipsId));
                  if(StringUtils.isNotEmpty(docIdList)){
                      String[] oldDocId = docIdList.split(Constants.SEPERATER);
                      List<String> strDocId = Arrays.asList(oldDocId);
                      for(CarBizTipsDoc carBizTipsDoc : tipsDocList){

                          Integer doc = carBizTipsDoc.getId();
                          if(strDocId.contains(doc.toString())){
                              continue;
                          }
                          docIds.add(doc);
                      }
                  }else {
                      for(CarBizTipsDoc carBizTipsDoc : tipsDocList){

                          docIds.add(carBizTipsDoc.getId());
                      }
                  }
              }

              MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;

              MultiValueMap<String, MultipartFile> multiFileMap = req.getMultiFileMap();

              //多文件上传
              MultipartFile multipartFile;
              for (Map.Entry<String, List<MultipartFile>> entry: multiFileMap.entrySet()){

                  //
                  List<MultipartFile> list = entry.getValue();
                  for (MultipartFile fileDetail : list) {
                      multipartFile = fileDetail;
                      Map<String, Object> mapResult = this.fileUpload(multipartFile);
                      Boolean ok = (Boolean) mapResult.get("ok");
                      if (ok == null || !ok) {
                          logger.error("供应商小贴士-上传附件-异常");

                      } else {
                          CarBizTipsDoc doc = new CarBizTipsDoc();
                          doc.setDocName(mapResult.get("fileName").toString());
                          doc.setCreateTime(new Date());
                          doc.setTipsId(tipsId);
                          doc.setUpdateTime(new Date());
                          doc.setDocUrl(mapResult.get("fileUrl").toString());
                          // doc.setDocUrl(FtpConstants.FTP+FtpConstants.FTPURL+":"+FtpConstants.FTPPORT + result.get("oppositeUrl").toString());
                          int code = docExMapper.insertTipsDoc(doc);
                          if (code > 0) {
                              logger.info("====doc文档上传成功====");

                              if (CollectionUtils.isNotEmpty(docIds)) {
                                  Iterator<Integer> iterator = docIds.iterator();
                                  while (iterator.hasNext()) {
                                      Integer docDelId = (Integer) iterator.next();
                                      logger.info("删除上次上传文档" + docDelId);
                                      docExMapper.deleteByDocId(docDelId);
                                  }
                                  docIds.clear();
                              }
                          } else {
                              logger.info("====doc上传文档失败======");
                          }

                      }
                  }

              }
          }
        }
        return tipsId;
    }


    /**
     * 文件服务器上传
     * @param file
     * @return
     */
    public Map<String,Object> fileUpload(MultipartFile file){
        Map<String,Object> map = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String uuid = UUID.randomUUID().toString();
                String timeStamp = System.currentTimeMillis() + "";
                // 文件存放服务端的位置
                String rootPath = this.getRemoteFileDir();
                File filePath = new File(rootPath);

                logger.info("文件路径:" +rootPath);
                if (!filePath.exists())
                    filePath.mkdirs();
                // 写文件到服务器
                String  absoluteUrl = rootPath + uuid + "_" + timeStamp + "." + extension;
                File serverFile = new File(absoluteUrl);
                file.transferTo(serverFile);
                logger.info("消息附件上传地址：" + absoluteUrl);
                map.put("ok",true);
                map.put("fileUrl",absoluteUrl);
                map.put("fileName",file.getOriginalFilename());
            } catch (Exception e) {
                logger.info("文件上传失败");
            }
        } else {
            logger.info("文件上传失败");
        }
        return map;
    }

    /**
     * 注意月份
     * @return
     */
    private String getRemoteFileDir() {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator).append("u01").append(File.separator).append("upload").append(File.separator).append("supplierTips")
                .append(File.separator).append(now.get(Calendar.YEAR)).append(File.separator).append(now.get(Calendar.MONTH)+1)
                .append(File.separator);
        return sb.toString();
    }


    /**
     * 删除
     * @param id
     * @return
     * @throws MessageException
     */
    public  int deleteTipsById(Integer id) throws MessageException{
        try {
            int code  =  supplierTipsExMapper.deleteTipsById(id);
            if(code > 0 ){
                 docExMapper.deleteAllDocByTipsId(id);
            }
            return 1;
        } catch (Exception e) {
            logger.info("删除异常" + e.getMessage());
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }


    public PageDTO supplierlistDto(Integer pageNum,Integer pageSize) throws MessageException{

        try {
            Page page = PageHelper.startPage(pageNum,pageSize,true);

            List<CarBizSupplierTipsDto> dtoList = dtoExMapper.listTips();

            int total = (int)page.getTotal();

            PageHelper.clearPage();

            PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, dtoList);

            return pageDTO;
        } catch (Exception e) {
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));

        }
    }


    public CarBizSupplierTipsDetail tipsDetail(Integer id) throws MessageException{

        CarBizSupplierTipsDetail detail = null;
        try {
            CarBizSupplierTips tips = supplierTipsExMapper.selectByPrimaryKey(id);

            if(tips != null && tips.getId() > 0){
                detail  = new CarBizSupplierTipsDetail(tips);
                //获取创建人
                List<Integer> createUser = new ArrayList<>();
                createUser.add(tips.getUserId());
                List<CarAdmUser> createrList = carAdmUserExMapper.queryUsers(createUser,null,null,null,null);
                if (CollectionUtils.isNotEmpty(createrList)){
                    detail.setUserName(createrList.get(0).getUserName());
                }

                List<TipsDocDto> tipsDocDtoList = new ArrayList<>();
                List<CarBizTipsDoc> tipsDocList = docExMapper.listTipsDoc(Long.valueOf(id));

                if(CollectionUtils.isNotEmpty(tipsDocList)){
                    for(CarBizTipsDoc doc : tipsDocList){
                        TipsDocDto docDto = new TipsDocDto(doc.getId(),doc.getDocName(),doc.getDocUrl());
                        tipsDocDtoList.add(docDto);
                    }
                }
                detail.setCarBizTipsDocList(tipsDocDtoList);
            }
            return detail;
        } catch (Exception e) {
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));

        }
    }

    public int  addReadCount(Integer id) throws MessageException{

        try {
            return supplierTipsExMapper.addReadCount(id);
        } catch (Exception e) {
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));

        }
    }


    public PageDTO tipsSearch(String range,String keyword,
                             String startDate,String endDate,String userName,Integer pageSize,Integer pageNum){

        List<Integer> idList = null;
        if (StringUtils.isNotBlank(userName)){
            idList = admUserExMapper.queryIdListByName(userName);
        }

        List<CarBizSupplierTipsDto> listDto = new ArrayList<>();

        if(StringUtils.isNotEmpty(userName) &&  CollectionUtils.isEmpty(idList)){
            return new PageDTO(pageNum,pageSize,0,listDto);
        }

        String[] arrRange = range.split(Constants.SEPERATER);
        String start = startDate == null ? null : DateUtil.parseDate(startDate, Constants.DATE_FORMAT).toString();
        String end = endDate;
        if(endDate != null){
            DateUtil.getDayAfterDate(DateUtil.parseDate(endDate, Constants.DATE_FORMAT));
        }
        Page page = PageHelper.startPage(pageNum,pageSize,true);


        if(arrRange.length >1){
            listDto = dtoExMapper.searchTipsAndDoc(keyword,start,end,idList);
        }else {
            String ran = arrRange[0];
            if(ran.equals(Constants.TITLE)){
                listDto = dtoExMapper.searchTips(keyword,start,end,idList);
            }else if(ran.equals(Constants.ATTACHMENT)){
                listDto = dtoExMapper.searchDoc(keyword,start,end,idList);
            }
        }
        int total = (int)page.getTotal();

        return new PageDTO(pageNum,pageSize,total,listDto);

    }

}
