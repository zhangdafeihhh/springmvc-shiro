package com.zhuanche.service.impl.driver;

import com.zhuanche.dao.driver.DriverBigdataDistrictMapper;
import com.zhuanche.entity.driver.DriverBigdataDistrict;
import com.zhuanche.service.driver.DriverBigdataDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service("driverBigdataDistrictService")
public class DriverBigdataDistrictServiceImpl implements DriverBigdataDistrictService {

    @Autowired
    private DriverBigdataDistrictMapper driverBigdataDistrictMapper;

    @Override
    public List<DriverBigdataDistrict> findListByCityId(Integer cityId) {
        DriverBigdataDistrict params = new DriverBigdataDistrict();
        params.setCityId(cityId);
        List<DriverBigdataDistrict> list = null;
        try {
            //获取当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df_hh = new SimpleDateFormat("HH");
            String format = df_hh.format(new Date());
            params.setTime(format);
            params.setDate(df.parse(df.format(new Date())));
            list = driverBigdataDistrictMapper.findListByCityId(params);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df_hh = new SimpleDateFormat("HH");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse("2018-04-26 00:24:24");
            String format = df.format(date);
            String format1 = df_hh.format(dateFormat.parse("2018-04-26 00:24:24"));
            System.out.println(format);
            System.out.println(format1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
