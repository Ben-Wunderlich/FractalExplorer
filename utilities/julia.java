package utilities;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class julia{
    WritableImage image;
    int width;
    double cVal;
    double expansion;
    double darkness;
    double xMin;
    double xMax;
    double yMin;
    double yMax;
    formula xEq;
    formula yEq;


    public static int colour=0;



    public julia(int width, double c, double expand,double dark,
    double xMin, double xMax, double yMin, double yMax, String xEq, String yEq){
        this.width = width;
        this.cVal = c;
        this.expansion = expand;
        this.darkness = dark;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.xEq = new formula(xEq);
        this.yEq = new formula(yEq);
        image  = new WritableImage(width, width);
        makeFractal();
        //System.out.println(rangeScale(20, 0, 255, 5, 15));
    }

    public WritableImage getImage(){
        return image;
    }

    private void makeFractal(){
        PixelWriter writer = image.getPixelWriter();
        //int[] arr = new arr[]
        double currX;
        double currY;

        for(int i = 0; i<width; i++){
            currX = rangeScale((double)i, xMin, xMax, 0, (double)width);
            for(int j = 0; j< width; j++){
                currY = rangeScale((double)j, yMin, yMax, 0, (double)width);
                int[] pix = getPixel(currX,currY);
                writer.setArgb(i, j, makeARGB(pix[0],pix[1],pix[2]));
            }
        }

    }

    private int makeARGB(int r, int g, int b){
        return 255 << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    private int[] getPixel(double x, double y){
        double xTemp;

        double xEqTemp;
        double yEqTemp;

        int i = 0;
        while(keepIterating(i, x, y)){
            xTemp = squareNum(x) - squareNum(y);
            y = (2*x*y)+cVal;
            x = xTemp + cVal;

            //user defined expression here
            xEqTemp = x;yEqTemp =y;
            x = xEq.doEquation(xEqTemp, yEqTemp);
            y = yEq.doEquation(xEqTemp, yEqTemp);

            x *= expansion;
            y *= expansion;
            i++;
        }
        i = (int)rangeScale((double)i, 0, 255, 0, darkness);
        return colourify(i);
    }

    private int[] colourify(int i){
        switch(colour){
            case 0:
                return new int[]{0,i,i};
            case 1:
                return new int[]{i,i,i};
            case 2:
                return new int[]{255-i,255-i,255-i};
            case 3:
                return new int[]{i,0,i};
            case 4:
                return new int[]{0,255-i,0};
            case 5:
                return new int[]{i,i,0};       
            default:
                return new int[]{0,i,i};
        }
    }

    private boolean keepIterating(int i, double x, double y){
        if((i < darkness) && ((squareNum(x) + squareNum(y)) < 4)){
            return true;
        }
        return false;
    }

    private double squareNum(double num){
        return num*num;
    }

    private double rangeScale(double val, double Tmin, double Tmax, double Rmin, double Rmax){
        double temp = (val-Rmin)/(Rmax-Rmin);
        return (double)(temp*(Tmax-Tmin) + Tmin);
    }

}