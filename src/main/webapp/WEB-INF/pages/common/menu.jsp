<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div class="col-md-3 left_col">
	<div class="left_col scroll-view">
		<div class="navbar nav_title" style="border: 0;">
			<a href="${ctx}index.html" class="site_title"><span>车辆管理系统</span></a>
		</div>
		<div class="clearfix"></div>
		<br/>
		<!-- sidebar menu -->
		<div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
			<div class="menu_section">
				<%--<h3>菜单</h3>--%>
				<shiro:hasPermission name="district_menu">
					<ul class="nav side-menu">
						<li>
							<a><i class="fa fa-home"></i>商圈管理<span class="fa fa-chevron-down"></span></a>
							<ul class="nav child_menu">
								<li><a href="${ctx}district/index.html">实时商圈</a></li>
							</ul>
						</li>
					</ul>
				</shiro:hasPermission>
				<shiro:hasPermission name="driver_duty_menu">
					<ul class="nav side-menu">
						<li>
							<a><i class="fa fa-home"></i>司机排班管理<span class="fa fa-chevron-down"></span></a>
							<ul class="nav child_menu">
								<li><a href="${ctx}">强制上班时长设置</a></li>
							</ul>
						</li>
					</ul>
				</shiro:hasPermission>
			</div>
		</div>
		<!-- /sidebar menu -->
	</div>
</div>