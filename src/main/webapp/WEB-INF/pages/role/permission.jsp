<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<%@include file="/WEB-INF/pages/common/header.jsp"%>
		<link rel="stylesheet" type="text/css" href="${ctx}resources/css/bootstrap-treeview/bootstrap-treeview.css">
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
				<div class="right_col">
					<div class="row">
						<div class="page-title">
							<div class="x_panel">
								<div class="x_title">
									<h2>角色【${currRole.roleName}】授权</h2>
									<div class="clearfix"></div>
								</div>
								<div class="x_content">
										<div id="messageSuccess" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>修改授权成功！</div>
										<div id="messageFail" class="alert alert-warning"><button data-dismiss="alert" class="close">×</button>修改授权失败！</div>
									<div class="table-responsive">
										<div class="col-md-12 col-sm-12 col-xs-12">
											<div id="treeview" class=""></div>
											<div class="btn-group" style="margin-right:15px; margin-left:15px;">
												<input type="button" onclick="savePermission()" class="btn btn-sm btn-primary" value="保存">
												<input type="hidden" name="roleId" value="${roleId}" id="roleId">
											</div>
										</div>
									</div>
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
		<!-- BootstraptreeView -->
		<script src="${ctx}resources/js/bootstrapTreeView/bootstrap-treeview.js"></script>
		<script type="text/javascript">
            $(function() {
                $("#messageSuccess").hide();
                $("#messageFail").hide();
                var nodeCheckedSilent = false;
                var nodeUncheckedSilent = false;
                $('#treeview').treeview({
                    data: ${permissions},
                    showCheckbox : true,
                    levels:5,
                    onNodeChecked: function(event, data){
                        //选择节点
                        if(nodeCheckedSilent){
                            return;
                        }
                        nodeCheckedSilent = true;
                        //选中全部父节点
                        checkAllParent(data);
                        checkAllSon(data);
                        nodeCheckedSilent = false;
                    },
                    onNodeUnchecked: function(event, data){
                        //取消选择节点
                        if(nodeUncheckedSilent){
                            return;
                        }
                        nodeUncheckedSilent = true;
                        uncheckAllParent(data);
                        uncheckAllSon(data);
                        nodeUncheckedSilent = false;
                    }

                });

            });

            function savePermission(){

                var arr = new Array();
                arr =$('#treeview').treeview('getChecked', 0);
                if(arr.length==0){
                    alert("请选择节点");
                    return;
                }
                var ids ="";
                for(var i=0 ; i< arr.length;i++){
                    var node = arr[i];
                    ids = ids+node.id+",";
                }
                var rid = $("#roleId").val();
                $.ajax({
                    url:"${ctx}role/modify.html",
                    data:{roleId:rid,permissionIds:ids},
                    type:'POST',
                    success:function(data){
                        if(data=="true"){
                            $("#messageSuccess").show();
                            $("#messageFail").hide();
                        }else{
                            $("#messageFail").show();
                            $("#messageSuccess").hide();
                        }
                        flag = false;
                    },
                    error:function(data){
                        flag = false;
                    }
                });
            }

            //选中全部父节点
            function checkAllParent(node){
                $('#treeview').treeview('checkNode',node.nodeId,{silent:true});
                //getParent(node | nodeId)：返回给定节点的父节点，如果没有则返回undefined。
                var parentNode = $('#treeview').treeview('getParent',node.nodeId);
                if(!("nodeId" in parentNode)){
                    return;
                }else{
                    checkAllParent(parentNode);
                }
            }

            //级联选中所有子节点
            function checkAllSon(node){
                $('#treeview').treeview('checkNode',node.nodeId,{silent:true});
                if(node.nodes!=null&&node.nodes.length>0){
                    for(var i in node.nodes){
                        checkAllSon(node.nodes[i]);
                    }
                }
            }
            //取消全部父节点
            function uncheckAllParent(node){
                $('#treeview').treeview('uncheckNode',node.nodeId,{silent:true});
                //getSiblings(node | nodeId)：返回给定节点的兄弟节点的数组，如果没有则返回undefined。
                var siblings = $('#treeview').treeview('getSiblings', node.nodeId);
                var parentNode = $('#treeview').treeview('getParent',node.nodeId);
                if(!("nodeId" in parentNode)) {
                    return;
                }
                var isAllUnchecked = true;  //是否全部没选中
                for(var i in siblings){
                    if(siblings[i].state.checked){
                        isAllUnchecked=false;
                        break;
                    }
                }
                if(isAllUnchecked){
                    uncheckAllParent(parentNode);
                }

            }

            //级联取消所有子节点
            function uncheckAllSon(node){
                $('#treeview').treeview('uncheckNode',node.nodeId,{silent:true});
                if(node.nodes!=null&&node.nodes.length>0){
                    for(var i in node.nodes){
                        uncheckAllSon(node.nodes[i]);
                    }
                }
            }
		</script>
	</body>
</html>