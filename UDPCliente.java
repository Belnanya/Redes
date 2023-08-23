package src.Redes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;


public class UDPCliente {
    public static void main(String[] args) {
        final String serverIP = "localhost";
        final int serverPort = 12345;

        DatagramSocket clienteSocket = null;
        Scanner scanner = new Scanner(System.in);

        try {
            clienteSocket = new DatagramSocket();

            ReceiverThread receiverThread = new ReceiverThread(clienteSocket);
            Thread receiver = new Thread(receiverThread);
            receiver.start();//empieza a enviar mnsj

            while (true) {
                System.out.print("Ingrese un mensaje: ");
                String message = scanner.nextLine();
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(serverIP), serverPort);
                clienteSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (clienteSocket != null) {
                clienteSocket.close();
            }
        }
    }
}