package cliente_adivina;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author HugoGuillin
 */
public class Cliente_adivina {

    /**
     * Esta clase solicita un nº al usuario, se conecta al servidor que genera un nº aleatorio
     * y espera su respuesta de si el nº del usuario coincide con el generado en el servidor.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner entrada;
        Socket socket=null;
        DataOutputStream dos=null;
        DataInputStream dis=null;
        int intento=0, numero=0;
        boolean acierto=false, esNumero=false;
        
        try{
            socket=new Socket("localhost", 2000);//Se crea la conexión con el servidor
            
            dos=new DataOutputStream(socket.getOutputStream());//Se crea el flujo de salida
            dis=new DataInputStream(socket.getInputStream());//Se crea el flujo de entrada
            
            System.out.println("Introduce un número entero entre 0 y 100.");//Se le solicita un nº al usuario
            
            while(!acierto){//Mientras que el nº del usuario no coincida con el nº del servidor...
                
                while(!esNumero){//Mientras que el usuario no introduzca un nº entero y comprendido en el rango solicitado...
                    entrada=new Scanner(System.in);
                    if(entrada.hasNextInt()){//Se comprueba si el usuario ha introducido un nº entero
                        intento=entrada.nextInt();
                        
                        if (intento>=0 && intento<=100) {//Se comprueba si el nº está dentro del rango solicitado
                            esNumero=true;    
                            
                        }else{
                            System.err.println("Solo se aceptan números enteros entre 0 y 100. Vuelve a intentarlo");
                        }                            
                    }else{
                        System.err.println("Solo se aceptan números enteros entre 0 y 100. Vuelve a intentarlo");
                        entrada.next();//Se solicita otro nº
                    }
                }
                esNumero=false;//Se pone a false por si el nº no coincide con el del servidor y se le debe solicitar
                               //otro nº al usuario
                    
                dos.writeInt(intento);//Se le pasa el nº del usuario al servidor

                numero=dis.readInt();//Se lee la respuesta del servidor
               
                switch (numero) {
                    case 0:
                        //Si el nº es el correcto se sale del bucle y se acaba el programa
                        System.out.println("Muy bien, has acertado el número!!!");
                        acierto=true;
                        break;
                    case 1:
                        //Si el servidor devuelve un 1 es porque el nº del usuario es mayor
                        System.out.println("Te has pasado. El número que debes adivinar es menor");
                        break;
                    case 2:
                        //Si el servidor devuelve un 2 es porque el nº del usuario es menor
                        System.out.println("Te has quedado corto. El número que debes adivinar es mayor");
                        break;
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
