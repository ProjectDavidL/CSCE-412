
import java.util.*;
import java.lang.*;


/////////////////////////////////////////////////////
// Assignment 2: CSCE A412
// Derek Crain, email: djcrain@alaska.edu
// David Lee,  email: dclee907@gmail.com
// Purpose: The program maximizes the function f(x1, x2) = 21.5 + x1 * sin(4πx1) + x2 * sin(20πx2)
// Inputs: Two string variables. 
/////////////////////////////////////////////////////

public class Assignment_2 
{
  public static Random r = new Random();
  public static double t = (1/Math.sqrt(2*Math.sqrt(2))); // based off of n, which is the number of chromosomes in the individual
  public static double tprime = (1/Math.sqrt(2*2));
  public static double thresh = 0.10;
  public static int parentsNum = 6;       // Number of Parents
  public static int offspringsNum = 42; // Number ofOffsprings

  public class Individual{
    
    double x1;
    double x2;
    double o1;
    double o2;
    double fitness;
  
    public Individual(){
      x1 = ((r.nextInt(15) - 3) + r.nextInt(101)/100.00);
      x2 = ((r.nextInt(2) + 4) + r.nextInt(101)/100.00); 
      o1 = o2 = 1.0; // altered by mutation
      evaluateFitness();
    }
  
    public Individual(double ex1, double ex2, double sig1, double sig2){
      x1 = ex1;
      x2 = ex2;
      o1 = sig1;
      o2 =  sig2;
      evaluateFitness();
    }
  
    void evaluateFitness()
    {
      fitness = Math.floor((21.5 + (x1*Math.sin(4*Math.PI*x1)) + (x2*Math.sin(20*Math.PI*x2)))*100)/100;
    }
    
    void mutation()
    {
      double[] prevVals = {x1,x2,o1,o2,fitness};
      double indNorm = r.nextGaussian();
      double x1Norm = r.nextGaussian();
      double x2Norm = r.nextGaussian();
      o1 = (o1 * Math.exp((tprime*indNorm) + t*x1Norm));
      o2 = (o2 * Math.exp((tprime*indNorm) + t*x2Norm));
      sigGood();
      x1 = x1 + o1 * x1Norm;
      x2 = x2 + o2 * x2Norm;
      if(!xGood()){
        x1 = prevVals[0];
        x2 = prevVals[1];
        o1 = prevVals[2];
        o2 = prevVals[3];
        fitness = prevVals[4];
        mutation();
      }
      evaluateFitness();
    }
  
    boolean xGood(){
      if(x1 >= -3.0 && x1 <= 12.0){
        if(x2 >= 4.0 && x2 <= 6.0)
        {
          return true;
        }
      }
      return false;} // returns true if x1 and x2 is in boundary values
    
  
    void sigGood(){
      if(o1 < thresh)
      {
        o1 = thresh;
      }
      if(o2 < thresh){
        o2 = thresh;
      }
    }
  }
    
    class Population{
      List<Individual> parents = new ArrayList<>();
      List<Individual> children = new ArrayList<>();
    
      public Population(){
        for(int x=0; x < parentsNum; x++){
          Individual a = new Individual();
          parents.add(a);      
        }
        
      }
    
      void recombination()
      {
        int parent1, parent2, whichOne;
        double x1,x2,o1,o2;
        
        // rcombination logic. 
        { // x1 block
          parent1 = r.nextInt(parentsNum);
          parent2 = r.nextInt(parentsNum);
          whichOne = r.nextInt(2) + 1;
          if(whichOne == 1){
            x1 = parents.get(parent1).x1;
          }
          else{
            x1 = parents.get(parent2).x1;
          }
        }
        { // x2 block
          parent1 = r.nextInt(parentsNum);
          parent2 = r.nextInt(parentsNum);
          whichOne = r.nextInt(2) + 1;
          if(whichOne == 1){
            x2 = parents.get(parent1).x2;
          }
          else{
            x2 = parents.get(parent2).x2;
          }
        }
        { // o1 block
          parent1 = r.nextInt(parentsNum);
          parent2 = r.nextInt(parentsNum);
          o1 = (parents.get(parent1).o1 + parents.get(parent2).o1)/2;
          if(o1 < thresh){
            o1 = thresh;
          }
        }
        { // o2 block
          parent1 = r.nextInt(parentsNum);
          parent2 = r.nextInt(parentsNum);
          o2 = (parents.get(parent1).o2 + parents.get(parent2).o2)/2;
          if(o2 < thresh){
            o2 = thresh;
          }
        }
        Individual a = new Individual(x1,x2,o1,o2);
        a.mutation();
        children.add(a);
      }

      
  List<Individual> ranking(){
    List<Individual> rankings = new ArrayList<>(parentsNum);

    for(int i = 0; i < offspringsNum; i++){
      for(int j = 0; j < parentsNum; j++){
        try{
          if(rankings.get(j) == null || rankings.get(j).fitness <= children.get(i).fitness){
            rankings.add(j, children.get(i));
            break;
          }
        } catch (IndexOutOfBoundsException r){
          rankings.add(j, children.get(i));
          break;
        }
      }
  /*    try{
        if(rankings.get(0) == null || rankings.get(0).fitness <= children.get(i).fitness){
          rankings.add(0, children.get(i));
        }
        else{
          try{
            if(rankings.get(1) == null || rankings.get(1).fitness <= children.get(i).fitness){
              rankings.add(1, children.get(i));
            }
            else{
                try{
                  if(rankings.get(2) == null || rankings.get(2).fitness <= children.get(i).fitness){
                    rankings.add(2, children.get(i));
                  }
                } catch( IndexOutOfBoundsException e){
                  rankings.add(2, children.get(i));
                }
              }
            } catch( IndexOutOfBoundsException f){
              rankings.add(1, children.get(i));
            }
          }
        } catch (IndexOutOfBoundsException r){
          rankings.add(0, children.get(i));
        } */
      }

 //   for(int i = 0; i < 3; i++){
 //     System.out.println("Rank " + i + ": " + rankings.get(i).fitness);
 //   }
    return rankings;
  }
}
  
  public static void main(String[] args)
  {
    int terminationcount = 100;
    int i = 0;
    int count = 0;
    List<Individual> ranks;
    Population p = new Assignment_2().new Population();
    //System.out.println(p.parents.get(2).fitness);
    
    while(count < terminationcount){

      // child creation block
      i = 0;
      while (i < offspringsNum){
        p.recombination();
        i++;
      }

      ranks = p.ranking();
      for(int j = 0; j < parentsNum; j++){
        p.parents.set(j, ranks.get(j));
      }
      ranks.clear();
      p.children.clear();
      count++;
    }
    System.out.println("Best Fitness: " + p.parents.get(0).fitness);
    System.out.println("x1: " + p.parents.get(0).x1 + " x2: " + p.parents.get(0).x2);
    System.out.println("o1: " + p.parents.get(0).o1 + " o2: " + p.parents.get(0).o2);
  }

  
}

