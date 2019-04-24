package mapper.driver;

import java.util.List;

import com.zhuanche.entity.driver.SysLog;

public interface SysLogMapper {
    int deleteByPrimaryKey(Integer sysLogId);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Integer sysLogId);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

	List<SysLog> selectByPrimaryList();
}