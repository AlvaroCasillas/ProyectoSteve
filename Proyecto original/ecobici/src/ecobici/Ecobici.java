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
import org.apache.commons.math3.distribution.LogNormalDistribution;
import java.io.FileWriter;

/**
 *
 * @author GerardoSteve
 */
public class Ecobici {

    /**
     * @param args the command line arguments
     */
    
   // static double ocupaciónTotal = 0; //promedio de todas las estaciones
   // static int TUED = 0; //total de usuarios esperando dejar
   // static int TUET = 0; // total de usuarios esperando tomar
    
    static int NumEsperando,NumEsperando2;
    static Usuario usuario;
    static int llegadas, último;
    static int Espt,Espd;
    final static Scanner ingreso = new Scanner(System.in);
    static int nEst; //número de estaciones
    static double tnow;
    static double t; //auxiliar
    static int n; //número de eventos en el calendario
    static ArrayQueue calendario;
    static double fin;
    static int reps;
    static Evento actual;
    static double r;
    static Estación[] estaciones;
    static double[][] arribos;
    static int e1, e2, e3; //params. estaciones
    static double[] e4; //tasas tiempo entre llegadas
    static int k;
    static int u; //contador (id) de usuarios
    static ArrayQueue[] esperando;
    static ArrayQueue servidos = new ArrayQueue();//usuarios: esperaron y ya no
    static int bicis; //número total de bicicletas
    static double AV,tiV,tfV,qV,V; //para calcular promedio vacías
    static double ALl,tiLl,tfLl,qLl,Ll; //para calcular promedio llenas
   // static double AValt, ALlalt, qValt,qLlalt; //para método alternativo
    static double suet,sued,suetyd,sue,sut,sud,su,sune,//sumas de usuarios
            fuet,fued,fue,fune;//fracciones usuarios que esperaron
    static double stet,sted,ste,tet,ted,te; //tiempos de espera
    static double SQV,SQV2,QV,sQV,SQLl,SQLl2,QLl,sQLl;//vacías/llenas global
    static double qInef,SInef,SInef2,Inef,sInef; //Ineficiente = vacía+llena
    static double SUT,SUT2,SUD,SUD2,FUET,sUET,FUED,sUED;//usuarios global
    static double SUE,SUE2,FUE,sUE,SUNE,SUNE2,FUNE,sUNE; //usuarios global gral
    static double STET,STET2,STED,STED2,TET,sTET,TED,sTED; //tiempos global
    static double STE,STE2,TE,sTE; //tiempos de espera global general
    static int periodo; // periodo actual
    static int periodos; //cuántos periodos hay
    static double durper; // duración de cada periodo
    static LogNormalDistribution lognormal;
    static double mLogNormal,sdLogNormal,mNormal,sdNormal;
//    static String[] resultados;
//    static String[] globales;
    static int[][] bicicletas; //población de soluciones
    static int pob, iter;//número de soluciones, iteraciones
    static double[][] objetivo; //número a minimizar
    static double mutación,alfa,beta;//probabilidad de mutación, pesos para obj.
    static Cruzamiento c;
//    static String[][] res; //de cada estación
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        BufferedReader br = null;
        try {
            br =new BufferedReader(new FileReader
            ("C:\\Users\\lablogistica\\Desktop\\ecobici\\DatosEntrada.csv"));
            String line = br.readLine(); //primera línea es de títulos
            line = br.readLine(); //segunda línea ya tiene los valores
            String [] fields = line.split(",");
            reps=Integer.parseInt(fields[0]); //número de repeticiones
            fin=Double.parseDouble(fields[1]); //duración de c/repetición
            periodos=Integer.parseInt(fields[2]); //cantidad de periodos
            durper=Double.parseDouble(fields[3]); //duración de c/periodo
            nEst=Integer.parseInt(fields[4]); //número de estaciones
            mLogNormal=Double.parseDouble(fields[5]);//tiempo medio de viaje
            sdLogNormal=Double.parseDouble(fields[6]);//desv.est tiempo de viaje
            pob=Integer.parseInt(fields[7]); //cuántas soluciones hay
            mutación=Double.parseDouble(fields[8]); //prob. de mutación
            alfa=Double.parseDouble(fields[9]); //peso del tiempo de espera
            beta=Double.parseDouble(fields[10]); //peso de la fracc. de usuarios
            iter=Integer.parseInt(fields[11]);//iteraciones para la optimización
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
        bicicletas=new int[pob][nEst];
        String S="C:\\Users\\lablogistica\\Desktop\\ecobici\\PoblaciónFinal.csv";
        try {
                br =new BufferedReader(new FileReader(S));
                for (int j = 0; j < nEst; j++) {
                    String line = br.readLine();
                    String [] fields = line.split(",");
                    for (int k = 0; k < pob; k++) {
                        bicicletas[k][j]=Integer.valueOf(fields[k+2]);
                    }
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
        System.out.println("Optimización: "+iter+" iteraciones de "+pob+" corridas cada una.");
        System.out.println("Simulación: "+reps+" repeticiones de "+(int)fin+
                " minutos cada una, con "+periodos+" periodos por jornada, "
                + "cada uno de "+durper+" minutos.");
        System.out.println("Total de estaciones: "+nEst+". Tiempo medio de"
                + " viaje: "+mLogNormal+" minutos, con una desviación estándar "
                + "de "+sdLogNormal+" minutos.");
        System.out.println("\n");
        mNormal=Math.log(Math.pow(mLogNormal,2)/Math.sqrt(Math.pow(mLogNormal,
                2)+Math.pow(sdLogNormal,2)));
        sdNormal=Math.sqrt(Math.log(1+Math.pow(sdLogNormal,2)/
                Math.pow(mLogNormal,2)));
        lognormal = new LogNormalDistribution(mNormal,sdNormal);
        estaciones = new Estación[nEst];
        esperando = new ArrayQueue[nEst];
        arribos=new double[nEst][periodos];
        
        /*
        String csv= "C:\\Users\\lablogistica\\Desktop\\ecobici\\Resultados.csv";
        FileWriter writer = null;
        resultados=new String[21];
        resultados[0]="Rep:";
        resultados[1]="Bicicletas:";
        resultados[2]="Usuarios que tomaron bicicleta:";           
        resultados[3]="Usuarios que esperaron tomar bicicleta:";
        resultados[4]="Siguen esperando tomar:";
        resultados[5]="Fracc. de usuarios que esperaron tomar bicicleta:";
        resultados[6]="Usuarios que dejaron bicicleta:";
        resultados[7]="Usuarios que esperaron dejar bicicleta:";
        resultados[8]="Siguen esperando dejar:";
        resultados[9]="Fracc. de usuarios que esperaron dejar bicicleta:";
        resultados[10]="Usuarios que esperaron tomar y dejar:";
        resultados[11]= "Usuarios totales que esperaron:";
        resultados[12]="Fracc. de usuarios que esperaron:";
        resultados[13]="No esperaron:";
        resultados[14]="Fracc. de usuarios que no esperaron:";
        resultados[15]="Tiempo promedio de espera para tomar bicicleta:";
        resultados[16]="Tiempo promedio de espera para dejar bicicleta:";
        resultados[17]="Tiempo promedio de espera:";
        resultados[18]="Fracc. promedio de tiempo con estaciones vac?ias:";
        resultados[19]="Fracc. promedio de tiempo con estaciones llenas:";
        resultados[20]="Fracc. promedio de tiempo con estaciones "
                   + "llenas o vac?ias:";
        /*
        res=new String[nEst][11];
        for (int m = 0; m < nEst; m++) {
            res[m][0]="Usuarios que tomaron bicicleta: ";
            res[m][1]="Usuarios que esperaron tomar bicicleta: ";
            res[m][2]="Fracc. de usuarios que esperaron tomar bicicleta: ";
            res[m][3]="Tiempo promedio de espera para tomar bicicleta: ";
            res[m][4]="Tiempo que estuvo vacía: ";
            res[m][5]="Usuarios que dejaron bicicleta: ";
            res[m][6]="Usuarios que esperaron dejar bicicleta: ";
            res[m][7]="Fracc. de usuarios que esperaron dejar bicicleta: ";
            res[m][8]="Tiempo promedio de espera para dejar bicicleta: ";
            res[m][9]="Tiempo que estuvo llena: ";
            res[m][10]="Balance: ";
        }
        */
        for (int it = 0; it < iter; it++) {
            System.out.println("\n");
            System.out.println("Iteración: "+it);
            System.out.println("\n");
            objetivo=new double[pob][2];
            for (int opt = 0; opt < pob; opt++) {
    //            SQV=0;SQV2=0;QV=0;sQV=0;SQLl=0;SQLl2=0;QLl=0;sQLl=0;
    //            SInef=0;SInef2=0;Inef=0;sInef=0;
    //            SUT=0;SUT2=0;SUD=0;SUD2=0;FUET=0;sUET=0;FUED=0;sUED=0;
    //            STET=0;STET2=0;STED=0;STED2=0;TET=0;sTET=0;TED=0;sTED=0;
    //            SUE2=0;sUE=0;SUNE=0;SUNE2=0;FUNE=0;sUNE=0;STE2=0;sTE=0;
                TE=0;FUE=0;STE=0;SUE=0;
                System.out.println("Corrida de optimización: "+(opt+1));
                System.out.println("\n");
                int j=0;
                int l=0;
                for(int i=0;i<reps;i++){ //repeticiones
                    llegadas=0; último=0;
    //                System.out.println("\n");
                    System.out.println("Repetición "+(i+1));
    //                resultados[0]+=","+(i+1);
                    j=0;
                    n=0;
                    u=0;
                    tnow=0;
    //                AV=0;tiV=0;tfV=0;qV=0;V=0;
    //                ALl=0;tiLl=0;tfLl=0;qLl=0;Ll=0;
    //                qInef=0;
                  //  AValt=0;qValt=0;ALlalt=0;qLlalt=0;
                    periodo=0;
                    suet=0;sued=0;suetyd=0;fuet=0;fued=0;stet=0;sted=0;tet=0;ted=0;
                    sut=0;sud=0;sue=0;su=0;sune=0;fue=0;fune=0;ste=0;te=0;
    //                NumEsperando=0;NumEsperando2=0;Espt=0;Espd=0;
                    bicis=0;
                try {
                 br =new BufferedReader(new FileReader
                ("C:\\Users\\lablogistica\\Desktop\\ecobici\\estaciones.csv"));
                 String line = br.readLine();
                 while (null!=line) {
                    String [] fields = line.split(",");
                    e1=Integer.parseInt(fields[0]); //id;
                    e2=Integer.parseInt(fields[1]); //capacidad;
        //            e3=Integer.parseInt(fields[2]); //bicicletas;
                    e3=bicicletas[opt][j];
                    e4 = new double[periodos];
                    for (int m=0; m<periodos; m++)
                        e4[m]=Double.valueOf(fields[m+3]); //tasas-tiempo entre llegadas
                    estaciones[j] = new Estación(e1,e2,e3,e4);
                    //System.out.println(estaciones[j].toString());
                    esperando[j] = new ArrayQueue();
                    bicis=bicis+e3;
                    j++;
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
                if (nEst==j){
                    System.out.println("Todas las estaciones creadas, total: "+
                            bicis+" bicicletas y "+j+" estaciones.");
    //                resultados[1]+=","+bicis;
                }

                else
                    System.out.println("Error en creación de estaciones.");
                try {
                 br =new BufferedReader(new FileReader
                ("C:\\Users\\lablogistica\\Desktop\\ecobici\\tasasArribos.csv"));
                 String line = br.readLine();
                 j=0;
                  while (null!=line) {
                    String [] fields = line.split(",");
                    line = br.readLine();
                     for (l = 0; l < periodos; l++) {
                         arribos[j][l]=Double.parseDouble(fields[l]);
                     }
                     j++;
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
                if (nEst==j&&periodos==l)
                       System.out.println("Tasas de arribo asignadas correctamente.");
                   else
                       System.out.println("Error en asignación de tasas de arribo.");
               /*
                       for (l = 0; l < nEst; l++) {
                           System.out.println("t="+tnow+", bicis E"+(l+1)+"="+
                              estaciones[l].Bicis+","+ " espacios E"+(l+1)+"="
                              +estaciones[l].EspDisp);
                       }
               */       
                    calendario=new ArrayQueue();
                    for (l = 0; l < nEst; l++) {
                        //calendariza llegadas iniciales
                       calendario.enqueue(new Evento(estaciones[l],tnow, periodo)); 
                       n++;  
                     }
                    while (tnow<fin){ //Lo que dura cada repetición
                        t= ((Evento) calendario.first()).tiempo;
                        for(l=0;l<n;l++){ //encontrar el tiempo menor
                            calendario.enqueue(calendario.dequeue());
                            if(((Evento)calendario.first()).tiempo<t)
                                t=((Evento)calendario.first()).tiempo;
                        }
                        //pone al principio el evento con t menor:
                        while(((Evento)calendario.first()).tiempo!=t) 
                            calendario.enqueue(calendario.dequeue());
                        tnow=t;
                        // actualiza periodo
                        if (tnow/durper>periodo+1 && periodo<periodos-1)
                            periodo++;
                        actual=(Evento) calendario.dequeue();
                        n--;
                        //Llegada a tomar bici:
                        if(actual.tipo==1){
                          // System.out.println("t="+tnow+", "+actual.nombre);
                            //si no hay bicis, el usuario espera:
                            if(estaciones[actual.estación-1].Bicis==0){ 
                                u++;
                                ((ArrayQueue) esperando[actual.estación-1]).enqueue
                                 (new Usuario(u,tnow,1,actual.estación));
                                estaciones[actual.estación-1].espt++;
                            }
                            //si sí hay bicis:
                            else{
                                //el usuario toma una bici y comienza el viaje:
                                estaciones[actual.estación-1].ut++;
                                //si no hay usuarios esperando:
                                 if(esperando[actual.estación-1].isEmpty()){
                                     //si no había espacios, ahora deja de estar llena:
    //                                 if(estaciones[actual.estación-1].EspDisp==0){
    //                                     tfLl=tnow;
    //                                     ALl=ALl+(tfLl-tiLl)*Ll;
    //                                     Ll--;
    //                                     tiLl=tnow;
    //                                     estaciones[actual.estación-1].tf=tnow;
    //                                     estaciones[actual.estación-1].recalculaLlena();
    //                                 }
                                     estaciones[actual.estación-1].Bicis--;
                                     estaciones[actual.estación-1].EspDisp++;
                                     //si se quedó sin bicis la estación, 
                                     //comienza a contar tiempo de vacía:
    //                                 if(estaciones[actual.estación-1].Bicis==0){
    //                                     tfV=tnow;
    //                                     AV=AV+(tfV-tiV)*V;
    //                                     V++;
    //                                     tiV=tnow;
    //                                     estaciones[actual.estación-1].ti=tnow;
                                       //  System.out.println("Se vació estación "
                                       //  + actual.estación);
    //                                 }
                                 }
                                 //si sí hay usuarios esperando, no hay espacios:
                                 else{
                                     ((Usuario)esperando
                                             [actual.estación-1].first()).Tf=tnow;
                                     estaciones[actual.estación-1].ted=
                                             estaciones[actual.estación-1].ted+
                                             ((Usuario)esperando
                                             [actual.estación-1].first()).tiempo();
                                     if(((Usuario)esperando[actual.estación-1].
                                             first()).tipo==3)
                                         estaciones[actual.estación-1].uetyd++;
                                     servidos.enqueue(esperando
                                             [actual.estación-1].dequeue());
                                     estaciones[actual.estación-1].espd--;
                                     estaciones[actual.estación-1].ued++;
                                     estaciones[actual.estación-1].ud++;
                                 }
                             /*    System.out.println("bicis E"+actual.estación
                                    +"="+estaciones[actual.estación-1].Bicis+
                                    ", espacios E"+actual.estación
                                    +"="+estaciones[actual.estación-1].EspDisp);
                                 /* para asignar viaje según probabilidades:
                                 while(arribos[actual.estación-1][k]<r)
                                      k++;
                                 */
                                 //calendariza dejar la bici:
                                 calendario.enqueue(new Evento(tnow));
                                 n++;
                             }  
                            //calendariza nueva llegada:
                            calendario.enqueue(new Evento(
                                    estaciones[actual.estación-1],tnow,periodo));
                            n++;
                            r=Math.random();
                            k=0;
                        }
                        //Llegada a dejar bici:
                        //si no esperó para tomar la bici.
                        else if (actual.tipo==2){
                            //si no hay espacios, el usuario espera:
                            if(estaciones[actual.estación-1].EspDisp==0){
                                u++;
                                esperando[actual.estación-1].enqueue(new 
                                     Usuario(u,tnow,2,actual.estación));
                                estaciones[actual.estación-1].espd++;
                              //  System.out.println("t="+tnow+", "+actual.nombre);
                            }
                            // si sí hay espacios:
                            else{
                                //el usuario deja la bici y finaliza el viaje:
                                estaciones[actual.estación-1].ud++;

        //                        System.out.println("t="+tnow+", "+actual.nombre);
                                // si no hay usuarios esperando:
                                if(esperando[actual.estación-1].isEmpty()){
                                    //si no había bicis, ahora deja de estar vacía:
    //                                if(estaciones[actual.estación-1].Bicis==0){
    //                                    tfV=tnow;
    //                                    AV=AV+(tfV-tiV)*V;
    //                                    V--;
    //                                    tiV=tnow;
    //                                    estaciones[actual.estación-1].tf=tnow;
    //                                    estaciones[actual.estación-1].recalculaVacía();
    //                                }
                                    estaciones[actual.estación-1].Bicis++;
                                    estaciones[actual.estación-1].EspDisp--;
                                    //si se quedó sin espacios la estación, 
                                    //comienza a contar tiempo de llena:
    //                                if(estaciones[actual.estación-1].EspDisp==0){
    //                                    tfLl=tnow;
    //                                    ALl=ALl+(tfLl-tiLl)*Ll;
    //                                    Ll++;
    //                                    tiLl=tnow;
    //                                    estaciones[actual.estación-1].ti=tnow;
                                       // System.out.println("Se llenó estación "
                                       //     + actual.estación);
    //                                }
                                }
                                //si sí los hay, no hay bicicletas:
                                else {
                                    ((Usuario)esperando
                                                [actual.estación-1].first()).Tf=tnow;
                                    estaciones[actual.estación-1].tet=
                                                estaciones[actual.estación-1].tet+
                                                ((Usuario)esperando
                                                [actual.estación-1].first()).tiempo();
                                    calendario.enqueue(new Evento(tnow,((Usuario)
                                       esperando[actual.estación-1].first()).id));
                                    servidos.enqueue(esperando
                                                [actual.estación-1].dequeue());
                                    estaciones[actual.estación-1].espt--;
                                    estaciones[actual.estación-1].uet++;
                                    estaciones[actual.estación-1].ut++;
                                    //calendariza dejar la bici:

                                    n++;
                                }
                          /*   System.out.println("bicis E"+actual.estación
                                +"="+estaciones[actual.estación-1].Bicis+
                                ", espacios E"+actual.estación
                                +"="+estaciones[actual.estación-1].EspDisp);
                                */
                            }
                        }
                        //si esperó para tomar la bici
                        else{
                            //si no hay espacios, el usuario espera:
                            if(estaciones[actual.estación-1].EspDisp==0){
                                esperando[actual.estación-1].enqueue(new 
                                     Usuario(actual.u,tnow,3,actual.estación));
                                estaciones[actual.estación-1].espd++;
                              //  System.out.println("t="+tnow+", "+actual.nombre);
                            }
                            // si sí hay espacios:
                            else{
                                //el usuario deja la bici y finaliza el viaje:
                                estaciones[actual.estación-1].ud++;
                               // System.out.println("t="+tnow+", "+actual.nombre);
                                // si no hay usuarios esperando:
                                if(esperando[actual.estación-1].isEmpty()){
                                    //si no había bicis, ahora deja de estar vacía:
    //                                if(estaciones[actual.estación-1].Bicis==0){
    //                                    tfV=tnow;
    //                                    AV=AV+(tfV-tiV)*V;
    //                                    V--;
    //                                    tiV=tnow;
    //                                    estaciones[actual.estación-1].tf=tnow;
    //                                    estaciones[actual.estación-1].recalculaVacía();
    //                                }
                                    estaciones[actual.estación-1].Bicis++;
                                    estaciones[actual.estación-1].EspDisp--;

                                    //si se quedó sin espacios la estación, 
                                    //comienza a contar tiempo de llena:
    //                                if(estaciones[actual.estación-1].EspDisp==0){
    //                                    tfLl=tnow;
    //                                    ALl=ALl+(tfLl-tiLl)*Ll;
    //                                    Ll++;
    //                                    tiLl=tnow;
    //                                    estaciones[actual.estación-1].ti=tnow;
                                       // System.out.println("Se llenó estación "
                                       //     + actual.estación);
    //                                }
                                }
                                //si sí los hay, no hay bicicletas:
                                else {
                                    ((Usuario)esperando
                                                [actual.estación-1].first()).Tf=tnow;
                                    estaciones[actual.estación-1].tet=
                                                estaciones[actual.estación-1].tet+
                                                ((Usuario)esperando
                                                [actual.estación-1].first()).tiempo();
                                    calendario.enqueue(new Evento(tnow,((Usuario)
                                       esperando[actual.estación-1].first()).id));
                                    servidos.enqueue(esperando
                                                [actual.estación-1].dequeue());
                                    estaciones[actual.estación-1].espt--;
                                    estaciones[actual.estación-1].uet++;
                                    estaciones[actual.estación-1].ut++;
                                    //calendariza dejar la bici:

                                    n++;
                                }
                          /*   System.out.println("bicis E"+actual.estación
                                +"="+estaciones[actual.estación-1].Bicis+
                                ", espacios E"+actual.estación
                                +"="+estaciones[actual.estación-1].EspDisp);
                                */
                            }

                        }
                    }
    //                tfV=tnow;
    //                AV=AV+(tfV-tiV)*V;
    //                tfLl=tnow;
    //                ALl=ALl+(tfLl-tiLl)*Ll;

    //                System.out.println("\n");
                    
                    while(!servidos.isEmpty())
                        servidos.dequeue();
//                        System.out.println(((Usuario) servidos.dequeue()).toString());
//                    System.out.println("\n");
/*
                    for(int cont=0;cont<nEst;cont++){
                        while(!esperando[cont].isEmpty()){
                            usuario = (Usuario) esperando[cont].dequeue();
                            if(usuario.tipo==1)
                                NumEsperando++;
                            else
                                NumEsperando2++;
                            }
                    }
                    System.out.println("Siguen esperando tomar: "+NumEsperando+", "
                            + "y dejar: "+NumEsperando2);
                    NumEsperando=0;NumEsperando2=0;
                    System.out.println("\n");
       */
                    for (l = 0; l < nEst; l++) {
                          estaciones[l].calculaFinal(tnow);
        //                  estaciones[l].resultados();
                         //Luego leerlo en la siguiente repetición y optimizar
                         //System.out.println(estaciones[l].toStringFinal());
                          sut=sut+estaciones[l].ut;
                          suet=suet+estaciones[l].uet;
                          sud=sud+estaciones[l].ud;
                          sued=sued+estaciones[l].ued;
                          suetyd+=estaciones[l].uetyd;
                          stet=stet+estaciones[l].tet;
                          sted=sted+estaciones[l].ted;
    //                      Espt+=estaciones[l].espt;
    //                      Espd+=estaciones[l].espd;
                 //         AValt=AValt+estaciones[l].tVac;
                 //         ALlalt=ALlalt+estaciones[l].tLlen;
                      }

                   //notar: no considera usuarios que esperaron doble,
                   //tanto tomar como dejar. 

    //               System.out.println("\n");
                   su=sut+sud;
        //           System.out.println("Usuarios servidos: "+(int)su);
        //           resultados[?]+=","+su;
        //           System.out.println("Usuarios que tomaron bicicleta: "+(int)sut);
        //           resultados[2]+=","+sut;
        //           System.out.println("Usuarios que esperaron tomar bicicleta: "
        //                   +(int)suet);
        //           resultados[3]+=","+suet;
        //           System.out.println("Siguen esperando tomar: "+Espt);
        //           resultados[4]+=","+Espt;
    //               fuet=suet/sut;
        //           System.out.println("Fracción de usuarios que esperaron tomar "
        //                   + "bicicleta: "+fuet);
        //           resultados[5]+=","+fuet;
        //           System.out.println("Usuarios que dejaron bicicleta: "+(int)sud);
        //           resultados[6]+=","+sud;
        //           System.out.println("Usuarios que esperaron dejar bicicleta: "
        //                   +(int)sued);
        //           resultados[7]+=","+sued;
        //           System.out.println("Siguen esperando dejar: "+Espd);
        //           resultados[8]+=","+Espd;
    //               fued=sued/sud;
        //           System.out.println("Fracción de usuarios que esperaron dejar "
        //                   + "bicicleta: "+fued);
        //           resultados[9]+=","+fued;
        //           System.out.println("Usuarios que esperaron ambas: "+(int)suetyd);
        //           resultados[10]+=","+suetyd;
                   sue=suet+sued-suetyd;
        //           System.out.println("Usuarios totales que esperaron: "+(int)sue);
        //           resultados[11]+=","+sue;
                   fue=sue/sut;
        //           System.out.println("Fracción de usuarios que esperaron: "+fue);
        //           resultados[12]+=","+fue;
    //               sune=sut-sue;
        //           System.out.println("No esperaron: "+(int)sune);
        //           resultados[13]+=","+sune;
    //               fune=sune/sut;
        //           System.out.println("Fracción de usuarios que no esperaron: "+fune);
        //           resultados[14]+=","+fune;
    //               tet=stet/sut;
        //           System.out.println("Tiempo promedio de espera para tomar "
        //                   + "bicicleta: "+tet+" minutos");
        //           resultados[15]+=","+tet;
    //               ted=sted/sud;
        //           System.out.println("Tiempo promedio de espera para dejar "
        //                   + "bicicleta: "+ted+" minutos");
        //           resultados[16]+=","+ted;
                   ste=stet+sted;
                   te=ste/su;
        //           System.out.println("Tiempo promedio de espera: "+te);
        //           resultados[17]+=","+te;
    //               qV=AV/(fin*nEst);
        //           System.out.println("Fracción promedio de tiempo "
        //                + "con estaciones vacías: "+qV);
        //           resultados[18]+=","+qV;
             //      qValt=AValt/(fin*nEst);
                  // System.out.println("Promedio de vacías (alt): "+qValt);
    //               qLl=ALl/(fin*nEst);
        //           System.out.println("Fracción promedio de tiempo "
        //                + "con estaciones llenas: "+qLl);
        //           resultados[19]+=","+qLl;
             //      qLlalt=ALlalt/(fin*nEst);
                  // System.out.println("Promedio de llenas (alt): "+qLlalt+"\n");
    //               qInef=qLl+qV;
        //           System.out.println("Fracción promedio de tiempo con estaciones "
        //                   + "llenas o vacías: "+qInef);
        //           resultados[20]+=","+qInef;

        //        System.out.println("\n");
        //        System.out.println("Llegadas programadas: "+llegadas);
        //        System.out.println("Creadas por default al último: "+último);
        //        System.out.println("Efectivas: "+(llegadas-último));
    //               SQV=SQV+qV;
    //               SQV2=SQV2+(qV*qV);
    //               SQLl=SQLl+qLl;
    //               SQLl2=SQLl2+(qLl*qLl);
    //               SInef=SInef+qInef;
    //               SInef2=SInef2+(qInef*qInef);
    //               SUT=SUT+fuet;
    //               SUT2=SUT2+(fuet*fuet);
    //               SUD=SUD+fued;
    //               SUD2=SUD2+(fued*fued);
                   SUE=SUE+fue; //suma de la fracción de usuarios que esperó
    //               SUE2=SUE2+(fue*fue);
    //               SUNE=SUNE+fune;
    //               SUNE2=SUNE2+(fune*fune);
    //               STET=STET+tet;
    //               STET2=STET2+(tet*tet);
    //               STED=STED+ted;
    //               STED2=STED2+(ted*ted);
                   STE=STE+te;
    //               STE2=STE2+(te*te);
                   /*
                    for (int m = 0; m < n; m++) {
                        if(((Evento) calendario.dequeue()).tipo==1)
                            NumEsperando++;
        //                    System.out.println(((Evento)calendario.first()).nombre
        //                            +", en t="+((Evento) calendario.dequeue()).tiempo);
                        else
                            NumEsperando2++;
                    }
                    if(calendario.isEmpty())
                        System.out.println("Se vació el calendario. Quedaron "+n
                            +" eventos: " +NumEsperando+" llegadas y "+NumEsperando2
                            +" arribos con bicicleta.");
                    */
               }
                /*
                try{
                    writer = new FileWriter(csv);
                        for (int m = 0; m < 21; m++)
                            writer.append(resultados[m]).append("\n");
                    } catch (Exception e) {
                       System.err.println("Error! "+e.getMessage());
                    } finally {
                       if (null!=writer){
                          try {
                             writer.flush();
                          } catch (IOException e) {
                            System.err.println("Error flushing file !!"+e.getMessage());
                          }
                          try {
                             writer.close();
                          } catch (IOException e) {
                             System.err.println("Error closing file !!"+e.getMessage());
                          }
                       }
                    }

                String csv2= "C:\\Users\\lablogistica\\Desktop\\ecobici\\Globales.csv";
                FileWriter w = null;
                globales=new String[21];
                System.out.println("\n");
                globales[0]="Estadísticas globales:";
                System.out.println(globales[0]);
                FUET=SUT/reps;
                globales[1]="Fracc. promedio de usuarios que esperaron tomar "
                        + "bicicleta: ";
                System.out.println(globales[1]+FUET);
                globales[1]+=","+FUET;
                sUET=SUT2/(reps-1)-(SUT*SUT)/(reps*(reps-1));
                globales[2]="Varianza de fracc. de usuarios que esperaron tomar "
                        + "bicicleta: ";
                System.out.println(globales[2]+sUET);
                globales[2]+=","+sUET;
                FUED=SUD/reps;
                globales[3]="Fracc. promedio de usuarios que esperaron dejar "
                        + "bicicleta: ";
                System.out.println(globales[3]+FUED);
                globales[3]+=","+FUED;
                sUED=SUD2/(reps-1)-(SUD*SUD)/(reps*(reps-1));
                globales[4]="Varianza de fracc. de usuarios que esperaron dejar "
                        + "bicicleta: ";
                System.out.println(globales[4]+sUED);
                globales[4]+=","+sUED;
                */
                FUE=SUE/reps;
    //            globales[5]=;
                System.out.println("Fracc. promedio de usuarios que esperaron: "+FUE);
    //            globales[5]+=","+FUE;
                /*
                sUE=SUE2/(reps-1)-(SUE*SUE)/(reps*(reps-1));
                globales[6]="Varianza de fracc. de usuarios que esperaron: ";
                System.out.println(globales[6]+sUE);
                globales[6]+=","+sUE;
                FUNE=SUNE/reps;
                globales[7]="Fracc. promedio de usuarios que no esperaron: ";
                System.out.println(globales[7]+FUNE);
                globales[7]+=","+FUNE;
                sUNE=SUNE2/(reps-1)-(SUNE*SUNE)/(reps*(reps-1));
                globales[8]="Varianza de fracc. de usuarios que no esperaron: ";
                System.out.println(globales[8]+sUE);
                globales[8]+=","+sUE;
                TET=STET/reps;
                globales[9]="Tiempo de espera promedio para tomar bicicleta: ";
                System.out.println(globales[9]+TET+" minutos");
                globales[9]+=","+TET;
                sTET=STET2/(reps-1)-(STET*STET)/(reps*(reps-1));
                globales[10]="Varianza del tiempo para tomar bicicleta: ";
                System.out.println(globales[10]+sTET);
                globales[10]+=","+sTET;
                TED=STED/reps;
                globales[11]="Tiempo de espera promedio para dejar bicicleta: ";
                System.out.println(globales[11]+TED+" minutos");
                globales[11]+=","+TED;
                sTED=STED2/(reps-1)-(STED*STED)/(reps*(reps-1));
                globales[12]="Varianza del tiempo para dejar bicicleta: ";
                System.out.println(globales[12]+sTED);
                globales[12]+=","+sTED;
                */
                TE=STE/reps;
    //            globales[13]=;
                System.out.println("Tiempo de espera promedio: "+TE+" minutos");
    //            globales[13]+=","+TE;
                /*
                sTE=STE2/(reps-1)-(STE*STE)/(reps*(reps-1));
                globales[14]="Varianza del tiempo de espera: ";
                System.out.println(globales[14]+sTE);
                globales[14]+=","+sTE;
                QV=SQV/reps;
                globales[15]="Fracc. promedio de tiempo con estaciones vacías: ";
                System.out.println(globales[15]+QV);
                globales[15]+=","+QV;
                sQV=SQV2/(reps-1)-(SQV*SQV)/(reps*(reps-1));
                globales[16]="Varianza de la fracc. de tiempo con estaciones vacías: ";
                System.out.println(globales[16]+sQV);
                globales[16]+=","+sQV;
                QLl=SQLl/reps;
                globales[17]="Fracc. promedio de tiempo con estaciones llenas: ";
                System.out.println(globales[17]+QLl);
                globales[17]+=","+QLl;
                sQLl=SQLl2/(reps-1)-(SQLl*SQLl)/(reps*(reps-1));
                globales[18]="Varianza de la fracc. de tiempo con estaciones llenas: ";
                System.out.println(globales[18]+sQLl);
                globales[18]+=","+sQLl;
                Inef=SInef/reps;
                globales[19]="Fracc. prom. de tiempo con estaciones llenas o vacías: ";
                System.out.println(globales[19]+Inef);
                globales[19]+=","+Inef;
                sInef=SInef/(reps-1)-(SInef*SInef)/(reps*(reps-1));
                globales[20]="Varianza de la fracc. de tiempo con estaciones llenas o "
                        + "vacías: ";
                System.out.println(globales[20]+sInef);
                globales[20]+=","+sInef;

                try{
                    w = new FileWriter(csv2);
                    for (int m = 0; m < 21; m++)
                            w.append(globales[m]).append("\n");
                } catch (Exception e) {
                   System.err.println("Error! "+e.getMessage());
                } finally {
                   if (null!=w){
                      try {
                         w.flush();
                      } catch (IOException e) {
                         System.err.println("Error flushing file !! "+e.getMessage());
                      }
                      try {
                         w.close();
                      } catch (IOException e) {
                         System.err.println("Error closing file !! "+e.getMessage());
                      }
                   }
                }
                */
                int buscador=0;
                double resultado=alfa*TE+beta*FUE;
                while(objetivo[buscador][1]!=0&&resultado>objetivo[buscador][1])
                    buscador++;
                for (int i = opt; i > buscador; i--) {
                        objetivo[i][0]=objetivo[i-1][0];
                        objetivo[i][1]=objetivo[i-1][1];
                }
                objetivo[buscador][0]=opt;
                objetivo[buscador][1]=resultado;
                System.out.println("Variable a minimizar: "+resultado);
                System.out.println("\n");
            }
            if(it+1<iter){
                for (int i = 0; i < pob; i++)
                    System.out.println("Ranking: "+(i+1)+", solución: "+(objetivo[i][0]
                            +1)+", puntaje: "+objetivo[i][1]);

                int[][] biciaux = new int[pob][nEst];
                int lugar=0;
                c=new Cruzamiento(estaciones);
                for (int i = 0; i < 10; i++) {
                    biciaux[i]=bicicletas[(int) objetivo[i][0]];
                    lugar++;
                }
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if(i!=j){
                            biciaux[lugar]=c.cruza(biciaux[i], biciaux[j], mutación);
                            lugar++;
                        }
                    }
                }
                bicicletas=biciaux;
                String s="C:\\Users\\lablogistica\\Desktop\\ecobici\\Opt"+(it+1)+".csv";
                FileWriter w=null;
                try{
                    w = new FileWriter(s);
                    for (int i = 0; i < pob; i++) {
                        for (int j = 0; j < nEst; j++)
                            w.append(bicicletas[i][j]+",");
                        w.append("\n");
                    }
                } catch (Exception e) {
                   System.err.println("Error! "+e.getMessage());
                } finally {
                   if (null!=w){
                      try {
                         w.flush();
                      } catch (IOException e) {
                         System.err.println("Error flushing file !! "+e.getMessage());
                      }
                      try {
                         w.close();
                      } catch (IOException e) {
                         System.err.println("Error closing file !! "+e.getMessage());
                      }
                   }
                }
            }
        }
        String s="C:\\Users\\lablogistica\\Desktop\\ecobici\\Solución.csv";
        FileWriter w=null;
        try{
            w = new FileWriter(s);
            for (int i = 0; i < nEst; i++)
                    w.append(bicicletas[(int)objetivo[0][0]][i]+"\n");
        } catch (Exception e) {
           System.err.println("Error! "+e.getMessage());
        } finally {
           if (null!=w){
              try {
                 w.flush();
              } catch (IOException e) {
                 System.err.println("Error flushing file !! "+e.getMessage());
              }
              try {
                 w.close();
              } catch (IOException e) {
                 System.err.println("Error closing file !! "+e.getMessage());
              }
           }
        }
        
    }
    
}
