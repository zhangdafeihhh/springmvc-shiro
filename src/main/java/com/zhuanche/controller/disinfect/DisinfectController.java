package com.zhuanche.controller.disinfect;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.disinfect.DisinfectParamDTO;
import com.zhuanche.dto.disinfect.DisinfectResultDTO;
import com.zhuanche.serv.bigdata.BigDataDriverInfoService;
import com.zhuanche.serv.disinfect.DisinfectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 消毒列表
     *
     * @param disinfectParamDTO
     * @return
     */
    @RequestMapping("/list")
    public AjaxResponse list(@Validated DisinfectParamDTO disinfectParamDTO) {

        List<DisinfectResultDTO> list = new ArrayList<>();
        DisinfectResultDTO dto = new DisinfectResultDTO();
        dto.setCityId(disinfectParamDTO.getCityId());
        dto.setDisinfectImgUrl("www.baiduc.com");
        list.add(dto);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setResult(list);
        pageDTO.setTotal(100);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 导出消毒列表
     *
     * @param disinfectParamDTO
     */
    @RequestMapping("/exportDisinfectList")
    public void exportDisinfectList(@Validated DisinfectParamDTO disinfectParamDTO) {

    }

}
