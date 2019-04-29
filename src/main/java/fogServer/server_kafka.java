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
import java.util.Scanner;

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
//            Integer tmpLatitude = 0;
//            Integer tmpLongitude = 0;
            String tmpOutput = "";
            boolean init = true;

            while (true) {
                InputStream IS = SS.getInputStream();
                byte[] bt = new byte[256];
                int size = IS.read(bt);

                //String output = new String(bt, 0, size, "UTF-8");
                String output = new String(bt, 0, size, "UTF-8");

                String line[] = output.split(",");
                System.out.println(output);
//		if(line.length!=4 || line[3].equals("")) continue;
                String destination = line[3];
//                String newLine = line[0] + "," + line[1] + "," + line[2] + "," + line[3];

//		System.out.println(destination);
//		System.out.println(newLine);

                producer.send(new ProducerRecord<String, String>(destination, output));
                producer.flush();
//		System.out.println("send!");

//                producer.send(new ProducerRecord<String, String>("kts", output));
                //System.out.println(output.split(",")[0] + "> " + output);
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
                //              System.out.println("--" + Count + " user login");

                userThread ust = new userThread(SS, Count);
                ust.start();
                Count++;
            }

        } catch (IOException e) {
            System.out.println("--SERVER CLOSE");
        }
    }
}//

public class server_kafka {

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        ServerSocket MSS = null;

        try {
            MSS = new ServerSocket();
            MSS.bind(new InetSocketAddress(InetAddress.getLocalHost(), 4040));

            //        System.out.println("--SERVER Close : input num");
            System.out.println("--SERVER Waiting...");
            connectThread cnt = new connectThread(MSS);
            cnt.start();

            int temp = input.nextInt();


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

