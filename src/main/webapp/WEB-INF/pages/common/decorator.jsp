<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>

    <%@include file="/WEB-INF/pages/common/header.jsp"%>
    <sitemesh:write property='head'/>
</head>
<body class="nav-md">
<div class="container body">
    <div class="main_container">
        <!--左侧菜单-->
        <%@include file="/WEB-INF/pages/common/top.jsp"%>
        <sitemesh:write property='body'>
             <%@include file="/WEB-INF/pages/common/menu.jsp"%>

        </sitemesh:write>
        <!-- top navigation -->

        <!-- /top navigation -->


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
</body>
</html>