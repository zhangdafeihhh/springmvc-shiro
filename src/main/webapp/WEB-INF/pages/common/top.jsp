<%@page import="com.zhuanche.shiro.session.WebSessionUtil"%>
<%@page import="com.zhuanche.shiro.realm.SSOLoginUser" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
			<div class="top_nav">
				<div class="nav_menu">
					<nav>
						<div class="nav toggle">
							<a id="menu_toggle"><i class="fa fa-bars"></i></a>
						</div>
						<ul class="nav navbar-nav navbar-right">
							<li class="">
								<a href="javascript:void(0);" class="user-profile dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
									${currentLoginUser.name }  ( ${currentLoginUser.loginName } )
			                        <span class=" fa fa-angle-down"></span>
			                    </a>
			                    <ul class="dropdown-menu dropdown-usermenu pull-right">
			                        <%--<li><a href="javascript:;">个人资料</a></li>--%>
			                        <%--<li>--%>
			                            <%--<a href="javascript:;"> <span>设置</span></a>--%>
			                        <%--</li>--%>
			                        <li>
			                            <a href="${ssoLogoutUrl}?service=${cmsLogoutUrl}"><i class="fa fa-sign-out pull-right"></i>退出</a>
			                        </li>
			                    </ul>
			                </li>
			            </ul>
			        </nav>
			    </div>
			</div>