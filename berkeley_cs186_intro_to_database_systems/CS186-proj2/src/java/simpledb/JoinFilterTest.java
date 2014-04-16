package simpledb;

import java.io.*;

public class JoinFilterTest {
    public static void main(String[] argv) {
        
        // construct a 3-column table column
        Type types[] = new Type[]{Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE};
        String names[] = new String[]{"field0", "field1", "field2"};
        TupleDesc descriptor = new TupleDesc(types, names);
        
        // create the table, associate it with some_data_file.dat
        // and tell the catalog about the schema of this table.
        File file1 = new File("data/JoinFilterTest_1.dat");
        HeapFile table1 = new HeapFile(file1, descriptor);
        Database.getCatalog().addTable(table1, "t1");
        
        File file2 = new File("data/JoinFilterTest_2.dat");
        HeapFile table2 = new HeapFile(file2, descriptor);
        Database.getCatalog().addTable(table2, "t2");
        
        // construct the query: we use a simple SeqScan, which spoonfeeds
        // tuples via its iterator.
        TransactionId tid = new TransactionId();
        SeqScan ss1 = new SeqScan(tid, table1.getId(), "t1");
        SeqScan ss2 = new SeqScan(tid, table2.getId(), "t2");
        
        // create a filter for the where condition
        Filter sf1 = new Filter(new Predicate(0, Predicate.Op.GREATER_THAN, new IntField(1)), ss1);
        
        JoinPredicate p = new JoinPredicate(1, Predicate.Op.EQUALS, 1);
        Join j = new Join(p, sf1, ss2);
        
        try {
            j.open();
            while (j.hasNext()) {
                Tuple tup = j.next();
                System.out.println(tup);
            }
            j.close();
            Database.getBufferPool().transactionComplete(tid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
