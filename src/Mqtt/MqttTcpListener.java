package Mqtt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Reinout on 1/03/2017.
 */
public class MqttTcpListener {
    public static void main(String args[]) throws Exception{
        MqttJavaApplication mqttJavaApplication = new MqttJavaApplication();
        mqttJavaApplication.build();
        ServerSocket serverSocket = new ServerSocket(6789);
        while(true){
            Socket socket = serverSocket.accept();
            BufferedReader inputStream =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String songData = inputStream.readLine();
            System.out.println(songData);
            mqttJavaApplication.sendMessage(songData);
            socket.close();
        }
    }
}
