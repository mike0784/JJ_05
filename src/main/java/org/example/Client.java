package org.example;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufReader;
    private BufferedWriter bufWriter;
    private String name;

    public Client(Socket socket, String user)
    {
        this.socket = socket;
        this.name = user;
        try {
            bufWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufReader, bufWriter);
        }
    }

    public void sendMessage()
    {
        try {
            bufWriter.write(name);
            bufWriter.newLine();
            bufWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String message = scanner.nextLine();
                bufWriter.write(name+": "+ message);
                bufWriter.newLine();
                bufWriter.flush();
            }
        } catch (IOException e){
            closeEverything(socket, bufReader, bufWriter);
        }
    }

    public void listenForMessage()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroup;
                while (socket.isConnected())
                {
                    try {
                        messageFromGroup = bufReader.readLine();
                        System.out.println(messageFromGroup);
                    } catch (IOException e) {
                        closeEverything(socket, bufReader, bufWriter);
                    }
                }
            }
        }).start();
    }

    private void closeEverything(Socket soc, BufferedReader reader, BufferedWriter writer)
    {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (soc != null) {
                soc.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Ваше имя: ");
        String name = scan.nextLine();
        Socket socket = new Socket("localhost", 1300);
        Client client = new Client(socket, name);
        client.listenForMessage();
        client.sendMessage();
    }
}
