package mapper.mdbcarmanage.ex;

import java.util.List;
import java.util.Set;

import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;

public interface CarRelateGroupExMapper {
	
	List<CarRelateGroup> queryByParams(CarRelateGroup group);
	
	List<Integer> queryDriversByParams(CarRelateGroup group);
	
}