import java.util.ArrayDeque;
public class Operaciones {
    private ArrayDeque<String> ecuacion;
    public Operaciones(ArrayDeque<String> ecuacion) {
        this.ecuacion = ecuacion;
    }
    private ArrayDeque<String> postfija(){
        ArrayDeque<String> postfija=new ArrayDeque<>();
        ArrayDeque<String> pila=new ArrayDeque<>();
        while (!ecuacion.isEmpty()){
            String n=ecuacion.pollLast();
            if (Character.isDigit(n.charAt(0))) postfija.push(n);
            else if (pila.isEmpty()) pila.push(n);
            else if (n.equals(")")){
                String n1;
                while (!pila.isEmpty() && !(n1=pila.pop()).equals("(")) {
                    postfija.push(n1);
                }
            }else if (!(prioridadExpresion(n.charAt(0))>prioridadPila(pila.getFirst().charAt(0)))){
                postfija.push(pila.pop());
                pila.push(n);
            }else pila.push(n);
        }
        while (!pila.isEmpty()) postfija.push(pila.pop());
        return postfija;
    }
    private int prioridadExpresion(char n){
        if (n=='^') return 4;
        else if (n=='*' || n=='/') return 2;
        else if (n=='+' || n=='-') return 1;
        else return 5;
    }
    private int prioridadPila(char n){
        if (n=='^') return 3;
        else if (n=='*' || n=='/') return 2;
        else if (n=='+' || n=='-') return 1;
        else return 0;
    }
    public String operar() throws IllegalArgumentException{
        ArrayDeque<String> posfija=postfija();
        ArrayDeque<String> pila=new ArrayDeque<>();
        if (postfija().contains("(")) throw new IllegalArgumentException("SYNTAX ERROR");
        while (!posfija.isEmpty()){
            String n=posfija.pollLast();
            if (Character.isDigit(n.charAt(0))) pila.push(n);
            else {
                double o2= Double.parseDouble(pila.pop());
                double o1= Double.parseDouble(pila.pop());
                pila.push(String.valueOf(operar(n.charAt(0),o1,o2)));
            }
        }
        return pila.pop();
    }
    private double operar(char n,double o1,double o2){
        if (n=='+') return o1+o2;
        else if (n=='-') return o1-o2;
        else if (n=='*') return o1*o2;
        else if (n=='/') return o1/o2;
        else return Math.pow(o1,o2);
    }
}