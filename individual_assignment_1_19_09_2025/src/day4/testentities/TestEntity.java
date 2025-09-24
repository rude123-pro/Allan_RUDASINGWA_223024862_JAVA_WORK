package day4.testentities;

import day4.entities.Circle;
import day4.entities.Rectangle;
import day4.entities.Square;
import day4.entities.Triangle;

import java.util.Scanner;

public class TestEntity {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        String cont;
        do {
            System.out.println("Select a shape :\n1. Rectangle\n 2. Circle \n3. Square\n 4. Triangle");
            int selection = reader.nextInt();
            char choice;
            switch (selection){
                case 1:
                    displayChoices();
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        Rectangle pc = inputRectangleValue();
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {
                        Rectangle pc = inputRectangleValue();
                        System.out.println("The Perimeter: "+pc.calculatePerimeter());
                    }
                    else {
                        System.out.println("Invalid Choice!");
                    }
                    break;
                case 2:
                    displayChoices();
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        Circle pc =inputCircleValues();
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {
                        Circle pc =inputCircleValues();
                        System.out.println("The Circumference: "+pc.calculateCircumference());
                    }
                    else {
                        System.out.println("Invalid Choice!");
                    }
                    break;
                case 3:
                    displayChoices();
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        Square pc =inputSquareValues();
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {
                        Square pc =inputSquareValues();
                        System.out.println("The Perimeter: "+pc.calculatePerimeter());
                    }
                    else {
                        System.out.println("Invalid Choice!");
                    }
                    break;

                case 4:
                    displayChoices();
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        Triangle pc = inputTriangleValues();
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {

                        Triangle pc = inputTriangleValues();
                        System.out.println("The Perimeter: "+pc.calculatePerimeter());
                    }
                    else {
                        System.out.println("Invalid Choice!");
                    }
                    break;
                default:
                    System.out.println("Invalid Selection!");

            }
            System.out.println("Would you like to continue?? (yes/no)");
            cont = reader.next();
        }
        while (cont.equals("YES")||cont.equals("yes"));
        System.out.println("Thank you, for using our program.");
        reader.close();
    }


    public static Rectangle inputRectangleValue(){
        Scanner reader = new Scanner(System.in);
            System.out.println("Enter Length: ");
            double l = reader.nextDouble();
            System.out.println("Enter Width: ");
            double w = reader.nextDouble();
            return new Rectangle(l,w);

    }
    public static Circle inputCircleValues(){
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter Radius: ");
        double r = reader.nextDouble();
        return new Circle(r);

    }
    public static Square inputSquareValues(){
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter Side: ");
        double s = reader.nextDouble();
       return new Square(s);

    }
    public static Triangle inputTriangleValues(){
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter Base: ");
        double b = reader.nextDouble();
        System.out.println("Enter Height: ");
        double h = reader.nextDouble();
        return new Triangle(b,h);
    }

    public  static void displayChoices(){
        System.out.println("What would you like to calculate: \nA. Area \nB. Perimeter");
    }

}
