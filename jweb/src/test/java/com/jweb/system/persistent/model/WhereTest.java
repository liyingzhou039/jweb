package com.jweb.system.persistent.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class WhereTest {
	@Test
	public void toSql() {
		List<String> sexs = new ArrayList<>(2);
		sexs.add("男");
		sexs.add("女");
		
		Where w = 
		Where.build()
		.and("userName",Expression.eq,"userName")
		.or("age", Expression.eq, "age")
		.and("sex", Expression.in,sexs)
		.orSub(Where.build().and("name",Expression.like,"%like%").or("name1", Expression.lt, "name1"))
		.or("age", Expression.eq, 30)
		.and("sex", Expression.in,sexs)
		.andSub(Where.build().and("name",Expression.like,"%like%").or("name2", Expression.lt, "name2").and("name3", Expression.lt, "name3"))
		.or("score", Expression.gt, "score");
		System.out.println(w);
	}
}
