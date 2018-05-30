package com.zhuanche.common.sitemesh;

import org.sitemesh.SiteMeshContext;
import org.sitemesh.content.ContentProperty;
import org.sitemesh.content.tagrules.TagRuleBundle;
import org.sitemesh.content.tagrules.html.ExportTagToContentRule;
import org.sitemesh.tagprocessor.State;

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
  * @create: 2018-05-30 10:45
  *
*/

public class MyTagRuleBundle implements TagRuleBundle {
    @Override
    public void install(State state, ContentProperty contentProperty, SiteMeshContext siteMeshContext) {
        state.addRule("myfooter", new ExportTagToContentRule(siteMeshContext, contentProperty.getChild("myfooter"), false));
        state.addRule("menu", new ExportTagToContentRule(siteMeshContext, contentProperty.getChild("menu"), false));
    }

    @Override
    public void cleanUp(State state, ContentProperty contentProperty, SiteMeshContext siteMeshContext) {

    }
}
