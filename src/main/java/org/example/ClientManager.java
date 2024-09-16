package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {
    private Socket socket;
    private BufferedReader bufReader;
    private BufferedWriter bufWriter;
    private String name;
    public static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket)
    {
        try {
            this.socket = socket;
            bufWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufReader.readLine();
            clients.add(this);
            broadcastMessage("Server: " + name + " подключился к чату");
        } catch (IOException e) {
            closeEverything(socket, bufReader, bufWriter);
        }
    }

    @Override
    public void run()
    {
        String messageFromClient;

        while (socket.isConnected())
        {
            try {
                messageFromClient = bufReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufReader, bufWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String messageSend)
    {
        for (ClientManager client: clients) {
            try {
                if (!client.name.equals(name))
                {
                    client.bufWriter.write(messageSend);
                    client.bufWriter.newLine();
                    client.bufWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufReader, bufWriter);
            }
            
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufReader, BufferedWriter bufWriter)
    {
        removeClient();
        try {
            if (bufReader != null) {
                bufReader.close();
            }
            if (bufWriter != null) {
                bufWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void removeClient()
    {
        clients.remove(this);
        broadcastMessage("SERVER: " + name + " покинул чат");
    }
}
