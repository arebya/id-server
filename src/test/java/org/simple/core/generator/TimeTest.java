package org.simple.core.generator;

import java.util.Date;

/**
 * @author shiya
 * @version 1.0
 * @date 2018/11/8
 **/
public class TimeTest {


    public static void main(String[] args) {
        int time = 2147483646;
        Date d = new Date(Long.valueOf(time));


        System.out.println(Integer.toBinaryString(time));

        System.out.println(d);


    }
}



