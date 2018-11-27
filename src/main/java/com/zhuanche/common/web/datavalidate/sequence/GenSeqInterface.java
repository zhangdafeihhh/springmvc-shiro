package com.zhuanche.common.web.datavalidate.sequence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class GenSeqInterface {
	private static final String dir = "C:\\Users\\admin\\git\\mp-manage\\src\\main\\java\\com\\zhuanche\\common\\web\\datavalidate\\sequence\\";
	
	public static void genSeq( int count  ) throws IOException {
		//生成多个SEQ类
		final String seq_template = FileUtils.readFileToString(new File( dir+"Seq0.java" ), "UTF-8");
		for( int i=1;i<count;i++) {
			File seqFile = new File( dir+"Seq"+i+".java" );
			if(seqFile.exists()) {
				seqFile.delete();
			}
			String seqJava = seq_template.replace("0", ""+i );
			FileUtils.writeStringToFile(seqFile , seqJava, "UTF-8", false);
			System.out.println("生成接口类成功:" + seqFile );
		}
		
		//重写SeqAll类
		List<String> allSeqnames = new ArrayList<String>();
		for( int i=0;i<count;i++) {
			allSeqnames.add( "Seq"+i+".class" );
		}
		String newSeqName = "{"+ allSeqnames.stream().collect(Collectors.joining(" ,")) + "}";
		
		final String seqall_template = FileUtils.readFileToString(new File( dir+"SeqAll.java" ), "UTF-8");
		int fromIndex = seqall_template.indexOf("{");
		int endIndex = seqall_template.indexOf("}", fromIndex);
		String oldSeqName= seqall_template.substring(fromIndex, endIndex+1);
		String newFileContent = seqall_template.replace(oldSeqName, newSeqName);
		FileUtils.writeStringToFile(new File( dir+"SeqAll.java" ) , newFileContent, "UTF-8", false);
		System.out.println("重写SeqAll.java类成功" );
	}
	
	
	

	//-----------------------------------------------------------------------------------------以下生成接口类
	public static void main(String[] args) throws IOException {
		GenSeqInterface.genSeq(  100   );  //生成100个接口类
	}
}