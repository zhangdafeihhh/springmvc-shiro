package mapper.driver;

import com.zhuanche.entity.driver.DriverAppraisalAppeal;

public interface DriverAppraisalAppealMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverAppraisalAppeal record);

    int insertSelective(DriverAppraisalAppeal record);

    DriverAppraisalAppeal selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverAppraisalAppeal record);

    int updateByPrimaryKeyWithBLOBs(DriverAppraisalAppeal record);

    int updateByPrimaryKey(DriverAppraisalAppeal record);
}