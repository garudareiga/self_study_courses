package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import simpledb.Aggregator.Op;
import simpledb.IntegerAggregator.IntegerAggregatorInfo;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    // Add by Ray
    int gbfield;
    Type gbfieldtype;
    int afield;
    Op what;
    HashMap<Field, Integer> gbcount = null;
    int nogbcount;
    TupleDesc td = null;
        
    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        if (what == Aggregator.Op.COUNT) {
            this.gbfield = gbfield;
            this.gbfieldtype = gbfieldtype;
            this.afield = afield;
            this.what = what;
            gbcount = new HashMap<Field, Integer>();
            nogbcount = 0;
            
            Type[] typeArr = null;
            if (gbfield == Aggregator.NO_GROUPING) {
                typeArr = new Type[1];
                typeArr[0] = Type.INT_TYPE;
            } else {
                typeArr = new Type[2];
                typeArr[0] = gbfieldtype;
                typeArr[1] = Type.INT_TYPE;
            }
            td = new TupleDesc(typeArr);           
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
        Field gbf = tup.getField(gbfield);
        
        if (gbfield == Aggregator.NO_GROUPING) {
            nogbcount += 1; 
        } else {
            if (!gbcount.containsKey(gbf)) {
                gbcount.put(gbf, 0);
            }
            gbcount.put(gbf, gbcount.get(gbf) + 1);
        }
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        //throw new UnsupportedOperationException("please implement me for proj2");
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        
        if (gbfield == Aggregator.NO_GROUPING) {
            Tuple t = new Tuple(td);
            t.setField(0, new IntField(nogbcount));
            tuples.add(t);
        } else {
            for (Map.Entry<Field, Integer> entry : gbcount.entrySet()) {
                Tuple t = new Tuple(td);               
                t.setField(0, entry.getKey());
                t.setField(1, new IntField(entry.getValue()));
                tuples.add(t);
            }
        }
        
        return new TupleIterator(td, tuples);
    }

}
