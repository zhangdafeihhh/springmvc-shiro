package com.zhuanche.controller.message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.mdbcarmanage.CarMessageGroup;
import com.zhuanche.serv.mdbcarmanage.MessageGroupService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author fanht
 * @Description 创建分组
 * @Date 2020/2/05 上午10:26
 * @Version 1.0
 */
@Controller
@RequestMapping("/messageGroup")
public class MessageGroupController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageGroupService groupService;




    @RequestMapping(value = "/createGroup",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource",mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse createGroup(@Verify(param = "groupName",rule = "required") String groupName,
                                    @RequestParam(value = "groupDesc",required = false) String groupDesc,
                                    @RequestParam(value = "level",required = false) Integer level,
                                    @RequestParam(value = "cities",required = false) String cities,
                                    @RequestParam(value = "suppliers",required = false) String suppliers,
                                    @RequestParam(value = "teamId",required = false) String teamId){
        logger.info(MessageFormat.format("创建消息组入参：groupName:{0},groupDesc:{1},level:{2}," +
                "cities:{3},suppliers:{4},teamId:{5}",groupName,groupDesc,level,cities,suppliers,teamId));
        try {
            //查询下小组名是否已经重复
            Integer count = groupService.isRepeatGroupName(groupName);
            if(count != null && count > 0){
                logger.info("分组名称已存在");
                return AjaxResponse.fail(RestErrorCode.MESSAGE_GROUP_EXIST);
            }

            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            CarMessageGroup group = new CarMessageGroup();
            group.setGroupName(groupName);
            group.setGroupDesc(groupDesc);
            group.setLevel(level);
            group.setCities(cities);
            group.setCreater(loginUser.getName());
            group.setCreaterId(loginUser.getId());
            group.setSuppliers(suppliers);
            group.setTeamids(teamId);
            group.setStatus(1);
            group.setCreateTime(new Date());
            group.setUpdateTime(new Date());
            int code = groupService.createGroup(group);
            if(code > 0){
                return AjaxResponse.success(null);
            } else {
              return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }
        } catch (Exception e) {
            logger.error("创建消息组异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/detailGroup")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource",mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse detailGroup(Integer id){
        logger.info("获取分组详情：" + id);
        try {
            CarMessageGroup group = groupService.detailGroup(id);
            return AjaxResponse.success(group);
        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    @RequestMapping(value = "/editGroup",method = RequestMethod.POST)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource",mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse editGroup(@Verify(param = "id",rule = "required") Integer id,
                                  @Verify(param = "groupName",rule = "required") String groupName,
                                  @RequestParam(value = "groupDesc",required = false) String groupDesc,
                                  @RequestParam(value = "level",required = false) Integer level,
                                  @RequestParam(value = "cities",required = false) String cities,
                                  @RequestParam(value = "suppliers",required = false) String suppliers,
                                  @RequestParam(value = "teamId",required = false) String teamId){
        logger.info(MessageFormat.format("修改消息组入参：groupName:{0},groupDesc:{1},level:{2}," +
                "cities:{3},suppliers:{4},teamId:{5},id:{6}",groupName,groupDesc,level,cities,suppliers,teamId,id));
        try {
            Integer searchId = groupService.isRepeatGroupName(groupName);
            if(searchId != null && searchId > 0){
                if(searchId != id){
                    logger.info("分组名称已存在");
                    return AjaxResponse.fail(RestErrorCode.MESSAGE_GROUP_EXIST);
                }
            }

            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            CarMessageGroup group = new CarMessageGroup();
            group.setGroupName(groupName);
            group.setGroupDesc(groupDesc);
            group.setCities(cities);
            group.setCreater(loginUser.getName());
            group.setCreaterId(loginUser.getId());
            group.setLevel(level);
            group.setSuppliers(suppliers);
            group.setTeamids(teamId);
            group.setId(Long.valueOf(id));
            group.setUpdateTime(new Date());
            int code = groupService.editGroup(group);
            if(code > 0){
                return AjaxResponse.success(null);
            } else {
                return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }
        } catch (Exception e) {
            logger.error("编辑消息组异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/editGroupStatus",method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource",mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse editGroupStatus(@Verify(param = "id",rule = "required") Integer id,
                                        @Verify(param = "id",rule = "required") Integer status){
        logger.info("启用禁用入参：id:" + id + ",status:" + status);
        try {
            CarMessageGroup group =  new CarMessageGroup();
            group.setStatus(status);
            group.setId(Long.valueOf(id));
            int code  = groupService.editGroup(group);
            if(code > 0 ){
                return AjaxResponse.success(null);
            }else {
                return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }

        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/searchGroup")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource",mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse searchGroup(String groupName,
                                    Integer status,
                                    String creater,
                                    String beginCreateTime,
                                    String endCreateTime,
                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum){
        logger.info("查询消息组入参：groupName:" + groupName + ",status:" + status,",creater"+creater+
                ",beginCreateTime"+beginCreateTime+",endCreateTime" + endCreateTime);
        try {
            CarMessageGroup group = new CarMessageGroup();
            if(StringUtils.isNotEmpty(groupName)){
                group.setGroupName(groupName);
            }

            if(status != null && status >= 0){
                group.setStatus(status);
            }
            if(StringUtils.isNotEmpty(creater)){
                group.setCreater(creater);
            }
            if(StringUtils.isNotEmpty(beginCreateTime)){
                group.setBeginCreateTime(beginCreateTime);
            }
            if(StringUtils.isNotEmpty(endCreateTime)){
                group.setEndCreateTime(endCreateTime);
            }
            Page page = PageHelper.startPage(pageNum,pageSize,true);

            List<CarMessageGroup> messageGroupList = groupService.searchGroup(group);

            int total = (int)page.getTotal();

            PageDTO pageDTO = new PageDTO(pageNum,pageSize,total,messageGroupList);

            return AjaxResponse.success(pageDTO);

        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }




    @ResponseBody
    @RequestMapping(value = "/allGroup")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource",mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse allGroup(){
        logger.info("查询所有的消息组入参:");
        try {
            CarMessageGroup group = new CarMessageGroup();

            group.setStatus(1);


            List<CarMessageGroup> messageGroupList = groupService.searchGroup(group);

            JSONArray jsonArray = new JSONArray();

            messageGroupList.forEach(list ->{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",list.getId());
                jsonObject.put("groupName",list.getGroupName());
                jsonArray.add(jsonObject);
            });

            return AjaxResponse.success(jsonArray);

        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

}
