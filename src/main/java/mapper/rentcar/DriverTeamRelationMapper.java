package mapper.rentcar;

import com.zhuanche.dto.driver.DriverTeamRelationEntity;

import java.util.List;

public interface DriverTeamRelationMapper {

    //参考car-manager项目里的DriverTeamRelationDao

    List<DriverTeamRelationEntity> queryForListObjectNoLimit(DriverTeamRelationEntity params);

    public DriverTeamRelationEntity queryForObject(DriverTeamRelationEntity params);

    public DriverTeamRelationEntity queryForObjectGroup(DriverTeamRelationEntity params);

}
