package mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;


public class getShowDetailExpect {
    HashMap<String, Integer> fogPort = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguHash = new HashMap<String, Integer>();

    public getShowDetailExpect(int port) {
        try {
            fogSetting fogSetting = new fogSetting();
            sigunguHash = fogSetting.getSigunguCode();
            fogPort = fogSetting.getFogPort();
            double qValue = fogSetting.getqValue();

            DatagramSocket ds = new DatagramSocket(port);

            while (true) {
                byte buffer[] = new byte[512];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                System.out.println("ready");
                ds.receive(dp);

                String location = new String(dp.getData()).trim();

                String sigunguCode = sigunguHash.get(location).toString();
                int fogNum = fogPort.get(sigunguCode);

                int fogBitIndex = fogSetting.getfogBitMaskIndex(sigunguCode);

                System.out.println("location : " + location);
                System.out.println("sigunguCode : " + sigunguCode);
                System.out.println("fogBitIndex : " + fogBitIndex);
                System.out.println("fogNum : " + fogNum);

                /**** Connect to MongoDB ****/
                // Since 2.10.0, uses MongoClient
                MongoClient mongo = new MongoClient("192.168.99.100", fogNum);

                /**** Get database ****/
                // if database doesn't exists, MongoDB will create it for you
                DB db = mongo.getDB("testdb");


                /**** Get collection / table from 'testdb' ****/
                // if collection doesn't exists, MongoDB will create it for you
                DBCollection table = db.getCollection("taxiDataNoiseBit");

                /**** Find and display ****/
                Iterator<DBObject> documentList = table.find().iterator();

                HashMap<String, Double> dataStatus = new HashMap<String, Double>();
                dataStatus.put("00:00~06:00", 0.0);
                dataStatus.put("06:00~12:00", 0.0);
                dataStatus.put("12:00~18:00", 0.0);
                dataStatus.put("18:00~24:00", 0.0);

                HashMap<String, Double> totalCount = new HashMap<String, Double>();
                totalCount.put("00:00~06:00", 0.0);
                totalCount.put("06:00~12:00", 0.0);
                totalCount.put("12:00~18:00", 0.0);
                totalCount.put("18:00~24:00", 0.0);

                while (documentList.hasNext()) {
                    String line = documentList.next().toString();
//                    System.out.println(line);
                    String[] lineTmp = line.split(" : ");
                    String codeDayTime = lineTmp[3].substring(2);

                    String[] timeTmp = codeDayTime.split("\\|")[2].replace("\"","").split("");

                    int time = 0;
                    if (timeTmp[0].equals("0")) {
                        time = Integer.parseInt(timeTmp[1]);
                    } else {
                        time = Integer.parseInt(timeTmp[0]) * 10 + Integer.parseInt(timeTmp[1]);
                    }

                    if (time <= 12) totalCount.put("00:00~06:00", totalCount.get("00:00~06:00") + 1);
                    else if (time <= 24) totalCount.put("06:00~12:00", totalCount.get("06:00~12:00") + 1);
                    else if (time <= 36) totalCount.put("12:00~18:00", totalCount.get("12:00~18:00") + 1);
                    else totalCount.put("18:00~24:00", totalCount.get("18:00~24:00") + 1);

                    String[] noiseBit = lineTmp[4].substring(2).replaceAll("]}}", "").split(" , ");
                    // correct location
                    if (Double.parseDouble(noiseBit[fogBitIndex]) > 0) {
                        if (time <= 12) dataStatus.put("00:00~06:00", dataStatus.get("00:00~06:00") + 1);
                        else if (time <= 24) dataStatus.put("06:00~12:00", dataStatus.get("06:00~12:00") + 1);
                        else if (time <= 36) dataStatus.put("12:00~18:00", dataStatus.get("12:00~18:00") + 1);
                        else dataStatus.put("18:00~24:00", dataStatus.get("18:00~24:00") + 1);
                    }
                }

//                System.out.println(totalCount);
//                System.out.println(dataStatus);

                dataStatus.put("00:00~06:00", (dataStatus.get("00:00~06:00") - totalCount.get("00:00~06:00") * qValue) / (0.5 - qValue));
                dataStatus.put("06:00~12:00", (dataStatus.get("06:00~12:00") - totalCount.get("06:00~12:00") * qValue) / (0.5 - qValue));
                dataStatus.put("12:00~18:00", (dataStatus.get("12:00~18:00") - totalCount.get("12:00~18:00") * qValue) / (0.5 - qValue));
                dataStatus.put("18:00~24:00", (dataStatus.get("18:00~24:00") - totalCount.get("18:00~24:00") * qValue) / (0.5 - qValue));

//                System.out.println(dataStatus);

                byte[] sendByte = (dataStatus.get("00:00~06:00") + "," +
                        dataStatus.get("06:00~12:00") + "," +
                        dataStatus.get("12:00~18:00") + "," +
                        dataStatus.get("18:00~24:00")).getBytes();

                InetAddress ia = dp.getAddress();
                dp = new DatagramPacket(sendByte, sendByte.length, ia, port);
                ds.send(dp);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new getShowDetailExpect(30131);
    }
}
