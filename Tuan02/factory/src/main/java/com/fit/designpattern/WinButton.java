package com.fit.designpattern;

public class WinButton implements Button {
    @Override
    public void paint() {
        System.out.println("Render Windows Button");
    }
}
