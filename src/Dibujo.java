import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Dibujo {

    private Trazo cabeza;
    private Trazo trazoSeleccionado;

    public Dibujo(){
        cabeza=null;

    }

    public void agregar(Trazo n){
        if (n !=null) {
            if(cabeza == null){
                cabeza=n;
            }
            else{
                Trazo apuntador = cabeza;
                while (apuntador.siguiente != null){
                    apuntador= apuntador.siguiente;
                }
                apuntador.siguiente= n;
            }
            n.siguiente= null;
        }
    }


    public void desdeArchivos(String nombreArchivo) {
        BufferedReader br = Archivo.abrirArchivo(nombreArchivo);
        try {
            String linea = br.readLine();
            while (linea != null) {
                // Cambiamos el separador a ; para coincidir con el formato de guardado
                String[] datos = linea.split(";");
                if (datos.length >= 5) {
                    // Convertimos el string del tipo a un TipoTrazo
                    TipoTrazo tipo = TipoTrazo.valueOf(datos[0]);
                    Trazo n = new Trazo(
                        tipo.toString(),
                        Integer.parseInt(datos[1]),
                        Integer.parseInt(datos[2]),
                        Integer.parseInt(datos[3]),
                        Integer.parseInt(datos[4])
                    );
                    agregar(n);
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception ex) {
            System.err.println("Error al leer el archivo: " + ex.getMessage());
        }
    }

    public void haciaArchivo() {
        JFileChooser guardarArchivo = new JFileChooser();
        guardarArchivo.setApproveButtonText("Guardar");
        int seleccion = guardarArchivo.showSaveDialog(null);
        
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = new File(guardarArchivo.getSelectedFile() + ".txt");
            Trazo apuntador = cabeza;

            try (BufferedWriter salida = new BufferedWriter(new FileWriter(archivo))) {
                while (apuntador != null) {
                    // Guardamos en el mismo formato que leemos
                    salida.write(String.format("%s;%d;%d;%d;%d%n",
                        apuntador.tipo,
                        apuntador.x1,
                        apuntador.y1,
                        apuntador.x2,
                        apuntador.y2));
                    apuntador = apuntador.siguiente;
                }
                JOptionPane.showMessageDialog(null, "Archivo guardado exitosamente");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al guardar el archivo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public void seleccionar(int x, int y) {
        Trazo actual = cabeza;
        while (actual != null) {
            if (contienePunto(actual, x, y)) {
                trazoSeleccionado = actual;
                return;
            }
            actual = actual.siguiente;
        }
        trazoSeleccionado = null;
    }

    public void eliminar() {
        if (trazoSeleccionado == null) {
            return;
        }

        if (cabeza == trazoSeleccionado) {
            cabeza = cabeza.siguiente;
            trazoSeleccionado = null;
            return;
        }

        Trazo actual = cabeza;
        while (actual.siguiente != null && actual.siguiente != trazoSeleccionado) {
            actual = actual.siguiente;
        }

        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            trazoSeleccionado = null;
        }
    }

    private boolean contienePunto(Trazo trazo, int x, int y) {
        switch (trazo.tipo) {
            case LINEA:
                return distanciaPuntoALinea(trazo.x1, trazo.y1, trazo.x2, trazo.y2, x, y) < 5;
            case RECTANGULO:
                int rx = Math.min(trazo.x1, trazo.x1 + trazo.x2);
                int ry = Math.min(trazo.y1, trazo.y1 + trazo.y2);
                return x >= rx && x <= rx + trazo.x2 && y >= ry && y <= ry + trazo.y2;
            case CIRCULO:
                double cx = trazo.x1 + trazo.x2/2.0;
                double cy = trazo.y1 + trazo.y2/2.0;
                double distancia = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));
                return distancia <= Math.max(trazo.x2, trazo.y2)/2.0;
            default:
                return false;
        }
    }

    private double distanciaPuntoALinea(int x1, int y1, int x2, int y2, int px, int py) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        return Math.sqrt(Math.pow(px - xx, 2) + Math.pow(py - yy, 2));
    }


    public void dibujar(JPanel pnl) {
        Graphics g = pnl.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, pnl.getWidth(), pnl.getHeight());
        
        Trazo apuntador = cabeza;
        while(apuntador != null) {
            // Si el trazo está seleccionado, dibujarlo en blanco
            if (apuntador == trazoSeleccionado) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.BLUE);
            }
            
            switch (apuntador.tipo) {
                case LINEA:
                    g.drawLine(apuntador.x1, apuntador.y1, apuntador.x2, apuntador.y2);
                    break;
                case RECTANGULO:
                    g.drawRect(apuntador.x1, apuntador.y1, apuntador.x2, apuntador.y2);
                    break;
                case CIRCULO:
                    g.drawOval(apuntador.x1, apuntador.y1, apuntador.x2, apuntador.y2);
                    break;
            }
            apuntador = apuntador.siguiente;
        }
    }
}