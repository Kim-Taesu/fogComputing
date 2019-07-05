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


public class getShowDetail {
    HashMap<String, Integer> fogPort = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguHash = new HashMap<String, Integer>();

    public getShowDetail(int port) {
        try {
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

//                System.out.println("location : " + location);
//                System.out.println("fogNum : " + fogNum);

                /**** Connect to MongoDB ****/
                // Since 2.10.0, uses MongoClient
                MongoClient mongo = new MongoClient("192.168.99.100", fogNum);

                /**** Get database ****/
                // if database doesn't exists, MongoDB will create it for you
                DB db = mongo.getDB("testdb");


                /**** Get collection / table from 'testdb' ****/
                // if collection doesn't exists, MongoDB will create it for you
                DBCollection table = db.getCollection("taxiData");

                /**** Find and display ****/
                Iterator<DBObject> documentList = table.find().iterator();

                HashMap<String, Double> dataStatus = new HashMap<String, Double>();
                dataStatus.put("00:00~06:00", 0.0);
                dataStatus.put("06:00~12:00", 0.0);
                dataStatus.put("12:00~18:00", 0.0);
                dataStatus.put("18:00~24:00", 0.0);

                while (documentList.hasNext()) {
                    String line = documentList.next().toString();
//                    System.out.println(line);
                    String[] lineTmp = line.split(" , ")[1].replace("}", "").split(" : ");
                    String sigungu = lineTmp[1].split(",")[2].replace("}", "").replace("\"","");
//                    System.out.println(sigungu);
                    if (!sigungu.equals(sigunguCode)) continue;

                    String[] timeTmp = lineTmp[1].split(",")[1].split("");
                    int time = 0;
                    if (timeTmp[0].equals("0")) {
                        time = Integer.parseInt(timeTmp[1]);
                    } else {
                        time = Integer.parseInt(timeTmp[0]) * 10 + Integer.parseInt(timeTmp[1]);
                    }
                    if (time <= 12) dataStatus.put("00:00~06:00", dataStatus.get("00:00~06:00") + 1);
                    else if (time <= 24) dataStatus.put("06:00~12:00", dataStatus.get("06:00~12:00") + 1);
                    else if (time <= 36) dataStatus.put("12:00~18:00", dataStatus.get("12:00~18:00") + 1);
                    else dataStatus.put("18:00~24:00", dataStatus.get("18:00~24:00") + 1);
                }
                byte[] sendByte = (dataStatus.get("00:00~06:00") + "," +
                        dataStatus.get("06:00~12:00") + "," +
                        dataStatus.get("12:00~18:00") + "," +
                        dataStatus.get("18:00~24:00")).getBytes();

//                System.out.println(dataStatus);
                InetAddress ia = dp.getAddress();
                dp = new DatagramPacket(sendByte, sendByte.length, ia, port);
                ds.send(dp);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new getShowDetail(30123);
    }
}
