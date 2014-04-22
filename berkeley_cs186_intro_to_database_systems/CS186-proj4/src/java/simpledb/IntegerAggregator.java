package simpledb;

import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {
    
    // Add by Ray
    int gbfield;
    Type gbfieldtype;
    int afield;
    Op what;
    HashMap<Field, IntegerAggregatorInfo> gbAggregateInfo = null;
    IntegerAggregatorInfo nogbAggregateInfo = null;
    TupleDesc td = null;
    
    class IntegerAggregatorInfo {
        int sum;
        int count;
        int max;
        int min;
        
        public IntegerAggregatorInfo() {
            sum = 0;
            count = 0;
            max = Integer.MIN_VALUE;
            min = Integer.MAX_VALUE;
        }
    }

    private static final long serialVersionUID = 1L;

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.what = what;
        gbAggregateInfo = new HashMap<Field, IntegerAggregatorInfo>();
        nogbAggregateInfo = new IntegerAggregatorInfo();
        
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
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
        Field gbf = tup.getField(gbfield);
        IntField af = (IntField) tup.getField(afield);
        
        IntegerAggregatorInfo aggregateInfo = null;
        
        if (gbfield == Aggregator.NO_GROUPING) {
            aggregateInfo = nogbAggregateInfo;
        } else {
            if (!gbAggregateInfo.containsKey(gbf)) {
                gbAggregateInfo.put(gbf, new IntegerAggregatorInfo());
            }
            aggregateInfo = gbAggregateInfo.get(gbf);            
        }
        
        switch(what) {
            case MAX:
                aggregateInfo.max = Math.max(aggregateInfo.max, af.getValue());
                break;
            case MIN:
                aggregateInfo.min = Math.min(aggregateInfo.min, af.getValue());
                break;
            case SUM:
                aggregateInfo.sum += af.getValue();
                break;
            case COUNT:
                aggregateInfo.count += 1;
                break;
            case AVG:
                aggregateInfo.sum += af.getValue();
                aggregateInfo.count += 1;
                break;
            default:
                break;
        }
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        //throw new
        //UnsupportedOperationException("please implement me for proj2");
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        
        if (gbfield == Aggregator.NO_GROUPING) {
            Tuple t = new Tuple(td);
            int aggregateVal = 0;
            switch(what) {
            case MAX:
                aggregateVal = nogbAggregateInfo.max;
                break;
            case MIN:
                aggregateVal = nogbAggregateInfo.min;
                break;
            case SUM:
                aggregateVal = nogbAggregateInfo.sum;
                break;
            case COUNT:
                aggregateVal = nogbAggregateInfo.count;
                break;
            case AVG:
                aggregateVal = nogbAggregateInfo.sum/nogbAggregateInfo.count;
                break;
            default:
                break;
            }
            t.setField(0, new IntField(aggregateVal));
            tuples.add(t);
        } else {
            for (Map.Entry<Field, IntegerAggregatorInfo> entry : gbAggregateInfo.entrySet()) {
                Tuple t = new Tuple(td);
                
                int aggregateVal = 0;
                IntegerAggregatorInfo aggregateInfo = entry.getValue();
                
                switch(what) {
                case MAX:
                    aggregateVal = aggregateInfo.max;
                    break;
                case MIN:
                    aggregateVal = aggregateInfo.min;
                    break;
                case SUM:
                    aggregateVal = aggregateInfo.sum;
                    break;
                case COUNT:
                    aggregateVal = aggregateInfo.count;
                    break;
                case AVG:
                    aggregateVal = aggregateInfo.sum/aggregateInfo.count;
                    break;
                default:
                    break;
                }
                
                t.setField(0, entry.getKey());
                t.setField(1, new IntField(aggregateVal));
                tuples.add(t);
            }
        }
        
        return new TupleIterator(td, tuples);
    }
}
