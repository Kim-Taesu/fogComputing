package mongodb;

import java.util.HashMap;

public class fogPort {
    HashMap<String, Integer> fog = new HashMap<String, Integer>();
    HashMap<String, Integer> sigunguFog = new HashMap<String, Integer>();

//    "1168","1165","1171","1144"

    public void initSigungu() {
        sigunguFog.put("Seoul_Jongno-gu", 1111);
        sigunguFog.put("Seoul_Jung-gu", 1114);
        sigunguFog.put("Seoul_Yongsan-gu", 1117);
        sigunguFog.put("Seoul_Seongdong-gu", 1120);
        sigunguFog.put("Seoul_Gwangjin-gu", 1121);
        sigunguFog.put("Seoul_Dongdaemun-gu", 1123);
        sigunguFog.put("Seoul_Jungnang-gu", 1126);
        sigunguFog.put("Seoul_Seongbuk-gu", 1129);
        sigunguFog.put("Seoul_Gangbuk-gu", 1130);
        sigunguFog.put("Seoul_Dobong-gu", 1132);
        sigunguFog.put("Seoul_Nowon-gu", 1135);
        sigunguFog.put("Seoul_Eunpyeong-gu", 1138);
        sigunguFog.put("Seoul_Seodaemun-gu", 1141);
        sigunguFog.put("Seoul_Mapo-gu", 1144);
        sigunguFog.put("Seoul_Yangcheon-gu", 1147);
        sigunguFog.put("Seoul_Gangseo-gu", 1150);
        sigunguFog.put("Seoul_Guro-gu", 1153);
        sigunguFog.put("Seoul_Geumcheon-gu", 1154);
        sigunguFog.put("Seoul_Yeongdeungpo-gu", 1156);
        sigunguFog.put("Seoul_Dongjak-gu", 1159);
        sigunguFog.put("Seoul_Gwanak-gu", 1162);
        sigunguFog.put("Seoul_Seocho-gu", 1165);
        sigunguFog.put("Seoul_Gangnam-gu", 1168);
        sigunguFog.put("Seoul_Songpa-gu", 1171);
        sigunguFog.put("Seoul_Gangdong-gu", 1174);


    }

    public void initFog() {
        fog.put("1168", 32810);
        fog.put("1111", 32816);
        fog.put("1114", 32817);
        fog.put("1117", 32818);

        fog.put("1165", 32807);
        fog.put("1121", 32819);
        fog.put("1120", 32820);

        fog.put("1171", 32808);
        fog.put("1123", 32821);
        fog.put("1126", 32822);

        fog.put("1144", 32809);
        fog.put("1129", 32823);
        fog.put("1130", 32824);

        fog.put("1132", 32812);
        fog.put("1135", 32825);
        fog.put("1138", 32826);

        fog.put("1141", 32813);
        fog.put("1150", 32827);
        fog.put("1147", 32828);

        fog.put("1153", 32814);
        fog.put("1154", 32829);
        fog.put("1156", 32830);

        fog.put("1159", 32815);
        fog.put("1162", 32831);
        fog.put("1174", 32832);

    }

    public void addFog(int fogNum, int port) {
        fog.put("fog" + fogNum, port);
    }

    public void setFogPort(int fogNum, int port) {
        fog.put("fog" + fogNum, port);
    }

    public int getFogPort(int fogNum) {
        return fog.get("fog" + fogNum);
    }
}
