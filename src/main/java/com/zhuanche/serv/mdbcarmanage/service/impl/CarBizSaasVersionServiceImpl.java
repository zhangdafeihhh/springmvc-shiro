package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.exception.ExceptionCode;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersionDetail;
import com.zhuanche.serv.mdbcarmanage.service.CarBizSaasVersionService;
import mapper.mdbcarmanage.ex.CarBizSaasVersionDetailExMapper;
import mapper.mdbcarmanage.ex.CarBizSaasVersionExMapper;
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
 * @Author: nysspring@163.com
 * @Description: saas系统版本更新记录服务，包含更新记录及附件信息的基础服务及部分业务服务
 * @Date: 18:59 2019/5/13
 */
@Service
public class CarBizSaasVersionServiceImpl implements CarBizSaasVersionService{

    private Logger LOGGER = LoggerFactory.getLogger(CarBizSaasVersionServiceImpl.class);

    @Autowired
    private CarBizSaasVersionExMapper carBizSaasVersionExMapper;

    @Autowired
    private CarBizSaasVersionDetailExMapper carBizSaasVersionDetailExMapper;


    @Override
    public int saveCarBizSaasVersion(CarBizSaasVersion record) {
        return carBizSaasVersionExMapper.insertSelective(record);
    }

    @Transactional
    @Override
    public Boolean createSaasVersionRecord(CarBizSaasVersion record, MultipartFile file, String detailIds, HttpServletRequest request) throws ServiceException {
        Boolean isUpdate = Boolean.FALSE;
        if(record.getId() != null){
            isUpdate = Boolean.TRUE;
        }
        LOGGER.info("新建或更新版本记录及附件  record={},isUpdate={}",JSON.toJSONString(record),isUpdate);
        try {
            if (!isUpdate) {
                //新建
                int i = carBizSaasVersionExMapper.insertSelective(record);
                LOGGER.info("版本记录新增成功 record={},i={}", JSON.toJSONString(record), i);
            } else {
                //更新
                int i = carBizSaasVersionExMapper.updateByPrimaryKeySelective(record);
                LOGGER.info("版本记录更新成功 record={},i={}", JSON.toJSONString(record), i);
            }
        }catch (Exception e){
            LOGGER.error("版本记录新建或更新时操作数据库异常 e={}",e);
            throw new ServiceException(ExceptionCode.DB_OPTION_ERROR.getIndex(),ExceptionCode.DB_OPTION_ERROR.getErrMsg());
        }
        //到这里可以认为版本记录表已经操作成功 以下逻辑操作详情表附件等
        List<CarBizSaasVersionDetail> detailList = null;
        List<Integer> detailIdListForDelete = new ArrayList<>();

        try {
            //如果是更新操作  不管有无新的附件  老附件只有一种可能删除或者不动   筛选要删除的id集合
            if (isUpdate) {
                detailList = carBizSaasVersionDetailExMapper.listCarBizSaasVersionDetail(record.getId());//库中数据
                if (StringUtils.isNotBlank(detailIds)) {
                    //当前情况detailIds不为空则库中必定有数据  此时不判空  若为空则系统错误
                    List<String> paramDetailIdList = Arrays.asList(detailIds.split(Constants.SEPERATER));
                    LOGGER.info("更新操作，无新增附件，要更新的附件paramDetailIdList={}", paramDetailIdList);
                    for (CarBizSaasVersionDetail carBizSaasVersionDetail : detailList) {
                        if (paramDetailIdList.contains(carBizSaasVersionDetail.getId().toString())) {
                            continue;
                        }
                        detailIdListForDelete.add(carBizSaasVersionDetail.getId());//要删除的id集合
                    }
                } else {
                    if (detailList != null && !detailList.isEmpty()) {
                        for (CarBizSaasVersionDetail carBizSaasVersionDetail : detailList) {
                            detailIdListForDelete.add(carBizSaasVersionDetail.getId());//要删除的id集合
                        }
                    }
                }
            }
            LOGGER.info("detailIdListForDelete={}",detailIdListForDelete);

            if (file == null || file.isEmpty()) {
                //没有附件  此时若是新建  没有附件  则无需操作  只有更新的情况下需要处理
                //批量删除  可调整为逻辑删除 历史都是物理删除 此处暂物理删除
                if (detailIdListForDelete != null && !detailIdListForDelete.isEmpty()) {
                    //批量删除 一次连接
                    int i = carBizSaasVersionDetailExMapper.deleteBatch(detailIdListForDelete);
                    LOGGER.info("批量删除条数 i={}", i);
                }
            } else {
                LOGGER.info("***********上传版本记录附件开始***********");
                //有附件  分新增与更新  新增直接保存附件  更新的话  两种情况  1新增了附件  2新增了部分，删除了部分
                //更新操作要删除的附件id集合已经提取，直接删除即可  新增操作直接上传完后保存库中路径即可
                MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
                MultiValueMap<String, MultipartFile> multiFileMap = req.getMultiFileMap();
                //多文件上传
                MultipartFile multipartFile;
                for (Map.Entry<String, List<MultipartFile>> entry : multiFileMap.entrySet()) {
                    List<MultipartFile> list = entry.getValue();
                    for (MultipartFile fileDetail : list) {
                        multipartFile = fileDetail;
                        Map<String, Object> mapResult = this.fileUpload(multipartFile);
                        Boolean ok = (Boolean) mapResult.get("ok");
                        if (ok == null || !ok) {
                            LOGGER.error("版本更新记录-上传附件-异常 record={}", JSON.toJSONString(record));
                        } else {
                            //上传成功后落库相关数据
                            CarBizSaasVersionDetail carBizSaasVersionDetail = new CarBizSaasVersionDetail();
                            carBizSaasVersionDetail.setDetailName(mapResult.get("fileName").toString());
                            carBizSaasVersionDetail.setDetailUrl(mapResult.get("fileUrl").toString());
                            carBizSaasVersionDetail.setVersionId(record.getId());//新增时使用主键返回这里也是可以获取到的
                            int i = carBizSaasVersionDetailExMapper.insertSelective(carBizSaasVersionDetail);
                        }
                    }
                    //删除要删除的数据
                    if (detailIdListForDelete != null && !detailIdListForDelete.isEmpty()) {
                        int j = carBizSaasVersionDetailExMapper.deleteBatch(detailIdListForDelete);
                        LOGGER.info("批量删除条数 j={}", j);
                    }
                }
                LOGGER.info("***********上传版本记录附件结束***********");
            }
        }catch (Exception e){
            LOGGER.info("附件操作异常 e={}",e);
            throw new ServiceException(ExceptionCode.VERSION_DETAIL_OPTION_ERROR.getIndex(),ExceptionCode.VERSION_DETAIL_OPTION_ERROR.getErrMsg());
        }

        return Boolean.TRUE;
    }

