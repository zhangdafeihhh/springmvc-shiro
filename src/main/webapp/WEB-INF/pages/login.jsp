<%@ page import="java.util.Calendar" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%@include file="/WEB-INF/pages/common/header.jsp" %>
    <!-- Bootstrap -->
    <!-- NProgress -->
    <link href="${ctx}resources/css/nprogress.css" rel="stylesheet">
</head>

    <body class="login">
        <div>
            <div class="login_wrapper">
                <div class="animate form login_form">
                    <section class="login_content">
                        <form id="loginForm" class="form-horizontal form-label-left" action="http:${ctx}login.html" method="post">
                            <h1>请输入帐号信息</h1>
                            <div align="left">
                                <%String e = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME); %>
                                <%if(StringUtils.isNotBlank(e)){
                                    if(e.equals("org.apache.shiro.authc.IncorrectCredentialsException")){%>
                                <span style='color: red;'>*密码错误，您还有${5-retryCount }次机会!</span>
                                <%}else if(e.equals("org.apache.shiro.authc.ExcessiveAttemptsException")){%>
                                <span style='color: red;'>*密码输入错误超过限制，请30分钟后再试!</span>
                                <%}else if(e.equals("org.apache.shiro.authc.UnknownAccountException")){%>
                                <span style='color: red;'>*用户名不存在！</span>
                                <%}else if(e.equals("org.apache.shiro.authc.LockedAccountException")){%>
                                <span style='color: red;'>*账号锁定,请联系管理员!</span>
                                <%}else if(e.equals("org.apache.shiro.authc.AccountException")){%>
                                <span style='color: red;'>*登录失败，请稍后重试!</span>
                                <%}else if(e.equals("org.apache.shiro.authc.AuthenticationException")){%>
                                <span style='color: red;'>*帐号或密码不正确!</span>
                                <%}else{%>
                                <span style='color: red;'>*系统出现异常,请联系管理员!</span>
                                <%}}%>
                            </div>
                            <div>
                                <input type="text" name="username" class="form-control" placeholder="请输入帐号"/>
                            </div>
                            <div>
                                <input type="password" name="password" class="form-control" placeholder="请输入帐号密码"/>
                            </div>
                            <div>
                                <button type="button" onclick="submitForm()" class="width-35 pull-center btn btn-sm btn-primary">
                                    <i class="ace-icon fa fa-key"></i>
                                    <span class="bigger-110">登录</span>
                                </button>
                                <%--<a class="reset_pass" href="#">忘记密码?</a>--%>
                            </div>
                        <div class="clearfix"></div>
                        <div class="separator">
                            <div class="clearfix"></div>
                            <br/>
                            <div>
                                <h1><img src="//images.01zhuanche.com/statics/web/images/logo.png" alt=""/></h1>
                                <p>Copyright  ©<%=Calendar.getInstance().get(Calendar.YEAR)%><a href="http://www.01zhuanche.com/">首约科技.</a>All Rights Reserved.</p>
                                <p>经营许可编号：京ICP证070101号 吉ICP备05004090号</p>
                            </div>
                        </div>
                        </form>
                    </section>
                </div>
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
        <!-- validator -->
        <script src="${ctx}resources/js/validator.js"></script>

        <!-- Custom Theme Scripts -->
        <script src="${ctx}resources/js/custom.min.js"></script>
    <script>
        function submitForm(){
            $("#loginForm").submit();
        }

    </script>
    </body>
</html>