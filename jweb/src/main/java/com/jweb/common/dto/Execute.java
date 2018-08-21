package com.jweb.common.dto;

public class Execute {
    public static final String CREATE="_create";
    public static final String UPDATE="_update";
    public static final String REMOVE="_remove";
    private String method;
    private Object bean;

    public Execute(String method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

}
