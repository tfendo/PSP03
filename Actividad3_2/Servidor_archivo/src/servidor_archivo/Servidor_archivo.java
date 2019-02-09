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
public class Servidor_archivo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Socket socket=null;
        DataOutputStream dos=null;
        DataInputStream dis=null;
        BufferedReader br=null;
        File archivo=null;
        
        //Guardo los archivos disponibles en el servidor, para saber si alguno coincide con el que solicita el cliente
        File[] listaArchivos=new File("/Users/HugoGuillin/NetBeansProjects/Servidor_archivo").listFiles();
        String nombreArchivo, contenido;
        
        try{
            ServerSocket ss=new ServerSocket(1500); //Se pone el servidor en escucha
            socket=ss.accept(); //Se acepta la conexión con el cliente
            System.out.println("Conexión creada!");
            
            dis=new DataInputStream(socket.getInputStream()); //Se crea el flujo de entrada
            dos=new DataOutputStream(socket.getOutputStream()); //Se crea el flujo de salida
            
            nombreArchivo=dis.readUTF(); //Se guarda el nombre del archivo solicitado por el cliente
            System.out.println("El archivo solicitado es \""+nombreArchivo+"\"");
            
            for (File la : listaArchivos) { //Se comprueba si el archivo solicitado existe en el servidor
                if(nombreArchivo.equalsIgnoreCase(la.getName())){
                    archivo=la;
                }
            }
            
            if(archivo==null){ //Si el archivo no existe en el servidor se pasa un código al flujo de salida 
                                //que interpreta el programa cliente
                    dos.writeUTF("0"); 
                    System.err.println("El archivo solicitado no existe");
            
            }else{
                br=new BufferedReader(new FileReader(archivo)); //Se lee el contenido del archivo solicitado...
                while((contenido=br.readLine())!=null){
                    dos.writeUTF(contenido); // y se escribe dicho contenido en el flujo de salida
                }
                System.out.println("Contenido del archivo \""+nombreArchivo+"\" enviado al cliente");
            }
            
            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }finally{
            try{ //Se cierran todos los streams y conexiones
                if(br!=null) br.close();
                if(dis!=null) dis.close();
                if(dos!=null) dos.close();
                if(socket!=null) socket.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
}
