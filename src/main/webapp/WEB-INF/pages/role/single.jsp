<%@ page contentType="text/html;charset=UTF-8" language="java"%>
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
			<div class="">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="x_title">
							<h2>角色管理</h2>
							<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<br />
							<form id="roleForm" data-parsley-validate class="form-horizontal form-label-left"  action="${ctx}role/edit.html" method="POST">
								<input type="hidden" name="id" value="${role.id}"/>
								<div class="form-group">
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="roleCode">角色代码:<span class="required">*</span>
									</label>
									<div class="col-md-6 col-sm-6 col-xs-12">
										<input type="text" id="roleCode" required="required" class="form-control col-md-7 col-xs-12" name="roleCode"placeholder="角色代码" value="${role.roleCode}"
										<c:if test="${not empty role.id}">disabled="disabled"</c:if>>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="roleName">角色名:<span class="required">*</span>
									</label>
									<div class="col-md-6 col-sm-6 col-xs-12">
										<input type="text" id="roleName" name="roleName" required="required" class="form-control col-md-7 col-xs-12"  placeholder="角色名" value="${role.roleName}">
									</div>
								</div>
								<div class="ln_solid"></div>
								<div class="form-group">
									<div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
										<button class="btn btn-primary" type="reset">重置</button>
										<button type="submit" class="btn btn-success">保存记录</button>
									</div>
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
<script src="${ctx}resources/js/datepicker/daterangepicker.js"></script>
<!-- Custom Theme Scripts -->
<script src="${ctx}resources/js/custom.min.js"></script>
<!-- iCheck -->
<script src="${ctx}resources/js/icheck.min.js"></script>
<!-- Custom Theme Scripts -->
<script src="${ctx}resources/js/custom.min.js"></script>
<!-- Google Analytics -->
<script type="text/javascript">
    // 新建用户表单验证
    $("#roleForm").validate({
        messages: {
            roleCode: {
                required: '请输入角色代码',
                remote: '角色代码已经存在'
            },
            roleName: {
                required: '请输入角色名称'
            }
        },
        rules: {
            roleCode: {
                required:true,
                remote: "${ctx}role/checkRoleCode.html"
            },
            roleName: "required"
        }
    });

    // 提交表单
    $("#submitFormBtn").click(function () {
        if ($("#roleForm").valid()) {
            $("#roleForm").submit();
        }
    });
</script>
</body>
</html>
</html>