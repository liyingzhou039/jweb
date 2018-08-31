package com.jweb.common.persistent.model;


import org.junit.Test;
public class WhereTest {
	@Test
	public void toSql() {
		/*List<String> sexs = new ArrayList<>(2);
		sexs.add("男");
		sexs.add("女");*/
		
		String[] sexs = new String[] {"男","女"};
		
		
		Where w =
		Where.create("userName",Expression.eq,"userName")
		.or("age", Expression.eq, "age")
		.and("sex", Expression.in,sexs)
		.orSub(Where.create("name",Expression.like,"%like%").or("name1", Expression.lt, "name1"))
		.or("age", Expression.eq, 30)
		.and("sex", Expression.in,sexs)
		.andSub(Where.create("name",Expression.like,"%like%").or("name2", Expression.lt, "name2").and("name3", Expression.lt, "name3"))
		.or("score", Expression.gt, "score");
		System.out.println(w);

		Where w1 =
				Where.create().sub(Where.create().sub(Where.create("1",Expression.eq,1).and("2",Expression.eq,2)))
				.orSub(Where.create().sub(Where.create("1",Expression.eq,1).and("2",Expression.eq,2)))
				.and("3",Expression.eq,3);
		System.out.println(w1);
	}
	@Test
	public void array() {
		String[] ss = "1,2,3,4".split(",");
		if(ss.getClass().isArray()) {
			Object[] os = (Object[]) ss;
			for(Object o:os) {
				System.out.println(o+":"+o.getClass().getName());
			}
		}
	}

	@Test
	public void expression(){
		String exString="name=1 && age<100 || ((ttt=3 &&sex in[2,3] ) && (age>10 && name = '%li%')) && hhh=4";
		Where wh = Where.parse(exString);
		System.out.println(wh.toString());
	}


}
