package simpledb;

import java.util.*;

/**
 * Implements a DbIterator by wrapping an Iterable<Tuple>.
 */
public class HeapFileIterator implements DbFileIterator {

    Iterator<Tuple> i = null;
    HeapFile f = null;
    TransactionId tid = null;
    ArrayList<Tuple> tuples = null;
    int pgNo = 0;

    /**
     * Constructs an iterator from the specified Iterable, and the specified
     * descriptor.
     * 
     * @param tuples
     *            The set of tuples to iterate over
     */
    public HeapFileIterator(HeapFile f, TransactionId tid) {
        this.f = f;
        this.tid = tid;
        tuples = new ArrayList<Tuple>();
    }

    public void open() 
        throws DbException, TransactionAbortedException {
        pgNo = 0;
        i = pageIterator();
    }

    public boolean hasNext() 
        throws DbException, TransactionAbortedException {
        if (i != null && (i.hasNext() || pgNo < f.numPages() - 1))
            return true;
        return false;
    }

    public Tuple next() 
        throws DbException, TransactionAbortedException, NoSuchElementException {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }
        
        if (i.hasNext()) {
            return i.next();
        } else {
            pgNo++;
            i = pageIterator();
            return i.next();
        }
    }

    public void rewind() 
        throws DbException, TransactionAbortedException{
        close();
        open();
    }

    public void close() {
        i = null;
    }
    
    private Iterator<Tuple> pageIterator() 
        throws DbException, TransactionAbortedException {
        tuples.clear();
        HeapPageId pid = new HeapPageId(f.getId(), pgNo);
        HeapPage p = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
        Iterator<Tuple> iter = p.iterator();
        while (iter.hasNext()) {
            tuples.add(iter.next());
        }
        return tuples.iterator();
    }
}
