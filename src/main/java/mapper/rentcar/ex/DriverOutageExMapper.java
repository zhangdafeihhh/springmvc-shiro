package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.DriverOutage;

import java.util.List;

public interface DriverOutageExMapper {


     List<DriverOutage> queryForListObjectNoLimit(DriverOutage record);
    
     DriverOutage queryDriverNameByPhone(DriverOutage record);


    //永久
     List<DriverOutage> queryAllForListObject(DriverOutage record);

     int queryAllForInt(DriverOutage record);

     List<DriverOutage> queryDriverOutageByDriverId(DriverOutage record);

     DriverOutage queryDriverOutageAllByDriverId(DriverOutage record);


     DriverOutage queryForObject(DriverOutage params) ;

     List<DriverOutage> queryForListObject(DriverOutage params) ;

    
     int queryForInt(DriverOutage params);
}