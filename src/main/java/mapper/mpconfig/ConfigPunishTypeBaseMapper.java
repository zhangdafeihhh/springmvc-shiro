package mapper.mpconfig;

import com.zhuanche.entity.mpconfig.ConfigPunishTypeBaseEntity;

/**
 * @author kjeakiry
 */
public interface ConfigPunishTypeBaseMapper {


    /**
     * queryPunishTypeSpecial
     * @param params
     * @return
     */
    ConfigPunishTypeBaseEntity queryPunishTypeSpecial(ConfigPunishTypeBaseEntity params);

    /**
     * queryPunishTypeBase
     * @param params
     * @return
     */
    ConfigPunishTypeBaseEntity queryPunishTypeBase(ConfigPunishTypeBaseEntity params);
}
