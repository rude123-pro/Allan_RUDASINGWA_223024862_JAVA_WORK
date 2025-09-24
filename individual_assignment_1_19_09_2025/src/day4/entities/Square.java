package day4.entities;

public class Square {
    private double side ;

    public Square(double side) {
        this.side = side;
    }
    public double calculateArea(){
        return (this.side*this.side);
    }
    public double calculatePerimeter(){
        return (this.side*4);
    }
}
