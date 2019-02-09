package cliente_archivo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author HugoGuillin
 */
public class Cliente_archivo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Socket socket=null;
        DataInputStream dis=null;
        DataOutputStream dos=null;
        Scanner entrada=new Scanner(System.in);
        String nombreArchivo, contenido;
        
        try{
            socket=new Socket("localhost", 1500); //Se crea la conexión con el servidor
            
            System.out.println("Introduce el nombre del archivo que deseas leer del servidor:");
            nombreArchivo = entrada.nextLine(); //Se le solicita al usuario el nombre del archivo del que necesita recuperar su contenido
            
            dos=new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(nombreArchivo); //Se le solicita al servidor el archivo indicado por el usuario
            
            dis=new DataInputStream(socket.getInputStream());
            contenido=dis.readUTF(); //Se obtiene la respuesta del servidor
            
            if(contenido.equals("0")){//Si se obtiene un 0 del servidor, significa que el archivo solicitado no existe en el servidor
                System.err.println("\nEL ARCHIVO SOLICITADO NO EXISTE EN EL SERVIDOR\n");
                
            }else{ //En caso contrario se imprime por pantalla el contenido del archivo solicitado
                System.out.println("\n********CONTENIDO DEL ARCHIVO "+nombreArchivo.toUpperCase()+"********\n");
                    System.out.println(contenido);
                System.out.println("\n*************************************************");
            }            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }finally{
            try{ //Se cierran todos los streams y conexiones
                if(dos!=null) dos.close();
                if(dis!=null) dis.close();
                if(socket!=null) socket.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
}
