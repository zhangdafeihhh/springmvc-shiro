package com.zhuanche.objcompare;

import java.util.Date;

import com.zhuanche.objcompare.entity.Person;
import org.apache.commons.lang3.StringUtils;

import com.zhuanche.util.objcompare.CompareObejctUtils;

public class DemoTest {
	public static void main(String[] args) {

		Person person1 = new Person();
		person1.setName("123");
		person1.setAge(12);
		person1.setBirthday(new Date());

		Person person2 = new Person();
		person2.setName("abc");
		person2.setAge(22);
		person2.setBirthday(new Date());

		Person person3 = new Person();
		person3.setAge(22);

		Person person4 = new Person();
		person4.setName("abc");
		person4.setAge(22);
		CompareObejctUtils.contrastObj(person1, person2, (attr, results) -> {
			String old = attr.getOld();
			String fresh = attr.getFresh();
			String note = attr.getNote();

			if (StringUtils.isBlank(old)) {
				results.add(note + " 设置为：" + fresh);
			} else if (StringUtils.isBlank(fresh)) {
				results.add("将原有的  " + note + " " + old + " " + " 清空");
			} else {
				results.add("将  " + note + " 由  " + old + " 更新为   " + fresh);
			}
		});
		System.out.println();
		//System.out.println(CompareObejctUtils.contrastObj(person2, person3, null));
		//System.out.println(CompareObejctUtils.contrastObj(person3, person4, null));

	}
}
