package thread;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

class userThread extends Thread {


    Socket SS;
    int ID;

    userThread(Socket SS, int ID) {
        this.SS = SS;
        this.ID = ID;
    }

    @Override
    public void run() {

        Properties props = new Properties();
        props.put("bootstrap.servers", "117.16.123.192:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        try {

            Double tmpLatitude = 0.0;
            Double tmpLongitude = 0.0;
            String tmpOutput = "";
            boolean init = true;

            while (true) {
                InputStream IS = SS.getInputStream();
                byte[] bt = new byte[256];
                int size = IS.read(bt);

                //String output = new String(bt, 0, size, "UTF-8");
                String output = new String(bt, 0, size, "UTF-8");
                double latitude = Math.round(Double.parseDouble(output.split(",")[3])*100)/100.0;
                double longitude = Math.round(Double.parseDouble(output.split(",")[2])*100)/100.0;


                if (init) {
                    tmpLatitude = new Double(latitude);
                    tmpLongitude = new Double(longitude);
                    tmpOutput = new String(output);
                    init = false;
                } else if (!init && tmpLatitude != latitude || tmpLongitude != longitude) {
                    producer.send(new ProducerRecord<String, String>("cloud", tmpOutput + " => " + output));
                    producer.flush();
                    tmpLatitude = new Double(latitude);
                    tmpLongitude = new Double(longitude);
                    tmpOutput = new String(output);
                }

            }
        } catch (IOException e) {
            System.out.println("--" + ID + " user OUT");
        }
    }

}//

class connectThread extends Thread {

    ServerSocket MSS;
    int Count = 1;

    connectThread(ServerSocket MSS) {
        this.MSS = MSS;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket SS = MSS.accept();
                System.out.println("--" + Count + " user login");

                userThread ust = new userThread(SS, Count);
                ust.start();
                Count++;
            }

        } catch (IOException e) {

            System.out.println("--SERVER CLOSE");
        }
    }
}//

public class serverKafka {

    public static void main(String[] args) {
        ServerSocket MSS = null;

        try {
            MSS = new ServerSocket();
            MSS.bind(new InetSocketAddress(InetAddress.getLocalHost(), 4040));

            System.out.println("--SERVER Waiting...");
            connectThread cnt = new connectThread(MSS);
            cnt.start();

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            MSS.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }// MAIN
}