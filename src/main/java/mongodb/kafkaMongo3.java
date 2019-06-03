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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


//시작하기 전
// 1. 카프카 서버 start_thread_server 실행
// 2. 클라우드 서버 startGetFog 실행
// 3. client로 데이터 전송


public class kafkaMongo3 {
    static fogSetting fogSetting = new fogSetting();
    static HashMap<String, Integer> fogPort;
    static HashMap<Integer, double[]> fogBitMask;

    static double qValue = fogSetting.getqValue();
    static double pValue = fogSetting.getpValue();

    private static int fogPortNum=fogSetting.getFog3Port();

    private static MongoClient mongo;
    private static DB db;
    private static DBCollection table;

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

    public static double[] addNoise(double[] originalBitMask) {
        double[] result = new double[originalBitMask.length];
        for (int i = 0; i < originalBitMask.length; i++) {
            double randomNum = (Math.random() * 1) + 0;
            if (originalBitMask[i] == 0) {
                if (randomNum <= qValue) result[i] = 1;
                else result[i] = 0;
            } else {
                if (randomNum <= pValue) result[i] = 1;
                else result[i] = 0;
            }
        }
        return result;
    }


    public static void addMongo(String topic, String data) {
        String[] dataTmp = data.split(",");

//        System.out.println(topic + " || " + data);



        /**** Insert ****/
        // create a document to store key and value
        BasicDBObject document = new BasicDBObject();
        document.put(dataTmp[0], dataTmp[1] + "," + dataTmp[2] + "," + dataTmp[3]);
        table.insert(document);


        /** Bit Mask **/
        System.out.println("topic : " + topic + ", fogPortNum : " + fogPortNum);

        double[] dataBitMask = fogSetting.getInitBitMask().clone();
        int fogBigIndex = fogSetting.getfogBitMaskIndex(topic);
        dataBitMask[fogBigIndex]++;
        System.out.println("data bit mask");
        printBitmask(dataBitMask);

        double[] noiseFogBit = addNoise(dataBitMask);
        System.out.println("noise bit mask");
        printBitmask(noiseFogBit);

//        double[] expectBitMask = expectNoise(noiseFogBit, originalTotal);
        double[] expectBitMask = expectNoise(fogSetting.getFogBitMaskNoise(fogPortNum),fogSetting.fogBitMaskTotal(fogPortNum));
        System.out.println("expect bit mask");
        printBitmask(expectBitMask);

        System.out.println("original bit mask");
        printBitmask(fogSetting.getFogBitMask(fogPortNum));

        System.out.println("noise bit mask");
        printBitmask(fogSetting.getFogBitMaskNoise(fogPortNum));

        fogSetting.setFogBitMask(fogPortNum, dataBitMask);
        fogSetting.setFogBitMaskNoise(fogPortNum, noiseFogBit);
        fogSetting.setFogBitMaskExpect(fogPortNum, expectBitMask);
        System.out.println();
    }

    public static void printBitmask(double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    private static double[] expectNoise(double[] noiseFogBit, double n) {
        double result[] = new double[noiseFogBit.length];
        for (int i = 0; i < noiseFogBit.length; i++)
            result[i] = ((noiseFogBit[i] - n * qValue) / (0.5 - qValue));
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
        /**** Connect to MongoDB ****/
        // Since 2.10.0, uses MongoClient
        mongo = new MongoClient("192.168.99.100",fogPortNum);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        db = mongo.getDB("testdb");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you
        table = db.getCollection("taxiData");

        fogPort = fogSetting.getFogPort();
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

        List<String> topicList = fogSetting.getTopicList3();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);    // consumer 생성

        consumer.subscribe(topicList);      // topic 설정
        int kafkaSleepTime = fogSetting.getKafkaSleepTime();

        while (true) {  // 계속 loop를 돌면서 producer의 message를 띄운다.
            String sendData = "";

            //시간을 설정한다.
            Thread.sleep(kafkaSleepTime);

            ConsumerRecords<String, String> records = consumer.poll(500);
            for (ConsumerRecord<String, String> record : records) {
                String s = record.topic();
                addMongo(s, record.value());
                sendData += record.value() + "\n";
            }
            if (!sendData.equals("")) sendCloud(sendData);
        }
    }
}
