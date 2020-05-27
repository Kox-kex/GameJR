package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(ConsoleHelper.readInt())) { //серверу передаем порт
            System.out.println("Сревер запущен");
            while (true) {                                                // бесконечно прооверяем на новые сообщения пользывателя
                Socket socket = server.accept();                          // accept ждет пока кто нибудь не присоедениться к серверу
                try {
                    new Handler(socket).start();
                } finally {
                    //socket.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();  //список со всеми пользывателями чата

    public static void sendBroadcastMessage(Message message) {                         //разослать всем сообщение, т.к. в чате видят все сообщение
        connectionMap.forEach((a, b) -> {
            try {
                b.send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Сообщение не отправлено!");
            }
        });
    }


    private static class Handler extends  Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Было установленно соеденение с удаленным адрессом: " + socket.getRemoteSocketAddress());

            try (Connection connection = new Connection(socket)) {
                String userName = serverHandshake(connection);
                        sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                        try {
                            notifyUsers(connection, userName);
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом 1!");
                        }
                        try {
                            serverMainLoop(connection, userName);
                        } catch (IOException | ClassNotFoundException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом 2!");
                        }
                        if (connectionMap.containsKey(userName)) {
                            connectionMap.remove(userName);
                            sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                        }
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом 3!");
            }
        }




        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            // метод Рукопожатие -  просит ввести имя пользователя и потом проверяет это имя если все ок то добавляет имя и соеденение в карту
            connection.send(new Message(MessageType.NAME_REQUEST));
            Message message = connection.receive();
            if (message.getType() == MessageType.USER_NAME && !message.getData().isEmpty() && !connectionMap.containsKey(message.getData())) {
                connectionMap.put(message.getData(), connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
            } else serverHandshake(connection);
            return connection.receive().getData();
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            //отправка клиенту (новому участнику) информации об остальных клиентах
            connectionMap.forEach((a, b) -> {
                if (!a.equals(userName)) {
                    try {
                        connection.send(new Message(MessageType.USER_ADDED, a));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            // если напечатан текст то после проверки напечатать его в чат всем
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String text = userName + ":" + " " + message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, text));
                } else ConsoleHelper.writeMessage("error");
            }
        }
    }
}
