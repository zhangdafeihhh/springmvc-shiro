package com.zhuanche.serv.mdbcarmanage.service;

import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersionDetail;
import com.zhuanche.entity.mdbcarmanage.VersionModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface CarBizSaasVersionService {

    int saveCarBizSaasVersion(CarBizSaasVersion record);


    /**
     * 带有附件的版本记录，暂时不用   创建和编辑功能的接口
     * @param record
     * @param file
     * @param detailIds
     * @param request
     * @return
     * @throws ServiceException
     */
    Boolean createSaasVersionRecord(CarBizSaasVersion record, MultipartFile file, String detailIds, HttpServletRequest request) throws ServiceException;

    PageDTO listVersion(Integer pageNum, Integer pageSize);

    Boolean deleteVersion(Integer versionId);

    VersionModel versionDetail(Integer versionId);

    CarBizSaasVersionDetail selectDetailById(Integer detailId);

    String uploadImg(MultipartFile file, HttpServletRequest request,String rootPath);

    Boolean saveOrUpdateVersion(CarBizSaasVersion record);

    CarBizSaasVersion selectVersionById(Integer versionId);

}
