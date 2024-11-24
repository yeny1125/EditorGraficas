import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

public class FrmEditor extends JFrame {

    private JButton btnCargar;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnSeleccionar;
    private JComboBox cmbTipo;
    private JToolBar tbEditor;
    private JPanel pnlGrafica;
    private String nombreArchivo;

    Estado estado;
    int x;
    int y;
    Color color;
    Dibujo dibujo = new Dibujo();

    public FrmEditor() {
        tbEditor = new JToolBar();
        btnCargar = new JButton();
        btnGuardar = new JButton();
        btnEliminar = new JButton();
        cmbTipo = new JComboBox();
        btnSeleccionar = new JButton();
        pnlGrafica = new JPanel();

        setSize(600, 300);
        setTitle("Editor de gráficas");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        btnCargar.setIcon(new ImageIcon(getClass().getResource("/iconos/AbrirArchivos.png")));
        btnCargar.setToolTipText("Agregar");
        btnCargar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCargarClick(evt);
            }
        });
        tbEditor.add(btnCargar);

        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/iconos/Guardar.png")));
        btnGuardar.setToolTipText("Agregar");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGuardarClick(evt);
            }
        });
        tbEditor.add(btnGuardar);

        cmbTipo.setModel(new DefaultComboBoxModel(new String[] { "Línea", "Rectángulo", "Circulo" }));
        tbEditor.add(cmbTipo);

        btnSeleccionar.setIcon(new ImageIcon(getClass().getResource("/iconos/Seleccionar.png")));
        btnSeleccionar.setToolTipText("Seleccionar");
        btnSeleccionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnSeleccionarClick(evt);
            }
        });
        tbEditor.add(btnSeleccionar);

        btnEliminar.setIcon(new ImageIcon(getClass().getResource("/iconos/Eliminar.png")));
        btnEliminar.setToolTipText("Eliminar");
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnEliminarClick(evt);
            }
        });
        tbEditor.add(btnEliminar);

        pnlGrafica.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                pnlGraficaMouseClicked(evt);
            }
        });
        pnlGrafica.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent evt) {
                pnlGraficaMouseMoved(evt);
            }
        });

        pnlGrafica.setPreferredSize(new Dimension(300, 200));

        getContentPane().add(tbEditor, BorderLayout.NORTH);
        getContentPane().add(pnlGrafica, BorderLayout.CENTER);

        estado = Estado.NADA;
        color = Color.white;

        this.pack();
        limpiarPanel();
    }

    private void limpiarPanel() {
        Graphics g = pnlGrafica.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, pnlGrafica.getWidth(), pnlGrafica.getHeight());
    }

    private void btnCargarClick(ActionEvent evt) {
        String nombreArchivo = Archivo.elegirArchivo();
        dibujo.desdeArchivos(nombreArchivo);
        dibujo.dibujar(pnlGrafica);
    }

    private void btnGuardarClick(ActionEvent evt) {
        dibujo.haciaArchivo();
    }

    private void btnSeleccionarClick(ActionEvent evt) {
        estado = Estado.SELECCIONANDO;
    }

    private void btnEliminarClick(ActionEvent evt) {
        dibujo.eliminar();
        dibujo.dibujar(pnlGrafica);
    }

    private void pnlGraficaMouseClicked(MouseEvent evt) {
        if (estado == Estado.SELECCIONANDO) {
            dibujo.seleccionar(evt.getX(), evt.getY());
            dibujo.dibujar(pnlGrafica);
            return;
        }

        if (estado == Estado.NADA) {
            estado = Estado.TRAZANDO;
            x = evt.getX();
            y = evt.getY();
        } else {
            String tipoTrazo;
            int x1, y1, x2, y2;
            
            switch (cmbTipo.getSelectedIndex()) {
                case 0: // Línea
                    tipoTrazo = TipoTrazo.LINEA.toString();
                    x1 = x;
                    y1 = y;
                    x2 = evt.getX();
                    y2 = evt.getY();
                    break;
                case 1: // Rectángulo
                    tipoTrazo = TipoTrazo.RECTANGULO.toString();
                    x1 = Math.min(x, evt.getX());
                    y1 = Math.min(y, evt.getY());
                    x2 = Math.abs(evt.getX() - x + 1);
                    y2 = Math.abs(evt.getY() - y + 1);
                    break;
                case 2: // Círculo
                    tipoTrazo = TipoTrazo.CIRCULO.toString();
                    x1 = Math.min(x, evt.getX());
                    y1 = Math.min(y, evt.getY());
                    x2 = Math.abs(evt.getX() - x + 1);
                    y2 = Math.abs(evt.getY() - y + 1);
                    break;
                default:
                    return;
            }
            
            Trazo nuevoTrazo = new Trazo(tipoTrazo, x1, y1, x2, y2);
            dibujo.agregar(nuevoTrazo);
            dibujo.dibujar(pnlGrafica);
            estado = Estado.NADA;
        }
    }

    private void pnlGraficaMouseMoved(MouseEvent evt) {
        switch (estado) {
            case SELECCIONANDO:
                break;
            case TRAZANDO:
                limpiarPanel();
                dibujo.dibujar(pnlGrafica);
                Graphics g = pnlGrafica.getGraphics();
                g.setColor(Color.yellow);
                int x1, y1;
                switch (cmbTipo.getSelectedIndex()) {
                    case 0:
                        g.drawLine(x, y, evt.getX(), evt.getY());
                        break;
                    case 1:
                        x1 = evt.getX() < x ? evt.getX() : x;
                        y1 = evt.getY() < y ? evt.getY() : y;
                        g.drawRect(x1, y1, Math.abs(evt.getX() - x + 1), Math.abs(evt.getY() - y + 1));
                        break;
                    case 2:
                        x1 = evt.getX() < x ? evt.getX() : x;
                        y1 = evt.getY() < y ? evt.getY() : y;
                        g.drawOval(x1, y1, Math.abs(evt.getX() - x + 1), Math.abs(evt.getY() - y + 1));
                        break;
                }
                break;
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmEditor().setVisible(true);
            }
        });
    }
}
