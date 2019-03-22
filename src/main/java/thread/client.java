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

            File dataDirectory = new File("src/data/release/taxi_log_2008_by_id");
            File[] dataList = dataDirectory.listFiles();
            String line = "";


            while (true) {
                for (File tmp : dataList) {
                    System.out.println(tmp);
                    FileReader fileReader = new FileReader(tmp);
                    BufferedReader bufReader = new BufferedReader(fileReader);
                    while ((line = bufReader.readLine()) != null) {
                        byte[] as = line.getBytes("UTF-8");
                        OutputStream OS = CS.getOutputStream();
                        OS.flush();
                        OS.write(as);
                        Thread.sleep(1000);
                    }
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
