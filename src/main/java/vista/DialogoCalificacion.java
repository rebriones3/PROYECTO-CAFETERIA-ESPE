
package vista;

import controlador.ControladorCalificaciones;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author PERSONAL
 */
public class DialogoCalificacion extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogoCalificacion.class.getName());

    private int calificacionSeleccionada = 0;
    private JLabel[] estrellas;
    private JTextArea txtComentario;
    private String codigoTransaccion;
    private String estudiante;
    private boolean calificacionEnviada = false;
    
    public DialogoCalificacion(JFrame parent, String codigoTransaccion, String estudiante) {
        super(parent, "¿Cómo fue tu experiencia?", true);
        this.codigoTransaccion = codigoTransaccion;
        this.estudiante = estudiante;
        
        initComponent();
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Evitar que se cierre con ESC o X sin calificar
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                    DialogoCalificacion.this,
                    "¿Deseas omitir la calificación?\n(Nos ayuda mucho tu opinión)",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (opcion == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }
    
    private void initComponent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Ícono de agradecimiento
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Título
        JLabel lblTitulo = new JLabel("¡Gracias por tu pedido!");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(0, 153, 51));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("¿Cómo fue tu experiencia?");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(102, 102, 102));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel de estrellas
        JPanel panelEstrellas = crearPanelEstrellas();
        panelEstrellas.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Label de descripción de calificación
        JLabel lblDescripcion = new JLabel(" ");
        lblDescripcion.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblDescripcion.setForeground(new Color(102, 102, 102));
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Área de comentario
        JLabel lblComentario = new JLabel("Comentario (opcional):");
        lblComentario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblComentario.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtComentario = new JTextArea(3, 30);
        txtComentario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        txtComentario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JScrollPane scrollComentario = new JScrollPane(txtComentario);
        scrollComentario.setMaximumSize(new Dimension(400, 80));
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnEnviar = new JButton("Enviar Calificación");
        btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEnviar.setBackground(new Color(0, 153, 51));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setPreferredSize(new Dimension(180, 40));
        btnEnviar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnOmitir = new JButton("Omitir");
        btnOmitir.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnOmitir.setBackground(new Color(230, 230, 230));
        btnOmitir.setForeground(new Color(102, 102, 102));
        btnOmitir.setFocusPainted(false);
        btnOmitir.setPreferredSize(new Dimension(100, 40));
        btnOmitir.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnOmitir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Eventos de estrellas - actualizar descripción
        MouseAdapter estrellasListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel estrella = (JLabel) e.getSource();
                int rating = Integer.parseInt(estrella.getName());
                actualizarDescripcion(lblDescripcion, rating);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (calificacionSeleccionada == 0) {
                    lblDescripcion.setText(" ");
                } else {
                    actualizarDescripcion(lblDescripcion, calificacionSeleccionada);
                }
            }
        };
        
        for (JLabel estrella : estrellas) {
            estrella.addMouseListener(estrellasListener);
        }
        
        // Evento botón enviar
        btnEnviar.addActionListener(e -> enviarCalificacion());
        
        // Evento botón omitir
        btnOmitir.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas omitir la calificación?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
            
            if (opcion == JOptionPane.YES_OPTION) {
                dispose();
            }
        });
        
        // Agregar componentes
        mainPanel.add(lblIcono);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(lblTitulo);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(lblSubtitulo);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(panelEstrellas);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblDescripcion);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lblComentario);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(scrollComentario);
        mainPanel.add(Box.createVerticalStrut(25));
        panelBotones.add(btnEnviar);
        panelBotones.add(btnOmitir);
        mainPanel.add(panelBotones);
        
        add(mainPanel);
    }
    
    private JPanel crearPanelEstrellas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(Color.WHITE);
        
        estrellas = new JLabel[5];
        
        for (int i = 0; i < 5; i++) {
            final int rating = i + 1;
            
            JLabel estrella = new JLabel("☆");
            estrella.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            estrella.setForeground(new Color(200, 200, 200));
            estrella.setName(String.valueOf(rating));
            estrella.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Evento click
            estrella.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    seleccionarCalificacion(rating);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    resaltarEstrellas(rating);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (calificacionSeleccionada > 0) {
                        resaltarEstrellas(calificacionSeleccionada);
                    } else {
                        limpiarEstrellas();
                    }
                }
            });
            
            estrellas[i] = estrella;
            panel.add(estrella);
        }
        
        return panel;
    }
    
    private void seleccionarCalificacion(int rating) {
        calificacionSeleccionada = rating;
        resaltarEstrellas(rating);
    }
    
    private void resaltarEstrellas(int rating) {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                estrellas[i].setText("");
                estrellas[i].setForeground(new Color(255, 193, 7)); // Amarillo dorado
            } else {
                estrellas[i].setText("");
                estrellas[i].setForeground(new Color(200, 200, 200));
            }
        }
    }
    
    private void limpiarEstrellas() {
        for (JLabel estrella : estrellas) {
            estrella.setText("");
            estrella.setForeground(new Color(200, 200, 200));
        }
    }
    
    private void actualizarDescripcion(JLabel lblDescripcion, int rating) {
        String[] descripciones = {
            "Muy malo",
            "Malo",
            "Regular",
            "Bueno",
            "¡Excelente!"
        };
        
        if (rating >= 1 && rating <= 5) {
            lblDescripcion.setText(descripciones[rating - 1]);
        }
    }
    
    private void enviarCalificacion() {
        if (calificacionSeleccionada == 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecciona una calificación con las estrellas.",
                "Calificación requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String comentario = txtComentario.getText().trim();
        
        // Guardar en base de datos
        boolean exito = ControladorCalificaciones.guardarCalificacion(
            codigoTransaccion,
            estudiante,
            calificacionSeleccionada,
            comentario
        );
        
        if (exito) {
            calificacionEnviada = true;
            
            // Mensaje de agradecimiento
            JOptionPane.showMessageDialog(this,
                "¡Gracias por tu calificación!\n" +
                "Tu opinión nos ayuda a mejorar nuestro servicio.",
                "Calificación enviada",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Hubo un error al enviar tu calificación.\n" +
                "Por favor, intenta nuevamente.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isCalificacionEnviada() {
        return calificacionEnviada;
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}