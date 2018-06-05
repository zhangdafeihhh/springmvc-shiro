<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
			<div class="col-md-3 left_col">
				<div class="left_col scroll-view">
					<div class="navbar nav_title" style="border: 0;">
						<a href="${webctx}/index.html" class="site_title"><span>首约车辆管理系统</span></a>
					</div>
					<div class="clearfix"></div>
					<br/>
					<!-- sidebar menu -->
					<div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
						<div class="menu_section">
							<!-- 商圈管理 -->
							<shiro:hasPermission name="district_menu">
							<ul class="nav side-menu">
								<li>
									<a><i class="fa fa-home"></i>商圈管理<span class="fa fa-chevron-down"></span></a>
									<ul class="nav child_menu">
										<shiro:hasPermission name="district_list"><li><a href="${webctx}/district/index.html">实时商圈</a></li></shiro:hasPermission>
									</ul>
								</li>
							</ul>
							</shiro:hasPermission>
							
							<!-- 司机排班管理 -->
							<shiro:hasPermission name="driver_duty_menu" >
							<ul class="nav side-menu">
								<li>
									<a><i class="fa fa-home"></i>司机排班管理<span class="fa fa-chevron-down"></span></a>
									<ul class="nav child_menu">
										<shiro:hasPermission name="force_list"><li><a href="${webctx}/a/b/c.html">强制上班时长设置</a></li></shiro:hasPermission>
									</ul>
								</li>
							</ul>
							</shiro:hasPermission>
							
							<!-- 车队管理 -->
							<ul class="nav side-menu">
								<li>
									<a><i class="fa fa-home"></i>车队管理<span class="fa fa-chevron-down"></span></a>
									<ul class="nav child_menu">
										<li><a href="${webctx}/driverteam/list.html">车队列表</a></li>
									</ul>
								</li>
							</ul>
							
							<!-- 测试菜单 -->
							<ul class="nav side-menu">
								<li>
									<a><i class="fa fa-home"></i>功能示例菜单<span class="fa fa-chevron-down"></span></a>
									<ul class="nav child_menu">
										<li><a href="${webctx}/demo/hasPerm.html">有权限页面</a></li>
										<li><a href="${webctx}/demo/noPerm.html">无权限页面</a></li>
										<li><a href="${webctx}/demo/whenException.html">页面异常</a></li>
										<li><a href="${webctx}/demo/abcdefghijklmn.html">不存在的页面</a></li>
									</ul>
								</li>
							</ul>
						</div>
					</div>
					<!-- /sidebar menu -->
				</div>
			</div>