import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static java.lang.Integer.parseInt;

public class Mensaje implements Runnable{
    private DatagramSocket socket;
    private String lectorArchivo;
    private String nombrePActual;

    public Mensaje(DatagramSocket socket, String lectorArchivo, String nombrePActual) {
        this.socket = socket;
        this.lectorArchivo = lectorArchivo;
        this.nombrePActual = nombrePActual;
    }

    public static void mandarAlProximo(String emisor, String mensaje, String receptor, DatagramSocket socket, String linea, String nombrePActual){
        String mensajeCompleto=receptor + ":" + mensaje + ":" + emisor;
        InetAddress ipProxima;
        int puertoSiguiente;
        for(int i=0;i<linea.split("-").length;i++){ //.split separa
            String persona=linea.split("-")[i];
            String nombrePersona=persona.split(":")[0]; //HACE Q SEA IGUAL AL NOMBRE DEL RECEPTOR
            if(nombrePersona.equals(nombrePActual)){
                try{
                    if(i<linea.split("-").length){
                        ipProxima=InetAddress.getByName(linea.split("-")[i+1].split(":")[1]);
                        puertoSiguiente=parseInt(linea.split("-")[i+1].split(":")[2]);
                    }else{
                        ipProxima=InetAddress.getByName(linea.split("-")[0].split(":")[1]);
                        puertoSiguiente=parseInt(linea.split("-")[0].split(":")[2]);
                    }} catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                byte[] b1=mensajeCompleto.getBytes(StandardCharsets.UTF_8);
                DatagramPacket paqueteEnvio=new DatagramPacket(b1, b1.length, ipProxima, puertoSiguiente);
                try {
                    socket.send(paqueteEnvio);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void run(){
        while(true) {
            byte[] buffer = new byte[256];
            DatagramPacket recibido = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(recibido);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String respuesta = new String(recibido.getData());
            System.out.println("llego un mensaje");
            if (respuesta.split(":")[0].equals(nombrePActual)) {
                System.out.println("tenes un mensaje: " + respuesta.split(":")[1] + " / de: " + respuesta.split(":")[2]); //si es para vos llega tu mensaje
                System.out.println("nombre de la pc que quieras escribirle: ");
            } else {
                System.out.println("este mensaje no es para mi, reenvio al siguiente");
                mandarAlProximo(respuesta.split(":")[2], respuesta.split(":")[1], respuesta.split(":")[0], socket, lectorArchivo, nombrePActual); //le manda al siguiente
                System.out.println("nombre de la pc que quieras escribirle: ");
            }
        }
    }
}
