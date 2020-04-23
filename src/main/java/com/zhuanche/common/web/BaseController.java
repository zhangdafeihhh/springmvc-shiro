package com.zhuanche.common.web;

import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.CarBizSupplierService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wzq
 */
public class BaseController {


	@Autowired
    private CarBizSupplierService carBizSupplierService;

    @Autowired
    private CarBizCityService carBizCityService;


    public Map<String,Object> querySupplierName(int cityId, int supplierId){
        Map<String, Object> result = new HashMap<String, Object>();
        CarBizSupplier carBizSupplier = new CarBizSupplier();
        carBizSupplier.setSupplierId(supplierId);
        CarBizSupplier supplierEntity = carBizSupplierService.queryForObject(carBizSupplier);
        if(Objects.nonNull(supplierEntity)){
            result.put("supplierName", supplierEntity.getSupplierFullName());
        }else{
            result.put("supplierName", "");
        }
        CarBizCity cityEntity = carBizCityService.selectByPrimaryKey(cityId);
        if(Objects.nonNull(cityEntity)){
            result.put("cityName", cityEntity.getCityName());
        }else{
            result.put("cityName", "");
        }
        return result;
    }
	
	/*
	 * 下载
	 */
	public void fileDownload(HttpServletRequest request, HttpServletResponse response,String path) {
		
		File file = new File(path);// path是根据日志路径和文件名拼接出来的
	    String filename = file.getName();// 获取日志文件名称
	    try {
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();
			// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream os = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			os.write(buffer);// 输出文件
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
		if(StringUtils.isEmpty(fileName)) {
			fileName = "exportExcel";
		}
		response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//指定下载的文件名
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		ServletOutputStream os =  response.getOutputStream();
		wb.write(os);
		os.close();
	}

}