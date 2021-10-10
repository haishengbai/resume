package com.xiangxue.jack.bean;

public class FactoryBean {

    public Object factoryMethod() {
        System.out.println("=========factoryMethod=========");
        return new PropertyClass();
    }
}
