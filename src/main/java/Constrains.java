import javax.swing.*;
import java.awt.*;
public abstract class Constrains {
    protected static void addCompX(JComponent component,Container container, int x, int y, int width, int height, double weightx, int top, int left, int bottom, int right, int place, int stretch){
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.insets=new Insets(top,left,bottom,right);
        grid.anchor=place;
        grid.fill=stretch;
        container.add(component,grid);
    }
    protected static void addCompY(JComponent component,Container container,int x,int y,int width,int height,double weighty,int top,int left,int bottom,int right,int place,int stretch){
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weighty=weighty;
        grid.insets=new Insets(top,left,bottom,right);
        grid.anchor=place;
        grid.fill=stretch;
        container.add(component,grid);
    }
    protected static void addComp(JComponent component,Container container,int x,int y,int width,int height,double weightx,double weighty,int top,int left,int bottom,int right,int place,int stretch){
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.weighty=weighty;
        grid.insets=new Insets(top,left,bottom,right);
        grid.anchor=place;
        grid.fill=stretch;
        container.add(component,grid);
    }
    protected static void addCompIx(JComponent component,Container container,int x,int y,int width,int height,double weightx,double weighty,int top,int left,int bottom,int right,int ipadx,int place,int stretch) {
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.weighty=weighty;
        grid.insets=new Insets(top,left,bottom,right);
        grid.ipadx=ipadx;
        grid.anchor=place;
        grid.fill=stretch;
        container.add(component,grid);
    }
    protected static void addCompIy(JComponent component,Container container,int x,int y,int width,int height,double weightx,double weighty,int top,int left,int bottom,int right,int ipady,int place,int stretch) {
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.weighty=weighty;
        grid.insets=new Insets(top,left,bottom,right);
        grid.ipady=ipady;
        grid.anchor=place;
        grid.fill=stretch;
        container.add(component,grid);
    }
    protected static void addCompXIx(JComponent component,Container container,int x,int y,int width,int height,double weightx,int top,int left,int bottom,int right,int ipadx,int place,int stretch) {
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.insets=new Insets(top,left,bottom,right);
        grid.ipadx=ipadx;
        grid.anchor=place;
        grid.fill=stretch;
        container.add(component,grid);
    }
    protected static void addCompXIy(JComponent component,Container container,int x,int y,int width,int height,double weightx,int top,int left,int bottom,int right,int ipady,int place,int street){
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.insets=new Insets(top,left,bottom,right);
        grid.ipady=ipady;
        grid.anchor=place;
        grid.fill=street;
        container.add(component,grid);
    }
    protected static void addCompI(JComponent component,Container container,int x,int y, int width,int height,double weightx,double weighty,int top,int left,int bottom,int right,int ipadx,int ipady,int place,int street){
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.weighty=weighty;
        grid.insets=new Insets(top,left,bottom,right);
        grid.ipadx=ipadx;
        grid.ipady=ipady;
        grid.anchor=place;
        grid.fill=street;
        container.add(component,grid);
    }
    protected static void addComp(Component component,Container container,int x,int y,int width,int height,double weightx,double weighty,int top,int left,int bottom,int right,int place,int stretch){
        GridBagConstraints grid=new GridBagConstraints();
        grid.gridx=x;
        grid.gridy=y;
        grid.gridwidth=width;
        grid.gridheight=height;
        grid.weightx=weightx;
        grid.weighty=weighty;
        grid.insets=new Insets(top,left,bottom,right);
        grid.anchor=place;
        grid.fill=stretch;
        container.add(component,grid);
    }
}