package mongodb;

import java.util.HashMap;

public class fogSetting {
    HashMap<String, Integer> fogPort = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguCode = new HashMap<String, Integer>();
    HashMap<Integer, int[]> fogBitMask = new HashMap<Integer, int[]>();
    HashMap<Integer, int[]> fogBitMaskNoise = new HashMap<Integer, int[]>();
    HashMap<Integer, int[]> fogBitMaskExpect = new HashMap<Integer, int[]>();
    HashMap<String, Integer> fogBitMaskIndex = new HashMap<String, Integer>();

    String fog1List[] = {"1168","1111","1114","1117","1165"};
    String fog2List[] = {"1211","1120","1171","1123","1126"};
    String fog3List[] = {"1144","1129","1130","1132","1135"};
    String fog4List[] = {"1138","1141","1150","1147","1153"};
    String fog5List[] = {"1154","1156","1159","1162","1174"};

    private int fog1Port = 32768;
    private int fog2Port = 32769;
    private int fog3Port = 32770;
    private int fog4Port = 32771;
    private int fog5Port = 32772;


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

        for(int i=0;i<fog1List.length;i++){
            fogPort.put(fog1List[i],fog1Port);
            fogPort.put(fog2List[i],fog2Port);
            fogPort.put(fog3List[i],fog3Port);
            fogPort.put(fog4List[i],fog4Port);
            fogPort.put(fog5List[i],fog5Port);

            fogBitMaskIndex.put(fog1List[i],i);
            fogBitMaskIndex.put(fog2List[i],i);
            fogBitMaskIndex.put(fog3List[i],i);
            fogBitMaskIndex.put(fog4List[i],i);
            fogBitMaskIndex.put(fog5List[i],i);


        }

//        fogPort.put("1168", fog1Port);
//        fogBitMaskIndex.put("1168", 0);
//        fogPort.put("1111", fog1Port);
//        fogBitMaskIndex.put("1111", 1);
//        fogPort.put("1114", fog1Port);
//        fogBitMaskIndex.put("1114", 2);
//        fogPort.put("1117", fog1Port);
//        fogBitMaskIndex.put("1117", 3);
//        fogPort.put("1165", fog1Port);
//        fogBitMaskIndex.put("1165", 4);

//        fogPort.put("1121", fog2Port);
//        fogBitMaskIndex.put("1121", 0);
//        fogPort.put("1120", fog2Port);
//        fogBitMaskIndex.put("1120", 1);
//        fogPort.put("1171", fog2Port);
//        fogBitMaskIndex.put("1171", 2);
//        fogPort.put("1123", fog2Port);
//        fogBitMaskIndex.put("1123", 3);
//        fogPort.put("1126", fog2Port);
//        fogBitMaskIndex.put("1126", 4);

//        fogPort.put("1144", fog3Port);
//        fogBitMaskIndex.put("1144", 0);
//        fogPort.put("1129", fog3Port);
//        fogBitMaskIndex.put("1129", 1);
//        fogPort.put("1130", fog3Port);
//        fogBitMaskIndex.put("1130", 2);
//        fogPort.put("1132", fog3Port);
//        fogBitMaskIndex.put("1132", 3);
//        fogPort.put("1135", fog3Port);
//        fogBitMaskIndex.put("1135", 4);

//        fogPort.put("1138", fog4Port);
//        fogBitMaskIndex.put("1138", 0);
//        fogPort.put("1141", fog4Port);
//        fogBitMaskIndex.put("1141", 1);
//        fogPort.put("1150", fog4Port);
//        fogBitMaskIndex.put("1150", 2);
//        fogPort.put("1147", fog4Port);
//        fogBitMaskIndex.put("1147", 3);
//        fogPort.put("1153", fog4Port);
//        fogBitMaskIndex.put("1153", 4);

//        fogPort.put("1154", fog5Port);
//        fogBitMaskIndex.put("1154", 0);
//        fogPort.put("1156", fog5Port);
//        fogBitMaskIndex.put("1156", 1);
//        fogPort.put("1159", fog5Port);
//        fogBitMaskIndex.put("1159", 2);
//        fogPort.put("1162", fog5Port);
//        fogBitMaskIndex.put("1162", 3);
//        fogPort.put("1174", fog5Port);
//        fogBitMaskIndex.put("1174", 4);

        int[] initBitMask = {0, 0, 0, 0, 0};
        fogBitMask.put(fog1Port, initBitMask.clone());
        fogBitMask.put(fog2Port, initBitMask.clone());
        fogBitMask.put(fog3Port, initBitMask.clone());
        fogBitMask.put(fog4Port, initBitMask.clone());
        fogBitMask.put(fog5Port, initBitMask.clone());

        fogBitMaskNoise.put(fog1Port, initBitMask.clone());
        fogBitMaskNoise.put(fog2Port, initBitMask.clone());
        fogBitMaskNoise.put(fog3Port, initBitMask.clone());
        fogBitMaskNoise.put(fog4Port, initBitMask.clone());
        fogBitMaskNoise.put(fog5Port, initBitMask.clone());

        fogBitMaskExpect.put(fog1Port, initBitMask.clone());
        fogBitMaskExpect.put(fog2Port, initBitMask.clone());
        fogBitMaskExpect.put(fog3Port, initBitMask.clone());
        fogBitMaskExpect.put(fog4Port, initBitMask.clone());
        fogBitMaskExpect.put(fog5Port, initBitMask.clone());

    }

    public HashMap<Integer, int[]> getFogBitMask() {
        return fogBitMask;
    }

    public HashMap<String, Integer> getFogPort() {
        return fogPort;
    }

    public Integer getfogBitMaskIndex(String topic) {
        return fogBitMaskIndex.get(topic);
    }

    public int[] getFogBitMask(int fogPort) {
        return fogBitMask.get(fogPort);
    }

    public int[] getFogBitMaskNoise(int fogPort) {
        return fogBitMaskNoise.get(fogPort);
    }

    public void setFogBitMask(int fogPort, int[] fogBitMask) {
        this.fogBitMask.put(fogPort,fogBitMask);
    }
    public void setFogBitMaskNoise(int fogPort, int[] fogBitMaskNoise) {
        this.fogBitMaskNoise.put(fogPort,fogBitMaskNoise);
    }

    public void setFogBitMaskExpect(int fogPort, int[] fogBitMaskExpect) {
        this.fogBitMaskExpect.put(fogPort,fogBitMaskExpect);
    }
}
