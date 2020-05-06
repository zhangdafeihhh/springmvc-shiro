package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarDriverFile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarDriverFileExtMapper {

    List<CarDriverFile> findByParams(CarDriverFile queryParam);
}