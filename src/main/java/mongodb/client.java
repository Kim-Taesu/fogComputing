package mongodb;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class client {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("client 1");

        Socket CS = null;
        try {
            CS = new Socket();
                       CS.connect(new InetSocketAddress("117.16.123.194", 4040));
            System.out.println("SUCCESS");

            while (true) {
                System.out.print("> ");
                String message = input.nextLine();
                byte[] as = message.getBytes("UTF-8");
                OutputStream OS = CS.getOutputStream();
                OS.write(as);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            CS.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }// MAIN
}

