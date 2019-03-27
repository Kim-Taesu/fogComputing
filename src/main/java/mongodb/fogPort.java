package mongodb;

import java.util.HashMap;

public class fogPort {
    HashMap<String, Integer> fog = new HashMap<String, Integer>();

//    "1168","1165","1171","1144"

    public void initFog() {
        fog.put("1168", 32810);
        fog.put("1111", 32810);
        fog.put("1114", 32810);
        fog.put("1117", 32810);

        fog.put("1165", 32807);
        fog.put("1121", 32807);
        fog.put("1120", 32807);

        fog.put("1171", 32808);
        fog.put("1123", 32808);
        fog.put("1126", 32808);

        fog.put("1144", 32809);
        fog.put("1129", 32809);
        fog.put("1130", 32809);

        fog.put("1132", 32812);
        fog.put("1135", 32812);
        fog.put("1138", 32812);

        fog.put("1141", 32813);
        fog.put("1150", 32813);
        fog.put("1147", 32813);

        fog.put("1153", 32814);
        fog.put("1154", 32814);
        fog.put("1156", 32814);

        fog.put("1159", 32815);
        fog.put("1162", 32815);
        fog.put("1174", 32815);

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
