package com.nowcoder.community;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class UnitTests {

    @BeforeClass
    public static void love() {
        System.out.println("before class");
    }

    @BeforeClass
    public static void kiss() {
        System.out.println("before class1");
    }

    @BeforeClass
    public static void make() {
        System.out.println("before class2");
    }

    @AfterClass
    public static void x() {
        System.out.println("after class");
    }

    @AfterClass
    public static void y() {
        System.out.println("after class1");
    }

    @AfterClass
    public static void z() {
        System.out.println("after class2");
    }

    @Before
    public void a() {
        System.out.println("before");
    }

    @Before
    public void c() {
        System.out.println("before1");
    }

    @Before
    public void b() {
        System.out.println("before2");
    }

    @After
    public void d() {
        System.out.println("after");
    }

    @After
    public void f() {
        System.out.println("after1");
    }

    @After
    public void e() {
        System.out.println("after2");
    }

    @Test
    public void test() {
        System.out.println("test");
        String s1 = "make";
        String s2 = "love";
        String s3 = "kiss";
        System.out.println(s1.compareToIgnoreCase(s2));
        System.out.println(s2.compareToIgnoreCase(s3));
        Assert.assertEquals(s1, "make");
    }
}
