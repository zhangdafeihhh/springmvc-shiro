import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

/**
 * @Description: mybatis generator反向工具类
 */
public class GeneratorMySql{
	public static void main(String[] args) throws Exception{
		System.out.println("==================start===============");
        List<String> warnings = new ArrayList<String>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
//        Configuration config = cp.parseConfiguration(GeneratorMySql.class.getClassLoader().getResourceAsStream("generatorConfig-rentcar.xml"));
        Configuration config = cp.parseConfiguration(GeneratorMySql.class.getClassLoader().getResourceAsStream("generatorConfig-driver.xml"));
//        Configuration config = cp.parseConfiguration(GeneratorMySql.class.getClassLoader().getResourceAsStream("generatorConfig-mdbcarmanage.xml"));
		
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        System.out.println("-------------------GeneratorMySql OK");
	}
}