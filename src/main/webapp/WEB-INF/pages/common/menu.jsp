<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div class="col-md-3 left_col">
    <div class="left_col scroll-view">
        <div class="navbar nav_title" style="border: 0;">
            <a href="${ctx}index.html" class="site_title"><span>车辆管理系统</span></a>
        </div>
        <div class="clearfix"></div>
        <br />
        <!-- sidebar menu -->
        <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
            <div class="menu_section">
                <h3>菜单</h3>
                <ul class="nav side-menu">
                        <li class="${menu eq 'system'?'active':''}"><a><i class="fa fa-home"></i>商圈管理<span class="fa fa-chevron-down"></span></a>
                            <ul class="nav child_menu" style="${menu eq 'system'?'display: block;':''}">
                                    <li class="${submenu eq 'user'?'current-page':''}"><a href="${ctx}district/index.html">商圈</a></li>
                            </ul>
                        </li>
                </ul>
            </div>
        </div>
        <!-- /sidebar menu -->
    </div>
</div>