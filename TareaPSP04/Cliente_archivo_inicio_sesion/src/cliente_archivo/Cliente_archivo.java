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
        String nombreArchivo, contenido, usuario, pass , opcion;
        boolean correcto = false, salir = false;
        
        try{
            socket=new Socket("localhost", 1500); //Se crea la conexión con el servidor
            dos=new DataOutputStream(socket.getOutputStream());            
            dis=new DataInputStream(socket.getInputStream());
            
            do{ //Mientras que no se introduzcan los datos de acceso correctos...
                System.out.println("DEBES INICIAR SESIÓN EN EL SERVIDOR");
                System.out.print("Usuario: ");
                usuario = entrada.nextLine();
                dos.writeUTF(usuario);
                System.out.print("Contraseña: ");
                pass = entrada.nextLine();
                dos.writeUTF(pass);
                System.out.println(dis.readUTF());
                correcto = dis.readBoolean();//Se lee la respuesta al respecto del servidor
            }while(!correcto);
            
            correcto = false;
            
            do{ //Mientras que el usuario no escoja la opción de salir...
                System.out.println(dis.readUTF()); //Se presenta el menú de opciones disponibles
                
                do{ //Mientras que el usuario no introduzca un número de opción correcto...
                    opcion = entrada.nextLine();
                    if(opcion.matches("[123]")){
                        dos.writeUTF(opcion);  
                        correcto = true;
                    }else{
                        System.err.println("Introduce el número de la opción deseada");
                    }
                }while(!correcto);
                correcto = false;
                
                switch (opcion){
                    case "1": //Se lee la lista de archivos disponibles en el directorio del servidor
                        System.out.println("\n**EL DIRECTORIO CONTIENE LOS SIGUIENTES ARCHIVOS: ");
                        System.out.println(dis.readUTF()+"\n");
                        break;

                    case "2": //Se solicita un nombre de archivo de los disponibles en el servidor
                        System.out.println("Introduce el nombre del archivo que deseas leer del servidor:");
                        nombreArchivo = entrada.nextLine(); //Se le solicita al usuario el nombre del archivo del que necesita recuperar su contenido
                        dos.writeUTF(nombreArchivo); //Se le solicita al servidor el archivo indicado por el usuario

                        contenido=dis.readUTF(); //Se obtiene la respuesta del servidor

                        if(contenido.equals("0")){//Si se obtiene un 0 del servidor, significa que el archivo solicitado no existe en el servidor
                            System.err.println("\nEL ARCHIVO SOLICITADO NO EXISTE EN EL SERVIDOR\n");

                        }else{ //En caso contrario se imprime por pantalla el contenido del archivo solicitado
                            System.out.println("\n********CONTENIDO DEL ARCHIVO "+nombreArchivo.toUpperCase()+"********\n");
                                System.out.println(contenido);
                            System.out.println("\n*************************************************\n");
                        }
                        break;

                    case "3": //Se procede a salir de la aplicación
                        System.out.println("SALIENDO DE LA APLICACIÓN...");
                        salir = true;
                        break;

                }
            }while(!salir);
            
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
