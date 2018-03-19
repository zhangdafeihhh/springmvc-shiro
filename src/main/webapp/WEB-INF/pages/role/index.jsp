<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<%@include file="/WEB-INF/pages/common/header.jsp"%>
</head>
<body class="nav-md">
<div class="container body">
	<div class="main_container">
		<c:set var="menu" value="system"/>
		<c:set var="submenu" value="role"/>
		<!--左侧菜单-->
		<%@include file="/WEB-INF/pages/common/menu.jsp"%>
		<!-- top navigation -->
		<%@include file="/WEB-INF/pages/common/top.jsp"%>
		<!-- /top navigation -->

		<!-- page content -->
		<div class="right_col" role="main">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="x_title">
							<h2>角色管理 </h2>
							<ul class="nav navbar-right panel_toolbox">
								<li>
									<a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
								</li>
							</ul>
							<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<c:if test="${not empty msg}">
								<div id="message" class="alert ${result==null?"alert-success":"alert-warning"}"><button data-dismiss="alert" class="close">×</button>${msg}</div>
							</c:if>
							<form action="${ctx}role/index.html" method="post">
							<div class="table-responsive">
								<div class="col-md-12 col-sm-12 col-xs-12">
									<input type="hidden" name="pageNo">
									<input type="hidden" name="pageSize">
									<label class="myStyle2">
										角色名：<input type="text" name="search_roleName" value="${param.search_roleName}">
									</label>
									<span><input type="submit" class="btn btn-xs btn-primary" value="查询"></span>
									<shiro:hasPermission name="role_manage_add">
										<div class="nav navbar-right">
											<a class="btn btn-xs btn-edit btn-info" href="${ctx}role/single.html" role="button" aria-expanded="false"><i class="fa fa-plus"></i>添加</a>
										</div>
									</shiro:hasPermission>
								</div>
								<table class="table table-striped jambo_table bulk_action">
									<thead>
									<tr class="headings">
										<th class="column-title">角色名</th>
										<th class="column-title">角色代码</th>
										<th class="column-title no-link last"><span class="nobr">操作</span></th>
									</tr>
									</thead>
									<tbody>
									<c:forEach var="entity" items="${page.content}">
									<tr class="even pointer">
										<td>${entity.roleName}</td>
										<td>${entity.roleCode}</td>
										<td>
                                            <shiro:hasPermission name="role_manage_edit">
											<a class="btn btn-xs btn-purple" data-id="${entity.id}" name="edit_role" href="${ctx}role/single.html?id=${entity.id}">
												<i class="ace-icon fa fa-pencil-square-o"></i> 编辑
											</a>
											</shiro:hasPermission>
											<shiro:hasPermission name="role_manage_authorization">
											<a class="btn btn-xs btn-purple" data-id="${entity.id}" name="permission_role" href="${ctx}role/permission.html?roleId=${entity.id}">
												<i class="ace-icon fa fa-share"></i> 授权
											</a>
											</shiro:hasPermission>
										</td>
									</tr>
									</c:forEach>
									</tbody>
								</table>
							</div><tags:pagination paginationSize="10" page="${page}"/>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /page content -->
		<!-- footer content -->
		<%@include file="/WEB-INF/pages/common/footer.jsp"%>
		<!-- /footer content -->
	</div>
</div>
<!-- jQuery -->
<script src="${ctx}resources/js/jquery.min.js"></script>
<!-- Bootstrap -->
<script src="${ctx}resources/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="${ctx}resources/js/fastclick.js"></script>
<!-- NProgress -->
<script src="${ctx}resources/js/nprogress.js"></script>
<!-- jQuery Sparklines -->
<!-- bootstrap-daterangepicker -->
<script src="${ctx}resources/js/moment/moment.min.js"></script>
<!-- Custom Theme Scripts -->
<script src="${ctx}resources/js/custom.min.js"></script>

<script src="${ctx}resources/js/icheck.min.js"></script>
</body>
</html>
</html>