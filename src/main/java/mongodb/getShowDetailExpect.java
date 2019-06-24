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
                String sendData = "";
                while (documentList.hasNext()) {
                    String line = documentList.next().toString();
                    System.out.println(line);

//                    String[] lineTmp = line.split(" , ")[1].split(" : ");
                    String[] lineTmp = line.split(" : ");
//                    for (int i = 0; i < lineTmp.length; i++)
//                        System.out.println(lineTmp[i]);
                    String taxiId = lineTmp[2].split(" , ")[1];
                    String codeDayTime = lineTmp[3].substring(2);
                    String[] noiseBit = lineTmp[4].substring(2).replaceAll("]}}", "").split(" , ");

                    System.out.println(codeDayTime.split("\\|")[0].substring(1) + " ::: " + sigunguCode);
                    if (codeDayTime.split("\\|")[0].substring(1).equals(sigunguCode))
                        sendData += taxiId + " : " + codeDayTime + "\n";
                    else continue;
                }

                System.out.println(sendData);
                byte[] sendByte = sendData.getBytes();

                InetAddress ia = dp.getAddress();
                dp = new DatagramPacket(sendByte, sendByte.length, ia, port);
                ds.send(dp);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new getShowDetailExpect(30125);
    }
}
