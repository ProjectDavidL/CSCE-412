/////////////////////////////////////////////////////
// Assignment 1: CSCE A412
// Galena Wilson, email: gvwilson@alaska.edu
// David Lee,  email: dclee907@gmail.com
// Purpose: The programs finds the longest common subsequence 
// of two strings. 
// Inputs: Two string variables. 
/////////////////////////////////////////////////////


import java.util.Scanner;
import java.lang.*;
        
public class Assignment_1 {
  
    public static void main(String[] args) {
    Scanner readString=new Scanner(System.in);
    int n=0;                                    // length of chromosomes         
    int M = 2000;                                 // population size
    int G = 2000;                                 // generation size
    String candidate_array[]=new String[M];     // population array
    int fitnessArray[]=new int[M];              // fitness for each candidate
    String matingPool[]=new String[M];          // stores tha mating pool
    String LCS="";                              // records the maximum LCS
    int maxFitness=0;                           // records the maximum fitness
    int maxGen=0;                               // records the gen. where the 
                                                // max fitness is enconutered
    
    System.out.println("Enter string 1:");         
    String string1=readString.nextLine();           
    System.out.println("Enter string 2: ");     //reads the first string  
    String string2=readString.nextLine();       //reads the second string  
 
    if (string1.length()>string2.length()){
        String temp=string1;
        string1=string2;
        string2=temp;
    }
        
    if (string1.length()<=string2.length())     
        n=string1.length();                        
    else 
        n=string2.length(); 
   
    initPopulation(n, candidate_array, M);
    
    for (int j=0; j<M; j++)
        fitnessArray[j]=0;
    
   for(int i=0; i<G; i++){
    fitnessFunction(candidate_array, string1, string2, n, fitnessArray, M);
   // 
    int tempMax=findMaxFitness(fitnessArray, M);
    if (maxFitness<tempMax && i!=0)
    {
     maxFitness=tempMax;
     LCS=findLCS(fitnessArray, candidate_array, M, string1);
     maxGen=i;
    }
    rouletteWheel(fitnessArray, matingPool, candidate_array, M); 
    createGeneration(candidate_array, matingPool, M, n, fitnessArray);
    
   }  
    System.out.println("LCS found in generation " + maxGen + ": " + LCS);
 }
/////////////////////////////////////////////////////
// Name: initPopulation
// Purpose: Creates the first population. 
// Parameters: An int, representing the maximum length 
// of the parents' chormosomes, an empty string array 
// for the parents' population and an int 
// variable, representing the population size    
///////////////////////////////////////////////////// 
 
 static void initPopulation(int n, String candidate_array[], int M)    
 {                                                                     
  String candidate=""; 
  for (int i=0;i<M; i++)
  {
   candidate_array[i]="";
  }
   for (int i=0; i<M; i++)
   {
    for (int j=0; j<n; j++)
    {
    if (Math.random()<=0.50)
    candidate=candidate+'1';
    else 
    candidate=candidate+'0';
    }  
    candidate_array[i]=candidate;
    candidate="";
    }   
 } 
 
 /////////////////////////////////////////////////////
 // Name: fitnessFunction
 // Purpose: Evaluates the parents' fintesses.
 // Parameters: An array representing the parents' population, 
 // two strings, an int representing the maximum length of the
 // candidate solution, an int array, representing each 
 // candidate's fitness and an integer for the population size. 
 /////////////////////////////////////////////////////
 
