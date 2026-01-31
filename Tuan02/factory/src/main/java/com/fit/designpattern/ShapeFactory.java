package com.fit.designpattern;

public class ShapeFactory {

    public static Shape createShape(String type) {
        switch (type) {
            case "CIRCLE":
                return new Circle();
            case "RECTANGLE":
                return new Rectangle();
            default:
                throw new IllegalArgumentException("Unknown shape");
        }
    }
}

