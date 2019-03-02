package servidor_archivo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author HugoGuillin
 */
public class Servidor_archivo extends Thread{
    
    private Socket cliente;
    private static int id = 1;
    private int idCliente;
    
    
    public Servidor_archivo(Socket cliente){
        this.cliente = cliente;
        idCliente = id++;
    }

    public int getIdCliente() {
        return idCliente;
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            ServerSocket ss = new ServerSocket(1500);
            
            while(true){
                Socket cliente = ss.accept();
                Servidor_archivo sa = new Servidor_archivo(cliente);
                System.out.println("Conexión creada con el cliente "+sa.getIdCliente()+"!");
                sa.start();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void run(){
        DataOutputStream dos=null;
        DataInputStream dis=null;
        BufferedReader br=null;
        File archivo=null;
        
        //Guardo los archivos disponibles en el servidor, para saber si alguno coincide con el que solicita el cliente
        File[] listaArchivos=new File("/Users/HugoGuillin/NetBeansProjects/Servidor_archivo").listFiles();
        String nombreArchivo, contenido;
        
        try{
            
            dis=new DataInputStream(cliente.getInputStream()); //Se crea el flujo de entrada
            dos=new DataOutputStream(cliente.getOutputStream()); //Se crea el flujo de salida
            
            nombreArchivo=dis.readUTF(); //Se guarda el nombre del archivo solicitado por el cliente
            System.out.println("El archivo solicitado por el cliente "+getIdCliente()+" es \""+nombreArchivo+"\"");
            
            for (File la : listaArchivos) { //Se comprueba si el archivo solicitado existe en el servidor
                if(nombreArchivo.equalsIgnoreCase(la.getName())){
                    archivo=la;
                }
            }
            
            if(archivo==null){ //Si el archivo no existe en el servidor se pasa un código al flujo de salida 
                                //que interpreta el programa cliente
                    dos.writeUTF("0"); 
                    System.err.println("El archivo solicitado por el cliente "+getIdCliente()+" no existe");
            
            }else{
                br=new BufferedReader(new FileReader(archivo)); //Se lee el contenido del archivo solicitado...
                while((contenido=br.readLine())!=null){
                    dos.writeUTF(contenido); // y se escribe dicho contenido en el flujo de salida
                }
                System.out.println("Contenido del archivo \""+nombreArchivo+"\" enviado al cliente "+getIdCliente());
            }
            
            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }finally{
            try{ //Se cierran todos los streams y conexiones
                if(br!=null) br.close();
                if(dis!=null) dis.close();
                if(dos!=null) dos.close();
                if(cliente!=null) cliente.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
}