    @Override
    public PageDTO listVersion(Integer pageNum, Integer pageSize) {
        Page page = PageHelper.startPage(pageNum, pageSize, true);
        List<CarBizSaasVersion> carBizSaasVersionList = carBizSaasVersionExMapper.listVersion();
        int total = (int)page.getTotal();
        PageHelper.clearPage();
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, carBizSaasVersionList);
        return pageDTO;
    }

    @Transactional
    @Override
    public Boolean deleteVersion(Integer versionId) {
        //删除主表
        int i = carBizSaasVersionExMapper.deleteByPrimaryKey(versionId);
        LOGGER.info("删除Version记录  id={}",versionId);
        //删除附件表  不一定有附件  所以j可以为0
        int j = carBizSaasVersionDetailExMapper.deleteByVersionId(versionId);
        LOGGER.info("删除version记录及附件表信息i={},j={}",i,j);
        return Boolean.TRUE;
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

                LOGGER.info("文件路径:" +rootPath);
                if (!filePath.exists())
                    filePath.mkdirs();
                // 写文件到服务器
                String  absoluteUrl = rootPath + uuid + "_" + timeStamp + "." + extension;
                File serverFile = new File(absoluteUrl);
                file.transferTo(serverFile);
                LOGGER.info("消息附件上传地址：" + absoluteUrl);
                map.put("ok",true);
                map.put("fileUrl",absoluteUrl);
                map.put("fileName",file.getOriginalFilename());
            } catch (Exception e) {
                LOGGER.info("文件上传失败");
            }
        } else {
            LOGGER.info("文件上传失败");
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
        sb.append(File.separator).append("u01").append(File.separator).append("upload").append(File.separator).append("saasVersion")
                .append(File.separator).append(now.get(Calendar.YEAR)).append(File.separator).append(now.get(Calendar.MONTH)+1)
                .append(File.separator);
        return sb.toString();
    }






































}
