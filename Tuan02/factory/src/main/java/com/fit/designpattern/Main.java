package com.fit.designpattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        Shape s1 = ShapeFactory.createShape("CIRCLE");
//        Shape s2 = ShapeFactory.createShape("RECTANGLE");
//
//        s1.draw();
//        s2.draw();
        GUIFactory factory;

        String os = "WINDOWS";

        if (os.equals("WINDOWS")) {
            factory = new WinFactory();
        } else {
            factory = new MacFactory();
        }

        Button button = factory.createButton();
        Checkbox checkbox = factory.createCheckbox();

        button.paint();
        checkbox.paint();
    }
}