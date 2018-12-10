package com.zhuanche.bus;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;

public class BeanUtilsTest {
	
	public static void main(String[] args) {
		A a = new A();
		a.setAge(18);
		a.setName("yyp");
		System.out.println(JSON.toJSONString(a));
		
		B b = new B();
		b.setAge(21);
		b.setName("abc");
		b.setBirthday(new Date());
		System.out.println(JSON.toJSONString(b));
		
		BeanUtils.copyProperties(a, b);
		System.out.println(JSON.toJSONString(b));
	}
	
	static class A {
		String name;
		Integer age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

	}

	static class B extends A {
		String name;
		Date birthday;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getBirthday() {
			return birthday;
		}

		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}

	}
}
