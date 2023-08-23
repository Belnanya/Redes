package src.Redes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    public static void main(String[] args) {
        final int serverPort = 12345;
        DatagramSocket serverSocket = null;

        List<ClientInfo> clients = new ArrayList<>();

        try {
            serverSocket = new DatagramSocket(serverPort);

            System.out.println("El server anda, rey. Esperando q se conecten...");

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Mensaje recibido del cliente: " + receivedMessage);

                ClientInfo clientInfo = new ClientInfo(receivePacket.getAddress(), receivePacket.getPort());
                if (!clients.contains(clientInfo)) {
                    clients.add(clientInfo);
                }

                for (ClientInfo client : clients) {
                    byte[] sendData = receivedMessage.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
                    serverSocket.send(sendPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}

