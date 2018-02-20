/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import javax.swing.JTable;

/**
 *
 * @author GerardoSteve
 */
public class Ecobici {

    /**
     * @param args the command line arguments
     */
    
   // final double ocupaciónTotal = 0; //promedio de todas las estaciones
   // final int TUED = 0; //total de usuarios esperando dejar
   // final int TUET = 0; // total de usuarios esperando tomar
    final static Scanner ingreso = new Scanner(System.in);
    static int nEst=2; //número de estaciones
    static int tnow;
    static int t; //auxiliar
    static int n; //número de eventos en el calendario
    static ArrayQueue calendario;
    final static int fin=60;
    static int reps=5;
    static Evento actual;
    static double r;
    static Estación[] estaciones;
    static double[][] datos;
    static int e1, e2, e3, e4, e5, e6, e7; //params. estaciones
    static int k;
    static int u; //contador (id) de usuarios
    static ArrayQueue[] esperando;
    static ArrayQueue servidos = new ArrayQueue(); //usuarios que esperaron y ya no
   // static JTable tabla = new JTable(datos, nombres);
    static final String SEPARATOR=",";
    static int bicis; //número total de bicicletas
    
    public static void main(String[] args) {
        // TODO code application logic here
       int i=0;
       int j=0;
       System.out.println("Número de repeticiones: ");
       reps= ingreso.nextInt();
       System.out.println("Número de estaciones: ");
       nEst= ingreso.nextInt();
       estaciones = new Estación[nEst];
       esperando = new ArrayQueue[nEst];
       datos=new double[nEst][nEst];
       bicis=0;
       BufferedReader br = null;
       try {
         br =new BufferedReader(new FileReader("/Users/admin/Desktop/estaciones.csv"));
         String line = br.readLine();
         while (null!=line) {
            String [] fields = line.split(SEPARATOR);
            e1=Integer.parseInt(fields[0]); //id;
            e2=Integer.parseInt(fields[1]); //capacidad;
            e3=Integer.parseInt(fields[2]); //bicicletas;
            e4=Integer.parseInt(fields[3]); //latitud
            e5=Integer.parseInt(fields[4]); //longitud
            e6=Integer.parseInt(fields[5]); //tiempo a la misma estación
            e7=Integer.parseInt(fields[6]); //tiempo entre llegadas
            estaciones[i] = new Estación(e1,e2,e3,e4,e5,e6,e7);
            System.out.println(estaciones[i].toString());
            esperando[i] = new ArrayQueue();
            bicis=bicis+e3;
            i++;
            line = br.readLine();
         }
         
      } catch (Exception e) {
         System.err.println("Error! "+e.getMessage());
      } finally {
         if (null!=br){
            try {
               br.close();
            } catch (IOException e) {
               System.err.println("Error closing file !! "+e.getMessage());
            }
         }
      }
       if (nEst==i)
            System.out.println("Todas las estaciones creadas, total: "+
                    bicis+" bicicletas y "+i+" estaciones.");
        else
            System.out.println("Error en creación de estaciones.");
       try {
         br =new BufferedReader(new FileReader("/Users/admin/Desktop/probabilidades.csv"));
         String line = br.readLine();
         i=0;
         while (null!=line) {
            String [] fields = line.split(SEPARATOR);
            line = br.readLine();
             for (j = 0; j < nEst; j++) {
                 datos[i][j]=Double.parseDouble(fields[j]);
             }
             i++;
         }
         
      } catch (Exception e) {
         System.err.println("Error! "+e.getMessage());
      } finally {
         if (null!=br){
            try {
               br.close();
            } catch (IOException e) {
               System.err.println("Error closing file !! "+e.getMessage());
            }
         }
      }
     if (nEst==i&&i==j)
            System.out.println("Probabilidades asignadas correctamente.");
        else
            System.out.println("Error en asignación de probabilidades.");
     /*  for (i = 0; i < nEst; i++) {
            System.out.println("id estación: ");
            e1 = ingreso.nextInt();
            System.out.println("capacidad: ");
            e2 = ingreso.nextInt();
            System.out.println("bicicletas: ");
            e3 = ingreso.nextInt();
            System.out.println("latitud: ");
            e4 = ingreso.nextInt();
            System.out.println("longitud: ");
            e5 = ingreso.nextInt();
            System.out.println("tiempo a la misma estación: ");
            e6 = ingreso.nextInt();
            System.out.println("tiempo entre llegadas: ");
            e7 = ingreso.nextInt();
            estaciones[i] = new Estación(e1,e2,e3,e4,e5,e6,e7);
            esperando[i] = new ArrayQueue();
       }
      */ 
        for(i =0;i<reps;i++){ //repeticiones
           n=0;
           u=0;
           tnow=0;
      
           //est1= new Estación(1,5,5,5,5,5,1); //Estación 1 en coordenadas (5,5)
           //est2=new Estación(2,5,5,10,10,5,2); //Estación 2 en coordenadas (10,10)
           //estaciones[0]=est1;
           //estaciones[1]=est2;
           //esperando[0]=esperando1;
           //esperando[1]=esperando2;
            for (j = 0; j < nEst; j++) {
                System.out.println("t="+tnow+", bicis E"+(j+1)+"="+
                   estaciones[j].Bicis+","+ " espacios E"+(j+1)+"="
                   +estaciones[j].EspDisp);
            }
           
           calendario=new ArrayQueue();
           for (j = 0; j < nEst; j++) {
              calendario.enqueue(new Evento(1,estaciones[j],tnow)); //calendariza llegadas iniciales
              n++;  
            }
           while (tnow<fin){
               t= ((Evento) calendario.first()).tiempo;
               for(j=0;j<n;j++){ //encontrar el tiempo menor
                   calendario.enqueue(calendario.dequeue());
                   if(((Evento)calendario.first()).tiempo<t)
                       t=((Evento)calendario.first()).tiempo;
               }
               while(((Evento)calendario.first()).tiempo!=t) //pone al principio el evento con t menor
                   calendario.enqueue(calendario.dequeue());
               tnow=t;
               actual=(Evento) calendario.dequeue();
               n--;
               
               if(actual.tipo==1){ //es una llegada a tomar bici
                   System.out.println("t="+tnow+", "+actual.nombre);
                   if(estaciones[actual.estación-1].Bicis==0){ //si no hay bicis, el usuario espera
                       ((ArrayQueue) esperando[actual.estación-1]).enqueue
                        (new Usuario(u,tnow,1,actual.estación));
                       u++;
                   }
                   else{
                        if(esperando[actual.estación-1].isEmpty()){
                            estaciones[actual.estación-1].Bicis--;
                            estaciones[actual.estación-1].EspDisp++;  
                        }
                        else{
                            ((Usuario)esperando
                                    [actual.estación-1].first()).Tf=tnow;
                            servidos.enqueue(esperando
                                    [actual.estación-1].dequeue());
                        }
                        System.out.println("bicis E"+actual.estación
                           +"="+estaciones[actual.estación-1].Bicis+
                           ", espacios E"+actual.estación
                           +"="+estaciones[actual.estación-1].EspDisp);
                        while(datos[actual.estación-1][k]<r) //menor que el número de estaciones
                             k++;
                        calendario.enqueue(new Evento(2,
                                estaciones[actual.estación-1],
                        estaciones[k],tnow)); //calendariza dejar la bici
                        n++;
                    }  
                   
                   calendario.enqueue(new Evento(1, 
                           estaciones[actual.estación-1],tnow)); //calendariza nueva llegada
                   n++;
                   r=Math.random();
                   k=0;
                   
               }
               //dejar bici:
               else if(estaciones[actual.estación-1].EspDisp==0){ //si no hay espacios, el usuario espera
                       esperando[actual.estación-1].enqueue(new 
                            Usuario(u,tnow,2,actual.estación));
                       u++;
                       System.out.println("t="+tnow+", "+actual.nombre);
                   }
               else{
                   System.out.println("t="+tnow+", "+actual.nombre);
                   
                    if(esperando[actual.estación-1].isEmpty()){
                        estaciones[actual.estación-1].Bicis++;
                        estaciones[actual.estación-1].EspDisp--;  
                    }
                    else {
                        ((Usuario)esperando
                                    [actual.estación-1].first()).Tf=tnow;
                            servidos.enqueue(esperando
                                    [actual.estación-1].dequeue());
                    }
                    
                    System.out.println("bicis E"+actual.estación
                       +"="+estaciones[actual.estación-1].Bicis+
                       ", espacios E"+actual.estación
                       +"="+estaciones[actual.estación-1].EspDisp);
               }
           }
           while(!servidos.isEmpty())
               System.out.println(((Usuario) servidos.dequeue()).toString());
           int cont;
           for(cont=0;cont<nEst;cont++){
               while(!esperando[cont].isEmpty())
                   esperando[cont].dequeue();
           }
       }
    }
    
}
