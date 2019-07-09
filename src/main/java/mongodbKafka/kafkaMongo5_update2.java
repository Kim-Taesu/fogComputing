package mongodbKafka;

/*
시작하기 전
1. 카프카 서버(117.16.123.192) start_thread_server 실행
2. 클라우드 서버(117.16.123.194) startGetFog 실행
3. example.threadToKafkaExample.sendData.java를 실행하여 client로 데이터 전송

기능
1. 카프카로부터 데이터를 시간 주기마다 가져온다.
2. 가져온 카프카 데이터에 노이즈를 추가하고 원본 데이터, 노이즈 데이터를 몽고디비에 저장한다.
*/

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.bson.Document;

import java.util.List;

public class kafkaMongo5_update2 {
    static fogSetting fogSetting = new fogSetting();

    static double qValue = fogSetting.getqValue();
    static double pValue = fogSetting.getpValue();

    private static int fogPortNum = fogSetting.getFog5Port();
    private static List<String> topicList = fogSetting.getTopicList(fogPortNum);

    private static MongoClient mongo = fogSetting.getMongo(fogPortNum);
    private static MongoDatabase originDB = mongo.getDatabase("originTaxiData");
    private static MongoDatabase noiseDB = mongo.getDatabase("noiseTaxiData");
    static KafkaConsumer<String, String> consumer = fogSetting.getConsumer();

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

        //original data bitmask
        double[] dataBitMask = fogSetting.getInitBitMask().clone();
        int fogBigIndex = fogSetting.getfogBitMaskIndex(topic);
        dataBitMask[fogBigIndex]++;

        //noise data bitmask
        double[] noiseFogBit = addNoise(dataBitMask);

        /**** original Insert ****/
        Document document = new Document();
        // data structure : taxiId, day,
        document.put("taxiId", taxiId);
        document.put("day", Integer.parseInt(dataTmp[1]));
        document.put("time", Integer.parseInt(dataTmp[2]));
        document.put("dest", dataTmp[3]);
        MongoCollection originTable = originDB.getCollection(dataTmp[3]);
        originTable.insertOne(document);

        /**** noise Insert ****/
        document = new Document();
        String newString = "";
        for (int i = 0; i < noiseFogBit.length; i++) {
            if (i == noiseFogBit.length - 1) newString += noiseFogBit[i];
            else newString += noiseFogBit[i] + ",";
        }
        document.put("taxiId", taxiId);
        document.put("day", Integer.parseInt(dataTmp[1]));
        document.put("time", Integer.parseInt(dataTmp[2]));
        document.put("dest", dataTmp[3]);
        document.put("bitMask", newString);
        MongoCollection noiseTable = noiseDB.getCollection(dataTmp[3]);
        noiseTable.insertOne(document);
    }

    public static void main(String[] args) {
        originDB.drop();
        noiseDB.drop();

        //fog 초기화
        fogSetting.initMongo(fogPortNum);

        // 해당 포그 노드가 관리하는 토픽 리스트
        System.out.println(topicList);

        // topic 설정
        consumer.subscribe(topicList);
        while (true) {  // 계속 loop를 돌면서 producer의 message를 띄운다.
            //시간을 설정한다.
            ConsumerRecords<String, String> records = consumer.poll(500);
            for (ConsumerRecord<String, String> record : records)
                addMongo(record.topic(), record.value());
        }
    }
}
