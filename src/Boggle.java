/* 
    Estructuras de datos avanzadas - Proyecto final

    Docente:
    Norma Leticia Mendez Mariscal

    Integrantes:
    Luis Humberto Izquierdo Moran 329630
    Axel Rivas Guillen 329830

    El codigo consiste en implementar el juego "Boggle" en java
    utilizando un trie que contenga las palabras que pueden
    formarse en el tablero (63, 165, 187) y una tabla hash para almacenar las
    palabras ya ingresadas (50, 208, 517) y asi evitar ganar puntos usando la
    misma palabra.

    P.D - Los numeros en parentesis son las lineas de codigo referentes a los temas.
*/

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Boggle extends JFrame implements ActionListener{
    /**
     *
     */
    private static final long serialVersionUID = 1L;//solo porque el editor de codigo dijo que se requeria

    //declaracion de variables
    JPanel panel;
    JButton[][] tablero;
    JButton inicio, salir, terminarPalabra;
    JLabel presentacion, proyecto, palabra, logo, miembros, docente, luis, axel, score, bonus, pntaje;
    ImageIcon uach;
    List<JButton> ocupadas = new ArrayList<>();
    static HashMap<Integer, String> palabrasUsadas = new HashMap<Integer, String>();
    int puntuacion;
    //tamaño de alfabeto
    static NodoTrie raiz;
    static int tam_Alfabeto = 26;
    final static int MAX = 80257;
    String[] consonantes = {"B", "C", "D", "F", "G", "H", "J", "K",//21 en total
                            "L", "M", "N", "P", "Q", "R", "S", "T",
                            "V", "W", "X", "Y", "Z"
                           };
    String[] vocales = {"A", "E", "I", "O", "U"};//5 en total
    String letra;

    //Nodo Trie
    static class NodoTrie{
        NodoTrie[] hijos = new NodoTrie[tam_Alfabeto]; 
    
        boolean finalPalabra;

        NodoTrie(){
            finalPalabra = false;
            for(int i = 0; i < tam_Alfabeto; i++){
                hijos[i] = null;
            }
        }
    };
    

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        new Boggle();
    }

    Boggle() {
        iniciarVentana();
        portada();
    }

    private void iniciarVentana() {
        this.setResizable(false);
        this.setVisible(true);
        this.setTitle("Boggle");
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void portada() {
        Font arial18 = new Font("arial", Font.BOLD, 18);
        
        panel = new JPanel();
        this.getContentPane().add(panel);
        panel.setLayout(null);
        
        presentacion = new JLabel();
        presentacion.setBounds(200, 40, 280, 20);
        presentacion.setText("Estructuras de datos avanzadas");
        presentacion.setFont(arial18);
        
        proyecto = new JLabel();
        proyecto.setBounds(275, 70, 125, 20);
        proyecto.setText("Proyecto final");
        proyecto.setFont(arial18);

        miembros = new JLabel();
        miembros.setBounds(300, 150, 110, 20);
        miembros.setText("Integrantes:");
        miembros.setFont(arial18);

        luis = new JLabel();
        luis.setBounds(300, 180, 325, 20);
        luis.setText("Luis Humberto Izquierdo Moran - 329630");
        luis.setFont(new Font("arial", Font.BOLD, 14));

        axel = new JLabel();
        axel.setBounds(300, 200, 235, 20);
        axel.setText("Axel Rivas Guillen - 329830");
        axel.setFont(new Font("arial", Font.BOLD, 14));
        
        inicio = new JButton();
        inicio.setText("Iniciar");
        inicio.setFont(arial18);
        inicio.setBounds(525, 350, 90, 30);
        inicio.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        uach = new ImageIcon("uach.png");
        
        logo = new JLabel(); 
        logo.setBounds(50, 125, 162, 212);
        logo.setIcon(new ImageIcon(uach.getImage().getScaledInstance(logo.getWidth(), logo.getHeight(), Image.SCALE_SMOOTH)));
        
        panel.add(presentacion);
        panel.add(proyecto);
        panel.add(miembros);
        panel.add(luis);
        panel.add(axel);
        panel.add(logo);
        panel.add(inicio);

        //estableciendo el fondo de los componentes despues de agregarlos
        //asegura que se muestren correctamente
        presentacion.setBackground(null);
        proyecto.setBackground(null);
        miembros.setBackground(null);
        luis.setBackground(null);
        axel.setBackground(null);
        logo.setBackground(null);
        inicio.setBackground(Color.WHITE);
        inicio.setForeground(Color.BLUE);

        inicio.addActionListener(this);
    }

    //metodo para insertar palabras (especificamente para insertar las palabras del archivo)
    void insertar(String llave){
        int nivel;
        int largo = llave.length();
        int index;

        NodoTrie nodoAux = raiz;

        for (nivel = 0; nivel < largo; nivel++){
            //calcula el valor ASCII de la llave.
            index = llave.charAt(nivel) - 'a';
            if(nodoAux.hijos[index] == null){
                nodoAux.hijos[index] = new NodoTrie();
            }
            nodoAux = nodoAux.hijos[index];
        }
       
        //el ultimo nodo se marca como final de palabra.
        nodoAux.finalPalabra = true;
    }

    //metodo para buscar palabras
    //true = si esta. false = no esta.
    boolean buscar(String llave){
        int nivel = 0;
        int largo = llave.length();
        int index;
        NodoTrie nodoAux = raiz;

        for (; nivel < largo; nivel++){
            index = llave.charAt(nivel) - 'a';

            if (nodoAux.hijos[index] == null)
                return false;
       
            nodoAux = nodoAux.hijos[index];
        }
        
        return (nodoAux != null && nodoAux.finalPalabra);
    }

    
    //los indices de la tabla hash se calculan sumando el valor ASCII
    //de los caracteres de la llave.
    int indiceHash(String llave){
        int lenght = llave.length();
        int indice = 0;
        for(int i = 0; i<lenght; i++){
            indice += llave.charAt(i);
        }
        return indice;
    }
    
    //desaparece los elementos de la portada e inicializa los necesarios para jugar
    private void iniciarTablero(){
        logo.setVisible(false);
        presentacion.setVisible(false);
        proyecto.setVisible(false);
        miembros.setVisible(false);
        luis.setVisible(false);
        axel.setVisible(false);
        inicio.setVisible(false);
        
        panel.removeAll();
        /* panel.remove(presentacion);
        panel.remove(proyecto);
        panel.remove(inicio);*/

        raiz = new NodoTrie();
        
        //procedimiento para insertar el archivo de texto "diccionario20202.txt" en el trie.
        try{
            File myObj = new File("diccionario20202.txt");
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
                insertar(data);
            }
            myReader.close();
        }catch(FileNotFoundException ex){
            System.out.println("ERROR");
            ex.printStackTrace();
        }

        Font arial = new Font("arial", Font.BOLD, 14);
        
        tablero = new JButton[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tablero[i][j] = new JButton();
                tablero[i][j].setBounds((i+1)*50, (j+1)*50, 50, 50);
                tablero[i][j].setText(caracterAleatorio());
                tablero[i][j].setFont(arial);
                tablero[i][j].addActionListener(this);
                panel.add(tablero[i][j]);
                tablero[i][j].setBackground(Color.BLUE);
                tablero[i][j].setForeground(Color.WHITE);
            }
        }

        score = new JLabel();
        score.setBounds(490, 200, 120, 30);
        score.setText("Puntuacion:");
        score.setFont(arial);

        bonus = new JLabel();
        bonus.setBounds(620, 200, 40, 30);
        bonus.setFont(arial);

        salir = new JButton();
        salir.setBounds(560, 380, 90, 30);
        salir.setText("Salir");
        salir.setFont(arial);
        salir.addActionListener(this);

        terminarPalabra = new JButton();
        terminarPalabra.setBounds(490, 100, 150, 30);
        terminarPalabra.setText("Terminar palabra");
        terminarPalabra.setFont(new Font("arial", Font.BOLD, 12));
        terminarPalabra.addActionListener(this);

        palabra = new JLabel();
        palabra.setOpaque(true);
        palabra.setBounds(490, 50, 150, 30);
        palabra.setFont(arial);

        panel.add(palabra);
        panel.add(terminarPalabra);
        panel.add(score);
        panel.add(bonus);
        panel.add(salir);

        terminarPalabra.setBackground(Color.WHITE);
        terminarPalabra.setForeground(Color.BLUE);

        palabra.setBackground(Color.WHITE);
        palabra.setForeground(Color.BLACK);

        salir.setBackground(Color.WHITE);
        salir.setForeground(Color.RED);

        puntuacion = 0;
    }

    //regresa una letra aleatoria en base a los arreglos de vocales y consonantes
    private String caracterAleatorio(){
        Random ran = new Random();
        
        int tipo = ran.nextInt(2);//numero que nos ayda a decidir si el caracter es vocal o consonante
        int indice;
        
        //consonante
        if(tipo == 0){
            indice = ran.nextInt(21);//hay 21 consonantes
            letra = consonantes[indice];
        }

        //vocal
        else if(tipo == 1){
            indice = ran.nextInt(5);
            letra = vocales[indice];
        }
        return letra;
    }

    private void reiniciarTablero(){
        palabra.setText("");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tablero[i][j].setEnabled(true);
                tablero[i][j].setBackground(Color.BLUE);
                tablero[i][j].setForeground(Color.WHITE);
            }
        }
        ocupadas.clear();
    }

    //verifica si las casillas estan en el rango permitido
    private void buscarAdyacente(JButton botonPulsado){
        int x = botonPulsado.getBounds().x / 50;
        int y = botonPulsado.getBounds().y / 50;

        x--;
        y--;

        ocupadas.add(botonPulsado);

        for (JButton b : ocupadas) {
            b.setBackground(Color.LIGHT_GRAY);
        }

        botonPulsado.setEnabled(false);
        botonPulsado.setBackground(Color.WHITE);

        // palabra.setText("x,y: " + x + ", " + y);

        //la letra no esta en la orilla ni es esquina
        if(x < 7 && x > 0 && y < 7 && y > 0){
            for (int i = (x-1); i <= (x+1); i++) {
                for (int j = (y-1); j <= (y+1); j++) {
                    if(!ocupadas.contains(tablero[i][j])){
                        disponibles(tablero[i][j]);
                    }
                }
            }
        }
        else if(x == 0){
            if(y == 0){
                
                //esquina superior izquierda
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        if(!ocupadas.contains(tablero[i][j])){
                            disponibles(tablero[i][j]);
                        }
                    }
                }
            }
            else if(y < 7){
                
                //orilla izquierda
                for (int i = x; i <= (x+1); i++) {
                    for (int j = (y-1); j <= (y+1); j++) {
                        if(!ocupadas.contains(tablero[i][j])){
                            disponibles(tablero[i][j]);
                        }
                    }
                }
            }
            else{

                //esquina inferior izquierda
                for (int i = x; i <= (x+1); i++) {
                    for (int j = (y-1); j <= y; j++) {
                        if(!ocupadas.contains(tablero[i][j])){
                            disponibles(tablero[i][j]);
                        }
                    }
                }
            }
        }
        else if(y == 0){

            //orilla superior
            if(x < 7 && x > 0){
                for (int i = (x-1); i <= (x+1); i++) {
                    for (int j = y; j <= (y+1); j++) {
                        if(!ocupadas.contains(tablero[i][j])){
                            disponibles(tablero[i][j]);
                        }
                    }
                }
            }

            //esquina superior derecha
            else{
                for (int i = 6; i < 8; i++){
                    for (int j = 0; j < 2; j++){
                        if(!ocupadas.contains(tablero[i][j])){
                            disponibles(tablero[i][j]);
                        }
                    }
                }
            }
        }
        else if(y == 7){

            //orilla inferior
            if(x > 0 && x < 7){
                for (int i = (x-1); i <= (x+1); i++) {
                    for (int j = (y-1); j <= y; j++) {
                        if(!ocupadas.contains(tablero[i][j])){
                            disponibles(tablero[i][j]);
                        }
                    }
                }
            }
            else{
                //esquina inferior derecha
                for (int i = 6; i < 8; i++){
                    for (int j = 6; j < 8; j++){
                        if(!ocupadas.contains(tablero[i][j])){
                            disponibles(tablero[i][j]);
                        }
                    }
                }
            }
        }
        else if(x == 7 && y > 0 && y < 7){

            //orilla derecha
            for (int i = (x-1); i <= x; i++) {
                for (int j = (y-1); j <= (y+1); j++) {
                    if(!ocupadas.contains(tablero[i][j])){
                        disponibles(tablero[i][j]);
                    }
                }
            }
        }
    }
    
    private void desactivarBotones(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tablero[i][j].setEnabled(false);
                tablero[i][j].setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    //activa las casillas en el rango permitido
    private void disponibles(JButton pulsado){
        pulsado.setBackground(Color.BLUE);
        pulsado.setForeground(Color.WHITE);
        pulsado.setEnabled(true);
    }

    //se llama cuando un boton es presionado
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();//source = que boton llamo la funcion
        if(source == inicio){
            iniciarTablero();
        }
        else if(source == salir){
            String comentario;
            if(puntuacion < 0){
                comentario = "\nQUE PASO QUE PASO VAMOS AY?!?!";
            }
            else if(puntuacion == 0){
                comentario = "\nTE FUISTE COMO EMPEZASTE";
            }
            else{
                comentario = "\nFELICIDADES!!!!";
            }
            JOptionPane.showMessageDialog(this, "LA PUNTUACION FUE: " + puntuacion + comentario);

            System.exit(0);
        }
        else if(source == terminarPalabra){
            String entrada = palabra.getText().toLowerCase();
            int aux = 0;
            aux += entrada.length();

            //true = si esta en el trie la palabra introducida.
            if(buscar(entrada) == true){
                //false = no se ha introducido la palabra, true = ya fue introducida.
                if(palabrasUsadas.containsKey(indiceHash(entrada))){
                    reiniciarTablero();
                    score.setText("Puntuacion:  " + puntuacion);
                    bonus.setForeground(Color.YELLOW);
                    bonus.setText("+" + 0);
                }
                //como la palabra no se encuentra en el hash map,
                //se guarda en tal.
                else{
                    palabrasUsadas.put(indiceHash(entrada), entrada);
                    puntuacion += entrada.length();
                    reiniciarTablero();
                    score.setText("Puntuacion:  " + puntuacion);
                    bonus.setForeground(Color.GREEN);
                    bonus.setText("+" + aux);
                }
            }
            //false = no esta en el trie la palabra introducida, se le resta puntos.
            else{
                puntuacion -= entrada.length();
                reiniciarTablero();
                score.setText("Puntuacion:  " + puntuacion);
                bonus.setForeground(Color.RED);
                bonus.setText("-" + aux);
            }
        }
        else{
            palabra.setText(palabra.getText() + e.getActionCommand());
            desactivarBotones();
            buscarAdyacente((JButton)source);
        }
    }
}
