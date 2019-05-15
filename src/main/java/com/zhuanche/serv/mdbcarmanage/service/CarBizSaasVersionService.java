package com.zhuanche.serv.mdbcarmanage.service;

import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CarBizSaasVersionService {

    int saveCarBizSaasVersion(CarBizSaasVersion record);


    Boolean createSaasVersionRecord(CarBizSaasVersion record, MultipartFile file, String detailIds, HttpServletRequest request) throws ServiceException;

    PageDTO listVersion(Integer pageNum, Integer pageSize);

    Boolean deleteVersion(Integer versionId);
}
