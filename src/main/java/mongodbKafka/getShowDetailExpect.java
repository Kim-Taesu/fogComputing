package mongodbKafka;

import com.mongodb.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class getShowDetailExpect {
    HashMap<String, Integer> fogPort = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguHash = new HashMap<String, Integer>();
    HashMap<Integer, String> timeValue = new HashMap<Integer, String>();
    HashMap<String, Double> expectStatus = new HashMap<String, Double>();
    HashMap<String, Double> noiseStatus = new HashMap<String, Double>();
    HashMap<String, Double> totalCount = new HashMap<String, Double>();


    public getShowDetailExpect(int port) {
        try {
            fogSetting fogSetting = new fogSetting();
            sigunguHash = fogSetting.getSigunguCode();
            fogPort = fogSetting.getFogPort();
            double qValue = fogSetting.getqValue();
            DatagramSocket ds = new DatagramSocket(port);
            while (true) {
                init();
                byte buffer[] = new byte[512];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                System.out.println("ready");
                ds.receive(dp);

                String location = new String(dp.getData()).trim();

                String sigunguCode = sigunguHash.get(location).toString();
                int fogNum = fogPort.get(sigunguCode);
                Integer topicIndex = fogSetting.getfogBitMaskIndex(sigunguCode);

                /**** Connect to MongoDB ****/
                // Since 2.10.0, uses MongoClient
                MongoClient mongo = new MongoClient("192.168.99.100", fogNum);

                /**** Get database ****/
                // if database doesn't exists, MongoDB will create it for you
                DB db = mongo.getDB("noiseTaxiData");

                Iterator<String> collectionList = db.getCollectionNames().iterator();

                BasicDBObject fields = new BasicDBObject();
                BasicDBObject where = new BasicDBObject();
                where.put("time", 1);
                where.put("bitMask", 1);
                where.put("_id", 0);

                while (collectionList.hasNext()) {
                    DBCollection collection = db.getCollection(collectionList.next());
                    Iterator<DBObject> documentList = collection.find(fields, where);
                    while (documentList.hasNext()) {
                        Map tmp = documentList.next().toMap();
                        String[] bitMask = tmp.get("bitMask").toString().split(",");
                        int time = Integer.parseInt(tmp.get("time").toString());
                        totalCount.put(timeValue.get(time / 2), totalCount.get(timeValue.get(time / 2)) + 1);
                        if (Double.parseDouble(bitMask[topicIndex]) > 0)
                            noiseStatus.put(timeValue.get(time / 2), noiseStatus.get(timeValue.get(time / 2)) + 1);
                    }
                }

                Iterator<String> expectKey = expectStatus.keySet().iterator();
                String sendData = "";
                while (expectKey.hasNext()) {
                    String key = expectKey.next();
                    double tmpValue = (noiseStatus.get(key) - totalCount.get(key) * qValue) / (0.5 - qValue);
                    expectStatus.put(key, tmpValue);
                    if (!expectKey.hasNext()) sendData += tmpValue;
                    else sendData += tmpValue + ",";

                }

//                System.out.println(totalCount);
//                System.out.println(noiseStatus);
//                System.out.println(expectStatus);

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

        expectStatus.put("00:00~01:00", 0.0);
        expectStatus.put("01:00~02:00", 0.0);
        expectStatus.put("02:00~03:00", 0.0);
        expectStatus.put("03:00~04:00", 0.0);
        expectStatus.put("04:00~05:00", 0.0);
        expectStatus.put("05:00~06:00", 0.0);
        expectStatus.put("06:00~07:00", 0.0);
        expectStatus.put("07:00~08:00", 0.0);
        expectStatus.put("08:00~09:00", 0.0);
        expectStatus.put("09:00~10:00", 0.0);
        expectStatus.put("10:00~11:00", 0.0);
        expectStatus.put("11:00~12:00", 0.0);
        expectStatus.put("12:00~13:00", 0.0);
        expectStatus.put("13:00~14:00", 0.0);
        expectStatus.put("14:00~15:00", 0.0);
        expectStatus.put("15:00~16:00", 0.0);
        expectStatus.put("16:00~17:00", 0.0);
        expectStatus.put("17:00~18:00", 0.0);
        expectStatus.put("18:00~19:00", 0.0);
        expectStatus.put("19:00~20:00", 0.0);
        expectStatus.put("20:00~21:00", 0.0);
        expectStatus.put("21:00~22:00", 0.0);
        expectStatus.put("22:00~23:00", 0.0);
        expectStatus.put("23:00~24:00", 0.0);

        noiseStatus.put("00:00~01:00", 0.0);
        noiseStatus.put("01:00~02:00", 0.0);
        noiseStatus.put("02:00~03:00", 0.0);
        noiseStatus.put("03:00~04:00", 0.0);
        noiseStatus.put("04:00~05:00", 0.0);
        noiseStatus.put("05:00~06:00", 0.0);
        noiseStatus.put("06:00~07:00", 0.0);
        noiseStatus.put("07:00~08:00", 0.0);
        noiseStatus.put("08:00~09:00", 0.0);
        noiseStatus.put("09:00~10:00", 0.0);
        noiseStatus.put("10:00~11:00", 0.0);
        noiseStatus.put("11:00~12:00", 0.0);
        noiseStatus.put("12:00~13:00", 0.0);
        noiseStatus.put("13:00~14:00", 0.0);
        noiseStatus.put("14:00~15:00", 0.0);
        noiseStatus.put("15:00~16:00", 0.0);
        noiseStatus.put("16:00~17:00", 0.0);
        noiseStatus.put("17:00~18:00", 0.0);
        noiseStatus.put("18:00~19:00", 0.0);
        noiseStatus.put("19:00~20:00", 0.0);
        noiseStatus.put("20:00~21:00", 0.0);
        noiseStatus.put("21:00~22:00", 0.0);
        noiseStatus.put("22:00~23:00", 0.0);
        noiseStatus.put("23:00~24:00", 0.0);


        totalCount.put("00:00~01:00", 0.0);
        totalCount.put("01:00~02:00", 0.0);
        totalCount.put("02:00~03:00", 0.0);
        totalCount.put("03:00~04:00", 0.0);
        totalCount.put("04:00~05:00", 0.0);
        totalCount.put("05:00~06:00", 0.0);
        totalCount.put("06:00~07:00", 0.0);
        totalCount.put("07:00~08:00", 0.0);
        totalCount.put("08:00~09:00", 0.0);
        totalCount.put("09:00~10:00", 0.0);
        totalCount.put("10:00~11:00", 0.0);
        totalCount.put("11:00~12:00", 0.0);
        totalCount.put("12:00~13:00", 0.0);
        totalCount.put("13:00~14:00", 0.0);
        totalCount.put("14:00~15:00", 0.0);
        totalCount.put("15:00~16:00", 0.0);
        totalCount.put("16:00~17:00", 0.0);
        totalCount.put("17:00~18:00", 0.0);
        totalCount.put("18:00~19:00", 0.0);
        totalCount.put("19:00~20:00", 0.0);
        totalCount.put("20:00~21:00", 0.0);
        totalCount.put("21:00~22:00", 0.0);
        totalCount.put("22:00~23:00", 0.0);
        totalCount.put("23:00~24:00", 0.0);
    }

    public static void main(String[] args) throws Exception {
        new getShowDetailExpect(30132);
    }
}
