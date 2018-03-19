<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div class="col-md-3 left_col">
    <div class="left_col scroll-view">
        <div class="navbar nav_title" style="border: 0;">
            <a href="${ctx}index.html" class="site_title"><span>内容管理系统</span></a>
        </div>
        <div class="clearfix"></div>
        <br />
        <!-- sidebar menu -->
        <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
            <div class="menu_section">
                <h3>菜单</h3>
                <ul class="nav side-menu">
                    <shiro:hasPermission name="system_manager">
                        <li class="${menu eq 'system'?'active':''}"><a><i class="fa fa-home"></i>系统管理<span class="fa fa-chevron-down"></span></a>
                            <ul class="nav child_menu" style="${menu eq 'system'?'display: block;':''}">
                                <shiro:hasPermission name="user_manager">
                                    <li class="${submenu eq 'user'?'current-page':''}"><a href="${ctx}admin/index.html">用户管理</a></li>
                                </shiro:hasPermission>
                                <shiro:hasPermission name="role_manage">
                                    <li class="${submenu eq 'role'?'current-page ':''}"><a href="${ctx}role/index.html">角色管理</a></li>
                                </shiro:hasPermission>
                            </ul>
                        </li>
                    </shiro:hasPermission>
                </ul>
            </div>
        </div>
        <!-- /sidebar menu -->
    </div>
</div>