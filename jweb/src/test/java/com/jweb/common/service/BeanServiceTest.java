package com.jweb.common.service;


import com.jweb.common.util.RegexUtil;
import com.jweb.common.util.StringUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeanServiceTest {
    @Test
    public void sqlWords(){
        String sql="select * from student TTT where t.name=? and t.userName=?";
        List<String> words= RegexUtil.getRegText("([0-9a-zA-Z_]+)",sql);
        for(String word:words)
            System.out.println(word);
        //去掉关键字
        List<String> keys = new ArrayList<>(Arrays.asList(
            "select","from","limit","order","by","asc","desc","join",
                "on","in","left","right","inner","group","and","or",
                "where","update","delete","into","as","all","union"
        ));
        List<String> nokeyWords = new ArrayList<>();
        for(String word:words){
            if(!keys.contains(word.toLowerCase())){
                nokeyWords.add(word);
            }
        }

        for(String word:nokeyWords)
            System.out.println(word+"---"+StringUtil.toSlide(word));

    }

    @Test
    public void orderBy(){
        System.out.println("#"+parseOrderBy("  name Asc ,userName    desc  ")+"#");
    }
    private String parseOrderBy(String orderBy){
        if(null==orderBy) return "";
        String[] cols = orderBy.split(",");
        StringBuffer orderBySql = new StringBuffer();
        for(String col:cols){
            if(col!=null&& !col.trim().equals("")){
                col = col.trim();
                String[] words = col.split("\\s+");
                orderBySql.append(StringUtil.toSlide(words[0].trim()));
                if(words.length>1){
                    orderBySql.append(" ");
                    orderBySql.append(words[1]);
                }
                orderBySql.append(",");
            }
        }
        orderBy = orderBySql.toString();
        if(orderBy.endsWith(","))
            orderBy =orderBy.substring(0,orderBy.length()-1);
        return orderBy.toLowerCase();
    }
}