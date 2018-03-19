<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<%@include file="/WEB-INF/pages/common/header.jsp"%>
	<link rel="stylesheet" href="${ctx}resources/css/select2.min.css" />
</head>
<body class="nav-md">
<div class="container body">
	<div class="main_container">
		<c:set var="menu" value="system"/>
		<c:set var="submenu" value="user"/>
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
							<h2>用户管理</h2>
							<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<br />
							<form id="submitFrom" data-parsley-validate class="form-horizontal form-label-left"  action="${ctx}admin/save.html" method="POST" >
								<input type="hidden" name="id" value="${entity.id}"/>
								<div class="form-group">
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="loginName">登录名:<span class="required">*</span>
									</label>
									<div class="col-md-6 col-sm-6 col-xs-12">
										<input type="text" id="loginName" name="loginName" required="required" disabled="disabled" class="form-control col-md-7 col-xs-12"  placeholder="登录名" value="${entity.loginName}">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="name">姓名:<span class="required">*</span>
									</label>
									<div class="col-md-6 col-sm-6 col-xs-12">
										<input type="text" id="name" name="name" required="required" class="form-control col-md-7 col-xs-12"  placeholder="姓名" value="${entity.name}">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="mobile">手机号:<span class="required">*</span>
									</label>
									<div class="col-md-6 col-sm-6 col-xs-12">
										<input type="text" id="mobile" name="mobile" required="required" class="form-control col-md-7 col-xs-12"  placeholder="手机号" value="${entity.mobile}">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="email">邮箱:<span class="required">*</span>
									</label>
									<div class="col-md-6 col-sm-6 col-xs-12">
										<input type="text" id="email" name="email" required="required" class="form-control col-md-7 col-xs-12"  placeholder="邮箱" value="${entity.email}">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="role">角色:<span class="required">*</span>
									</label>
									<div class="col-md-6 col-sm-6 col-xs-12">
									<input type="hidden" id="hiddenRole" name="hiddenRole">
									<select id="role" name="role" class="js-example-tokenizer form-control" placeholder="角色"  multiple="multiple">
										<c:if test="${allRoles.size()>0}">
											<c:forEach var="allRoles" items="${allRoles}" >
												<c:if test="${adminRole.size()>0}">
													<c:forEach var="aRole" items="${adminRole}" >
														<c:if test="${aRole.id==allRoles.id}">
															<option value="${allRoles.id}" selected="selected">${allRoles.text} </option>
														</c:if>
													</c:forEach>
												</c:if>
											</c:forEach>
										</c:if>
									</select>
									<span id="roleError"></span>
									</div>
								</div>
								<div class="ln_solid"></div>
								<div class="form-group">
									<div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
										<button onclick="submitFrom()"  class="btn btn-success">保存记录</button>
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
<script src="${ctx}resources/js/jquery.validate.min.js"></script>
<script src="${ctx}resources/js/select2.full.js"></script>
<script type="text/javascript">
    // 新建用户表单验证
    $("#submitFrom").validate({
        messages: {
            name: {
                required: '请输入姓名'
            }
        },
        rules: {
            roleCode: {
                required:true,
            },
            roleName: "required"
        }
    });
    jQuery(function($) {
        var allRoles = ${allRoles};
        $("#role").select2({
            data: allRoles,
            tags: true,
            tokenSeparators: [',', ' ']
        });
    });
    function submitFrom() {
        var reslist=$("#role").select2("data");
        var res="";
        $.each(reslist,function(i,n){
            console.log(res);
            res = res+n.id+',';
        });
        $("#hiddenRole").val(res);
        //alert(res);
        if(!res||res.length==0){
            //alert(res);
		}
        //return;
        $("#submitFrom").validate({
            rules: {
                name: {
                    required:true,
                    rangelength:[2,16]
                },
                email: {
                    required: true,
                    email: true
                },
                leader:{
                    required:true
                }
            },
            messages: {
                name: {
                    required:"*请输入姓名！",
                    rangelength:"*长度应该在2至16个字符之间！"
                },
                email: "*请输入一个有效的邮箱！",
            },
            errorPlacement:function(error, element) {
                var id = element.attr("id");
                if(id=="leader"){
                    error.insertAfter(element.next());
                }else{
                    error.appendTo(element.parent());
                }

            }
        });
    }


</script>
</body>
</html>
</html>