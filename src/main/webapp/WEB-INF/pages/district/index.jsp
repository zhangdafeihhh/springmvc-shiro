<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
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
		<div class="right_col" role="main">
			<div class="">
				<div class="row">
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="x_panel">
							<div class="x_content" style="height:550px;">
								<div id="container"></div>
								<div id="myPageTop">
									<table>
										<tr>
											<td>
												<label>按关键字搜索：</label>
											</td>
											<td class="column2">
												<label>左击获取经纬度：</label>
											</td>
										</tr>
										<tr>
											<td>
												<input type="text" placeholder="请输入关键字进行搜索" id="tipinput">
											</td>
											<td class="column2">
												<input type="text" readonly="true" id="lnglat">
											</td>
										</tr>
									</table>
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
<link rel="stylesheet" href="http://cache.amap.com/lbs/static/main1119.css"/>
<script src="http://webapi.amap.com/maps?v=1.4.4&key=db4b48fb15aecc4e23ffd395910dd5a6"></script>
<script type="text/javascript" src="http://cache.amap.com/lbs/static/addToolbar.js"></script>
<script>
  var map = new AMap.Map('container', {
        resizeEnable: true,
        zoom:11,
        center: [116.397428, 39.90923]
    });
  var tableHtml = '<div id="gridWin" >\
	<form>\
		<div id="searchForm" style="filter:alpha(opacity=1); background:#FFF; filter:alpha(Opacity=80);-moz-opacity:0.5;opacity: 0.5;" > </div>\
	</form> \
	<div id="mainGrid" style="margin:0; padding:0"></div>\
</div>';
  AMap.homeControlDiv = function() {
  }
  AMap.homeControlDiv.prototype = {
      addTo : function(mymap, dom) {
          dom.appendChild(this._getHtmlDom(map));
          this.addMyownEvent();
      },
      addMyownEvent : function(){
          $(".btn-clipboard").click(function(){
              if(controlUI != null)
                  controlUI.style.display='none';
              homeControlbtn.style.display='block';
          });
      },
      _getHtmlDom : function(mymap) {
          this.map = mymap;
          controlUI = document.createElement("DIV");
          controlUI.className = ""; // 设置控件容器的宽度
          controlUI.style.width = "1000px"; // 设置控件容器的宽度
          controlUI.style.borderStyle = "solid";
          controlUI.style.borderWidth = "0px";
          controlUI.style.textAlign = "center"; // 设置控件的位置
          controlUI.style.position = "absolute";
          controlUI.style.left = "60px"; // 设置控件离地图的左边界的偏移量
          controlUI.style.top = "1px"; // 设置控件离地图上边界的偏移量
          controlUI.style.zIndex = "300"; // 设置控件在地图上显示 // 设置控件字体样式
          controlUI.style.fontFamily = "Arial,sens-serif";
          controlUI.style.fontSize = "12px";
          controlUI.style.paddingLeft = "0px";
          controlUI.style.paddingRight = "0px";
          controlUI.innerHTML = tableHtml;
          controlUI.onclick = function() {
          }
          return controlUI;
      }
  }
  var homeControl = new AMap.homeControlDiv(map); // 新建自定义插件对象
  map.addControl(homeControl);// 地图上添加插件
</script>
</body>
</html>
</html>