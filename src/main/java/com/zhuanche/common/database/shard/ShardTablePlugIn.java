package com.zhuanche.common.database.shard;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
/**
 * Mybatis简易插件：扩展以支持逻辑分表
 * @author ZHAOYALI
 **/
@Intercepts({ @Signature(type = StatementHandler.class,method = "prepare", args = { Connection.class, Integer.class}) })
public class ShardTablePlugIn implements Interceptor{
	/**Mapper方法中的分表参数**/
	public final static String SHARD_PARAM = "_SHARD_PARAM";
    /**需要分表的表名**/
    private Set<String> shardable_table_names               =  new HashSet<String>(); 
    /**分表策略实现类**/
    private Object               shardTableStrategyObject     = null;
    /**对象领域模型 与 表名 映射**/
    private Map<Class<?>,String> tablenameMappings   = new HashMap<Class<?>,String>();
    /**对象领域模型 与 分表策略实现类的某方法  映射**/
    private Map<Class<?>,Method> methodMappings    = new HashMap<Class<?>,Method>();
	
	/** 线程安全类，初始化常量，避免重复创建**/
    private final static ObjectFactory               DEFAULT_OBJECT_FACTORY                   = new DefaultObjectFactory();
    private final static ObjectWrapperFactory  DEFAULT_OBJECT_WRAPPER_FACTORY  = new DefaultObjectWrapperFactory();	
    private final static ReflectorFactory           DEFAULT_REFLECTOR_FACTORY             = new DefaultReflectorFactory();
    private final static String                          BOUNDSQL_SQL_NAME                          = "sql";
    
	@SuppressWarnings("rawtypes")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
		BoundSql boundSql      = statementHandler.getBoundSql();
		//1.获得原始SQL
		String sql             = boundSql.getSql();
		if(sql!=null && sql.trim().toUpperCase().equals("SELECT LAST_INSERT_ID()")){
	        return invocation.proceed();		
		}
		
        //1.获得Mapper方法的参数
		Object methodParameter = boundSql.getParameterObject();
        MetaObject boundSqlMetaObject = MetaObject.forObject(boundSql, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        
        String tableSeq = null;//分表的表序
        //2.获得表序
        if(methodParameter!=null && methodParameter instanceof MapperMethod.ParamMap){//采用注解指定参数名的情况下
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap)methodParameter;
            if(paramMap.containsKey(SHARD_PARAM)){
            	tableSeq = paramMap.get(SHARD_PARAM).toString();
            }
            //2.1.表序获取成功，则替换表名
            if(tableSeq!=null && tableSeq.length()>0){
            	for(String tablename : shardable_table_names){
            		sql = sql.replace(tablename, tablename+"_"+tableSeq);
            	}
            	boundSqlMetaObject.setValue(BOUNDSQL_SQL_NAME, sql);
            }
        }else if(methodParameter!=null && methodParameter instanceof HashMap){//采用PageHelper的情况下
        	HashMap paramMap = (HashMap)methodParameter;
            if(paramMap.containsKey(SHARD_PARAM)){
            	tableSeq = paramMap.get(SHARD_PARAM).toString();
            }
            //2.1.表序获取成功，则替换表名
            if(tableSeq!=null && tableSeq.length()>0){
            	for(String tablename : shardable_table_names){
            		sql = sql.replace(tablename, tablename+"_"+tableSeq);
            	}
            	boundSqlMetaObject.setValue(BOUNDSQL_SQL_NAME, sql);
            }
        }else if(methodParameter!=null && tablenameMappings.keySet().contains(methodParameter.getClass()) ){//参数为某个PO的情况下
        	//2.1表序获取成功，则替换表名
    		String tablename = tablenameMappings.get(methodParameter.getClass());
    		Method method    = methodMappings.get(methodParameter.getClass());
    		Object res = method.invoke(shardTableStrategyObject, methodParameter);
    		if(res!=null && res.toString().length()>0){
        		tableSeq = res.toString();
        		sql = sql.replace(tablename, tablename+"_"+tableSeq);
        		boundSqlMetaObject.setValue(BOUNDSQL_SQL_NAME, sql);
    		}
        }
        return invocation.proceed();		
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		if(properties==null || properties.isEmpty()){
			return;
		}
		//解析分表策略配置
		String shardTableStrategyClass = properties.getProperty("shardTableStrategyClass");
		if(shardTableStrategyClass==null||shardTableStrategyClass.trim().length()==0){
			return;
		}
		try{
			shardTableStrategyObject = Class.forName(shardTableStrategyClass.trim()).newInstance();
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
		//获得所有方法
		Method[] methods = shardTableStrategyObject.getClass().getDeclaredMethods();
		if(methods==null||methods.length==0){
			return;
		}
		//每个分表的策略，逐个解析
		for(Method method : methods){
			ShardTableConfig config = method.getAnnotation(ShardTableConfig.class);
			if(config==null){
				continue;
			}
			String tablename     = config.tablename();        //表名
			Class<?> moduleClass = config.moduleClass();//表名对应的持久层的对象领域模型的类
			if(tablename==null || tablename.trim().length()==0 || moduleClass==null ){
				continue;
			}
			shardable_table_names.add(tablename.trim());
			tablenameMappings.put(moduleClass, tablename.trim());
			methodMappings.put(moduleClass, method);
		}
	}
}