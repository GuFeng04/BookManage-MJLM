package com.test;

import book.manage.sql.SQLUtil;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void test1(){
        SQLUtil.doSqlWork(bookMapper -> {
            bookMapper.getBorrowList().forEach(System.out::println);
        });
    }

    @Test
    public void test2(){
        SQLUtil.doSqlWork(bookMapper -> {
            bookMapper.getStudentList().forEach(System.out::println);
        });
    }

    @Test
    public void test3(){
        SQLUtil.doSqlWork(bookMapper -> {
            bookMapper.getBookList().forEach(System.out::println);
        });
    }
}
