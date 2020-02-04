package utilities;

import javax.naming.TimeLimitExceededException;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.lang.Math;

public class julia{
    WritableImage image;
    int width;
    int height;
    double cVal;
    double expansion;
    double darkness;
    double xMin;
    double xMax;
    double yMin;
    double yMax;
    formula xEq;
    formula yEq;
    final long maxTime = 10000;


    public static int colour=0;



    public julia(int width, int height, double c, double expand,double dark,
    double xMin, double xMax, double yMin, double yMax, String xEq, String yEq) throws TimeLimitExceededException{
        this.width = width;
        this.height = height;
        this.cVal = c;
        this.expansion = expand;
        this.darkness = dark;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        
        try{
        String trimmedX = xEq.replaceAll("\\s+","").toLowerCase();
        this.xEq = new formula(trimmedX);
        }
        catch(Exception e){
            throw new NumberFormatException("'"+xEq+"' is not a valid equation");
        }

        try{
            String trimmedY = yEq.replaceAll("\\s+","").toLowerCase();
            this.yEq = new formula(trimmedY);
        }
        catch(Exception e){
            throw new NumberFormatException("'"+yEq+"' is not a valid equation");
        }
        image  = new WritableImage(width, height);
        boolean result = makeFractal();
        if(result){//if was error
            throw new TimeLimitExceededException("took too long");
        }
        //System.out.println(rangeScale(20, 0, 255, 5, 15));
    }

    public WritableImage getImage(){
        return image;
    }

    private boolean makeFractal(){
        PixelWriter writer = image.getPixelWriter();
        //int[] arr = new arr[]
        double currX;
        double currY;

        long startTime = System.currentTimeMillis();
        for(int i = 0; i<width; i++){
            currX = rangeScale((double)i, xMin, xMax, 0, (double)width);
            for(int j = 0; j< height; j++){
                currY = rangeScale((double)j, yMin, yMax, 0, (double)height);
                int[] pix = getPixel(currX,currY);
                writer.setArgb(i, j, makeARGB(pix[0],pix[1],pix[2]));
            }
            if(System.currentTimeMillis() - startTime > maxTime){
                return true;
            }
        }
        return false;
    }

    private int makeARGB(int r, int g, int b){
        return 255 << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    private int[] getPixel(double x, double y){
        double xTemp;

        double xEqTemp;
        double yEqTemp;

        int i = 0;
        try{
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
        }
        catch(Exception e){
            throw new NumberFormatException("a formula you entered was invalid");
        }
        i = (int)rangeScale((double)i, 0, 255, 0, darkness);
        return colourify(i);
    }

    private int[] wackyNewColour(int i, boolean invert){
        double start = Math.sin((double)i);
        int a = (int)rangeScale(start, 0, 255, -1, 1);
        if(invert){
            return new int[]{255-a,255-a,255-a};
        }
        return new int[]{a,a,a};
    }

    private int[] colourify(int i){
        switch(colour){
            case 1:
                return new int[]{0,i,i};
            case 2:
                return new int[]{i,i,i};
            case 3:
                return new int[]{255-i,255-i,255-i};
            case 4:
                return new int[]{i,0,i};
            case 5:
                return new int[]{0,255-i,0};
            case 6:
                return new int[]{i,i,0};
            case 7:
                return wackyNewColour(i, false);
            case 8:
                return wackyNewColour(i, true); 
            default:
                return new int[]{0,i,i};
        }
    }

    private boolean withinBounds(double x, double y){
        //return (squareNum(x) + squareNum(y) < 4);//circle
        return (x < xMax && x > xMin) && (y < yMax && y > yMin);//field of view
    }

    private boolean keepIterating(int i, double x, double y){
        return((i < darkness) && withinBounds(x, y));
    }

    private double squareNum(double num){
        return num*num;
    }

    private double rangeScale(double val, double Tmin, double Tmax, double Rmin, double Rmax){
        double temp = (val-Rmin)/(Rmax-Rmin);
        return (double)(temp*(Tmax-Tmin) + Tmin);
    }

}