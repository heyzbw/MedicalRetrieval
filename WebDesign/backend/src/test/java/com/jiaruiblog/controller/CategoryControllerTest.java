package com.jiaruiblog.controller;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CategoryControllerTest {

    @Test
    public void insert() {
        List<String> list = new ArrayList<>();
        list.add("恶性肿瘤");
        list.add("癌症");
        if(list.contains("恶")){
            System.out.println("yes");
        }
        else {
            System.out.println("no");
        }
    }

    @Test
    public void update() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void list() {
    }

    @Test
    public void addRelationship() {
    }

    @Test
    public void removeRelationship() {
    }
}