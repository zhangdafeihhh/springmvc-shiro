import java.io.File;
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
        /** eclipse IDE 执行代码 start*/
        /*List<String> warnings = new ArrayList<String>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(GeneratorMySql.class.getClassLoader().getResourceAsStream("generatorConfig-rentcar.xml"));
//        Configuration config = cp.parseConfiguration(GeneratorMySql.class.getClassLoader().getResourceAsStream("generatorConfig-driver.xml"));
//        Configuration config = cp.parseConfiguration(GeneratorMySql.class.getClassLoader().getResourceAsStream("generatorConfig-mdbcarmanage.xml"));

        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);*/
        /** eclipse IDE 执行代码 end*/
        /** InteliJ IDE 执行代码 start*/
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        //指定 逆向工程配置文件
//        File configFile = new File("F:\\gitLocalRep\\mp-manage\\src\\test\\java\\generatorConfig-mdbcarmanage.xml");

     File configFile = new File("C:\\IdeaProjects\\mp-manage\\src\\test\\java\\generatorConfig-bigdata.xml");
       // File configFile = new File("C:\\Users\\admin\\IdeaProjects\\mp-manage\\src\\test\\java\\generatorConfig-driver.xml");

        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);
        /** InteliJ IDE 执行代码 end*/
        System.out.println("-------------------GeneratorMySql OK");
	}
}