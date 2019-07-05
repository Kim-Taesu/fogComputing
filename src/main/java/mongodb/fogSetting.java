package mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class fogSetting {
    HashMap<String, Integer> fogPort = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguCode = new HashMap<String, Integer>();
    HashMap<Integer, double[]> fogBitMask = new HashMap<Integer, double[]>();
    HashMap<Integer, double[]> fogBitMaskNoise = new HashMap<Integer, double[]>();
    HashMap<String, Integer> fogBitMaskIndex = new HashMap<String, Integer>();
    List<String[]> fogList = new ArrayList<String[]>();
    List<Integer> fogPortList = new ArrayList<Integer>();

    private int kafkaSleepTime = 1000 * 60;

    private String fog1[] = {"1168", "1111", "1114", "1117", "1165"};
    private String fog2[] = {"1121", "1120", "1171", "1123", "1126"};
    private String fog3[] = {"1144", "1129", "1130", "1132", "1135"};
    private String fog4[] = {"1138", "1141", "1150", "1147", "1153"};
    private String fog5[] = {"1154", "1156", "1159", "1162", "1174"};

    private List<String> topicList1 = new ArrayList<String>();
    private List<String> topicList2 = new ArrayList<String>();
    private List<String> topicList3 = new ArrayList<String>();
    private List<String> topicList4 = new ArrayList<String>();
    private List<String> topicList5 = new ArrayList<String>();

    private int fog1Port = 32773;
    private int fog2Port = 32769;
    private int fog3Port = 32770;
    private int fog4Port = 32771;
    private int fog5Port = 32772;

    private double[] initBitMask = {0.0, 0.0, 0.0, 0.0, 0.0};
    private Double epsilon = 1.0;
    private double qValue = 1 / (Math.exp(epsilon) + 1);
    private double pValue = 0.5;


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


        for (int i = 0; i < fog1.length; i++) topicList1.add(fog1[i]);
        for (int i = 0; i < fog1.length; i++) topicList2.add(fog2[i]);
        for (int i = 0; i < fog1.length; i++) topicList3.add(fog3[i]);
        for (int i = 0; i < fog1.length; i++) topicList4.add(fog4[i]);
        for (int i = 0; i < fog1.length; i++) topicList5.add(fog5[i]);

        fogPortList.add(fog1Port);
        fogPortList.add(fog2Port);
        fogPortList.add(fog3Port);
        fogPortList.add(fog4Port);
        fogPortList.add(fog5Port);

        for (int i = 0; i < fogPortList.size(); i++) {
            for (int j = 0; j < fogList.size(); j++) {
                fogPort.put(fogList.get(i)[j], fogPortList.get(i));
                fogBitMaskIndex.put(fogList.get(j)[i], i);
            }
            fogBitMask.put(fogPortList.get(i), initBitMask.clone());
            fogBitMaskNoise.put(fogPortList.get(i), initBitMask.clone());
        }

        System.out.println("epsilon : " + epsilon);
        System.out.println("qValue : " + qValue);
    }

    public List<String> getTopicList1() {
        return topicList1;
    }

    public List<String> getTopicList2() {
        return topicList2;
    }

    public List<String> getTopicList3() {
        return topicList3;
    }

    public List<String> getTopicList4() {
        return topicList4;
    }

    public List<String> getTopicList5() {
        return topicList5;
    }

    public HashMap<Integer, double[]> getFogBitMask() {
        return fogBitMask;
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

    public int getKafkaSleepTime() {
        return kafkaSleepTime;
    }
}