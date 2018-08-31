package com.jweb.common.persistent.model;

public enum Expression {
		in("${name} in (''${value})"),
        eq("${name} = ${value}"),
        neq("${name} != ${value}"),
        gt("${name} > ${value}"),
        lt("${name} < ${value}"),
        like("${name} like ${value}");
	private String expression;
	private Expression(String expression) {
		this.expression = expression;
	}
	public String value() {
		return this.expression;
	}
}
