package com.nowcoder.community;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommonTest.class)
public class CommonTest {

    @Test
    public void stringTest() {
        System.out.println(StringUtils.isBlank("123 454"));
        System.out.println(StringUtils.isBlank(" 123454"));
        System.out.println(StringUtils.isBlank("123454 "));
        System.out.println(StringUtils.isBlank(" "));
        System.out.println(StringUtils.isBlank(""));
        //String s;
        //System.out.println(s);
    }


}
