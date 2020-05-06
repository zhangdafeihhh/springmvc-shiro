package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.CarDriverFileDto;
import com.zhuanche.entity.mdbcarmanage.CarDriverFile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarDriverFileExtMapper {

    List<CarDriverFileDto> findByParams(CarDriverFileDto queryParam);
}