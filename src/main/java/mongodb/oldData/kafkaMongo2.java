package mongodb.oldData;

import com.mongodb.*;
import mongodb.fogSetting;
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


public class kafkaMongo2 {
    static mongodb.fogSetting fogSetting = new fogSetting();
    static HashMap<String, Integer> fogPort;
    static HashMap<Integer, double[]> fogBitMask;

    static double qValue = fogSetting.getqValue();
    static double pValue = fogSetting.getpValue();

    private static int fogPortNum = fogSetting.getFog2Port();

    private static MongoClient mongo;
    private static DB db;
    private static DBCollection table;
    private static DBCollection noiseBitTable;

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
        String taxiId = dataTmp[0];
        String topicDayTime = topic + "|" + dataTmp[1] + "|" + dataTmp[2];


//        System.out.println("topic : " + topic + ", fogPortNum : " + fogPortNum);

        //original data bitmask
        double[] dataBitMask = fogSetting.getInitBitMask().clone();
        int fogBigIndex = fogSetting.getfogBitMaskIndex(topic);
        dataBitMask[fogBigIndex]++;

        //noise data bitmask
        double[] noiseFogBit = addNoise(dataBitMask);

        /**** Insert ****/
        BasicDBObject document = new BasicDBObject();
        // data structure : taxiId, day,
        document.put(taxiId, dataTmp[1] + "," + dataTmp[2] + "," + dataTmp[3]);
        table.insert(document);

        HashMap<String, double[]> valueTmp = new HashMap<String, double[]>();
        valueTmp.put(topicDayTime, noiseFogBit);
        /**** Insert ****/
        document = new BasicDBObject();
        document.put(taxiId, valueTmp);
        noiseBitTable.insert(document);

//        /**** Find and display ****/
//        Iterator<DBObject> documentList = table.find().iterator();
//        while (documentList.hasNext())
//            System.out.println(documentList.next().toString());
//
//        /**** Find and display ****/
//        documentList = noiseBitTable.find().iterator();
//        while (documentList.hasNext())
//            System.out.println(documentList.next().toString());
//        System.out.println("data bit mask");
//        printBitmask(dataBitMask);
//        System.out.println("noise bit mask");
//        printBitmask(noiseFogBit);
//        System.out.println();
    }

    public static void printBitmask(double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }


    public static void main(String[] args) throws Exception {
        /**** Connect to MongoDB ****/
        mongo = new MongoClient("192.168.99.100", fogPortNum);
        System.out.println(fogPortNum);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        db = mongo.getDB("testdb");
        db.dropDatabase();
        System.out.println("drop DB");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you
        table = db.getCollection("taxiData");
        noiseBitTable = db.getCollection("taxiDataNoiseBit");
        fogPort = fogSetting.getFogPort();
        fogBitMask = fogSetting.getFogBitMask();

        Properties configs = new Properties();
        // 환경 변수 설정
        configs.put("bootstrap.servers", "117.16.123.192:9092");     // kafka server host 및 port
        configs.put("session.timeout.ms", "100000");             // session 설정
        configs.put("group.id", "taxiData");                // topic 설정
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");    // key deserializer
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // value deserializer

        List<String> topicList = fogSetting.getTopicList2();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);    // consumer 생성
        consumer.subscribe(topicList);      // topic 설정
        int kafkaSleepTime = fogSetting.getKafkaSleepTime();

        System.out.println(topicList);

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
