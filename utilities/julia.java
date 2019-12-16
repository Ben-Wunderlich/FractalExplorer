package utilities;

//import javafx.scene.paint.*;

import java.lang.Math;
//import java.util.Arrays;

//import javafx.scene.image.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class julia{
    WritableImage image;
    int width;
    double c;
    double expansion;
    double darkness;
    double xView;
    double yView;



    public julia(int width, double c, double expand, double dark, double xView, double yView){
        this.width = width;
        this.c = c;
        this.expansion = expand;
        this.darkness = dark;
        this.xView = xView;
        this.yView = yView;
        image  = new WritableImage(width, width);
        makeFractal();
        //printArr();
    }

   /* private void printArr(){
        for(int i=0; i<image.length;i++){
            for(int j=0;j<image[0].length;j++){
                System.out.print(image[i][j][0]);
                System.out.print(image[i][j][1]);
                System.out.print(image[i][j][2]);
            }
        }
    }*/

    public WritableImage getImage(){
        return image;
    }

    private void makeFractal(){
        PixelWriter writer = image.getPixelWriter();
        for(int i = 0; i<width; i++){
            double currX = rangeScaleDouble((double)i, -xView, xView, 0, (double)width);
            for(int j = 0; j< width; j++){
                double currY = rangeScaleDouble((double)j, -yView, yView, 0, (double)width);
                int[] pix = getPixel(currX,currY);
                writer.setArgb(i, j, makeARGB(pix[0],pix[1],pix[2]));
            }
        }
    }

    private int makeARGB(int r, int b, int g){
        int result = (255 & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
        return result;
    }

    private int[] getPixel(double x, double y){
        //use c, darkness, expansion

        double xTemp;

        int i = 0;
        while(keepIterating(i, x, y)){
            xTemp = squareNum(x) - squareNum(y);
            y = (2*x*y)+c;
            x = xTemp + x;

            x *= expansion;
            y *= expansion;
            i++;
        }
        //double valTemp = Math.sin((double)i);
        //i = rangeScale(valTemp, 0, 255, 0, darkness);
        i = rangeScale((double)i, 0, 255, 0, darkness);
        //System.out.println(i);
        return new int[]{0,i,i};
    }

    private boolean keepIterating(int i, double x, double y){
        if((i < darkness) && (squareNum(x) + squareNum(y) < 4)){
            return true;
        }
        return false;
    }

    private double squareNum(double num){
        return num*num;
    }

    private int rangeScale(double val, double Tmin, double Tmax, double Rmin, double Rmax){
        double temp = (val-Rmin)/(Rmax-Rmin);
        return (int)(temp*(Tmax-Tmin) + Tmin);
    }

    private double rangeScaleDouble(double val, double Tmin, double Tmax, double Rmin, double Rmax){
        double temp = (val-Rmin)/(Rmax-Rmin);
        return (double)(temp*(Tmax-Tmin) + Tmin);
    }


}