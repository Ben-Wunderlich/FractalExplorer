package utilities;

//import java.lang.Math;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
//import utilities.formula;

public class julia{
    WritableImage image;
    int width;
    double cVal;
    double expansion;
    double darkness;
    double xView;
    double yView;
    formula xEq;
    formula yEq;



    public julia(int width, double c, double expand,
     double dark, double xView, double yView, String xEq, String yEq){
        this.width = width;
        this.cVal = c;
        this.expansion = expand;
        this.darkness = dark;
        this.xView = xView;
        this.yView = yView;
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
            currX = rangeScale((double)i, -xView, xView, 0, (double)width);
            for(int j = 0; j< width; j++){
                currY = rangeScale((double)j, -yView, yView, 0, (double)width);
                int[] pix = getPixel(currX,currY);
                writer.setArgb(i, j, makeARGB(pix[0],pix[1],pix[2]));
            }
        }

    }

    private int makeARGB(int r, int b, int g){
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

            //do user defined expression here
            xEqTemp = x;yEqTemp =y;
            x = xEq.doEquation(xEqTemp, yEqTemp);
            y = yEq.doEquation(xEqTemp, yEqTemp);

            x *= expansion;
            y *= expansion;
            i++;
        }
        //double valTemp = Math.sin((double)i);
        //i = rangeScale(valTemp, 0, 255, 0, darkness);
        i = (int)rangeScale((double)i, 0, 255, 0, darkness);
        //System.out.println(i);
        return new int[]{0,i,i};
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