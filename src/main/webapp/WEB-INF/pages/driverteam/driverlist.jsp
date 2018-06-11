<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
	<title>车队管理</title>
</head>
<body>

<form class="layui-form" action="">
  
  <div class="layui-form-item">
    <div class="layui-inline">
      <label class="layui-form-label">城市：</label>
      <div class="layui-input-inline layui-form" lay-filter="cityIdcontainer">
        <select id="cityId" name="cityId"  lay-filter="cityId">
          <option value="">9999</option>
            <option value="honghao">你的工号</option>
            <option value="teacher">你最喜欢的老师</option>
          </optgroup>
        </select>
      </div>
    </div>
    <div class="layui-inline">
      <label class="layui-form-label">供应商：</label>
      <div class="layui-input-inline layui-form" lay-filter="supplierIdcontainer">
        <select id="supplierId" name="supplierId"  lay-filter="supplierId">
          <option value="">直接选择或搜索选择</option>
          <option value="1">layer</option>
          <option value="2">form</option>
          <option value="3">layim</option>
        </select>
      </div>
    </div>
    <div class="layui-inline">
      <label class="layui-form-label">车队：</label>
      <div class="layui-input-inline layui-form" lay-filter="teamIdcontainer">
        <select id="teamId" name="teamId"  lay-filter="teamId">
          <option value="">直接选择0000</option>
          <option value="1">layer4444</option>
          <option value="2">form4444</option>
        </select>
      </div>
	   <button class="layui-btn layui-btn-radius"  lay-submit=“”  lay-filter="searchBtn"><i class="layui-icon">&#xe615;</i>查询</button>
    </div>
    
  </div>
</form>

<table id="demo"  class="layui-table"  lay-filter="test"></table>
<script type="text/html" id="barDemo">
  <a class="layui-btn layui-btn-xs" lay-event="edit"><i class="layui-icon">&#xe642;</i>编辑</a>
  <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="detail"><i class="layui-icon">&#xe60a;</i>班组</a>
  <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del"><i class="layui-icon">&#xe640;</i>删除</a>
</script>

<script>
layui.use(['form', 'layedit','layer','laydate','table'], function(){
   var $ = layui.$ ,form = layui.form ,layer = layui.layer  ,layedit = layui.layedit  ,laydate = layui.laydate, table=layui.table;
  //------------------------------------------------------初始化选择框、及定义选择框事件BEGIN
  //一、先清理一下选择框（可能是开发调试的数据）
  $('#cityId option').remove();   form.render('select', 'cityIdcontainer');
  $('#supplierId option').remove();  form.render('select', 'supplierIdcontainer');
  $('#teamId option').remove();   form.render('select', 'teamIdcontainer');
  var selected_cityId        = '';//变量赋值：已经选择的城市ID
  var selected_supplierId = '';//变量赋值：已经选择的供应商ID
  var selected_teamId      = '';//变量赋值：已经选择的车队ID
  
  //二、AJAX查询城市列表，渲染城市选择框
  $('#cityId').append('<option value="">请选择</option>');  
  $('#cityId').append('<option value="44">北京</option>');  
  $('#cityId').append('<option value="45">上海</option>');  
  form.render('select', 'cityIdcontainer');  
  
  //三、定义选择框事件
  //3.1 定义选择框事件：城市ID
  form.on('select(cityId)', function (data){
	  alert( data.value );
	  //A：变量赋值
	  
	  //B：清空 供应商选择框、车队选择框  及其变量赋值

	  //C：如果选择空值，不发起AJAX，直接返回
	  
	  //D：AJAX查询此城市的供应商，并渲染供应商选择框
	  
  });
  
  //3.2 定义选择框事件：供应商ID
  form.on('select(supplierId)', function (data){
	  //A：变量赋值
	  
	  //B：清空 车队选择框  及其变量赋值

	  //C：如果选择空值，不发起AJAX，直接返回
	  
	  //D：AJAX查询车队，并渲染车队选择框
	  
  });
  
  //3.3 定义选择框事件：车队ID
  form.on('select(teamId)', function (data){
	  //A：变量赋值
	  
  });
  //------------------------------------------------------初始化选择框、及定义选择框事件END
  
  //------------------------------------------------------定义表单提交事件BEGIN
  form.on('submit(searchBtn)', function(data){
	  
    layer.alert(JSON.stringify(data.field), {
      title: '最终的提交信息'
    });
    return false;
  });
  //------------------------------------------------------定义表单提交事件END
  
  //------------------------------------------------------定义数据表格BEGIN
  //第一个实例
  table.render({
    elem: '#demo'
   // ,skin: 'line'
   ,even: true
   ,size: 'sm'
   // ,checkbox: true
   // ,height: 'full-160'
    //,url: 'http://www.layui.com/demo/table/user/' //数据接口
    ,page: true //开启分页
    ,text:{none:'暂无数据'}
    ,cols: [[ //表头
        {type: 'checkbox' , fixed: 'left'}
        ,{type: 'space',title:'xXx' , fixed: 'left'}
        ,{type: 'numbers',title:'序号' , fixed: 'left'}
    
      ,{field: 'id', title: 'ID', width:80, sort: true , fixed: 'left'}
      ,{field: 'username', title: '用户名', width:100, minWidth: 90, edit: 'text' ,style:'color:green' }
      ,{field: 'sex', title: '性别', width:70, sort: true  }
      ,{field: 'city', title: '城市', width:80} 
      ,{field: 'sign', title: '签名', width: 77}
      ,{field: 'experience', title: '积分', width: 50, sort: true}
      ,{field: 'score', title: '评分', width: 50, sort: true}
      ,{field: 'classify', title: '职业', width: 80}
      ,{field: 'wealth', title: '财富', width: 135, sort: true}
      ,{field: 'option', title: '操作', width: 250, toolbar : '#barDemo' }
      
    ]]
  
    ,data:[
    	{"id":1,"username":"zhaoyali","sex":"male"},
    	{"id":2,"username":"zhaoyali","sex":"male"},
    	{"id":3,"username":"zhaoyali","sex":"male"},
    	{"id":4,"username":"zhaoyali","sex":"male"},
    	{"id":5,"username":"zhaoyali","sex":"male"},
    ] 
  
  });  
  
  //------------------------------------------------------定义数据表格END

  
  //监听指定开关
  form.on('switch(switchTest)', function(data){
    layer.msg('开关checked：'+ (this.checked ? 'true' : 'false'), {
      offset: '6px'
    });
    layer.tips('温馨提示：请注意开关状态的文字可以随意定义，而不仅仅是ON|OFF', data.othis)
  });
  
 

  
});

</script>


</body>
</html>