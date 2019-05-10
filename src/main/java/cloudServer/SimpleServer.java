import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SimpleServer {

    static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static HashMap<String, String> sigungu = new HashMap<String, String>();
    static HashMap<String, Integer> locationData = new HashMap<String, Integer>();

    void loadSigungu() throws Exception {
        File sigunguFile = new File("./data/sigungu.csv");
        FileReader fileReader = new FileReader(sigunguFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            String[] lineSplit = line.split(",");
            sigungu.put(lineSplit[1], lineSplit[0]);
            locationData.put(lineSplit[0], 0);
        }
    }

    public static void main(String[] args) throws Exception {

        SimpleServer ss = new SimpleServer();
        ss.loadSigungu();
        ss.ServerRun();
    }

    public void ServerRun() throws IOException {

        ServerSocket server = null;
        int port = 4040;
        Socket socket = null;

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            server = new ServerSocket(port);

            while (true) {

                System.out.println("-------접속 대기중------");
                socket = server.accept();         // 클라이언트가 접속하면 통신할 수 있는 소켓 반환
                System.out.println(socket.getInetAddress() + "로 부터 연결요청이 들어옴");
                Date time = new Date();
                String time1 = format1.format(time);
                time1 = time1.replace(" ", ",");

                File file = new File("./data/result/" + time1 + ".csv");
                FileWriter fw = new FileWriter(file, true);


                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                // 클라이언트로부터 데이터를 받기 위한 InputStream 선언


                String line = "";

                while ((line = br.readLine()) != null) {
                    String[] lineSplit = line.split(",");
                    String sigunguValue = sigungu.get(lineSplit[3]);
                    locationData.put(sigunguValue, locationData.get(sigunguValue) + 1);
                    System.out.println(line);
                }

                Iterator<String> locationDataKey = locationData.keySet().iterator();
                while (locationDataKey.hasNext()) {
                    String sigunguName = locationDataKey.next();
                    int sigunguValue = locationData.get(sigunguName);
                    if (sigunguValue > 0) {
                        fw.write(sigunguName + "," + locationData.get(sigunguName) + "\n");
                        fw.flush();
                    }
                }

                String command = "./startPython ./data/result/" + time1 + ".csv";
                String s;
                System.out.println(command);
                Runtime rt = Runtime.getRuntime();
                Process p = rt.exec(command);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }

                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void receiveData(String data, Socket socket) {
        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
            // 클라이언트로부터 데이터를 보내기 위해 OutputStream 선언

            bw.write(data);            // 클라이언트로 데이터 전송
            bw.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                bw.close();
                osw.close();
                os.close();
                socket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}


