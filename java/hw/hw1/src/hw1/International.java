package hw1;

import java.util.InputMismatchException;
import java.util.Scanner;

public class International {
    static double Mile2Km(double miles){
        return miles*(1.60934);
    }
    static double Km2Mile(double km){
        return km*(0.621371);
    }
    static double Celsius2Fahrenheit(double celsius){
        return celsius*1.8+32;
    }
    static double Fahrenheit2Celsius(double fahrenheit){
       return (fahrenheit-32)/1.8;
    }

    public static double ReadD(double minvalue,
                              double maxvalue)
    {
        double input = 0;
        Scanner scanner = new Scanner(System.in);

        do
        {
            System.out.print("Введите ваше число для перевода: ");
            try{ input = scanner.nextDouble();}catch(InputMismatchException e){
                scanner.skip("[^0-9]");
                continue;
            }


        } while (input < minvalue || input > maxvalue);


        System.out.print(input);
        return input;
    }

    public static int ReadI(int minvalue,
                               int maxvalue)
    {
        int input = 0;
        Scanner scanner = new Scanner(System.in);

        do
        {
            System.out.print("Введите режим: ");
            try{ input = scanner.nextInt();}catch(InputMismatchException e){
                scanner.skip("[^0-9]");
                continue;
            }


        } while (input < minvalue || input > maxvalue);

        return input;
    }

    public static boolean Menu(){
        System.out.println("Выберите, что вы хотите сделать: введите\n" +
                "1, если хотите перевести мили в километры,\n" +
                "2, если хотите перевести километры в мили,\n" +
                "3, если хотите перевести цельсии в фаренгейты,\n" +
                "4, если хотите перевести фаренгеты в цельсии,\n" +
                "что угодно другое для выхода.");
double answer;
        switch (ReadI(Integer.MIN_VALUE,Integer.MAX_VALUE)){
            case(1):
                answer = Mile2Km(ReadD(Double.MIN_VALUE, Double.MAX_VALUE));
                System.out.println(" mi = "+answer+" km!\n");
                return true;
            case(2):
                answer = Km2Mile(ReadD(Double.MIN_VALUE, Double.MAX_VALUE));
                System.out.println(" km = "+answer+" mi!\n");
                return true;
            case(3):
                answer = Celsius2Fahrenheit(ReadD(Double.MIN_VALUE, Double.MAX_VALUE));
                System.out.println(" ℃ = "+answer+" ℉!\n");
                return true;
            case(4):
                answer = Fahrenheit2Celsius(ReadD(Double.MIN_VALUE, Double.MAX_VALUE));
                System.out.println(" ℉ = "+answer+" ℃!\n");
                return true;
            default:
                System.out.println("До свидания!");
                return false;

        }

    }

    public static void main(String[] args) {
        boolean carryOn;
        do {
            carryOn = Menu();
        }while(carryOn);

    }
}
