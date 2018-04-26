<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
	<%@include file="/WEB-INF/pages/common/header.jsp"%>
	<style type="text/css">
		#tipTop {
			background-color: #fff;
			/*opacity:0.9; !*透明度*!*/
			padding-left: 10px;
			padding-right: 10px;
			position: absolute;
			font-size: 12px;
			right: 10px;
			top: 20px;
			border-radius: 3px;
			border: 1px solid #ccc;
			line-height: 30px;
		}
		.button1 {
			height: 28px;
			line-height: 28px;
			background-color: #0D9BF2;
			color: #FFF;
			border: 0;
			outline: none;
			padding-left: 5px;
			padding-right: 5px;
			border-radius: 3px;
			margin-bottom: 4px;
			cursor: pointer;
		}
	</style>
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
								<div id="tipTop">
									<table>
										<tbody>
										<tr>
											<td><strong>城市:&nbsp;&nbsp;</strong></td>
											<td>
												<select name="cityId" id="cityId">
													<%--<option value=""></option>--%>
													<c:forEach var="city" items="${cityList}">
														<option value="${city.cityId}">${city.cityName}</option>
													</c:forEach>
												</select>
											</td>
											<td>&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="button1" onclick="district(1)" value="组合商圈"/>&nbsp;&nbsp;&nbsp;&nbsp;</td>
											<td><input type="button" class="button1" onclick="district(2)" value="大数据商圈"/>&nbsp;&nbsp;&nbsp;&nbsp;</td>
											<td><input type="button" class="button1" onclick="district(3)" value="默认商圈"/></td>
										</tr>
										</tbody>
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
    var result1;
    var result2;
    var polygons = new Array();
    var yuanCityId;
  var map = new AMap.Map('container', {
        resizeEnable: true,
        zoom:11,
        center: [116.397428, 39.90923]
    });

  //点击按钮查看 1.组合商圈 2.大数据商圈 3.默认商圈
  function district(value){
      var cityId =   $("#cityId ").val();
      if(!cityId||cityId==''){
          alert('请选择城市');
          return ;
	  }
      map.clearMap();
      var cityName = $("#cityId").find("option:selected").text();
      if (!cityName) {
          cityName = '北京市';
      }
      map.setCity(cityName);

	  if(cityId==yuanCityId){
          if(value==2&&result1.length>0){
              setPoint(result1);
          }else if(value==3&&result2.length>0){
              setPoint2(result2);
          }else if(value==1&&(result1.length>0||result2.length>0)){
              setPoint(result1);
              setPoint2(result2);
		  }
		  return ;
	  }else{
          yuanCityId = cityId;
	  }
      // alert(val + "=====" + city);
      $.ajax({
          type: "POST",
          dataType: "json",
          url: "${ctx}district/queryDistrictData.html",
          data: {cityId:cityId},
          success: function (data) {
              result1 = data.result1;
              result2 = data.result2;
              if(value==1){
                  if(result1.length>0){
                      setPoint(result1);
                  }else{
                      alert("没有大数据商圈数据");
                  }
                  if(result2.length>0){
                      setPoint2(result2);
                  }else{
                      alert("没有默认商圈数据");
                  }
			  }else if(value==2){
                  if(result1.length>0){
                      setPoint(result1);
                  }else{
                      alert("没有大数据商圈数据");
                  }
			  } else{
                  if(result2.length>0){
                      setPoint2(result2);
                  }else{
                      alert("没有默认商圈数据");
                  }
			  }
          },
          error: function (data) {
              alert("网络错误");
          }
      })
  }
    function setPoint(data){
        if(data){
            for(var i=0;i<data.length;i++){
                var points = data[i].district;
                var pointArrays = points.split(";");
                if(pointArrays){
                    for(var k=0;k<pointArrays.length;k++){
                        var point = pointArrays[k];
                        if(point){
                            var arr = new Array(); //
                            var pointArray = point.split(":");
                            if(pointArray){
                                for(var j=0;j<pointArray.length;j++){
                                    if(pointArray[j]){
                                        arr.push(new AMap.LngLat(pointArray[j].split(',')[0],pointArray[j].split(',')[1]));
                                    }
                                }
                            }
                            var _p = new AMap.Polygon({
                                map: map,
                                path: arr,
                                strokeColor: "#0000ff",
                                strokeOpacity: 1,
                                strokeWeight: 2,
                                fillColor: "#f5deb3",
                                fillOpacity: 0
                            });
                            polygons.push(_p);
                        }
                    }
                }
            }
        }
    }

    function setPoint2(data){
        if(data){
            for(var i=0;i<data.length;i++){
                var points = data[i].point;
                var pointArrays = points.split("/");
                if(pointArrays){
                    for(var k=0;k<pointArrays.length;k++){
                        var point = pointArrays[k];
                        if(point){
                            var arr = new Array(); //
                            var pointArray = point.split(";");
                            if(pointArray){
                                for(var j=0;j<pointArray.length;j++){
                                    if(pointArray[j]){
                                        arr.push(new AMap.LngLat(pointArray[j].split(',')[0],pointArray[j].split(',')[1]));
                                    }
                                }
                            }
                            var _p = new AMap.Polygon({
                                map: map,
                                path: arr,
                                strokeColor: "#0000ff",
                                strokeOpacity: 1,
                                strokeWeight: 2,
                                fillColor: "#f5deb3",
                                fillOpacity: 0
                            });
                            polygons.push(_p);
                        }
                    }
                }
            }
        }
    }
</script>
</body>
</html>
</html>