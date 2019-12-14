package mutabletypes;

public class MutableDouble{

  private double value;

  public MutableDouble(double value) {
    this.value = value;
  }

  public double get() {
    return this.value;
  }

   public void set(double value) {
    this.value = value;
  }
}