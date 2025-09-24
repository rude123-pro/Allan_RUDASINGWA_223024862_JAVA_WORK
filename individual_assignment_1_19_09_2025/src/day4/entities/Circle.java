package day4.entities;

public class Circle {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }
    public double calculateArea(){
        return (Math.PI*Math.pow(this.radius,2));
    }
    public double calculateCircumference(){
        return ((Math.PI*this.radius)*2);
    }
}
