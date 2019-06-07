package dmeo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerThread implements Runnable {


    private Socket socket = null;

    BufferedReader br = null;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    @Override
    public void run() {
        String content = readFromClient();
      //  while((content = readFromClient())!=null){
            System.out.println(content);
//            for(Socket s : MyServer.sockets){
//                try {
//
//                    OutputStream os = s.getOutputStream();
//                    os.write((content+"\n").getBytes("utf-8"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
   // }
    public String readFromClient(){
        try {
            String result="";
            String line=null;

            while((line=br.readLine())!=null){
                result+=line;
            }


            return result;
        } catch (IOException e) {
          //  MyServer.sockets.remove(socket);

            e.printStackTrace();
            return null;
        }

    }

}
