package day4.testentities;

import day4.entities.Circle;
import day4.entities.Rectangle;
import day4.entities.Square;
import day4.entities.Triangle;

import java.util.Scanner;

public class TestEntities {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        String cont;
        do {
            System.out.println("Select a shape :\n1. Rectangle\n 2. Circle \n3. Square\n 4. Triangle");
            int selection = reader.nextInt();
            char choice;
            switch (selection){
                case 1:
                    System.out.println("What would you like to calculate: \nA. Area \nB. Perimeter");
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        System.out.println("Enter Length: ");
                        double l = reader.nextDouble();
                        System.out.println("Enter Width: ");
                        double w = reader.nextDouble();
                        Rectangle pc = new Rectangle(l,w);
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {
                        System.out.println("Enter Length: ");
                        double l = reader.nextDouble();
                        System.out.println("Enter Width: ");
                        double w = reader.nextDouble();
                        Rectangle pc = new Rectangle(l,w);
                        System.out.println("The Perimeter: "+pc.calculatePerimeter());
                    }
                    else {
                        System.out.println("Invalid Choice!");
                    }
                    break;
                case 2:
                    System.out.println("What would you like to calculate: \nA. Area \nB. Perimeter");
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        System.out.println("Enter Radius: ");
                        double r = reader.nextDouble();
                        Circle pc = new Circle(r);
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {
                        System.out.println("Enter Radius: ");
                        double r = reader.nextDouble();
                        Circle pc = new Circle(r);
                        System.out.println("The Circumference: "+pc.calculateCircumference());
                    }
                    else {
                        System.out.println("Invalid Choice!");
                    }
                    break;
                case 3:
                    System.out.println("What would you like to calculate: \nA. Area \nB. Perimeter");
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        System.out.println("Enter Side: ");
                        double s = reader.nextDouble();
                        Square pc = new Square(s);
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {
                        System.out.println("Enter Side: ");
                        double s = reader.nextDouble();
                        Square pc = new Square(s);
                        System.out.println("The Perimeter: "+pc.calculatePerimeter());
                    }
                    else {
                        System.out.println("Invalid Choice!");
                    }
                    break;

                case 4:
                    System.out.println("What would you like to calculate: \nA. Area \nB. Perimeter");
                    choice = reader.next().charAt(0);
                    if(choice == 'A' || choice=='a'){
                        System.out.println("Enter Base: ");
                        double b = reader.nextDouble();
                        System.out.println("Enter Height: ");
                        double h = reader.nextDouble();
                        Triangle pc = new Triangle(b,h);
                        System.out.println("The area: "+pc.calculateArea());
                    } else if (choice == 'B' || choice=='b') {
                        System.out.println("Enter base: ");
                        double b = reader.nextDouble();
                        System.out.println("Enter height: ");
                        double h = reader.nextDouble();
                        Triangle pc = new Triangle(b,h);
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
            if (!cont.equals("YES")&&!cont.equals("yes")&&!cont.equals("NO")&&!cont.equals("no")){
                System.out.println("Invalid input");
               cont="YES";
            }
        }
        while (cont.equals("YES")||cont.equals("yes"));
        System.out.println("Thank you, for using our program.");
        reader.close();
    }
}
