package com.atguigu.guli.service.edu.client;

import java.util.LinkedList;

public class Quick_Sort {
    static int sum=0;
    public static void main(String[] args) {

        new LinkedList<>();

        int[] m = {1, 3, 6, 8, 2, 4};
        sort(m, 0, m.length - 1);
        for (int i = 0; i < m.length; i++) {
            System.out.print(m[i] + "\t");
        }
    }

    public static void sort(int[] m, int l, int r) {

        if (l >= r) {
            return;
        }

            for (int i = 0; i < m.length; i++) {
                System.out.print(m[i] + "\t");
            }
        System.out.println();

        int n = hhhh(m, l, r);
        sort(m, l, n - 1);
        sort(m, n + 1, r);
    }

    public static int hhhh(int[] m, int l, int r) {
        int i = l;
        int j = r + 1;
        int flag = m[l];
        while (true) {
            while (m[++i] <= flag && i != r) {
                ;
            }
            while (m[--j] > flag && j != l) {
                ;
            }
            if (i >= j) {
                break;
            }
            swap(m, i, j);
        }
        swap(m, l, j);
        return j;
    }

    public static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
