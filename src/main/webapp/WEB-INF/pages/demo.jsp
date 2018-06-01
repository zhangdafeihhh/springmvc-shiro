<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
	<title>功能示例</title>
	<link href="/demo/demo/demo.css" rel="stylesheet">
</head>

<body>
			<div class="right_col" role="main">
				<div class="">
					<div class="row top_tiles">
						<div class="animated flipInY col-lg-3 col-md-3 col-sm-6 col-xs-12">
							<div class="tile-stats">
								<div class="icon"><i class="fa fa-caret-square-o-right"></i></div>
								<div class="count">179</div>
								<h3>这是测试页的内容</h3>
								<p>这是测试页的内容</p>
							</div>
						</div>
						<div class="animated flipInY col-lg-3 col-md-3 col-sm-6 col-xs-12">
							<div class="tile-stats">
								<div class="icon"><i class="fa fa-comments-o"></i></div>
								<div class="count">200</div>
								<h3>New Sign ups</h3>
								<p>Lorem ipsum psdea itgum rixt.</p>
							</div>
						</div>
						<div class="animated flipInY col-lg-3 col-md-3 col-sm-6 col-xs-12">
							<div class="tile-stats">
								<div class="icon"><i class="fa fa-sort-amount-desc"></i></div>
								<div class="count">3000</div>
								<h3>New Sign ups</h3>
								<p>Lorem ipsum psdea itgum rixt.</p>
							</div>
						</div>
						<div class="animated flipInY col-lg-3 col-md-3 col-sm-6 col-xs-12">
							<div class="tile-stats">
								<div class="icon"><i class="fa fa-check-square-o"></i></div>
								<div class="count">900</div>
								<h3>New Sign ups</h3>
								<p>Lorem ipsum psdea itgum rixt.</p>
							</div>
						</div>
					</div>
				</div>
				
				<button class="demobtn" urlsrc="${webctx}/demo/ajaxOK.json">示例：AJAX请求正常</button>
				<button class="demobtn" urlsrc="${webctx}/demo/ajaxNoPerm.json">示例：AJAX请求无权限</button>
				<button class="demobtn" urlsrc="${webctx}/demo/ajaxException.json">示例：AJAX请求异常</button>
				<button class="demobtn" urlsrc="${webctx}/demo/abcdefgabcfa.json">示例：AJAX请求不存在的资源</button>
				
			</div>
			
			<script type="text/javascript">
				$('button.demobtn').click(function (){
					var urlsrc =   $(this).attr('urlsrc');
					$.ajax({
			             type: "POST",
			             url: urlsrc ,
			             data: null,
			             dataType: "json",
			             success: function(data){
			            	 	alert( JSON.stringify( data )  );
	             				console.log("=========返回结果："+  JSON.stringify( data ) );
	                   	 }
					});
				} );
			</script>
</body>
</html>