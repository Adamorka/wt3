package by.bsuir.adam.wt.tasks.third.server;

import by.bsuir.adam.wt.tasks.third.ScannerData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{


    public static int PORT;
    private static final int BACKLOG = 40;

    private ServerSocket serverSocket;

    @Override
    public void run() {
        System.out.println("Input port");
        ScannerData sc = new ScannerData();
        PORT=sc.inputPort();
        try {
            serverSocket = new ServerSocket(PORT, BACKLOG, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server is running");

        while (true) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                Connect connect = new Connect(clientSocket, this);
                connect.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect(Connect connect) {
        try {
            connect.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client disconnected");
    }
}
