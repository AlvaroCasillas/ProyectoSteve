/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.sqrt;

/**
 *
 * @author GerardoSteve
 */
public class Estación{

    final double Capacidad; //espacios totales
   // final double Latitud;
   // final double Longitud;
    final int Id;
   // double ocupación; //porcentaje de ocupación
    int EspDisp; // espacios disponibles
    double Bicis; // espacios ocupados
    final double[] tasas; //tasas por minuto
   // final double tmisma; //tiempo a la misma estación
    double ut,ud,uet,ued,uetyd; // usuarios que toman bici, que dejan,
    //que esperaron tomar, que esperaron dejar, que esperaron ambas.
    double espt,espd; //usuarios esperando tomar/dejar
    double tet,ted; // tiempo espera tomar, tiempo espera dejar
    double tLlen,tVac; //tiempo que la estación estuvo llena/vacía
    //double ftLlen, ftVac; //fracciones de tiempo llena/vacía
    double fuet, fued; //fracciones de usuarios que esperaron
    double tpet,tped; //tiempos promedios de espera (tomar/dejar)
    double ti,tf; //auxiliares para calcular tLlena y tVacía
    int w;
    FileWriter writ;
    
    public Estación(int id, int c, int b, double[] tiempo){
        Capacidad= c;
      //  Latitud = l;
      //  Longitud = k;
        Id=id;
        EspDisp= c-b;
        Bicis = b;
        tasas=tiempo;
      //  tmisma=mismo;
        ut=0;
        ud=0;
        uet=0;//usuarios que esperaron tomar
        ued=0;//usuarios que esperaron dejar
        uetyd=0;//usuarios que esperaron tomar y dejar
        espt=0;espd=0;//los que están esperando
        tet=0;
        ted=0;
        fuet=0; fued=0;tpet=0;tped=0;
      //  ocupación=Bicis/Capacidad;
        ti=0;
        w=0; //contador de eventos Poisson en un periodo
        writ=null;
    }
    /*
    public double calculaOcupación(){
       return Bicis/Capacidad;
    }
    
    public double distanciaViaje (Estación destino){
        double d;
        //System.out.println(Longitud + "hola");
        //System.out.println(destino.Longitud + "holamundo");
        d= sqrt((Longitud-destino.Longitud)*(Longitud-destino.Longitud)
                +(Latitud-destino.Latitud)*(Latitud-destino.Latitud));
        return d;
    }
    */
    public void recalculaVacía(){
        double aux = tf-ti;
        tVac=tVac+aux;
    }
    public void recalculaLlena(){
        double aux = tf-ti;
        tLlen=tLlen+aux;
    }
    /*
    public double distanciaViaje (double lon, double lat){
        double d;
        d= sqrt((Longitud-lon)*(Longitud-lon)
                +(Latitud-lat)*(Latitud-lat));
        return d;
    }
    */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Estación: ").append(Id).append("\n");
        sb.append("Bicicletas: ").append(Bicis).append("\n");
        sb.append("Espacios: ").append(EspDisp).append("\n");
     //   sb.append("Latitud: ").append(Latitud).append("\n");
     //   sb.append("Longitud: ").append(Longitud).append("\n");
        return sb.toString();
    }
    
    public void calculaFinal(double t){
        if(EspDisp==0){
            tf=t;
            recalculaLlena();
        }
        if(Bicis==0){
            tf=t;
            recalculaVacía();
        }
        if(ut!=0){
            fuet=uet/ut;
            tpet=tet/ut;
        }
        else{
            fuet=0;
            tpet=0;
        }
        if(ud!=0){
            fued=ued/ud;
            tped=ted/ud;
        }
        else{
            fued=0;
            tped=0;
        }
    }
    
    public String toStringFinal(){
        double u=ut+ud;
        StringBuilder sb = new StringBuilder();
        sb.append("Estación: ").append(Id).append("\n");
        sb.append("Bicicletas: ").append(Bicis).append("\n");
        sb.append("Espacios: ").append(EspDisp).append("\n");
      //  sb.append("Latitud: ").append(Latitud).append("\n");
      //  sb.append("Longitud: ").append(Longitud).append("\n");
        sb.append("Usuarios servidos: ").append(u).append("\n");
        if(ut!=0)
            sb.append("Esperaron tomar: ").append(Math.round(uet)).append(" = ")
                .append(fuet).append("\n");
        else
            sb.append("Esperaron tomar: 0").append("\n");
        if(ud!=0)
            sb.append("Esperaron dejar: ").append(Math.round(ued)).append(" = ")
                .append(fued).append("\n");
        else
            sb.append("Esperaron dejar: 0").append("\n");
        if(u!=0)
            sb.append("No esperaron: ").append(Math.round(u-ued-uet))
              .append(" = ").append((u-ued-uet)*100/u).append("%").append("\n");
        else
            sb.append("No esperaron: 0").append("\n");
        sb.append("Tiempo que estuvo vacía: ").append(tVac)
                .append(" minutos").append("\n");
        sb.append("Tiempo que estuvo llena: ").append(tLlen)
                .append(" minutos").append("\n");
        sb.append("Tiempo promedio de espera global: ")
                .append((tet+ted)/u).append(" minutos").append("\n");
        sb.append("Tiempo promedio de espera para tomar bici: ");
        sb.append(tpet).append(" minutos").append("\n");
        sb.append("Tiempo promedio de espera para dejar bici: ");
        sb.append(tped).append(" minutos").append("\n");
        sb.append("Tiempo promedio de quienes esperaron tomar bici: ");
        if(uet!=0)
            sb.append(tet/uet).append(" minutos").append("\n");
        else
            sb.append("0 minutos").append("\n");
        sb.append("Tiempo promedio de quienes esperaron dejar bici: ");
        if(ued!=0)
            sb.append(ted/ued).append(" minutos").append("\n");
        else
            sb.append("0 minutos").append("\n");
        
        return sb.toString();
    }
    /*
    public void resultados(){
        Ecobici.res[Id-1][0]+=","+ut;
        Ecobici.res[Id-1][1]+=","+uet;
        Ecobici.res[Id-1][2]+=","+fuet;
        Ecobici.res[Id-1][3]+=","+tpet;
        Ecobici.res[Id-1][4]+=","+tVac;
        Ecobici.res[Id-1][5]+=","+ud;
        Ecobici.res[Id-1][6]+=","+ued;
        Ecobici.res[Id-1][7]+=","+fued;
        Ecobici.res[Id-1][8]+=","+tped;
        Ecobici.res[Id-1][9]+=","+tLlen;
        Ecobici.res[Id-1][10]+=","+(ud-ut);
//        if((uet+ued)!=uet&&(uet+ued)!=ued)
//            System.out.println("Estación "+Id+" tiene usuarios que esperan tanto esperar como dejar...");
    }
    
    public void imprimeFinal(){
        String s= "C:\\Users\\lablogistica\\Desktop\\ecobici\\Est"+Id+".csv";
        try{
            writ = new FileWriter(s);
            for (int i = 0; i < 11; i++)
                    writ.append(Ecobici.res[Id-1][i]).append("\n");
        } catch (Exception e) {
           System.err.println("Error! "+e.getMessage());
        } finally {
           if (null!=writ){
              try {
                 writ.flush();
              } catch (IOException e) {
                 System.err.println("Error flushing file !! "+e.getMessage());
              }
              try {
                 writ.close();
              } catch (IOException e) {
                 System.err.println("Error closing file !! "+e.getMessage());
              }
           }
        }
    }
    
    public double distanciaViaje (){
        double d;
        d= sqrt((Longitud-e.Longitud)*(Longitud-e.Longitud)
                +(Latitud-e.Latitud)*(Latitud-e.Latitud));
        return d;
    }
    */
}
