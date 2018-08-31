package com.jweb.common.persistent.model;

public class Condition {
	private String name;
	private Expression expression;
	private Object value;
	public Condition(){}
	public Condition(String name,Expression ex,Object value){
		this.name = name;
		this.expression = ex;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Expression getExpression() {
		return expression;
	}
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
