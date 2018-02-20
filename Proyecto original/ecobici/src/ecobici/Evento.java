/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import static ecobici.Ecobici.arribos;


/**
 *
 * @author GerardoSteve
 */
public class Evento {
    
    final int tipo; //1=tomar bici, 2=dejarla, 3=dejar habiendo esperado tomar
    final int estación;
    final String nombre;
    double tiempo;
    int u=-1; //para hacer tracking de usuario
    //final double c=1; //constante de tiempo
    final double duración=Ecobici.durper;
    final int cantper=Ecobici.periodos; //cantidad de periodos
    
    /*
    public Evento (int n, int e, double t){
        tipo=n;
        estación=e;
        tiempo=t;
        if (tipo==1)
            nombre="Toma bici en la estación E"+e;
        else
            nombre="Deja bici en la estación E"+e;
    }
    */
    //Las tasas están en eventos por minuto
    public Evento(Estación est, double t, int p){ //p es el periodo actual
        tipo=1;
        estación=est.Id;
        nombre="Toma bici en la estación E"+estación;
        tiempo=encuentraTiempo(t,est,p);
        Ecobici.llegadas++;
    }
    
    private double encuentraTiempo(double t, Estación est, int p){
        double tasa=est.tasas[p];
        if(tasa>=1/(7*duración)){
            est.w=0;
            double taux=t-Math.log(1-Math.random())/tasa;
            if(taux/duración>p+1&&p<cantper-1&&est.tasas[p+1]>1/(7*duración)){
                p++;
                return (p)*duración+(taux-(p)*duración)*tasa/est.tasas[p];
            }
            else if(taux/duración>p+1&&p<cantper-1)
                return encuentraTiempo((p+1)*duración,est,p+1);
            else
                return taux;
        }
        else{
            est.w++; 
            if(Math.random()<generaPoisson(tasa*duración,est.w))

                return t+Math.random()*(((p+1)*duración)-t);
            else if(p<cantper-1){
                est.w=0;
                return encuentraTiempo((p+1)*duración,est,p+1);
            }
            else{
//                Ecobici.último++;
                return Ecobici.fin;
            }
        }
    }
    private double generaPoisson(double lambda, int x){
        double p=0;
        for (int i = 0; i < x; i++) {
            p+=Math.exp(-lambda)*Math.pow(lambda, i)/factorial(i);
        }
        return 1-p;
    }
    private int factorial(int k){
        if(k==0)
            return 1;
        else
            return k*factorial(k-1);
    }
    /*
    public Evento(Estación est, Estación dest, double t){
        tipo=2;
        estación=dest.Id;
        nombre="Deja bici en la estación E"+estación;
        if(est==dest)
            tiempo=t+est.tmisma;
        else
            tiempo=est.distanciaViaje(dest.Longitud,dest.Latitud)*c+t;
    }
    */
    public Evento(double t){
        tipo=2;
        tiempo = t+Ecobici.lognormal.sample();
        if(tiempo>(Ecobici.periodos*Ecobici.durper))
            tiempo=tiempo-(Ecobici.periodos*Ecobici.durper);
        int columna = (int) Math.floor(tiempo/Ecobici.durper);
        int k=0;
        double r=Math.random();
        while(arribos[k][columna]<r)
            k++;
        estación=k+1;
        nombre="Deja bici en la estación E"+estación;
    }
    public Evento(double t, int m){
        tipo=3;
        u=m;
        tiempo = t+Ecobici.lognormal.sample();
        if(tiempo>(Ecobici.periodos*Ecobici.durper))
            tiempo=tiempo-(Ecobici.periodos*Ecobici.durper);
        int columna = (int) Math.floor(tiempo/Ecobici.durper);
        int k=0;
        double r=Math.random();
        while(arribos[k][columna]<r)
            k++;
        estación=k+1;
        nombre="Deja bici en la estación E"+estación;
    }
}