package simpledb;

import java.io.DataOutputStream;
import java.io.IOException;

import simpledb.Predicate.Op;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {
    // Add by Ray
    TransactionId t = null;
    DbIterator child = null;
    int tableid;
    TupleDesc td = null;
    boolean called = false;

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        // some code goes here
        this.t = t;
        this.child = child;
        this.tableid = tableid;
        
        TupleDesc expected = Database.getCatalog().getTupleDesc(tableid);
        if (!child.getTupleDesc().equals(expected)) {
            throw new DbException("TupleDesc of child differs from table to insert");
        }
        
        Type[] typeAr = new Type[1];
        typeAr[0] = Type.INT_TYPE;
        td = new TupleDesc(typeAr);
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        //return null;
        return td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        super.open();
        child.open();
    }

    public void close() {
        // some code goes here
        super.close();
        child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        child.rewind();
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        //return null;
        if (called == true) {
            return null;
        }
        
        called = false;
        
        int numInsert = 0;
        while (child.hasNext()) {
            Tuple tup = child.next();
            try {
                Database.getBufferPool().insertTuple(t, tableid, tup);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            numInsert++;
        }
        Tuple numInsertTup = new Tuple(td);
        numInsertTup.setField(0, new IntField(numInsert));
        return numInsertTup;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        //return null;
        return new DbIterator[]{ child };
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        child = children[0];
    }
}