 static void  fitnessFunction(String array[], String shorterStr, 
         String longerStr, int n, int fitness[], int M)
{
  String substring="";
  String tempLonger=longerStr;
  for (int i=0; i<M; i++)
  {
   if (!isEmpty(array[i]))
   { 
    substring = turnToString(n, array[i], shorterStr);
    if (longerStr.contains(substring) && substring.length()==n)
    {
    fitness[i]=n*5000;
    break;
    }
          
    for (int j=substring.length()-1; j>=0; j--){ 
     char character=substring.charAt(j); 
     //punish if the substring does not appear in the long string:
     if (longerStr.indexOf(character)==-1){
     fitness[i]=fitness[i]-5000;
     }
     for (int k=longerStr.length()-1; k>=0; k--){
     //get the next existing character and see if its place is
     //behind the current character 
      if (character==longerStr.charAt(k)) {
      fitness[i]=fitness[i]+2000;
      longerStr=longerStr.substring(0,k);
      break;    
      }
     }
    }     //punish if the string is empty:
   // System.out.println("Substring: "+substring+" " +fitness[i]);
    substring="";
    longerStr=tempLonger;
    }
     if (isEmpty(array[i]))
      fitness[i]=-(n*5000);   
    }
    normalizeFitness(fitness, M, n);
    }

