/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import static java.lang.Math.sqrt;
import static jdk.nashorn.internal.objects.NativeArray.pop;

/**
 *
 * @author GerardoSteve
 */
public class Viaje {

   // private final Bicicleta bici;
    private final Usuario usuario;
    private final Estación inicio;
    private final Estación destino;
    private final double t; //tiempo de viaje
    
    public Viaje(Usuario u, Estación est1, Estación est2){
        usuario = u;
        inicio = est1;
        destino=est2;
        t=0;
    }
    /*
    public double distanciaViaje (){
        double d;
        d= sqrt((inicio.Longitud-destino.Longitud)*(inicio.Longitud-destino.Longitud)
                +(inicio.Latitud-destino.Latitud)*(inicio.Latitud-destino.Latitud));
        return d;
    }
    */
}
