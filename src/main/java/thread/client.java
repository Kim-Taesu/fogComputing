package thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class client {
    public static void main(String[] args) {
        Socket CS = null;
        try {
            CS = new Socket();
            CS.connect(new InetSocketAddress("117.16.123.192", 4040));

            File file = new File("C:\\Users\\KTS\\Desktop\\data\\TaxiMach_Link_Dataset_Full_201709.txt");
            String line = "";
            boolean firstLine = true;


            while (true) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufReader = new BufferedReader(fileReader);
                while ((line = bufReader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] lineTmp = line.split(",");
                    if (!lineTmp[4].equals("")) {
                        String newLine = lineTmp[0] + "," + lineTmp[1] + "," + lineTmp[2] + "," + lineTmp[4];
                        System.out.println(newLine);


                        byte[] as = newLine.getBytes("UTF-8");
                        OutputStream OS = CS.getOutputStream();
                        OS.flush();
                        OS.write(as);
                    }
                    Thread.sleep(1000);
                }

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
