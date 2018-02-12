/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

/**
 *
 * @author LabLogistica
 */
public class Cruzamiento {
    int[] capacidades;
    
    public Cruzamiento(Estación[] est){
        int n=est.length;
        capacidades=new int[n];
        for (int i = 0; i < n; i++) {
            capacidades[i]=(int)est[i].Capacidad;
        }
    }
    
    public int[] cruza(int[] papá, int[] mamá, double mut){
        int n=papá.length;
        int[] hijo=new int[n];
        for (int i = 0; i < n; i++) {
            if(Math.random()<0.6)
                hijo[i]=papá[i];
            else
                hijo[i]=mamá[i];
            double r=Math.random();
            if(r<mut&&hijo[i]>1)
                hijo[i]--;
            else if(r>(1-mut)&&hijo[i]<capacidades[i])
                hijo[i]++;
        }
        return hijo;
    }
    
}
