package com.jweb.system.persistent.model;

public enum Expression {
		in("${name} in (${value})"),
        eq("${name} = ${value}"),
        neq("${name} != ${value}"),
        gt("${name} > ${value}"),
        lt("${name} < ${value}"),
        like("${name} like ${value}");
	private String oper;
	private Expression(String oper) {
		this.oper = oper;
	}
	public String value() {
		return this.oper;
	}
}
