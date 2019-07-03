package mongodb;

/*


시작하기 전
1. 카프카 서버(117.16.123.192) start_thread_server 실행
2. 클라우드 서버(117.16.123.194) startGetFog 실행
3. threadToKafka.sendData.java를 실행하여 client로 데이터 전송

기능
1. 카프카로부터 데이터를 시간 주기마다 가져온다.
2. 가져온 카프카 데이터에 노이즈를 추가하고 원본 데이터, 노이즈 데이터를 몽고디비에 저장한다.

*/

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class kafkaMongo5_update {
    static fogSetting fogSetting = new fogSetting();

    static double qValue = fogSetting.getqValue();
    static double pValue = fogSetting.getpValue();

    private static int fogPortNum = fogSetting.getFog5Port();
    private static List<String> topicList = fogSetting.getTopicList5();
    private static MongoClient mongo;
    private static DB db;
    private static DBCollection table;
    private static DBCollection noiseBitTable;

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
    }

    public static void main(String[] args) throws Exception {
        //몽고 디비 연결
        mongo = new MongoClient("192.168.99.100", fogPortNum);

        //해당 포그 포트 출력
        System.out.println(fogPortNum);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        db = mongo.getDB("testdb");
        db.dropDatabase();

        //처음 시작 시 초기화
        System.out.println("drop DB");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you

        // 원본 택시 데이터가 저장되는 콜렉션
        table = db.getCollection("taxiData");

        // 노이즈 택시 데이터가 저장되는 콜렉션
        noiseBitTable = db.getCollection("taxiDataNoiseBit");

        // 카프카 환경 변수 설정
        Properties configs = new Properties();
        configs.put("bootstrap.servers", "117.16.123.192:9092");     // kafka server host 및 port
        configs.put("session.timeout.ms", "100000");             // session 설정
        configs.put("group.id", "taxiData");                // topic 설정
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");    // key deserializer
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // value deserializer

        // consumer 생성
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);

        // 해당 포그 노드가 관리하는 토픽 리스트
        System.out.println(topicList);

        // topic 설정
        consumer.subscribe(topicList);

        int kafkaSleepTime = fogSetting.getKafkaSleepTime();

        while (true) {  // 계속 loop를 돌면서 producer의 message를 띄운다.
            //시간을 설정한다.
            Thread.sleep(kafkaSleepTime);
            ConsumerRecords<String, String> records = consumer.poll(500);
            for (ConsumerRecord<String, String> record : records)
                addMongo(record.topic(), record.value());
        }
    }
}
