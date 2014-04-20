package simpledb;

import java.util.*;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {
    // Add by Ray
    int buckets;
    int min;
    int max;
    int[] bucketHeights;
    double bucketWidth = 0.0;
    int numTuples = 0;
    
    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
    	// some code goes here
        this.buckets = buckets;
        this.min = min;
        this.max = max;
        bucketHeights = new int[buckets];
        bucketWidth = (double)(max - min + 1)/buckets;
        
        System.out.println(toString());
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	// some code goes here
        int bucketIndex = (int)((v - min)/bucketWidth);
        bucketHeights[bucketIndex] += 1;
        numTuples++;
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {

    	// some code goes here
        //return -1.0;
        double selectivity = 0.0;
        if (op == Predicate.Op.EQUALS || op == Predicate.Op.NOT_EQUALS) {
            int bucketIndex = (int)((v - min)/bucketWidth);
            selectivity = (double)bucketHeights[bucketIndex]/bucketWidth;
            if (op == Predicate.Op.NOT_EQUALS)
                selectivity = numTuples - selectivity;
        }
        else if (op == Predicate.Op.GREATER_THAN || op == Predicate.Op.GREATER_THAN_OR_EQ) {
            if (v < min)
                return 1.0;
            if (v > max)
                return 0.0;
            int bucketIndex = (int)((v - min)/bucketWidth);
            int width = min + (int)(bucketWidth*(bucketIndex + 1)) - v;
            if (op == Predicate.Op.GREATER_THAN)
                width -= bucketWidth;
            if (width > 0.0)
                selectivity = ((double)bucketHeights[bucketIndex])/width;
            
            for (int i = bucketIndex + 1; i < bucketHeights.length; i++) {
                selectivity += bucketHeights[i];
            }
        }
        else if (op == Predicate.Op.LESS_THAN || op == Predicate.Op.LESS_THAN_OR_EQ) {
            if (v < min)
                return 0.0;
            if (v > max)
                return 1.0;
            int bucketIndex = (int)((v - min)/bucketWidth);
            int width = v - (int)((min + bucketWidth*bucketIndex));
            if (op == Predicate.Op.LESS_THAN_OR_EQ)
                width += bucketWidth;
            if (width > 0.0)
                selectivity = ((double)bucketHeights[bucketIndex])/width;
            
            for (int i = 0; i < bucketIndex; i++) {
                selectivity += bucketHeights[i];
             }
        }
        selectivity /= numTuples;
        System.out.println("Selectivity=" + selectivity);
        return selectivity;
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return 1.0;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {

        // some code goes here
        //return null;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Min=%d, Max=%d, Buckets=%d, Width=%f", 
                min, max, buckets, bucketWidth));
        return sb.toString();
    }
}
