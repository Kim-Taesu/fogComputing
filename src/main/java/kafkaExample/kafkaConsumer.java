package kafkaExample;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class kafkaConsumer {
    public static void main(String[] args) {
        Properties configs = new Properties();
        // 환경 변수 설정
        configs.put("bootstrap.servers", "117.16.123.192:9092");     // kafka server host 및 port
        configs.put("session.timeout.ms", "10000");             // session 설정
        configs.put("group.id", "test20180604");                // topic 설정
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");    // key deserializer
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // value deserializer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);    // consumer 생성
        consumer.subscribe(Arrays.asList("1168","1165","1171","1144"));      // topic 설정
        while (true) {  // 계속 loop를 돌면서 producer의 message를 띄운다.
            ConsumerRecords<String, String> records = consumer.poll(500);
            for (ConsumerRecord<String, String> record : records) {
                String s = record.topic();

                System.out.println(s);
                if ("test20180604".equals(s)) {
                    System.out.println(record.value());
                } else {
                    continue;
//                    throw new IllegalStateException("get message on topic " + record.topic());

                }
            }
        }
    }
}