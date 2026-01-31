package com.fit.designpattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

//        Singleton singleton = Singleton.getInstance("F00");
//        Singleton anotherSingleton = Singleton.getInstance("BAR");
//        System.out.println(singleton.value);
//        System.out.println(anotherSingleton.value);
        Thread t1 = new Thread(() -> {
            Singleton s1 = Singleton.getInstance("FOO");
            System.out.println(s1.value);
        });

        Thread t2 = new Thread(() -> {
            Singleton s2 = Singleton.getInstance("BAR");
            System.out.println(s2.value);
        });

        t1.start();
        t2.start();
    }
}
