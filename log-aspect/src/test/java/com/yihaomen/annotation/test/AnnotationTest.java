package com.yihaomen.annotation.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yihaomen.service.IComputeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
public class AnnotationTest {

    private static final Logger log = LoggerFactory.getLogger(AnnotationTest.class);

    @Autowired   
    private IComputeService computeService;

    @Test
    public void testCompute() {
    	log.debug("in testCompute():>>>");
    	computeService.compute(10, "s1", 11, "s2");
    }
}