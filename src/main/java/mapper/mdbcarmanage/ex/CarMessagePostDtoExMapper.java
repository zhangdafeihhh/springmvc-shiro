package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.CarMessagePostDto;
import com.zhuanche.entity.mdbcarmanage.CarMessagePost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarMessagePostDtoExMapper {


    /**
     * 根据messageIds获取列表
     * @param userId 用户id
     * @param status 状态
     * @return
     */
    List<CarMessagePostDto> listCarMessagePostBymesageIds(@Param("userId")Integer userId,
                                                          @Param("status") Integer status,
                                                          @Param("postStatus")Integer postStatus,
                                                          @Param("postUserId")Integer postUserId);




}