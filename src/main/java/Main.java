import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;

public class Main extends JFrame {
    private ArrayDeque<String> ecuacion;
    private int len;
    private boolean inicio;

    public Main() {
        super("Calc");
        len=0;
        inicio=true;
        ecuacion=new ArrayDeque<>();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        init();
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }// 382 code line

    private void init() {
        JTextField field = new JTextField();
        field.setFont(new Font("Ubuntu Mono Regular",Font.BOLD,20));
        JTextField field1 = new JTextField("0");
        field1.setFont(new Font("Ubuntu Mono Regular",Font.BOLD,20));
        field1.setHorizontalAlignment(SwingConstants.RIGHT);
        field1.setEditable(false);
        field1.setBackground(Color.WHITE);
        field.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        field1.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        field.setFocusable(false);
        field1.setFocusable(false);
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.BLACK));
        field1.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));

        JButton cero = button("0", Color.BLACK, e -> concatNumber(field, '0'));
        JButton uno = button("1", Color.BLACK, e -> concatNumber(field, '1'));
        JButton dos = button("2", Color.BLACK, e -> concatNumber(field, '2'));
        JButton tres = button("3", Color.BLACK, e -> concatNumber(field, '3'));
        JButton cuatro = button("4", Color.BLACK, e -> concatNumber(field, '4'));
        JButton cinco = button("5", Color.BLACK, e -> concatNumber(field, '5'));
        JButton seis = button("6", Color.BLACK, e -> concatNumber(field, '6'));
        JButton siete = button("7", Color.BLACK, e -> concatNumber(field, '7'));
        JButton ocho = button("8", Color.BLACK, e -> concatNumber(field, '8'));
        JButton nueve = button("9", Color.BLACK, e -> concatNumber(field, '9'));
        JButton division = button("/", Color.BLACK, e -> concatOperator(field, '/'));
        JButton suma = button("+", Color.BLACK, e -> concatOperator(field, '+'));
        JButton resta = button("-", Color.BLACK, e -> concatOperator(field, '-'));
        JButton multiplicacion = button("*", Color.BLACK, e -> concatOperator(field, '*'));
        JButton punto = button(".", Color.BLACK, e -> dot(field));
        JButton potencia = button("<html>x<SUP>y</SUP></html>", Color.BLACK, e -> concatOperator(field, '^'));
        JButton parenA = button("(", Color.BLACK, e -> parentA(field));
        JButton parenC = button(")", Color.BLACK, e -> concatOperator(field, ')'));
        JButton igual = button("=", Color.BLACK, e -> operate(field, field1));

        Constrains.addCompIy(field,this,0,0,8,1,1,1,25,10,0,10,20,GridBagConstraints.SOUTH,GridBagConstraints.BOTH);
        Constrains.addCompIy(field1,this, 0, 1, 8, 1, 1, 1, 0, 10, 5, 10,20, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
        Constrains.addComp(siete,this,3,5,1,1,1,1,3,10,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(ocho,this,4,5,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(nueve,this,5,5,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(division,this,6,5,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(potencia,this,7,5,1,1,1,1,3,3,3,10,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(cuatro,this,3,6,1,1,1,1,3,10,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(cinco,this,4,6,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(seis,this,5,6,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(multiplicacion,this,6,6,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(parenA,this,7,6,1,1,1,1,3,3,3,10,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(uno,this,3,7,1,1,1,1,3,10,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(dos,this,4,7,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(tres,this,5,7,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(resta,this,6,7,1,1,1,1,3,3,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(igual,this,7,8,1,1,1,1,3,3,15,10,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(cero,this,3,8,2,1,1,1,3,10,15,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(punto,this,5,8,1,1,1,1,3,3,15,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(suma,this,6,8,1,1,1,1,3,3,15,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        Constrains.addComp(parenC,this,7,7,1,1,1,1,3,3,3,10,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
    }

    private void concatNumber(JTextField field, char character) {
        String s = String.valueOf(character);
        if (inicio){
            field.setText(s);
            inicio = false;
        }else field.setText(field.getText() + s);
    }

    private void concatOperator(JTextField field, char operator) {
        String s = String.valueOf(operator);
        if (!inicio && (ecuacion.isEmpty()
                || (Character.isDigit(field.getText().charAt(field.getText().length() - 1))
                || field.getText().endsWith(")")))) {
            if (!field.getText().endsWith(")")) ecuacion.push(field.getText().substring(len));
            ecuacion.push(s);
            field.setText(field.getText() + s);
            len = field.getText().length();
        }
    }

    private void operate(JTextField field, JTextField field1) {
        try{
            if (!inicio){
                if (field.getText().endsWith(")")) operateEquation(field1);
                else if (Character.isDigit(field.getText().charAt(field.getText().length() - 1))) {
                    ecuacion.push(field.getText().substring(len));
                    operateEquation(field1);
                }
            }
        }catch (IllegalArgumentException e){
            field1.setForeground(Color.RED);
            field1.setText(e.getMessage());
            field1.setForeground(Color.BLACK);
            field.setText("");
            resetEquation();
        }
    }

    private void operateEquation(JTextField field1) {
        field1.setText(new Operaciones(ecuacion).operar());
        resetEquation();
    }

    private void resetEquation() {
        ecuacion = new ArrayDeque<>();
        inicio = true;
        len = 0;
    }

    private void parentA(JTextField field) {
        String parenA = "(";
        if (inicio){
            field.setText(parenA);
            ecuacion.push(parenA);
            inicio = false;
            len = field.getText().length();
        }else if (!Character.isDigit(ecuacion.getFirst().charAt(0))) {
            field.setText(field.getText() + parenA);
            ecuacion.push(parenA);
            len=field.getText().length();
        }
    }

    private void dot(JTextField field) {
        if (inicio){
            inicio = false;
            field.setText("0.");
        } else if (!Character.isDigit(field.getText().charAt(field.getText().length() - 1))) {
            field.setText(field.getText() + "0.");
        } else field.setText(field.getText() + ".");
    }

    private JButton button(String name, Color color, ActionListener action){
        JButton button = new JButton(name);
        button.setForeground(color);
        button.setFont(new Font("Ubuntu Mono Regular", Font.BOLD, 13));
        button.setBackground((Character.isDigit(name.charAt(0)) && name.length() == 1) ? new Color(238, 238, 238) : new Color(205, 205, 205));
        button.setBorder(new RoundedBorder(10));
        button.addActionListener(action);
        return button;
    }
}