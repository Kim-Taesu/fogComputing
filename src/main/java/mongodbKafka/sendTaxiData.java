package mongodbKafka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class sendTaxiData {
    public static void main(String args[]) {
        Socket CS = null;
        try {
            CS = new Socket();
            CS.connect(new InetSocketAddress("117.16.123.192", 4040));

            File file = new File("C:\\Users\\KTS\\Desktop\\43_48.csv");
            String line = "";

            boolean firstLine = true;


//            while (true) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);
            while ((line = bufReader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
//                    System.out.println(line);
                if (line.split(",").length != 4) continue;


                byte[] as = line.getBytes("UTF-8");
//                    System.out.println(line.getBytes("UTF-8").length);
                OutputStream OS = CS.getOutputStream();
                OS.flush();
                OS.write(as);
                OS.flush();
//                    Thread.sleep(50);
            }
//            }

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            CS.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
