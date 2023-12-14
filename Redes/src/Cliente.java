import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import static java.lang.Integer.parseInt;


public class Cliente {
    private static int puerto=3312;
    public static int getPuerto() {
        return puerto;
    }
    public static void setPuerto(int puerto) {
        Cliente.puerto = puerto;
    }
    public static String nombrePropio(Scanner scannerLector){
        String nombreP=scannerLector.next();
        return nombreP;
    }
    public static String enviarNombre(){
        Scanner s1=new Scanner(System.in);
        System.out.println("nombrar al que le quieres escribir:");
        return s1.next();
    }
    public static boolean verificarPc(String nombre, String linea){
        for(int i=0;i<linea.split("-").length;i++){
            String persona=linea.split("-")[i];
            String nombrePerso=persona.split(":")[0];
            if(nombrePerso.equals(nombre)){
                return true;
            }
        }
        return false;
    }

    public static void enviarMensaje(String emisor, String mensaje, String receptor, DatagramSocket socket, String linea){
        String mensajeCompleto=receptor + ":" + mensaje + ":" + emisor;
        InetAddress ipProxima;
        int puertoSiguiente;
        for(int i=0;i<linea.split("-").length;i++){
            String persona=linea.split("-")[i];
            String nombrePersona=persona.split(":")[0];
            if(nombrePersona.equals(emisor)){
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

    public static void main(String[] args){
        String rutaConfig = "C:\\Users\\Usuario\\Documents\\Redes-main\\Redes\\src\\archivoConfig.txt";
        File archivoConfig = new File(rutaConfig);
        String rutaPersona = "C:\\Users\\Usuario\\Documents\\Redes-main\\Redes\\src\\archivoPersona.txt";
        File archivoPersona = new File(rutaPersona);
        try{
            Scanner leerMensaje=new Scanner(System.in);
            DatagramSocket socket=new DatagramSocket(puerto);
            Scanner scannerLector=new Scanner(archivoConfig);
            String info=scannerLector.nextLine();
            Scanner scannerLectorP=new Scanner(archivoPersona);
            String nombrePersona=Cliente.nombrePropio(scannerLectorP);
            if(Cliente.verificarPc(nombrePersona, info)) {
                System.out.println("se ha verificado tu nombre y estas en el archivo");
                Thread escucha=new Thread(new Mensaje(socket, info, nombrePersona));
                escucha.start();
                while(true){
                    String nombreReceptor=Cliente.enviarNombre();
                    if(Cliente.verificarPc(nombreReceptor, info)){
                        System.out.println("la pc esta en el archivo");
                        System.out.println("escriba su mensaje: ");
                        String mensaje= leerMensaje.next();
                        Cliente.enviarMensaje(nombrePersona, mensaje, nombreReceptor, socket, info);
                    }else{
                        System.out.println("pc incorrecta ingrese otro nombre");
                    }
                }
            }else{
                System.out.println("no se se mandan los mensajes");
            }
        } catch (FileNotFoundException | SocketException e){
            throw new RuntimeException(e);
        }
    }
}