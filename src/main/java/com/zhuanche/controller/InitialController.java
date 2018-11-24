package com.zhuanche.controller;

import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class InitialController {


    private static final Logger logger = LoggerFactory.getLogger(InitialController.class);

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @RequestMapping("/updateLevel")
    @ResponseBody
    public AjaxResponse initialUserLevel() {
        long startTime = System.currentTimeMillis();
        List<CarAdmUser> users = carAdmUserExMapper.queryAllUser();
        users.forEach(user -> {
            if (StringUtils.isNotBlank(user.getTeamId())) {
                user.setLevel(PermissionLevelEnum.TEAM.getCode());
            } else if (StringUtils.isNotBlank(user.getGroupIds())) {
                user.setLevel(PermissionLevelEnum.GROUP.getCode());
            } else if (StringUtils.isNotBlank(user.getSuppliers())) {
                user.setLevel(PermissionLevelEnum.SUPPLIER.getCode());
            } else if (StringUtils.isNotBlank(user.getCities())) {
                user.setLevel(PermissionLevelEnum.CITY.getCode());
            } else {
                user.setLevel(PermissionLevelEnum.ALL.getCode());
            }
        });
        carAdmUserExMapper.updateUserList(users);
        logger.info("initial data cost time" +  (System.currentTimeMillis() - startTime));
        return AjaxResponse.success("success");
    }
}
