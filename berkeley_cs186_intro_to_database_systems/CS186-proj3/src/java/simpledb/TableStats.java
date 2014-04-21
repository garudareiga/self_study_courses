package simpledb;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TableStats represents statistics (e.g., histograms) about base tables in a
 * query. 
 * 
 * This class is not needed in implementing proj1 and proj2.
 */
public class TableStats {

    private static final ConcurrentHashMap<String, TableStats> statsMap = new ConcurrentHashMap<String, TableStats>();

    static final int IOCOSTPERPAGE = 1000;

    public static TableStats getTableStats(String tablename) {
        return statsMap.get(tablename);
    }

    public static void setTableStats(String tablename, TableStats stats) {
        statsMap.put(tablename, stats);
    }
    
    public static void setStatsMap(HashMap<String,TableStats> s)
    {
        try {
            java.lang.reflect.Field statsMapF = TableStats.class.getDeclaredField("statsMap");
            statsMapF.setAccessible(true);
            statsMapF.set(null, s);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, TableStats> getStatsMap() {
        return statsMap;
    }

    public static void computeStatistics() {
        Iterator<Integer> tableIt = Database.getCatalog().tableIdIterator();

        System.out.println("Computing table stats.");
        while (tableIt.hasNext()) {
            int tableid = tableIt.next();
            TableStats s = new TableStats(tableid, IOCOSTPERPAGE);
            setTableStats(Database.getCatalog().getTableName(tableid), s);
        }
        System.out.println("Done.");
    }

    /**
     * Number of bins for the histogram. Feel free to increase this value over
     * 100, though our tests assume that you have at least 100 bins in your
     * histograms.
     */
    static final int NUM_HIST_BINS = 100;

    // Add by Ray
    int tableid;
    int ioCostPerPage;
    TupleDesc td = null;
    HeapFile f = null;
    ArrayList<Object> histograms = new ArrayList<>();
    int numTuples = 0;
    
    /**
     * Create a new TableStats object, that keeps track of statistics on each
     * column of a table
     * 
     * @param tableid
     *            The table over which to compute statistics
     * @param ioCostPerPage
     *            The cost per page of IO. This doesn't differentiate between
     *            sequential-scan IO and disk seeks.
     */
    public TableStats(int tableid, int ioCostPerPage) {
        // For this function, you'll have to get the
        // DbFile for the table in question,
        // then scan through its tuples and calculate
        // the values that you need.
        // You should try to do this reasonably efficiently, but you don't
        // necessarily have to (for example) do everything
        // in a single scan of the table.
        // some code goes here
        this.tableid = tableid;
        this.ioCostPerPage = ioCostPerPage;
        this.td = Database.getCatalog().getTupleDesc(tableid);

        // Scan through its tuples and calculate
        TransactionId tid = new TransactionId();
        f = (HeapFile)Database.getCatalog().getDbFile(tableid);
        
        IntField[] minValues = new IntField[td.numFields()];
        IntField[] maxValues = new IntField[td.numFields()];
        ArrayList<ArrayList<Field>> fields = new ArrayList<ArrayList<Field>>();
        
        DbFileIterator it = f.iterator(tid);
        try {
            it.open();
            while (it.hasNext()) {
                Tuple t = it.next();
                numTuples++;
                
                ArrayList<Field> fdList = new ArrayList<Field>();
                fields.add(fdList);
                
                for (int i = 0; i < td.numFields(); i++) {
                    Field fd = t.getField(i);
                    fdList.add(fd);
                    if (fd.getType() == Type.INT_TYPE) {
                        if (minValues[i] == null) {
                            minValues[i] = (IntField)fd;
                            maxValues[i] = (IntField)fd;
                        } else {
                            if (fd.compare(Predicate.Op.LESS_THAN, minValues[i])) {
                                minValues[i] = (IntField)fd;
                            }
                            if (fd.compare(Predicate.Op.GREATER_THAN, maxValues[i])) {
                                maxValues[i] = (IntField)fd;
                            }
                        }
                    }
                }
            }
            it.close();
        } catch (DbException | TransactionAbortedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        for (int i = 0; i < td.numFields(); i++) {
            if (td.getFieldType(i) == Type.INT_TYPE)
                histograms.add(new IntHistogram(NUM_HIST_BINS, ((IntField)minValues[i]).getValue(), 
                        ((IntField)maxValues[i]).getValue()));
            else
                histograms.add(new StringHistogram(NUM_HIST_BINS));
        }
        for (ArrayList<Field> fdList : fields) {
            for (int i = 0; i < td.numFields(); i++) {
                Field fd = fdList.get(i);
                if (fd.getType() == Type.INT_TYPE) {
                    ((IntHistogram)histograms.get(i)).addValue(((IntField)fd).getValue());
                } else {
                    ((StringHistogram)histograms.get(i)).addValue(((StringField)fd).getValue());
                }
            }
        }
    }

    /**
     * Estimates the cost of sequentially scanning the file, given that the cost
     * to read a page is costPerPageIO. You can assume that there are no seeks
     * and that no pages are in the buffer pool.
     * 
     * Also, assume that your hard drive can only read entire pages at once, so
     * if the last page of the table only has one tuple on it, it's just as
     * expensive to read as a full page. (Most real hard drives can't
     * efficiently address regions smaller than a page at a time.)
     * 
     * @return The estimated cost of scanning the table.
     */
    public double estimateScanCost() {
        // some code goes here
        //return 0;
        return (double)(f.numPages()*ioCostPerPage);
    }

    /**
     * This method returns the number of tuples in the relation, given that a
     * predicate with selectivity selectivityFactor is applied.
     * 
     * @param selectivityFactor
     *            The selectivity of any predicates over the table
     * @return The estimated cardinality of the scan with the specified
     *         selectivityFactor
     */
    public int estimateTableCardinality(double selectivityFactor) {
        // some code goes here
        //return 0;
        return (int)(numTuples*selectivityFactor);
    }

    /**
     * The average selectivity of the field under op.
     * @param field
     *        the index of the field
     * @param op
     *        the operator in the predicate
     * The semantic of the method is that, given the table, and then given a
     * tuple, of which we do not know the value of the field, return the
     * expected selectivity. You may estimate this value from the histograms.
     * */
    public double avgSelectivity(int field, Predicate.Op op) {
        // some code goes here
        //return 1.0;
        if (td.getFieldType(field) == Type.INT_TYPE) {
            return ((IntHistogram)histograms.get(field)).avgSelectivity();
        } else {
            return ((StringHistogram)histograms.get(field)).avgSelectivity();
        }
    }

    /**
     * Estimate the selectivity of predicate <tt>field op constant</tt> on the
     * table.
     * 
     * @param field
     *            The field over which the predicate ranges
     * @param op
     *            The logical operation in the predicate
     * @param constant
     *            The value against which the field is compared
     * @return The estimated selectivity (fraction of tuples that satisfy) the
     *         predicate
     */
    public double estimateSelectivity(int field, Predicate.Op op, Field constant) {
        // some code goes here
        //return 1.0;
        if (td.getFieldType(field) == Type.INT_TYPE) {
            return ((IntHistogram)histograms.get(field)).estimateSelectivity(op, ((IntField)constant).getValue());
        } else {
            return ((StringHistogram)histograms.get(field)).estimateSelectivity(op, ((StringField)constant).getValue());
        }
    }

    /**
     * return the total number of tuples in this table
     * */
    public int totalTuples() {
        // some code goes here
        //return 0;
        return numTuples;
    }

}
