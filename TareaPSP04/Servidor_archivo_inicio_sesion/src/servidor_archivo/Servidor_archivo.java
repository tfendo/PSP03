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
    private static int id = 1; //Le asigno un id a cada cliente para distinguirlos
    private int idCliente;
    private final String USUARIO = "hugo", CONTRASENHA = "1234";
    
    
    public Servidor_archivo(Socket cliente){
        this.cliente = cliente;
        idCliente = id++;
    }

    /**
     * Método getter para el campo de clase idCliente
     * @return El id del cliente conectado
     */
    public int getIdCliente() {
        return idCliente;
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            ServerSocket ss = new ServerSocket(1500);
            
            while(true){ //Bucle infinito para aceptar conexiones concurrentes
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
        File archivo;
        String user_cli, pass_cli;
        int estado = 1, opcion = 0;
        boolean correcto = false;
        
        //Guardo los archivos disponibles en el directorio en un array de archivos para utilizar más adelante
        File[] listaArchivos=new File("/Users/HugoGuillin/NetBeansProjects/Servidor_archivo_inicio_sesion").listFiles();
        String nombreArchivo, contenido;
        
        try{
            
            dis=new DataInputStream(cliente.getInputStream()); //Se crea el flujo de entrada
            dos=new DataOutputStream(cliente.getOutputStream()); //Se crea el flujo de salida
            
            do{ //Mientras que el usuario no escoja la opción de salir...
                switch (estado){
                    case 1:
                        user_cli = dis.readUTF(); //Leo el usuario introducido
                        pass_cli = dis.readUTF(); //Leo la contraseña introducida

                        if(user_cli.equalsIgnoreCase(USUARIO)){
                            if(pass_cli.equalsIgnoreCase(CONTRASENHA)){ //Si el usuario y contraseña son correctos se permite el acceso al directorio
                                estado = 2;
                                dos.writeUTF("HAS INICIADO SESIÓN EN EL SERVIDOR\n");
                                System.out.println("EL CLIENTE "+getIdCliente()+" HA INICIADO SESIÓN\n");
                                correcto = true;

                            }else{
                                dos.writeUTF("La contraseña es incorrecta\n");
                            }
                        }else{
                            dos.writeUTF("El nombre de ususario es incorrecto\n");
                        }
                        dos.writeBoolean(correcto); //Se envía una señal al cliente de que los datos de acceso son correctos
                        break;
                        
                    case 2:
                        dos.writeUTF("***ESCOGE UNA DE LAS SIGUIENTES OPCIONES***\n"
                                + "1- Ver el contenido del directorio actual\n"
                                + "2- Ver el contenido de un archivo\n"
                                + "3- Salir");
                        
                        opcion = Integer.valueOf(dis.readUTF()); // Se lee la opción introducida por el cliente
                        if(opcion==1) 
                            estado = 3;
                        else if(opcion==2)
                            estado = 4;
                        break;
                        
                    case 3:
                        StringBuffer sb = new StringBuffer();
                        for (File la : listaArchivos) {
                            sb.append(la.getName()+"\n"); //Se guardan los nombres de los archivos del directorio
                        }
                        dos.writeUTF(sb.toString()); //Se pasan los nombres de los archivos del directorio al cliente
                        estado = 2; //Volvemos a presentar el menú de opciones
                        break;
                        
                    case 4:
                        archivo = null; //Se reinicia la variable cada vez que se va a leer el nombre del archivo solicitado
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
                        estado = 2; //Volvemos a presentar el menú de opciones
                        break;                                               
                }
                
                if(opcion==3){ //Si el cliente pasa la opción 3 es porque desea salir de la aplicación
                    estado = -1;
                    System.out.println("\n**EL CLIENTE "+getIdCliente()+" HA CERRADO SESIÓN**\n");
                }
            }while(estado!=-1);

            
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