 /////////////////////////////////////////////////////
 // Name: turnToString
 // Purpose: Turns the binary sequence into a letter sequence.
 // Parameters: Ant integer variable representing the length of the 
 // string a string, representing the binary encoding of the 
 // candidate solution and a string, representing the shorter of the 
 // two input words.
 /////////////////////////////////////////////////////
 static String turnToString(int n, String candidate, String shorterStr)
 {
   String substring="";
   int count=0;
   for(int j=0; j<n; j++) //create substring from the binary representation
    {
     if (candidate.charAt(j)=='1'){
     substring=substring+shorterStr.charAt(j); 
     count++;
     } 
   }
   if (count>0)
       return substring;
   else 
       return "";
 }
 
/////////////////////////////////////////////////////
// Name: rouletteWheel
// Purpose: Fitness selection function
// Parameters: Array of integer with fitness values, 
// the mating pool array, an array with the parents' chormosomes,
// and an integer representing the population size.
///////////////////////////////////////////////////// 

static void rouletteWheel(int fitness[], String matingPool[], String candidateArray[], int M)
{
 double sumFit=0;
 double fitProbab[]=new double[M];
 for (int i=0; i<M; i++)
 {
  sumFit=sumFit+fitness[i];      
 }                                                  
  for (int j=0; j<M; j++)
 {
  fitProbab[j]=fitness[j]/sumFit;   
 }    
 for (int i=0; i<M; i++)
 {
  double probability=Math.random();    
  for (int j=0; j<M; j++)
  {
  double CDF=calculateCDF(j, fitProbab, M);             

  if (probability<CDF)
  {
   matingPool[i]=candidateArray[j];
   break;
  }
  }     
 }
}
/////////////////////////////////////////////////////
// Name: findLCS
// Purpose: Translates the genotype of the LCs
// into a phenotype
// Paramethers: an int Array with the population fitness, 
// a string array with the candidate solutions, an integer
// variable representing the population size, and 
// a string variable that represents the shorter of the two strings
/////////////////////////////////////////////////////
//finds and prints out the substring with the maximum fitness

static String findLCS(int fitArray[], String candArray [], int popSize, 
        String shorterStr)
{
   String temp=""; 
   int tempMax=findMaxFitness(fitArray, popSize);
   for (int i=0; i<popSize; i++)
   {
    if (fitArray[i]==tempMax){
    for(int j=0; j<shorterStr.length(); j++) 
    {
    if (candArray[i].charAt(j)=='1')
    temp=temp+shorterStr.charAt(j);  
    } 
    break;
    }
   }
   return temp;
}

/////////////////////////////////////////////////////
// Name: findMaxFitness
// Purpose: Finds the maximum fitness in the population 
// Parameters: An array of integers with the population
// fintesses, and an integer variable representing the population
// size
/////////////////////////////////////////////////////

static int findMaxFitness(int fitArray[],int popSize)
{  
   int maxFit=fitArray[0];
   for (int i=0; i<popSize; i++)
   {
    if (maxFit<fitArray[i])
    maxFit=fitArray[i];
   } 
 return maxFit;
}
/////////////////////////////////////////////////////
// Name: calculateCDF
// Purpose: Calculates the Cumulative Distribution Function
// of the candidates' fitness probabilities.
// Parameters: An integere variable representing the 
// index of the population 
// 
/////////////////////////////////////////////////////
 
static double calculateCDF(int end, double fitProbab[], int popSize)
{
 double cdf=0;
 for (int i=0; i<=end; i++)
 { 
  cdf=fitProbab[i]+cdf;
 }
 return cdf;
}

/////////////////////////////////////////////////////
// Name: crossOver 
// Purpose: Performs crossover on two stirngs. 
// Parameters: An int variable for the 
/////////////////////////////////////////////////////

static void crossOver(int index, int strLength, String matingArray[], 
        String candArray[])    
{
  String strOne, strTwo;
  int crosspoint=0;
  
  crosspoint=(int)(Math.random()*(strLength-1)+1);
 
  candArray[index]=matingArray[index].substring(0, crosspoint)+
          matingArray[index+1].substring(crosspoint);
  
  candArray[index+1]=matingArray[index+1].substring(0, crosspoint)+
            matingArray[index].substring(crosspoint);
  
}
/////////////////////////////////////////////////////
// Name: mutate
// Purpose: Mutates a string with a probability 
// of 1/length of the string
// Paramethers: A string variable representing the candidate solution
/////////////////////////////////////////////////////

static void mutate(String candidate)
{
 StringBuilder sb = new StringBuilder(candidate);
 double mut_probab=1.0/candidate.length(); 
 for (int i=0; i<candidate.length(); i++)
 {
  if (Math.random()<mut_probab)
  {
   if (candidate.charAt(i)=='1'){
   sb.setCharAt(i,'0');
   candidate=sb.toString();
   }
  else 
  {
   sb.setCharAt(i, '1');
   candidate=sb.toString();
  }
  }
 }
}
/////////////////////////////////////////////////////
// Name: createGeneration
// Purpose: Creates a new generation using crossover and mutation
// Paramethers: An array of strings representing the mating pool,
// an array of ints for the new candidate solutions, 
// an int representing the size of the population, 
// an integer for the length of the candidate strins,
// an array of integers representing the fitness of the 
// candidates. 
/////////////////////////////////////////////////////

static void createGeneration(String candidate_array[], String matingPool[], 
int populSize, int n, int fitnessArray[]){
int i=0;
while(i < populSize){
double p_Xover=Math.random();    
if (p_Xover<0.95) 
{
 crossOver(i, n, matingPool, candidate_array);
 mutate(candidate_array[i]);
 mutate(candidate_array[i+1]);
}
else 
{
 candidate_array[i]=matingPool[i];
 candidate_array[i+1]=matingPool[i+1];
 mutate(candidate_array[i]);
 mutate(candidate_array[i+1]);
}
i=i+2;
}
for (int j=0; j<populSize; j++)
fitnessArray[j]=0;
    
}

/////////////////////////////////////////////////////
// Name: normalizeFitness
// Purpose: Adds the negated smallest possible value
// to the fitness array to avoid negative CDFs.
// Parameters: An array of ints representing
// the popul. fintess, and int for the population size, 
// and an int for the length of the candidate solutions.
/////////////////////////////////////////////////////

static void normalizeFitness(int fitness[], int M, int n)
{    
 for (int k=0; k<M; k++)                            
 {
  fitness[k]=fitness[k]+n*5000;  
 }
}

/////////////////////////////////////////////////////
// Name: isEmpty
// Purpose: Checks if a givven array is empty 
// Paramethers: A string array.
/////////////////////////////////////////////////////

static boolean isEmpty(String candString)
{
 for (int i=0; i<candString.length(); i++)
 {
  if (candString.charAt(i)=='1'){
  return false;
  }
  }
  return true;
}

/////////////////////////////////////////////////////
// Name: nextChar
// Purpose: Find the next character in a string.
// Parameters: Starting point 
/////////////////////////////////////////////////////

static char nextChar(int j, String substring, String longerStr)
{
 char curChar=' ';
 for (int i=j; i<substring.length(); i++)
 {
  curChar=substring.charAt(i);
  if (longerStr.indexOf(curChar)>-1)
  break;
  }
  return curChar;
}

}




  