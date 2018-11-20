package com.zhuanche.controller;

import com.zhuanche.controller.rentcar.OrderController;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.serv.DemoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/demo")
public class DemoController{

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

	@Autowired
	private DemoService demoService;
	
	//---------------------------------------------------------------------请求页面时
	/**有权限**/
    //@RequiresRoles(value= {"strategy_manage","dispatcher"},logical=Logical.OR )
    @RequestMapping("/hasPerm")
    @MasterSlaveConfigs(configs={ 
  		  @MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER ),
  		  @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
      } )
    public String hasPerm(){
    	demoService.sayhello();
        return "demo";
    }
	/**无权限**/
	@RequiresPermissions(value = { "user:viewaaaaaaaaaa", "user:create-bbbbbbbbbb" }, logical = Logical.AND )
    @RequestMapping("/noPerm")
    public String noPerm(){
        return "demo";
    }
	/**出现异常**/
    @RequestMapping("/whenException")
    public String whenException(){
    	int abc = 899/0;
    	abc = abc+1;
        return "demo";
    }

	//---------------------------------------------------------------------请求AJAX时
    /**AJAX响应正常**/
    @RequestMapping("/ajaxOK")
    @ResponseBody
    public AjaxResponse ajaxOK(){
    	SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
    	AjaxResponse respJson = AjaxResponse.success(loginUser);
        return respJson;
    }
    
    /**AJAX响应无权限**/
	@RequiresPermissions(value = { "user:viewaaaaaaaaaa", "user:create-bbbbbbbbbb" }, logical = Logical.AND )
    @RequestMapping("/ajaxNoPerm")
    @ResponseBody
    public AjaxResponse ajaxNoPerm(){
    	SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
    	AjaxResponse respJson = AjaxResponse.success( loginUser);
        return respJson;
    }
    
    /**AJAX响应异常**/
    @RequestMapping("/ajaxException")
    @ResponseBody
    public AjaxResponse ajaxException(){
    	SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
    	AjaxResponse respJson = AjaxResponse.success(loginUser);
    	Integer.parseInt("0000ajaxException");
        return respJson;
    }
    @ResponseBody
    @RequestMapping(value = "/export", method = { RequestMethod.POST,RequestMethod.GET })
    public void exportDemo(HttpServletRequest request, HttpServletResponse response)  {


        List<String> headerList = new ArrayList<>();
        headerList.add("test");
        String fileName = "测试"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
        List<String> csvDataList = new ArrayList<>();

        try {

            CsvUtils utilEntity = new CsvUtils();
            for(int i=0;i<62;i++){
                logger.info("开始导出第"+(i+1)+"页数据");
                if(i == 0){
                    csvDataList.add("第1页");
                    utilEntity.exportCsvV2(response,csvDataList,headerList,fileName,true,false);

                }else{
                    csvDataList.add("第"+(i+1)+"页");
                    utilEntity.exportCsvV2(response,csvDataList,headerList,fileName,false,false);
                }
                try {
                    Thread.sleep(1000);
                    csvDataList.clear();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            utilEntity.exportCsvV2(response,csvDataList,headerList,fileName,false,true);
            logger.info("导出结束");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}