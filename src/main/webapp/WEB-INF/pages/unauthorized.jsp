<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 因shiroFilter中的unauthorizedUrl默认为unauthorized.jsp，而因框架BUG无法注入不起作用，所以只能在这里转发一下 --%>
<%  response.sendRedirect( request.getContextPath()+"/unauthorized.html"  ); %>