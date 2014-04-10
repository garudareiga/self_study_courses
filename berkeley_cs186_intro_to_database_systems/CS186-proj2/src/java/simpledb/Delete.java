package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {
    // Add by Ray
    TransactionId t = null;
    DbIterator child = null;
    TupleDesc td = null;
    boolean called = false;

    private static final long serialVersionUID = 1L;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
        this.t = t;
        this.child = child;
        
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
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
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
            Database.getBufferPool().deleteTuple(t, tup);
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
