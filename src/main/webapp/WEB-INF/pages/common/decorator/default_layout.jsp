<%@page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
	<title>首约车辆管理系统-<sitemesh:write property='title' /></title>
	<meta charset="utf-8">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="shortcut icon" href="//images.01zhuanche.com/statics/web/images/favicon.jpg" type="image/x-icon">
	<!-- 公共CSS  Bootstrap core CSS -->
	<link href="${webctx}/resources/css/bootstrap.min.css?v=${staticResourceVersion}" rel="stylesheet">
	<link href="${webctx}/resources/css/font-awesome.min.css?v=${staticResourceVersion}" rel="stylesheet">
	<link href="${webctx}/resources/css/animate.min.css?v=${staticResourceVersion}" rel="stylesheet">
	<link href="${webctx}/resources/css/custom.min.css?v=${staticResourceVersion}" rel="stylesheet">
	<!-- 公共javascript  Bootstrap -->
	<script src="${webctx}/resources/js/jquery.min.js?v=${staticResourceVersion}"></script><!-- jQuery -->
	<script src="${webctx}/resources/js/bootstrap.min.js?v=${staticResourceVersion}"></script><!-- Bootstrap -->
	<script src="${webctx}/resources/js/fastclick.js?v=${staticResourceVersion}"></script><!-- FastClick -->
	<script src="${webctx}/resources/js/nprogress.js?v=${staticResourceVersion}"></script><!-- NProgress -->
	<script src="${webctx}/resources/js/moment/moment.min.js?v=${staticResourceVersion}"></script><!-- bootstrap-daterangepicker -->
	<script>
	    var basePath = "${webctx}"; //JS中用到的URL根路径
	</script>
	
	<!-- 业务功能页特定的HEAD BEGIN -->
	<sitemesh:write property='head' />		
	<!-- 业务功能页特定的HEAD END -->
</head>
	
<body class="nav-md">
	<div class="container body">
		<div class="main_container">
			<!-- *******************左侧菜单BEGIN**************** -->
			<%@include file="/WEB-INF/pages/common/menu.jsp"%>
			<!-- *******************左侧菜单END**************** -->
			
			<!-- *******************页头BEGIN*************** -->
			<%@include file="/WEB-INF/pages/common/top.jsp"%>
			<!-- *******************页头END**************** -->

			<!-- *******************业务功能BEGIN************** -->
			<sitemesh:write property='body' />
			<!-- *******************业务功能END**************** -->
			
			<!-- *******************页脚BEGIN*************** -->
			<%@include file="/WEB-INF/pages/common/footer.jsp"%>
			<!-- *******************页脚END**************** -->
		</div>
	</div>
	
<script src="${webctx}/resources/js/custom.min.js?v=${staticResourceVersion}"></script><!-- Custom Theme Scripts -->
<script type="text/javascript">
$(function(){  
    //设置jQuery Ajax全局的默认响应
    $.ajaxSetup({
        error: function(jqXHR, textStatus, errorThrown ){  
            switch (jqXHR.status){
                case(500):  
                	alert( "HTTP_STATUS_500：系统发生异常，请联系管理员。" );
                    break;  
                case(401):  
                	alert(  "HTTP_STATUS_401：未登录，请重新登录。" );
                    break;  
                case(403):  
                	alert(  "HTTP_STATUS_403：您没有权限执行此操作！" );
                    break;  
                case(404):  
                	alert(  "HTTP_STATUS_404：请求资源不存在！" );
                    break;  
                case(408):  
                	alert(  "HTTP_STATUS_408：请求超时！" );
                    break;  
                default: 
                	alert(  "AJAX未知错误，请联系管理员。" );
                	console.log( jqXHR.status+"：AJAX未知错误，请联系管理员。"  );
            }  
        } 
    });  
});  

//对Date的扩展，将 Date 转化为指定格式的String
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function (fmt) {  
 var o = {
     "M+": this.getMonth() + 1, //月份 
     "d+": this.getDate(), //日 
     "h+": this.getHours(), //小时 
     "m+": this.getMinutes(), //分 
     "s+": this.getSeconds(), //秒 
     "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
     "S": this.getMilliseconds() //毫秒 
 };
 if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
 for (var k in o)
 if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
 return fmt;
}
</script>	
</body>
</html>