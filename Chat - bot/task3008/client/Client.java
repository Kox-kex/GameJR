package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {

    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() {
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() { return  true; }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка отправки");
            clientConnected = false;
        }
    }

    public void run() {
        Thread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("Ошибка");
            }
        }
        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено.\n" + "Для выхода наберите команду 'exit'.");
        } else ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");

        while (clientConnected) {
        String text = ConsoleHelper.readString();
        if (text.equals("exit")) break;
        if (shouldSendTextFromConsole()) sendTextMessage(text);
    }

    }



    public class SocketThread extends Thread {
        @Override
        public synchronized void run() {
            String serverAddress = getServerAddress();
            int serverPort = getServerPort();
            try {
                Socket socket = new Socket(serverAddress, serverPort);
                    connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
                ConsoleHelper.writeMessage("в ране ошибка!!!!!!!!");
            }

        }

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("Присоеденился участник с именем: " + userName);
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("Чат покинул: " + userName);
        }

        protected void notifyConnectionStatusChanged(boolean c) {
            synchronized (Client.this) {
                clientConnected = c;
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            Message message;
            while (true) {
                message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) connection.send(new Message(MessageType.USER_NAME, getUserName()));
                else if (message.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    break;
                } else { throw new IOException("Unexpected MessageType"); }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            Message message;
            while (true) {
                message = connection.receive();
                if (message.getType() == MessageType.TEXT) processIncomingMessage(message.getData());
                else if (message.getType() == MessageType.USER_ADDED) informAboutAddingNewUser(message.getData());
                else if (message.getType() == MessageType.USER_REMOVED) informAboutDeletingNewUser(message.getData());
                else throw new IOException("Unexpected MessageType");
            }
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
