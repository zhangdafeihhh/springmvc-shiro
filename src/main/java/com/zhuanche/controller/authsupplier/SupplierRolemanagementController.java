package com.zhuanche.controller.authsupplier;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.authc.RoleManagementService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zhuanche.common.enums.MenuEnum.ROLE_LIST;

/**
 * @Author fanht
 * @Description
 * @Date 2020/6/11 下午2:53
 * @Version 1.0
 */
@RestController
public class SupplierRolemanagementController {
    @Autowired
    private RoleManagementService roleManagementService;

    @RequestMapping("/supplierQueryRoleList")
    @RequiresPermissions(value = { "RoleManages_look" } )
    @RequestFunction(menu = ROLE_LIST)
    public AjaxResponse supplierQueryRoleList(
            @Verify(param="page",rule="required|min(1)") Integer page,
            @Verify(param="pageSize",rule="required|min(10)") Integer pageSize,
            String roleCode ,
            String roleName,
            Byte valid ) {
        PageDTO pageDto = roleManagementService.queryRoleList(page, pageSize, roleCode, roleName, valid);
        return AjaxResponse.success( pageDto );
    }


}
