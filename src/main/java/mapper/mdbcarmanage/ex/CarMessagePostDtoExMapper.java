package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.CarMessagePostDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarMessagePostDtoExMapper {


    /**
     * 根据messageIds获取列表
     * @param userId
     * @param status
     * @return
     */

    /**
     *
     * @param userId 用户id
     * @param status 状态 1.草稿 2 发布
     * @return
     */
    List<CarMessagePostDto> listCarMessagePostBymesageIds(@Param("userId")Integer userId,
                                                          @Param("status") Integer status);

    /**
     * 草稿和已发布列表
     * @param userId
     * @param status
     * @return
     */
    List<CarMessagePostDto> listDraftOrPublish(@Param("userId")Integer userId,
                                               @Param("status") Integer status);



}