<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
	<title>异常</title>
</head>

<body>
				<div class="">
					<div class="row top_tiles">
						<div class="animated flipInX col-lg-3 col-md-3 col-sm-6 col-xs-12" style="width:100%">
							<div class="tile-stats">
								<div class="icon"><i class="fa fa-caret-square-o-right"></i></div>
								<div class="count">系统运行异常，请立即联系管理员。</div>
								<h3>${exceptionMessage}</h3>
							</div>
						</div>
					</div>
				</div>
				
				<div class=""   <c:if test="${ envName =='online' }"> style="display:none;" </c:if> >
					<div class="row top_tiles">
						<div class="animated col-lg-3 col-md-3 col-sm-6 col-xs-12" style="width:100%">
							<div class="tile-stats">
								<p><pre>${exceptionDetail}</pre></p>
							</div>
						</div>
					</div>
				</div>
</body>
</html>