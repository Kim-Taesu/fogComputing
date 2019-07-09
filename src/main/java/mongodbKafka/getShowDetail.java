package mongodbKafka;

import com.mongodb.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;


public class getShowDetail {
    HashMap<String, Integer> fogPort = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguHash = new HashMap<String, Integer>();
    HashMap<Integer, String> timeValue = new HashMap<Integer, String>();
    HashMap<String, Double> originStatus = new HashMap<String, Double>();


    public getShowDetail(int port) {
        try {
            init();
            fogSetting fogSetting = new fogSetting();
            sigunguHash = fogSetting.getSigunguCode();
            fogPort = fogSetting.getFogPort();

            DatagramSocket ds = new DatagramSocket(port);

            while (true) {
                byte buffer[] = new byte[512];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
//                System.out.println("ready");
                ds.receive(dp);

                String location = new String(dp.getData()).trim();

                String sigunguCode = sigunguHash.get(location).toString();
                int fogNum = fogPort.get(sigunguCode);


                /**** Connect to MongoDB ****/
                // Since 2.10.0, uses MongoClient
                MongoClient mongo = new MongoClient("192.168.99.100", fogNum);

                /**** Get database ****/
                // if database doesn't exists, MongoDB will create it for you
                DB db = mongo.getDB("originTaxiData");

                /**** Get collection / table from 'testdb' ****/
                // if collection doesn't exists, MongoDB will create it for you
                DBCollection collection = db.getCollection(sigunguCode);
                int index = 0;
                for (int i = 0; i < 48; i += 2) {
                    BasicDBObject fields = new BasicDBObject();
                    fields.put("time", new BasicDBObject("$gte", i).append("$lte", i + 1));

                    DBCursor cursor = collection.find(fields);
                    originStatus.put(timeValue.get(index++), (double) cursor.size());
                }

                String sendData = "";
                Iterator<String> keys = originStatus.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (!keys.hasNext()) sendData += originStatus.get(key);
                    else sendData += originStatus.get(key) + ",";
                }

                byte[] sendByte = (sendData).getBytes();
                System.out.println(sendData);
                InetAddress ia = dp.getAddress();
                dp = new DatagramPacket(sendByte, sendByte.length, ia, port);
                ds.send(dp);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    void init() {
        timeValue.put(0, "00:00~01:00");
        timeValue.put(1, "01:00~02:00");
        timeValue.put(2, "02:00~03:00");
        timeValue.put(3, "03:00~04:00");
        timeValue.put(4, "04:00~05:00");
        timeValue.put(5, "05:00~06:00");
        timeValue.put(6, "06:00~07:00");
        timeValue.put(7, "07:00~08:00");
        timeValue.put(8, "08:00~09:00");
        timeValue.put(9, "09:00~10:00");
        timeValue.put(10, "10:00~11:00");
        timeValue.put(11, "11:00~12:00");
        timeValue.put(12, "12:00~13:00");
        timeValue.put(13, "13:00~14:00");
        timeValue.put(14, "14:00~15:00");
        timeValue.put(15, "15:00~16:00");
        timeValue.put(16, "16:00~17:00");
        timeValue.put(17, "17:00~18:00");
        timeValue.put(18, "18:00~19:00");
        timeValue.put(19, "19:00~20:00");
        timeValue.put(20, "20:00~21:00");
        timeValue.put(21, "21:00~22:00");
        timeValue.put(22, "22:00~23:00");
        timeValue.put(23, "23:00~24:00");

        originStatus.put("00:00~01:00", 0.0);
        originStatus.put("01:00~02:00", 0.0);
        originStatus.put("02:00~03:00", 0.0);
        originStatus.put("03:00~04:00", 0.0);
        originStatus.put("04:00~05:00", 0.0);
        originStatus.put("05:00~06:00", 0.0);
        originStatus.put("06:00~07:00", 0.0);
        originStatus.put("07:00~08:00", 0.0);
        originStatus.put("08:00~09:00", 0.0);
        originStatus.put("09:00~10:00", 0.0);
        originStatus.put("10:00~11:00", 0.0);
        originStatus.put("11:00~12:00", 0.0);
        originStatus.put("12:00~13:00", 0.0);
        originStatus.put("13:00~14:00", 0.0);
        originStatus.put("14:00~15:00", 0.0);
        originStatus.put("15:00~16:00", 0.0);
        originStatus.put("16:00~17:00", 0.0);
        originStatus.put("17:00~18:00", 0.0);
        originStatus.put("18:00~19:00", 0.0);
        originStatus.put("19:00~20:00", 0.0);
        originStatus.put("20:00~21:00", 0.0);
        originStatus.put("21:00~22:00", 0.0);
        originStatus.put("22:00~23:00", 0.0);
        originStatus.put("23:00~24:00", 0.0);

    }

    public static void main(String[] args) {
        new getShowDetail(30123);
    }
}
