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
                System.out.println("ready");
                ds.receive(dp);

                String location = new String(dp.getData()).trim();

                String sigunguCode = sigunguHash.get(location).toString();
                int fogNum = fogPort.get(sigunguCode);

                System.out.println("location : " + location);
                System.out.println("fogNum : " + fogNum);

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
                String sendData = "";
                while (documentList.hasNext()) {
                    String line = documentList.next().toString();

                    String[] lineTmp = line.split(" , ");

                    String sendDataTmp = lineTmp[1].replace("}", "");
                    String checkDataTmp= sendDataTmp.split(":")[1].split(",")[2].substring(0,4);

                    System.out.println(sigunguCode + " : " + checkDataTmp);

                    if(!checkDataTmp.equals(sigunguCode)) continue;


                    sendData += sendDataTmp + "\n";
                }

                System.out.println(sendData);
                byte[] sendByte = sendData.getBytes();

                InetAddress ia = dp.getAddress();
//                port = dp.getPort();
//                System.out.println("sendData ip : " + ia + " , sendData port : " + port);
                dp = new DatagramPacket(sendByte, sendByte.length, ia, port);
                ds.send(dp);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new getShowDetail(30126);
    }
}
