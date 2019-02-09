package servidor_adivina;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author HugoGuillin
 */
public class Servidor_adivina {

    /**
     * Esta clase hace las veces de servidor y espera la conexión de un cliente.
     * Crea un nº aleatorio y comprueba si el nº que le pasa el cliente es el nº generado,
     * si es mayor o es menor.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        boolean cierto=false;
        int num=new Random().nextInt(101), intento=0;
        Socket socket=null;
        DataOutputStream dos=null;
        DataInputStream dis=null;
        
        try{
            ServerSocket ss=new ServerSocket(2000);//Pongo el servidor en escucha
            
            socket=ss.accept();//Acepto la conexión con el cliente
            System.out.println("Conexión creada!");
            
            dos=new DataOutputStream(socket.getOutputStream());//Creo el flujo de salida
            dis=new DataInputStream(socket.getInputStream());//Creo el flujo de entrada
            
            while(!cierto){ //Mientras que el cliente no acierte el nº...
                
                intento=dis.readInt();//Leo el nº pasado por el cliente
                
                if(intento == num){//Si el cliente acierta el nº se acaba el bucle
                    dos.writeInt(0);
                    System.out.println("Número acertado!!!");
                    cierto=true;                   
                    
                }else{
                    if(intento>num){//Si el nº pasado es mayor, escribe un 1 en el flujo
                        dos.writeInt(1);
                        
                    }else if(intento<num){//Si el nº pasado es menor, escribe un 2 en el flujo
                        dos.writeInt(2);
                    }
                }
            }
            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        
        }finally{
            try{    //Se cierran los flujos y la conexión solo si se han llegado a crear
                if (dis!=null) {
                    dis.close();
                }
                if (dos!=null) {
                    dos.close();
                }
                if(socket!=null){
                    socket.close();
                }
                
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
}
