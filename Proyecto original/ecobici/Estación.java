/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import static java.lang.Math.sqrt;

/**
 *
 * @author GerardoSteve
 */
public class Estación{

    final int Capacidad; //espacios totales
    final double Latitud;
    final double Longitud;
    final int Id;
  //  final double ocupación; //porcentaje de ocupación
  //  final int UED; // usuarios esperando dejar
  //  final int UET; // usuarios esperando tomar
    int EspDisp; // espacios disponibles
    int Bicis; // espacios ocupados
  //  final Bicicleta[] bicis; // bicicletas
  //  final Estación e; //una estación de destino asociada, cuando this() es inicio
  //  final double prob; //probabilidad de viaje a la misma estación
    final int t; //interarrival time
    final int tmisma; //tiempo a la misma estación
    
    public Estación(int id, int c, int b, double l, double k, int mismo, 
            int tiempo){
        Capacidad= c;
        Latitud = l;
        Longitud = k;
        Id=id;
        EspDisp= c-b;
        Bicis = b;
        //e=est;
       // prob=p;
        t=tiempo;
        tmisma=mismo;
    }
    
    public double distanciaViaje (Estación destino){
        double d;
        //System.out.println(Longitud + "hola");
        //System.out.println(destino.Longitud + "holamundo");
        d= sqrt((Longitud-destino.Longitud)*(Longitud-destino.Longitud)
                +(Latitud-destino.Latitud)*(Latitud-destino.Latitud));
        return d;
    }
    
    public double distanciaViaje (double lon, double lat){
        double d;
        d= sqrt((Longitud-lon)*(Longitud-lon)
                +(Latitud-lat)*(Latitud-lat));
        return d;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Estación: ").append(Id).append("\n");
        sb.append("Bicicletas: ").append(Bicis).append("\n");
        sb.append("Espacios: ").append(EspDisp).append("\n");
        sb.append("Latitud: ").append(Latitud).append("\n");
        sb.append("Longitud: ").append(Longitud).append("\n");
        sb.append("Tiempo entre llegadas: ").append(t).append("\n");
        return sb.toString();
    }
    /*
    public double distanciaViaje (){
        double d;
        d= sqrt((Longitud-e.Longitud)*(Longitud-e.Longitud)
                +(Latitud-e.Latitud)*(Latitud-e.Latitud));
        return d;
    }
    */
}
