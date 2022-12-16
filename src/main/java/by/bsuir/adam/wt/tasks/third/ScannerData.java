package by.bsuir.adam.wt.tasks.third;

import java.util.Scanner;

public class ScannerData {
    Scanner sc = new Scanner(System.in);

    public int inputPort() {
        Scanner sc = new Scanner(System.in);
        int port;
        try {
            port = sc.nextInt();
        } catch (NumberFormatException e) {
            throw e;
        }
        return port;
    }

    public String inputString() {
        String str = sc.nextLine();
        return str;
    }

}
