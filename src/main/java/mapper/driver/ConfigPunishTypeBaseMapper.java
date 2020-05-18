package mapper.driver;

import com.zhuanche.entity.driver.punish.ConfigPunishTypeBaseEntity;

/**
 * @author kjeakiry
 */
public interface ConfigPunishTypeBaseMapper {


    /**
     * queryPunishTypeSpecial
     * @param params
     * @return
     */
    ConfigPunishTypeBaseEntity  queryPunishTypeSpecial(ConfigPunishTypeBaseEntity params);

    /**
     * queryPunishTypeBase
     * @param params
     * @return
     */
    ConfigPunishTypeBaseEntity queryPunishTypeBase(ConfigPunishTypeBaseEntity params);
}
