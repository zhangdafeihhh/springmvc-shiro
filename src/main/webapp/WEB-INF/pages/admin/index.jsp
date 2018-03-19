<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
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
				<!--左侧菜单-->
				<%@include file="/WEB-INF/pages/common/menu.jsp"%>
				<!-- top navigation -->
				<%@include file="/WEB-INF/pages/common/top.jsp"%>
				<!-- /top navigation -->
				<!-- page content -->
				<div class="right_col">
					<div class="row">
						<div class="page-title">
							<div class="x_panel">
								<div class="x_title">
									<h2>用户信息</h2>
									<div class="clearfix"></div>
								</div>
								<div class="x_content">
									<c:if test="${not empty msg}">
										<div id="message" class="alert ${result?"alert-success":"alert-warning"}"><button data-dismiss="alert" class="close">×</button>${msg}</div>
									</c:if>
									<form action="${ctx}admin/index.html" method="post">
										<div class="table-responsive">
											<div class="col-md-12 col-sm-12 col-xs-12">
												<input type="hidden" name="pageNo">
												<input type="hidden" name="pageSize">
												<label class="myStyle2">
													登录名：<input type="text" name="search_loginName" value="${loginName}">
												</label>
												<label class="myStyle2">
													姓名：<input type="text" name="search_name" value="${name}">
												</label>
												<label class="myStyle2">
													手机号：<input type="text" name="search_mobile" value="${mobile}">
												</label>
												<span><input type="submit" class="btn btn-sm btn-primary" value="查询"></span>
											</div>
											<table class="table table-striped jambo_table bulk_action">
												<thead>
												<tr class="headings">
													<th class="column-title">登录名</th>
													<th class="column-title">姓名</th>
													<th class="column-title">手机号</th>
													<th class="column-title">邮箱</th>
													<th class="column-title">状态</th>
													<th class="column-title">创建时间</th>
													<th class="column-title">最后一次登录时间</th>
													<th class="column-title no-link last"><span class="nobr">操作</span>
													</th>
												</tr>
												</thead>
												<tbody>
													<c:forEach items="${page.content}" var="entity">
														<tr class="even pointer">
															<td class=" ">${entity.loginName}</td>
															<td class=" ">${entity.name}</td>
															<td class=" ">${entity.mobile}</td>
															<td class=" ">${entity.email}</td>
															<td class=" ">
																<c:if test="${entity.status==1}">
																	<span class="label label-success">正常</span>
																</c:if>
																<c:if test="${entity.status==2}">
																	<span class="label label-danger">锁定</span>
																</c:if>
															</td>
															<td class=" "><fmt:formatDate value="${entity.createAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
															<td class=" "><fmt:formatDate value="${entity.lastLoginDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
															<td class="a-right a-right ">
																<shiro:hasPermission name="user_manager_logOff">
																<c:if test="${entity.status==1}"><a class="btn btn-xs btn-edit btn-info" href="#" onclick="operate('${entity.id}','${entity.status}','${entity.loginName}')">锁定</a></c:if>
																</shiro:hasPermission>
																<shiro:hasPermission name="user_manager_logOn">
																<c:if test="${entity.status==2}"><a class="btn btn-xs btn-danger" href="#" onclick="operate('${entity.id}','${entity.status}','${entity.loginName}')">解锁</a></c:if>
																</shiro:hasPermission>
																<shiro:hasPermission name="user_manager_edit">
																<c:if test="${entity.status==1}"><a class="btn btn-xs btn-edit btn-info" href="${ctx}admin/edit.html?id=${entity.id}">修改</a></c:if>
																</shiro:hasPermission>
															</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
											<div><tags:pagination paginationSize="10" page="${page}"/></div>
										</div>
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
	<script language="JavaScript">
		function operate(id,status,name) {
		    var title="确定要锁定用户【"+name+"】吗?";
		    if(status==2){
                title="确定要解锁用户【"+name+"】吗?";
			}
            if(confirm(title)){
                $.ajax({
                    type:"POST",
                    url:"${ctx}admin/operate.html",
                    data:{"id":id,"status":status},
                    success:function(data) {
                        alert(data);
                        window.location.reload();
                    },
                    error:function (e) {
                        alert("操作，请稍后重试！");
                    }
                });
            }
        }
	</script>
	</body>
</html>