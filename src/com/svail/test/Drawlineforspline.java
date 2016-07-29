package com.svail.test;


import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Drawlineforspline extends JFrame{

    private static final long serialVersionUID = 1L;
    List <PointStruct>plist;

    public Drawlineforspline(){
        init();
    }
    public Drawlineforspline(List<PointStruct> plist){
        init();
        this.plist=plist;
    }

    private void init(){
        this.setTitle("drawline");
        this.setBounds(200, 200, 500, 400);
        this.setBackground(Color.white);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public class Mypanel extends JPanel{
        public void paint(Graphics g){
            g.setColor(Color.red);
            //System.out.println(plist.size());
            for(int i=0;i<plist.size()-1;i++){
                g.drawLine(plist.get(i).ix, plist.get(i).iy, plist.get(i+1).ix, plist.get(i+1).iy);
            }
        }
    }
}