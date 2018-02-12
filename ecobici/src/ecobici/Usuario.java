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
public class Usuario {
    final int id;
    private final double Ti; // tiempo de inicio de espera
    double Tf; // tiempo de fin de espera
    final int tipo; // si espera para tomar bici, dejarla o ambos
    final int est; //  estación de espera
    
    public Usuario(int n, double ti, int t, int e){
        id = n;
        Ti = ti;
        Tf = 0;
        tipo=t; // 1= espera tomar bici, 2= espera dejar bici
        est=e;
    }
    public double tiempo(){
        double t=Tf-Ti;
        return t;
    }
            
    public String toString(){
        String st;
        double t=tiempo();
        if(tipo==1)
            if(Ti<=Tf)
                st="usuario "+id+" esperó tomar bici por "+t+
                    " minutos en la estación "+est;
            else
                st="usuario "+id+" sigue esperando tomar bici "
                        + "en la estación "+est;
        else if(tipo==2)
            if(Ti<=Tf)
                st="usuario "+id+" esperó dejar bici por "+t+
                    " minutos en la estación "+est;
            else
                st="usuario "+id+" sigue esperando dejar bici "
                        + "en la estación "+est;
        else
            if(Ti<=Tf)
                st="usuario "+id+" esperó tomar bici y "+t+" minutos para"
                        + " dejarla en la estación "+est;
            else
                st="usuario "+id+" esperó tomar bici por y sigue esperando "
                        + "dejar bici en la estación "+est;
        return st;
    }
}
