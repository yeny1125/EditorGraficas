public class Trazo {

    TipoTrazo tipo;
    int x1,y1,x2,y2;

    Trazo siguiente;

    public Trazo(String tipo, int x1,int y1,int x2,int y2){

        TipoTrazo tipoTrazo= TipoTrazo.LINEA;
        for (int i=0; i < TipoTrazo.values().length; i++){
            if (TipoTrazo.values()[i].toString().equals(tipo)){
                tipoTrazo = TipoTrazo.values()[i];

                System.out.println(tipoTrazo);
            }
        }
        this.tipo= tipoTrazo;
        this.x1= x1;
        this.y1= y1;
        this.x2= x2;
        this.y2= y2;
    }    
}
