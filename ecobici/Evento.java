/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

/**
 *
 * @author GerardoSteve
 */
public class Evento {
    
    final int tipo; //1=llegada usuario (tomar), 2=llegada bici (dejar)
    final int estación;
    final String nombre;
    final int tiempo;
    final double c=1; //constante de tiempo
    //final String estado;
    
    public Evento (int n, int e, int t /*, String edo*/){
        tipo=n;
        estación=e;
        tiempo=t;
        if (tipo==1)
            nombre="Llegada a la estación E"+e;
        else
            nombre="Deja bici en estación E"+e;
        //estado=edo;
    }
    
    public Evento(int n, Estación est, int t){
        tipo=n;
        estación=est.Id;
        tiempo=t+est.t;
        if (tipo==1){
            nombre="Llegada a la estación E"+estación;
        }
        else
            nombre="Deja bici en estación E"+estación;
    }
    
    public Evento(int n, Estación est, Estación dest, int t){
        tipo=n;
        estación=est.Id;
        if (tipo==1){
            nombre="Llegada a la estación E"+estación;
            tiempo=t+est.t;
        }
        else{
            nombre="Deja bici en estación E"+estación;
            if(est==dest)
                tiempo=t+est.tmisma;
            else
                tiempo=(int) Math.round(est.distanciaViaje(dest.Longitud,dest.Latitud)*c)+t;
        }
    }
    

}