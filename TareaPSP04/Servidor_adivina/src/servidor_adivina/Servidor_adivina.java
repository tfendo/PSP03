package servidor_adivina;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *Esta clase hace las veces de servidor y espera la conexión de un cliente.
 *Crea un nº aleatorio y comprueba si el nº que le pasa el cliente es el nº generado,
 *si es mayor o es menor.
 * @author HugoGuillin
 */
public class Servidor_adivina extends Thread {
    
    private Socket cliente;
    private static int id=1; // Le asigno un id a cada cliente que se conecta para saber cual ha acertado
    private int idCliente;
    

    public Servidor_adivina(Socket cliente){
        this.cliente=cliente;
        idCliente = id++;
        
    }

    public int getIdCliente() {
        return idCliente;
    }
    
    
    
    
    public static void main(String[] args) {
        try{
            ServerSocket ss = new ServerSocket(2000);
            
            while(true){
                Socket socket = ss.accept();
                
                Servidor_adivina sa = new Servidor_adivina(socket);
                System.out.println("Conexión creada con cliente "+sa.getIdCliente()+"!");
                sa.start();
                
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    public void run() {
        boolean cierto=false;
        int num=new Random().nextInt(101), intento=0;
        DataOutputStream dos=null;
        DataInputStream dis=null;
        
        try{
            
            dos=new DataOutputStream(cliente.getOutputStream());//Creo el flujo de salida
            dis=new DataInputStream(cliente.getInputStream());//Creo el flujo de entrada
            
            while(!cierto){ //Mientras que el cliente no acierte el nº...
                
                intento=dis.readInt();//Leo el nº pasado por el cliente
                
                if(intento == num){//Si el cliente acierta el nº se acaba el bucle
                    dos.writeInt(0);
                    System.out.println("El cliente "+getIdCliente() +" ha acertado el número!!!");
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
                if(cliente!=null){
                    cliente.close();
                }
                
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        
    }
    
    
    
}
