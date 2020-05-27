package com.javarush.task.task30.task3008;

import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements  Closeable{

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Socket getSocket() {
        return socket;
    }

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

    }
    public void send(Message message) throws IOException {
        synchronized (out) {                        // сериализация
            out.writeObject(message);
        }
    }
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (in) {                         // десериализация
            return (Message) in.readObject();
        }
    }
    // получение удаленного адресса
    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

}
