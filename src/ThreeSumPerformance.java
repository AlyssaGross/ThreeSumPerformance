import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;

public class ThreeSumPerformance {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 5;
    static int MAXINPUTSIZE = (int) Math.pow(2,14);
    static int MININPUTSIZE = 1;

    //set up variable to hold folder path and FileWriter/PrintWriter for printing results to a file
    static String ResultsFolderPath = "/home/alyssa/Results/"; // pathname to results folder 
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;


    public static void main (String[] args)
    {
        verifyThreeSum();                                                   //verify that ThreeSum is working correctly

        // run the whole experiment three times, and expect to throw away the data from the earlier runs, before java has fully optimized 
        System.out.println("Running first full experiment...");
        runFullExperiment("ThreeSum-ExpRun1-ThrowAway.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("ThreeSum-ExpRun2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("ThreeSum-ExpRun3.txt");

    }

    // ThreeSum finds triples that sum to zero and returns the number of triples found
    // The algorithm takes a brute force approach, meaning that every possible triple is created
    // and then the sum is checked to see if it is 0.
    public static int threeSum(long [] testList) {
        int len = testList.length;
        int count = 0;
        for(int i = 0; i<len-2; i++)                                        // control index of first element in triple
            for(int j = i +1; j< len; j++)                                  // control index of second element in triple
                for(int k = j+1; k < len; k++) {                            // control index of third element in triple
                    if ((testList[i] + testList[j] + testList[k]) == 0)     // check if sum is zero
                        count++;                                            // incrment count if true
                }
        return count;
    }

    //create a random list of integers of a specific length
    static long[] createRandomIntegerList(int size)
    {
        long [] newList = new long[size];
        for(int j=0; j<size; j++)
        {
            newList[j] = (long)(MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }

    // verifies the ThreeSum function works as is expected
    // creates three lists with known amount of triples that sum to zero, calls the threeSum function and if the
    // result matches the number of known triples, the function is properly working.
    static void verifyThreeSum()
    {
        int count;
        System.out.println("Verification for ThreeSum");
        //contains 5 triples that sum to 0
        long [] verifyList1 = {-33, 4333, 335, -540, 8274, 483, -2300, -450, 9843, -2668, 6000, 736, 1120, -9010, 2333, -5460};
        System.out.println("Verification 1 :");
        System.out.println("List : " + Arrays.toString(verifyList1));
        count = threeSum(verifyList1);
        System.out.println("  ThreeSum count:   " + count + "\n");
        //contains 3 triples that sum to 0
        long [] verifyList2 = {786, 122, -934, 39048, 2304, 324, 23422,  1022,-558, 9830, 929, 901, 394, -24351, 33, 234};
        System.out.println("Verification 2 :");
        System.out.println("List : " + Arrays.toString(verifyList2));
        count = threeSum(verifyList2);
        System.out.println("  ThreeSum count:   " + count + "\n");
        //contains 7 triples that some to 0
        long [] verifyList3 = {-39, 10, -405, 27, 540, 2111, 378, -598, -1706, 12, 9202, -3114, 3102, -12304, 1003, 2935};
        System.out.println("Verification 3 :");
        System.out.println("List : " + Arrays.toString(verifyList3));
        count = threeSum(verifyList3);
        System.out.println("  ThreeSum count:   " + count + "\n");
    }

    //runs the threeSum function for every input size for the specified number of trials
    //times the amount of time each trial took, and calculates the average for the input size
    //prints the input size along with the average time taken to run threeSum
    static void runFullExperiment(String resultsFileName){

        int count;
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return;
        }


        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch();                                   // create stopwatch for timing an individual trial 

        resultsWriter.println("#InputSize    AverageTime");                                             // # marks a comment in gnuplot data 
        resultsWriter.flush();

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*= 2) {                        // for each size of input we want to test: in this case starting small and doubling the size each time

            System.out.println("Running test for input size "+inputSize+" ... ");                       // progress message... 
            System.out.print("    Running trial batch...");
            long batchElapsedTime = 0;                                                                  // reset elapsed time for the batch to 0

            System.gc();                                                                                //force garbage collection before each batch of trials run so it is not included in the time

            // repeat for desired number of trials (for a specific size of input)...
            for (long trial = 0; trial < numberOfTrials; trial++) {                                     // run the trials 

                long[] testList = createRandomIntegerList(inputSize);                                   // generate a list to use for input in the threeSum function 

                TrialStopwatch.start();                                                                 // begin timing
                count = threeSum(testList);                                                             // run the threeSumFaster on the trial input
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();                     // stop timer and add to the total time elapsed for the batch of trials
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials;     // calculate the average time per trial in this batch 

            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch);              // print data for this size of input
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }
}
