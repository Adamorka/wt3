package by.bsuir.adam.wt.tasks.third.сlient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientInput extends Thread {
    private final Client client;

    public ClientInput(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (client.isRunning()) {
                String response = reader.readLine();
                client.sendMessage(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
