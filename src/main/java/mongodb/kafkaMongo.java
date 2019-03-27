package mongodb;

import com.mongodb.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class kafkaMongo {
    static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static Date time = new Date();

    static HashMap<String, Integer> fogInfo;


    public static void sendCloud(String data) throws Exception {
        Socket socket = null;
        OutputStream os = null;
        OutputStreamWriter osw =null;
        BufferedWriter bw = null;

        try{
            socket = new Socket("117.16.123.194", 4040);
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);            //서버로 전송을 위한 OutputStream

            bw.write(data);
            bw.flush();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                bw.close();
                osw.close();
                os.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void addMongo(String topic, String data) {
        String[] dataTmp = data.split(",");

        /**** Connect to MongoDB ****/
        // Since 2.10.0, uses MongoClient
        MongoClient mongo = new MongoClient("192.168.99.100", fogInfo.get(topic));

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        DB db = mongo.getDB("testdb");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you
        DBCollection table = db.getCollection("taxiData");

        /**** Insert ****/
        // create a document to store key and value
        BasicDBObject document = new BasicDBObject();
        document.put(dataTmp[0], dataTmp[1] + "," + dataTmp[2] + "," + dataTmp[3]);
        table.insert(document);

        /**** Find and display ****/
        DBCursor cursor = table.find();

//        while (cursor.hasNext()) {
//            System.out.println(cursor.next());
//        }

    }

    public static void dropDB() {
        Iterator<String> key = fogInfo.keySet().iterator();

        while (key.hasNext()) {
            /**** Connect to MongoDB ****/
            // Since 2.10.0, uses MongoClient
            MongoClient mongo = new MongoClient("192.168.99.100", fogInfo.get(key.next()));

            /**** Get database ****/
            // if database doesn't exists, MongoDB will create it for you
            DB db = mongo.getDB("testdb");
            db.dropDatabase();

//            /**** Get database ****/
//            // if database doesn't exists, MongoDB will create it for you
//            db = mongo.getDB("testdb");
//
            /**** Get collection / table from 'testdb' ****/
            // if collection doesn't exists, MongoDB will create it for you
            DBCollection table = db.getCollection("taxiData");
            table.drop();
        }
    }

    public static void main(String[] args) throws Exception {
        fogPort fogPort = new fogPort();
        fogPort.initFog();
        fogInfo = fogPort.fog;

        dropDB();
        System.out.println("drop DB");

        Properties configs = new Properties();
        // 환경 변수 설정
        configs.put("bootstrap.servers", "117.16.123.192:9092");     // kafka server host 및 port
        configs.put("session.timeout.ms", "100000");             // session 설정
        configs.put("group.id", "taxiData");                // topic 설정
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");    // key deserializer
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // value deserializer


        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);    // consumer 생성
        consumer.subscribe(Arrays.asList("1111", "1114", "1117", "1120",
                "1121", "1123", "1126", "1129",
                "1130", "1132", "1135", "1138",
                "1141", "1144", "1147", "1150",
                "1153", "1154", "1156", "1159",
                "1162", "1165", "1168", "1171", "1174"));      // topic 설정


        while (true) {  // 계속 loop를 돌면서 producer의 message를 띄운다.
            String sendData="";
            Thread.sleep(1000 * 60*10);

            ConsumerRecords<String, String> records = consumer.poll(500);
//            System.out.println("!");
            for (ConsumerRecord<String, String> record : records) {
                String s = record.topic();
//                System.out.println(s+ " || " + record.value());
                addMongo(s, record.value());
                sendData+=record.value()+"\n";
            }

            System.out.println(sendData);

            sendCloud(sendData);
        }
    }
}
