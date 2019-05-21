package com.zhuanche.common.syslog;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.util.CompareObjUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.driver.SysLog;
import com.zhuanche.mongo.SysSaveOrUpdateLog;
import com.zhuanche.serv.syslog.SysLogService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.NotFoundException;
import org.aspectj.lang.Signature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 系统日志：切面处理类
 */
@Component
@Aspect
public class SysLogAspect {
	private static final Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

	// 注入service,用来将日志信息保存在数据库
	@Autowired
	private SysLogService sysLogService;
    @Resource(name = "userOperationLogMongoTemplate")
    private MongoTemplate mongoTemplate;

	// 定义切点 @Pointcut
	// 在注解的位置切入代码
	@Pointcut("@annotation(com.zhuanche.common.syslog.SysLogAnn)")
	public void logPoinCut() {
		logger.info("--point cut start--");
	}

	@Around("logPoinCut()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		// 常见日志实体对象
		SysSaveOrUpdateLog sysLog = new SysSaveOrUpdateLog();
		// 获取登录用户账户
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		sysLog.setStartTime(new Date());
		// 拦截的实体类，就是当前正在执行的controller
		Object target = pjp.getTarget();
		// 拦截的方法名称。当前正在执行的方法
		String methodName = pjp.getSignature().getName();
		// 拦截的方法参数
		JSONArray operateParamArray = new JSONArray();
		Object[] args = pjp.getArgs();
		logger.info("--方法传入参数值--"+args.length+"值"+args);
		String classType = target.getClass().getName();
		Class<?> clazz = Class.forName(classType);
		logger.info("--classType--"+classType);
		String clazzName = clazz.getName();
		logger.info("--clazzName--"+clazzName);
		//String[] paramNames = LogAopUtil.getFieldsName(clazz, clazzName, methodName);
		
	    String[] paramNames = ((MethodSignature)pjp.getSignature()).getParameterNames(); // 参数名
		Map mapparam = getParamKeyValue(paramNames,args);
		//Map mapparam = getFieldsName(pjp);

		// String operateParam = JSON.toJSONString(args);
		/*
		 * for (int i = 0; i < args.length; i++) { Object paramsObj = args[i];
		 * //通过该方法可查询对应的object属于什么类型：String type =
		 * paramsObj.getClass().getName(); if(paramsObj instanceof String ||
		 * paramsObj instanceof JSONObject){ String str = (String) paramsObj;
		 * //将其转为jsonobject JSONObject dataJson = JSONObject.parseObject(str);
		 * if(dataJson == null || dataJson.isEmpty() ||
		 * "null".equals(dataJson)){ break; }else{
		 * operateParamArray.add(dataJson); } }else if(paramsObj instanceof
		 * Map){ //get请求，以map类型传参 //1.将object的map类型转为jsonobject类型 Map<String,
		 * Object> map = (Map<String, Object>) paramsObj; JSONObject json =new
		 * JSONObject(map); operateParamArray.add(json); } }
		 */
		// 设置请求参数
		// sysLog.setOperateParams(jsonParam.toJSONString());
		// 拦截的放参数类型
		Signature sig = pjp.getSignature();
		MethodSignature msig = null;
		if (!(sig instanceof MethodSignature)) {
			throw new IllegalArgumentException("该注解只能用于方法");
		}
		msig = (MethodSignature) sig;

		Class[] parameterTypes = msig.getMethod().getParameterTypes();
		Object object = null;
		// 获得被拦截的方法
		Method method = null;
		try {
			method = target.getClass().getMethod(methodName, parameterTypes);
			
		} catch (NoSuchMethodException e1) {
			logger.error("ControllerLogAopAspect around error", e1);
		} catch (SecurityException e1) {
			logger.error("ControllerLogAopAspect around error", e1);
		}
		if (null != method) {
			// 判断是否包含自定义的注解，说明一下这里的SystemLog就是我自己自定义的注解
			if (method.isAnnotationPresent(SysLogAnn.class)) {
				SysLogAnn sysLogAnn = method.getAnnotation(SysLogAnn.class);
				String key = sysLogAnn.parameterKey();
				JSONObject jsonParam = null;
				logger.info("----"+JSON.toJSON(mapparam).toString());
				logger.info("--获取日志bean--"+sysLogAnn.parameterObj());
				if (StringUtils.isNotBlank(sysLogAnn.parameterObj())) {
					JSONObject jsonObjPa = (JSONObject) JSON.toJSON(mapparam);
					for (Map.Entry<String, Object> entry : jsonObjPa.entrySet()) {
						jsonParam=(JSONObject) entry.getValue();
				         if (jsonParam != null) {
				            break;
				         }
				    }

					//jsonParam = (JSONObject) JSON.toJSON(mapparam.get(0));
				} else {
					jsonParam = new JSONObject(mapparam);
				}
				if (jsonParam==null) {
					// 不需要拦截直接执行
					object = pjp.proceed();
					return object;
				}
			  	String syslogkey=jsonParam.getString(key);
			  	
				operateParamArray.add(jsonParam);
				sysLog.setOperateParams(jsonParam.toJSONString());

				SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
				sysLog.setUsername(user.getName());

				sysLog.setModule(sysLogAnn.module());
				sysLog.setMethod(sysLogAnn.methods());
				// 请求查询操作前数据的spring bean
				String serviceClass = sysLogAnn.serviceClass();
				// 请求查询数据的方法
				String queryMethod = sysLogAnn.queryMethod();
				
				
				
				// 判断是否需要进行操作前的对象参数查询
				if (StringUtils.isNotBlank(sysLogAnn.parameterKey())
						&& StringUtils.isNotBlank(sysLogAnn.parameterType())
						&& StringUtils.isNotBlank(sysLogAnn.queryMethod())
						&& StringUtils.isNotBlank(sysLogAnn.serviceClass())) {
					boolean isArrayResult = sysLogAnn.paramIsArray();
					// 参数类型
					String paramType = sysLogAnn.parameterType();
					if (isArrayResult) {// 批量操作
						// JSONArray jsonarray = (JSONArray) object.get(key);
						// 从请求的参数中解析出查询key对应的value值
						String value = "";
						JSONArray beforeParamArray = new JSONArray();
						for (int i = 0; i < operateParamArray.size(); i++) {
							JSONObject params = operateParamArray.getJSONObject(i);
							JSONArray paramArray = (JSONArray) params.get(key);
							if (paramArray != null) {
								for (int j = 0; j < paramArray.size(); j++) {
									String paramId = paramArray.getString(j);
									// 在此处判断spring bean查询的方法参数类型
									Object data = getOperateBeforeData(paramType, serviceClass, queryMethod, paramId);
									JSONObject json = (JSONObject) JSON.toJSON(data);
									beforeParamArray.add(json);
								}
							}
						}
						sysLog.setBeforeParams(beforeParamArray.toJSONString());

					} else {// 单量操作

						// 从请求的参数中解析出查询key对应的value值
						String value = "";
						for (int i = 0; i < operateParamArray.size(); i++) {
							JSONObject params = operateParamArray.getJSONObject(i);
							value = params.getString(key);
							if (StringUtils.isNotBlank(value)) {
								break;
							}
						}
						// 在此处获取操作前的spring bean的查询方法
						Object data = getOperateBeforeData(paramType, serviceClass, queryMethod, value.trim());
						JSONObject beforeParam = (JSONObject) JSON.toJSON(data);
						sysLog.setBeforeParams(beforeParam.toJSONString());
					}
				}

                try {
                    //执行页面请求模块方法，并返回
                    object = pjp.proceed();
                    //获取系统时间
                    sysLog.setEndTime(new Date());
                    //将object 转化为controller封装返回的实体类：RequestResult
                    if (object instanceof AjaxResponse) {
                    	AjaxResponse requestResult = (AjaxResponse) object;
                        if(requestResult.isSuccess()){
                            //操作流程成功
                        	sysLog.setResultMsg(requestResult.getMsg());
                            if(requestResult.getCode()==0){
                            	if (StringUtils.isBlank(syslogkey)) {
                            		Object obj = requestResult.getData();
                                	JSONObject jsonObj = (JSONObject) JSON.toJSON(obj);
                                	syslogkey=jsonObj.getString(key);
								}
                            	sysLog.setLogKey(syslogkey);
                            	sysLog.setResultStatus(1);
                            }else{
                            	sysLog.setResultStatus(0);
                            }
                           String remarks= CompareObjUtil.getRemarks(sysLogAnn.objClass(), sysLog.getBeforeParams(), sysLog.getOperateParams());
                           sysLog.setRemarks(remarks);
                           logger.info(remarks);
                        }else{
                        	sysLog.setResultMsg("失败");
                        }
					}
                    //保存进数据库
                    if (StringUtils.isNotBlank(sysLog.getRemarks())) {
                      mongoTemplate.insert(sysLog);
					}
                   
                } catch (Throwable e) {
                	sysLog.setResultStatus(0);
                	sysLog.setEndTime(new Date());
                	sysLog.setResultMsg(e.getMessage());
                	//sysLogService.saveLog(sysLog);
                }
			} else {
				// 没有包含注解
				object = pjp.proceed();
			}
		} else {
			// 不需要拦截直接执行
			object = pjp.proceed();
		}
		return object;
	}

