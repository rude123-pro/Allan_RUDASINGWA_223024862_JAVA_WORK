package day4.entities;

public class Triangle {
    private double base;
    private  double height;
    private  double hyp;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
        this.hyp=Math.sqrt(Math.pow(base,2)+Math.pow(height,2));
    }
    public double calculateArea(){
        return (this.base*this.height)/2;
    }
    public double calculatePerimeter(){
        return (this.base+this.height+this.hyp);
    }


}
