package src.Redes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class ReceiverThread implements Runnable {
    private DatagramSocket socket;

    public ReceiverThread(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Mensaje del servidor: " + receivedMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}