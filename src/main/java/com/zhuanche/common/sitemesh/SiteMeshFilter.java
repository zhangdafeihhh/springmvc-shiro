package com.zhuanche.common.sitemesh;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

/**
  * @description: 自定义tag
  *
  * <PRE>
  * <BR>	修改记录
  * <BR>-----------------------------------------------
  * <BR>	修改日期			修改人			修改内容
  * </PRE>
  *
  * @author lunan
  * @version 1.0
  * @since 1.0
  * @create: 2018-05-30 10:43
  *
*/

public class SiteMeshFilter extends ConfigurableSiteMeshFilter {

    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        //customer decorator
        builder.addTagRuleBundle(new MyTagRuleBundle());
    }
}
