package com.zhuanche.util.objcompare;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.zhuanche.constants.BusConst;

public class CompareObjectUtils {

	private static BiConsumer<CompareObjectAttr, List<Object>> defaultConsumer = null;
	
	/** 分隔符 **/
	public static final String separator = "<::>";

	static {
		defaultConsumer = new BiConsumer<CompareObjectAttr, List<Object>>() {
			@Override
			public void accept(CompareObjectAttr attr, List<Object> results) {
				String old = attr.getOld();
				String fresh = attr.getFresh();
				String note = "[" + attr.getNote() + "]";

				if (StringUtils.isBlank(old)) {
					results.add(note + "设为 " + fresh);
				} else if (StringUtils.isBlank(fresh)) {
					results.add("将原有" + note + old + " 清空");
				} else {
					results.add("将" + note + "由 " + old + " 改为 " + fresh);
				}
			}
		};
	}

	public static List<Object> contrastObj(Object old, Object fresh,
			BiConsumer<CompareObjectAttr, List<Object>> consumer) {

		// 如果两个对象为null或者内容相等，则直接返回
		if (old == fresh || old.equals(fresh)) {
			return new ArrayList<>();
		}

		Class<? extends Object> clazz = old.getClass();
		if (!clazz.equals(fresh.getClass())) {
			throw new RuntimeException("对比的对象必须为同一种类");
		}

		try {
			Field[] fields = clazz.getDeclaredFields();
			List<Object> results = new ArrayList<>();
			for (Field field : fields) {
				field.setAccessible(true);
				String fieldName = field.getName();
				FieldNote fieldNote = field.getAnnotation(FieldNote.class);
				if (fieldNote == null) {
					continue;
				}
				String noteValue = fieldNote.value();
				FieldNote.Pattern pattern = fieldNote.pattern();

				PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
				Method getter = pd.getReadMethod();
				Object o1 = getter.invoke(old);
				Object o2 = getter.invoke(fresh);
				if (o1 == o2) {
					continue;
				}
				if ((o1 != null && o1.equals(o2)) || (o2 != null && o2.equals(o1))) {
					continue;
				}

				Function<Object, String> defaultFunction = new Function<Object, String>() {
					@Override
					public String apply(Object obj) {
						if (obj instanceof Date) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.value());
							return formatter.format(LocalDateTime.ofInstant(((Date) obj).toInstant(), ZoneId.systemDefault()));
						}
						if (obj instanceof BigDecimal) {
							return BusConst.decimalFormat.format(obj);
						}
						return obj.toString();
					}
				};

				String value1 = Optional.ofNullable(o1).map(defaultFunction).orElseGet(String::new);
				String value2 = Optional.ofNullable(o2).map(defaultFunction).orElseGet(String::new);

				if (consumer == null) {
					consumer = defaultConsumer;
				}
				CompareObjectAttr attr = new CompareObjectAttr();
				attr.setOld(value1);
				attr.setFresh(value2);
				attr.setNote(noteValue);
				consumer.accept(attr, results);
			}
			if (results.isEmpty()) {
				return new ArrayList<>();
			}
			return results;
		} catch (Exception e) {
			throw new RuntimeException("对比出错：" + e.getMessage());
		}
	}

	public static class CompareObjectAttr {
		private String old;
		private String fresh;
		private String note;

		public String getOld() {
			return old;
		}

		public void setOld(String old) {
			this.old = old;
		}

		public String getFresh() {
			return fresh;
		}

		public void setFresh(String fresh) {
			this.fresh = fresh;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

	}

}
