package by.bsuir.adam.wt.tasks.third.server;

import by.bsuir.adam.wt.tasks.third.server.command.Command;
import by.bsuir.adam.wt.tasks.third.server.command.CommandProvider;
import by.bsuir.adam.wt.tasks.third.server.command.exception.CommandException;
import by.bsuir.adam.wt.tasks.third.server.command.impl.DisconnectCommand;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class Connect extends Thread {
    private final Socket socket;
    private final Server server;

    private BufferedReader reader;
    private PrintWriter writer;

    public Connect(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessage("""
                Available commands (enter the command number):
                (1) AUTH USER/MANAGER
                (2) DISCONNECT
                (3) VIEW
                (4) CREATE (firstname) (lastname)
                (5) EDIT (id) (firstname) (lastname)""");

        var running = true;
        do {
            try {
                String request = readMessage();
                if (request == null) {
                    break;
                }

                Command command = CommandProvider.getInstance().getCommand(request);
                String response = command.complete(this, request);
                sendMessage(response);

                if (command instanceof DisconnectCommand) {
                    running = false;
                }
            } catch (CommandException e) {
                e.printStackTrace();
                sendMessage(e.getMessage());
            }
        } while (running);

        server.disconnect(this);
    }

    private String readMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connect that = (Connect) o;
        return socket.equals(that.socket) && server.equals(that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, server);
    }
}
