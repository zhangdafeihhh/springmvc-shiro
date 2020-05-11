package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarDriverFile;
import org.springframework.stereotype.Repository;

@Repository
public interface CarDriverFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarDriverFile record);

    int insertSelective(CarDriverFile record);

    CarDriverFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarDriverFile record);

    int updateByPrimaryKey(CarDriverFile record);
}