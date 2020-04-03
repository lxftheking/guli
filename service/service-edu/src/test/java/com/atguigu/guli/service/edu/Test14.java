package com.atguigu.guli.service.edu;

import org.junit.Test;

public class Test14 {


    @Test
    public void test01(){
        String[] m={"0xfe","0xfd","0xfb","0xf7","0xef","0xdf","0xbf","0x7f","0xff"};
        int t=0;
        while(true){
            if(t==9){
                t=0;
            }
            for (int i = t; i <m.length ; i++) {
                System.out.print(m[i]+"\t");
            }
            System.out.println();
            t++;

        }
    }

}
