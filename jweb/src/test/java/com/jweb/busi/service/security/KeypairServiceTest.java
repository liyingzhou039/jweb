package com.jweb.busi.service.security;

import com.jweb.JwebApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JwebApplication.class)
public class KeypairServiceTest {
    @Autowired
    KeypairService keypairService;
    @Test
    public void generateKeyPair(){

    }
}