package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.BusOrderMessageTask;

/**
 * Created by 郭宏光 on 2019/4/26.
 */
public interface BusOrderMessageTaskExMapper {

    BusOrderMessageTask selectByOrderNum(String orderNum);
}
