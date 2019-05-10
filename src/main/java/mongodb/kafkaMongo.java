package mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;


//시작하기 전
// 1. 카프카 서버 start_thread_server 실행
// 2. 클라우드 서버 startGetFog 실행
// 3. client로 데이터 전송


public class kafkaMongo {
    static fogSetting fogSetting = new fogSetting();
    static HashMap<String, Integer> fogPort;
    static HashMap<Integer, int[]> fogBitMask;


    static int epsilon = 1;
    static double qValue = 0.0;

    public static void sendCloud(String data) throws Exception {
        Socket socket = null;
        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            socket = new Socket("117.16.123.194", 4040);
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);           //서버로 전송을 위한 OutputStream

            bw.write(data);
            bw.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                osw.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int[] addNoise(int[] originalBitMask, int[] fogBitMaskNoise) {
        int[] result = new int[originalBitMask.length];
        qValue = 1 / (Math.exp(epsilon) + 1);
        System.out.print("noise : ");
        for (int i = 0; i < originalBitMask.length; i++) {
            double randomNum = (Math.random() * 1) + 0;
            if (originalBitMask[i] == 0) {
                if (randomNum < qValue) result[i] = 1 + fogBitMaskNoise[i];
                else result[i] = 0 + fogBitMaskNoise[i];
            } else {
                if (randomNum < 0.5) result[i] = 1 + fogBitMaskNoise[i];
                else result[i] = 0 + fogBitMaskNoise[i];
            }
            System.out.print(result[i] + " ");
        }
        System.out.println();
        return result;
    }


    public static void addMongo(String topic, String data) {
        String[] dataTmp = data.split(",");

        /**** Connect to MongoDB ****/
        // Since 2.10.0, uses MongoClient
        MongoClient mongo = new MongoClient("192.168.99.100", fogPort.get(topic));

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
        mongo.close();


        /** Bit Mask **/
        int fogPortNum = fogPort.get(topic);
        int[] fogBit = fogSetting.getFogBitMask(fogPortNum);
        int fogBitNum = fogSetting.getfogBitMaskIndex(topic);
        fogBit[fogBitNum] += 1;
        int originalTotal = 0;

        System.out.println("topic : " + topic + ", fogPortNum : " + fogPortNum);
        System.out.print("original : ");
        for (int i = 0; i < fogBit.length; i++) {
            System.out.print(fogBit[i] + " ");
            originalTotal += fogBit[i];
        }
        System.out.println();

        int[] noiseFogBit = addNoise(fogBit, fogSetting.getFogBitMaskNoise(fogPortNum));

        fogSetting.setFogBitMask(fogPortNum, fogBit);
        fogSetting.setFogBitMaskNoise(fogPortNum, noiseFogBit);
        fogSetting.setFogBitMaskExpect(fogPortNum, expectNoise(noiseFogBit, originalTotal));
    }

    private static int[] expectNoise(int[] noiseFogBit, int n) {
        int result[] = new int[noiseFogBit.length];
        qValue = 1 / (Math.exp(epsilon) + 1);
        System.out.print("expect : ");
        for (int i = 0; i < noiseFogBit.length; i++) {
            result[i] = (int) ((noiseFogBit[i] - n * qValue) / (0.5 - qValue));
            System.out.print(result[i] + " ");
        }
        System.out.println();

        return result;
    }

    public static void dropDB() {
        Iterator<String> key = fogPort.keySet().iterator();

        while (key.hasNext()) {
            /**** Connect to MongoDB ****/
            // Since 2.10.0, uses MongoClient
            MongoClient mongo = new MongoClient("192.168.99.100", fogPort.get(key.next()));

            /**** Get database ****/
            // if database doesn't exists, MongoDB will create it for you
            DB db = mongo.getDB("testdb");
            db.dropDatabase();
//
            /**** Get collection / table from 'testdb' ****/
            // if collection doesn't exists, MongoDB will create it for you
            DBCollection table = db.getCollection("taxiData");
            table.drop();
        }
    }

    public static void main(String[] args) throws Exception {
        fogPort = fogSetting.getFogPort();
        System.out.println(fogPort);
        fogBitMask = fogSetting.getFogBitMask();

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
            String sendData = "";

            //시간을 설정한다.
            Thread.sleep(1000 * 10);

            ConsumerRecords<String, String> records = consumer.poll(500);
            for (ConsumerRecord<String, String> record : records) {
                String s = record.topic();
                addMongo(s, record.value());
                sendData += record.value() + "\n";
            }
            sendCloud(sendData);
        }
    }
}
