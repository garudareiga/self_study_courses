package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
    // Add by Ray
    private File f = null;
    private TupleDesc td = null;

    private static final long serialVersionUID = 1L;
       
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.f = f;
        this.td = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        //return null;
        return f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
        return f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
        return td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        //return null;
        
        HeapPage p = null;
        if (pid.getTableId() == getId()) {
            int offset = pid.pageNumber()*BufferPool.PAGE_SIZE;
            //long offset = pid.pageNumber()*BufferPool.PAGE_SIZE;
            byte[] bytes = new byte[BufferPool.PAGE_SIZE];
            
            try {
                RandomAccessFile raf = new RandomAccessFile(f, "r");
                raf.seek(offset);
                raf.read(bytes, 0, BufferPool.PAGE_SIZE);
                raf.close();
                
                HeapPageId hpid = new HeapPageId(getId(), pid.pageNumber());
                p = new HeapPage(hpid, bytes);              
            } catch (IOException e) {
                //throw new DbException("IOException");
                e.printStackTrace();
            }
        }
        return p;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for proj1
        int offset = page.getId().pageNumber()*BufferPool.PAGE_SIZE;
        byte[] bytes = page.getPageData();
        
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        try {
            raf.seek(offset);
            raf.write(bytes, 0, BufferPool.PAGE_SIZE);
        } finally {
            raf.close();
        }
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        //return 0;
        return (int) Math.floor(f.length()/BufferPool.PAGE_SIZE);
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for proj1
        //return null;
        ArrayList<Page> pageList = new ArrayList<Page>();
        BufferPool bp = Database.getBufferPool();
        
        HeapPage p = null;
        int pgNo = 0;
        while (pgNo <= numPages()) {
            HeapPageId pid = new HeapPageId(getId(), pgNo);
            if (pgNo < numPages()) {
                p = (HeapPage) bp.getPage(tid, pid, Permissions.READ_WRITE);
                if (p.getNumEmptySlots() > 0) {
                    p.insertTuple(t);
                    pageList.add(p);
                    break;
                }
            } else {
                // Create a new page
                p = new HeapPage(pid, HeapPage.createEmptyPageData());
                p.insertTuple(t);
                pageList.add(p);
                writePage(p);
                break;
            }
            pgNo++;
        }
        return pageList;
    }

    // see DbFile.java for javadocs
    public Page deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        // not necessary for proj1
        //return null;
        BufferPool bp = Database.getBufferPool();
        RecordId rid = t.getRecordId();
        HeapPage p = (HeapPage) bp.getPage(tid, t.getRecordId().getPageId(), Permissions.READ_WRITE);
        if (p == null) {
            throw new DbException("This tuple is not a member of the file");
        }
        p.deleteTuple(t);
        return p;
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        //return null;
        return new HeapFileIterator(this, tid);
    }

}

