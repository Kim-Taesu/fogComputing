package example.kafkaExample;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class kafkaHbase {
    public static void main(String[] args) throws Exception {
        Properties configs = new Properties();
        // 환경 변수 설정

        configs.put("bootstrap.servers", "117.16.123.192:9092");     // kafka server host 및 port
        configs.put("session.timeout.ms", "10000");             // session 설정
        configs.put("group.id", "test20180604");                // topic 설정
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");    // key deserializer
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // value deserializer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);    // consumer 생성
        consumer.subscribe(Arrays.asList("test20180604"));      // topic 설정

        Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create());
        Admin admin = connection.getAdmin();
        TableName tableName = TableName.valueOf("kafkaTest");
        Table table = connection.getTable(tableName);
        if (!admin.tableExists(tableName)) {
            HTableDescriptor htable = new HTableDescriptor(tableName);
            htable.addFamily(new HColumnDescriptor("key"));
            htable.addFamily(new HColumnDescriptor("value"));
            admin.createTable(htable);
        } else {
            System.out.println("customer Htable is exists");
        }

        while (true) {  // 계속 loop를 돌면서 producer의 message를 띄운다.
            ConsumerRecords<String, String> records = consumer.poll(5000);
            for (ConsumerRecord<String, String> record : records) {
                String s = record.topic();
                if ("test20180604".equals(s)) {
                    String result = record.value();
//                    System.out.println(result);

                    String[] resultTmp = result.split(",");

                    Put p = new Put(Bytes.toBytes("row1"));
                    //Customer table has personal and address column families. So insert data for 'name' column in 'personal' cf
                    // and 'city' for 'address' cf
                    p.addColumn(Bytes.toBytes("key"), Bytes.toBytes("keyName"), Bytes.toBytes(resultTmp[0]));
                    p.addColumn(Bytes.toBytes("value"), Bytes.toBytes("valueName"), Bytes.toBytes(resultTmp[1]));
                    table.put(p);
                    Get get = new Get(Bytes.toBytes("row1"));
                    Result hbaseResult = table.get(get);
                    byte[] name = hbaseResult .getValue(Bytes.toBytes("key"), Bytes.toBytes("keyName"));
                    byte[] city = hbaseResult .getValue(Bytes.toBytes("value"), Bytes.toBytes("valueName"));
                    System.out.println("keyName: " + Bytes.toString(name) + " valueName: " + Bytes.toString(city));
                } else {
                    throw new IllegalStateException("get message on topic " + record.topic());
                }
            }
        }
    }
}
