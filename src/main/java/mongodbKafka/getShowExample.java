package mongodbKafka;

import com.mongodb.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class getShowExample {
    public static void main(String args[]) {
        /**** Connect to MongoDB ****/
        // Since 2.10.0, uses MongoClient
        MongoClient mongo = new MongoClient("192.168.99.100", 32773);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        DB db = mongo.getDB("noiseTaxiData");

        String sigunguCode = "1117";

        HashMap<Integer, String> timeValue = new HashMap<Integer, String>();
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


        HashMap<String, Double> dataStatus = new HashMap<String, Double>();
        dataStatus.put("00:00~01:00", 0.0);
        dataStatus.put("01:00~02:00", 0.0);
        dataStatus.put("02:00~03:00", 0.0);
        dataStatus.put("03:00~04:00", 0.0);
        dataStatus.put("04:00~05:00", 0.0);
        dataStatus.put("05:00~06:00", 0.0);
        dataStatus.put("06:00~07:00", 0.0);
        dataStatus.put("07:00~08:00", 0.0);
        dataStatus.put("08:00~09:00", 0.0);
        dataStatus.put("09:00~10:00", 0.0);
        dataStatus.put("10:00~11:00", 0.0);
        dataStatus.put("11:00~12:00", 0.0);
        dataStatus.put("12:00~13:00", 0.0);
        dataStatus.put("13:00~14:00", 0.0);
        dataStatus.put("14:00~15:00", 0.0);
        dataStatus.put("15:00~16:00", 0.0);
        dataStatus.put("16:00~17:00", 0.0);
        dataStatus.put("17:00~18:00", 0.0);
        dataStatus.put("18:00~19:00", 0.0);
        dataStatus.put("19:00~20:00", 0.0);
        dataStatus.put("20:00~21:00", 0.0);
        dataStatus.put("21:00~22:00", 0.0);
        dataStatus.put("22:00~23:00", 0.0);
        dataStatus.put("23:00~24:00", 0.0);


        Iterator<String> collectionList = db.getCollectionNames().iterator();
        while(collectionList.hasNext()){

        }
        DBCollection collection = db.getCollection(sigunguCode);

        /**** Find and display ****/
        BasicDBObject fields = new BasicDBObject();
        BasicDBObject where = new BasicDBObject();
        where.put("time", 1);
        where.put("bitMask", 1);
        where.put("_id", 0);
        Iterator<DBObject> documentList = collection.find(fields, where);
        while (documentList.hasNext()) {
            Map tmp = documentList.next().toMap();
            System.out.println(tmp);
            String[] bitMask = tmp.get("bitMask").toString().split(",");
            for (int i = 0; i < bitMask.length; i++) {
                if (Double.parseDouble(bitMask[i]) > 0) {

                }
            }
        }
//        DBCollection collection = db.getCollection(sigunguCode);
//        int index=0;
//        for (int i = 0; i < 48; i += 2) {
//            BasicDBObject fields = new BasicDBObject();
//            fields.put("time", new BasicDBObject("$gte", i).append("$lte", i + 1));
//
//            DBCursor cursor = collection.find(fields);
//            dataStatus.put(timeValue.get(index++), (double) cursor.size());
//        }
//        System.out.println(dataStatus);
    }

}

