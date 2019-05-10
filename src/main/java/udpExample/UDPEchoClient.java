package udpExample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoClient {

    private String str;
    private BufferedReader file;
    private static int SERVERPORT = 30005;

    public UDPEchoClient(String ip, int port) {
        try {
            InetAddress ia = InetAddress.getByName(ip);
            DatagramSocket ds = new DatagramSocket(port);
            while (true) {
                System.out.print("message : ");
                file = new BufferedReader(new InputStreamReader(System.in));
                str = file.readLine();
                byte buffer[] = str.getBytes();
                DatagramPacket dp = new DatagramPacket(
                        buffer, buffer.length, ia, SERVERPORT);
                ds.send(dp);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPEchoClient("localhost", 30005);
    }
};