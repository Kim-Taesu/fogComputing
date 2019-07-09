package mongodbKafka;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.*;

public class fogSetting {
    HashMap<String, Integer> fogPort = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguCode = new HashMap<String, Integer>();
    HashMap<String, Integer> fogBitMaskIndex = new HashMap<String, Integer>();
    List<ArrayList<String>> fogList = new ArrayList<ArrayList<String>>();
    List<Integer> fogPortList = new ArrayList<Integer>();

    private int fog1Port = 32773;
    private int fog2Port = 32769;
    private int fog3Port = 32770;
    private int fog4Port = 32771;
    private int fog5Port = 32772;

    ArrayList<String> fog1 = new ArrayList<String>(Arrays.asList("1168", "1111", "1114", "1117", "1165"));
    ArrayList<String> fog2 = new ArrayList<String>(Arrays.asList("1121", "1120", "1171", "1123", "1126"));
    ArrayList<String> fog3 = new ArrayList<String>(Arrays.asList("1144", "1129", "1130", "1132", "1135"));
    ArrayList<String> fog4 = new ArrayList<String>(Arrays.asList("1138", "1141", "1150", "1147", "1153"));
    ArrayList<String> fog5 = new ArrayList<String>(Arrays.asList("1154", "1156", "1159", "1162", "1174"));

//    private String fog1[] = {"1168", "1111", "1114", "1117", "1165"};
//    private String fog2[] = {"1121", "1120", "1171", "1123", "1126"};
//    private String fog3[] = {"1144", "1129", "1130", "1132", "1135"};
//    private String fog4[] = {"1138", "1141", "1150", "1147", "1153"};
//    private String fog5[] = {"1154", "1156", "1159", "1162", "1174"};

    HashMap<Integer, ArrayList<String>> topicList = new HashMap<Integer, ArrayList<String>>() {
        {
            put(fog1Port, fog1);
            put(fog2Port, fog2);
            put(fog3Port, fog3);
            put(fog4Port, fog4);
            put(fog5Port, fog5);
        }
    };

    private double[] initBitMask = {0.0, 0.0, 0.0, 0.0, 0.0};
    private Double epsilon = 1.0;
    private double qValue = 1 / (Math.exp(epsilon) + 1);
    private double pValue = 0.5;

    private MongoClient mongo;
    private KafkaConsumer<String, String> consumer;

    private void initKafka() {
        // 카프카 환경 변수 설정
        Properties configs = new Properties();
        configs.put("bootstrap.servers", "117.16.123.192:9092");     // kafka server host 및 port
        configs.put("session.timeout.ms", "100000");             // session 설정
        configs.put("group.id", "taxiData");                // topic 설정
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");    // key deserializer
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // value deserializer

        // consumer 생성
        consumer = new KafkaConsumer<String, String>(configs);
    }

    public KafkaConsumer<String, String> getConsumer() {
        return consumer;
    }

    public void initMongo(int portNum) {
        mongo = new MongoClient("192.168.99.100", portNum);
        MongoDatabase originDB = mongo.getDatabase("originTaxiData");
        for (int day = 1; day <= 7; day++) {
            for (int time = 0; time <= 48; time++) {
                if (time < 10) originDB.getCollection(day + "_0" + time);
                else originDB.getCollection(day + "_" + time);
            }
        }

        MongoDatabase noiseDB = mongo.getDatabase("noiseTaxiData");
        for (int day = 1; day <= 7; day++) {
            for (int time = 0; time <= 48; time++) {
                if (time < 10) noiseDB.getCollection(day + "_0" + time);
                else noiseDB.getCollection(day + "_" + time);
            }
        }
    }


    public fogSetting() {
        sigunguCode.put("Seoul_Jongno-gu", 1111);
        sigunguCode.put("Seoul_Jung-gu", 1114);
        sigunguCode.put("Seoul_Yongsan-gu", 1117);
        sigunguCode.put("Seoul_Seongdong-gu", 1120);
        sigunguCode.put("Seoul_Gwangjin-gu", 1121);
        sigunguCode.put("Seoul_Dongdaemun-gu", 1123);
        sigunguCode.put("Seoul_Jungnang-gu", 1126);
        sigunguCode.put("Seoul_Seongbuk-gu", 1129);
        sigunguCode.put("Seoul_Gangbuk-gu", 1130);
        sigunguCode.put("Seoul_Dobong-gu", 1132);
        sigunguCode.put("Seoul_Nowon-gu", 1135);
        sigunguCode.put("Seoul_Eunpyeong-gu", 1138);
        sigunguCode.put("Seoul_Seodaemun-gu", 1141);
        sigunguCode.put("Seoul_Mapo-gu", 1144);
        sigunguCode.put("Seoul_Yangcheon-gu", 1147);
        sigunguCode.put("Seoul_Gangseo-gu", 1150);
        sigunguCode.put("Seoul_Guro-gu", 1153);
        sigunguCode.put("Seoul_Geumcheon-gu", 1154);
        sigunguCode.put("Seoul_Yeongdeungpo-gu", 1156);
        sigunguCode.put("Seoul_Dongjak-gu", 1159);
        sigunguCode.put("Seoul_Gwanak-gu", 1162);
        sigunguCode.put("Seoul_Seocho-gu", 1165);
        sigunguCode.put("Seoul_Gangnam-gu", 1168);
        sigunguCode.put("Seoul_Songpa-gu", 1171);
        sigunguCode.put("Seoul_Gangdong-gu", 1174);

        fogList.add(fog1);
        fogList.add(fog2);
        fogList.add(fog3);
        fogList.add(fog4);
        fogList.add(fog5);

        fogPortList.add(fog1Port);
        fogPortList.add(fog2Port);
        fogPortList.add(fog3Port);
        fogPortList.add(fog4Port);
        fogPortList.add(fog5Port);

        for (int i = 0; i < fogPortList.size(); i++) {
            for (int j = 0; j < fogList.size(); j++) {
                // topic number : fog port
                fogPort.put(fogList.get(i).get(j), fogPortList.get(i));
                // topic number : index
                fogBitMaskIndex.put(fogList.get(j).get(i), i);
            }
        }

        initKafka();

        System.out.println("epsilon : " + epsilon);
        System.out.println("qValue : " + qValue);
    }

    public MongoClient getMongo(int portNum) {
        mongo = new MongoClient("192.168.99.100", portNum);
        return mongo;
    }

    public ArrayList<String> getTopicList(int fogPort) {
        return topicList.get(fogPort);
    }

    public HashMap<String, Integer> getFogPort() {
        return fogPort;
    }

    public Integer getfogBitMaskIndex(String topic) {
        return fogBitMaskIndex.get(topic);
    }

    public HashMap<String, Integer> getSigunguCode() {
        return sigunguCode;
    }

    public double getqValue() {
        return qValue;
    }

    public double getpValue() {
        return pValue;
    }

    public double[] getInitBitMask() {
        return initBitMask;
    }

    public int getFog1Port() {
        return fog1Port;
    }

    public int getFog2Port() {
        return fog2Port;
    }

    public int getFog3Port() {
        return fog3Port;
    }

    public int getFog4Port() {
        return fog4Port;
    }

    public int getFog5Port() {
        return fog5Port;
    }
}