	private Map getParamKeyValue(String[] paramNames, Object[] args) {
		Map<String, Object> nameAndArgs = new HashMap<String, Object>();
		if (paramNames.length>0 && args.length>0) {
			for (int i = 0; i < args.length; i++) {
				logger.info("参数名:"+paramNames[i]+"参数值:"+ args[i]);
				nameAndArgs.put(paramNames[i], args[i]);
			}
		}
		return nameAndArgs;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 *
	 * @param paramType:参数类型
	 * @param serviceClass：bean名称
	 * @param queryMethod：查询method
	 * @param value：查询id的value
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public Object getOperateBeforeData(String paramType, String serviceClass, String queryMethod, String value) {
		Object obj = new Object();
		// 在此处解析请求的参数类型，根据id查询数据，id类型有四种：int，Integer,long,Long
		if (paramType.equals("int")) {
			int id = Integer.parseInt(value);
			Method mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,
					Integer.class);
			// 用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
			obj = ReflectionUtils.invokeMethod(mh, SpringContextUtil.getBean(serviceClass), id);

		} else if (paramType.equals("Integer")) {
			Integer id = Integer.valueOf(value);
			/*
			 * Class<? extends Object> aaaa =
			 * SpringContextUtil.getBean(serviceClass).getClass();
			 */
			Method mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,
					Integer.class);
			// 用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
			obj = ReflectionUtils.invokeMethod(mh, SpringContextUtil.getBean(serviceClass), id);

		} else if (paramType.equals("long")) {
			long id = Long.parseLong(value);
			Method mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,
					Long.class);
			// 用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
			obj = ReflectionUtils.invokeMethod(mh, SpringContextUtil.getBean(serviceClass), id);

		} else if (paramType.equals("Long")) {
			Long id = Long.valueOf(value);
			Method mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,
					Long.class);
			// 用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
			obj = ReflectionUtils.invokeMethod(mh, SpringContextUtil.getBean(serviceClass), id);
		}
		return obj;
	}

	/*private  Map getFieldsName(ProceedingJoinPoint joinPoint)
			throws ClassNotFoundException, NoSuchMethodException {
		String classType = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Class<?> clazz = Class.forName(classType);
		Object[] args = joinPoint.getArgs();
		
		try {
			System.out.println(LogAopUtil.getNameAndArgs(this.getClass(), clazz.getName(), methodName, args));
		} catch (NotFoundException e) {
			e.printStackTrace();  
		}
		
		// 参数值
		Object[] args = joinPoint.getArgs();
		Class<?>[] classes = new Class[args.length];
		for (int k = 0; k < args.length; k++) {
			if (args[k] instanceof MultipartFile || args[k] instanceof ServletRequest
					|| args[k] instanceof ServletResponse) {
				continue;
			}

			if (!args[k].getClass().isPrimitive()) {
				// 获取的是封装类型而不是基础类型
				String result = args[k].getClass().getName();
				Class s = map.get(result);
				classes[k] = s == null ? args[k].getClass() : s;
			}
		}
		ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
		// 获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
		Method method = Class.forName(classType).getMethod(methodName, classes);
		// 参数名
		String[] parameterNames = pnd.getParameterNames(method);
		// 通过map封装参数和参数值
		HashMap<String, Object> paramMap = new HashMap();
		for (int i = 0; i < parameterNames.length; i++) {
			paramMap.put(parameterNames[i], args[i]);
		}
		return paramMap;
	}*/

	private Map<String, Object> getFieldsName(ProceedingJoinPoint joinPoint) throws Exception {
		String classType = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		// 参数值
		Object[] args = joinPoint.getArgs();

		Class<?> clazz = Class.forName(classType);
		Map<String, Object> paramMap=LogAopUtil.getNameAndArgsMap(this.getClass(), clazz.getName(), methodName, args);
		/*for (int k = 0; k < args.length; k++) {
			// 对于接受参数中含有MultipartFile，ServletRequest，ServletResponse类型的特殊处理，我这里是直接返回了null。（如果不对这三种类型判断，会报异常）
			if (args[k] instanceof MultipartFile || args[k] instanceof ServletRequest
					|| args[k] instanceof ServletResponse) {
				return null;
			}

			if (args[k]!=null && !args[k].getClass().isPrimitive()) {
				// 当方法参数是基础类型，但是获取到的是封装类型的就需要转化成基础类型
				// String result = args[k].getClass().getName();
				// Class s = map.get(result);
				// 当方法参数是封装类型
				Class s = args[k].getClass();
				classes[k] = s == null ? args[k].getClass() : s;
			}else{
				classes[k]=null;
			}
		}
		ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
		// 获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
		Method method = Class.forName(classType).getMethod(methodName, classes);
		// 参数名
		String[] parameterNames = pnd.getParameterNames(method);
		// 通过map封装参数和参数值
		HashMap<String, Object> paramMap = new HashMap();
		for (int i = 0; i < parameterNames.length; i++) {
			paramMap.put(parameterNames[i], args[i]);
		}*/
		return paramMap;

	}

	private static HashMap<String, Class> map = new HashMap<String, Class>();

	/*
	 * //切面 配置通知
	 * 
	 * @AfterReturning("logPoinCut()") public void saveSysLog(JoinPoint
	 * joinPoint) { System.out.println("切面。。。。。"); //保存日志 SysLog sysLog = new
	 * SysLog();
	 * 
	 * //从切面织入点处通过反射机制获取织入点处的方法 MethodSignature signature = (MethodSignature)
	 * joinPoint.getSignature(); //获取切入点所在的方法 Method method =
	 * signature.getMethod();
	 * 
	 * //获取操作 SysLogAnn sysLogAnn = method.getAnnotation(SysLogAnn.class); if
	 * (sysLogAnn != null) { String value = sysLogAnn.methods();
	 * sysLog.setMethod(value);;//保存获取的操作 }
	 * 
	 * //获取请求的类名 String className = joinPoint.getTarget().getClass().getName();
	 * //获取请求的方法名 String methodName = method.getName();
	 * sysLog.setMethod(className + "." + methodName);
	 * 
	 * //请求的参数 Object[] args = joinPoint.getArgs(); //将参数所在的数组转换成json String
	 * params = JSON.toJSONString(args); sysLog.setOperateParams(params);
	 * sysLog.setStartTime(new Date()); //获取用户名 SSOLoginUser user =
	 * WebSessionUtil.getCurrentLoginUser(); sysLog.setUsername(user.getName());
	 * //获取用户ip地址 HttpServletRequest request =
	 * HttpContextUtils.getHttpServletRequest();
	 * sysLog.setIp(IPUtils.getIpAddr(request));
	 * 
	 * //调用service保存SysLog实体类到数据库 sysLogService.save(sysLog); }
	 */

}