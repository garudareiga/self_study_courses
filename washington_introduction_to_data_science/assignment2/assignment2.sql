-- 1a
SELECT count(*) 
FROM (
    SELECT * FROM Frequency WHERE docid = '10398_txt_earn'
) x;

-- 1b
SELECT count(*) 
FROM (
	SELECT term
	FROM (SELECT * FROM Frequency WHERE docid = '10398_txt_earn' and count = 1)
) x;

-- 1c
SELECT count(*) 
FROM (
    SELECT term FROM (SELECT * FROM Frequency WHERE docid = '10398_txt_earn' and count = 1)
    UNION
    SELECT term FROM (SELECT * FROM Frequency WHERE docid = '925_txt_trade' and count = 1)
) x;

-- 1d
SELECT count(*)
FROM (
    SELECT * FROM Frequency WHERE term = 'parliament'
) x;

-- 1e
SELECT count(*)
FROM (
    SELECT docid FROM Frequency GROUP BY docid HAVING sum(count) > 300
) x;

-- 1f
SELECT count(*)
FROM (
	SELECT docid FROM Frequency WHERE term = 'transactions'
	INTERSECT
	SELECT docid FROM Frequency WHERE term = 'world'
) x;

-- g 
SELECT A.row_num, B.col_num, sum(A.value*B.value)
FROM A, B
WHERE A.col_num = B.row_num
GROUP BY A.row_num, B.col_num

-- h 
SELECT sim
FROM (
	SELECT F1.docid as d1, F2.docid as d2, sum(F1.count*F2.count) as sim
	FROM Frequency F1, Frequency F2
	WHERE F1.term = F2.term and F1.docid < F2.docid
	GROUP BY F1.docid, F2.docid
)
WHERE d1 = '10080_txt_crude' and d2 = '17035_txt_earn'

-- no need to compute similarity for the whole matrix
SELECT sum(F1.count*F2.count) as sim
FROM Frequency F1, Frequency F2
WHERE F1.term = F2.term and F1.docid = '10080_txt_crude' and F2.docid = '17035_txt_earn'

-- For test
CREATE TABLE Freq1 (
docid varchar(255),
term varchar(255),
count int
);

INSERT INTO Freq1 VALUES ('doc1', 'term1', 10);
INSERT INTO Freq1 VALUES ('doc1', 'term2', 1);
INSERT INTO Freq1 VALUES ('doc2', 'term1', 10);
INSERT INTO Freq1 VALUES ('doc2', 'term2', 5);
INSERT INTO Freq1 VALUES ('doc3', 'term1', 0);
INSERT INTO Freq1 VALUES ('doc3', 'term2', 0);

SELECT F1.docid as d1, F2.docid as d2, sum(F1.count*F2.count) as sim
FROM Freq1 F1, Freq1 F2
WHERE F1.term = F2.term and F1.docid < F2.docid
GROUP BY F1.docid, F2.docid

-- i 
CREATE VIEW keyWordSearch AS
SELECT * FROM frequency
UNION
SELECT 'q' as docid, 'washington' as term, 1 as count 
UNION
SELECT 'q' as docid, 'taxes' as term, 1 as count
UNION 
SELECT 'q' as docid, 'treasury' as term, 1 as count

SELECT max(sim)
FROM (
	SELECT S1.docid as d1, S2.docid as d2, sum(S1.count*S2.count) as sim
	FROM keyWordSearch S1, keyWordSearch S2
	WHERE S1.docid = 'q' and S1.term = S2.term and S1.docid <> S2.docid
	GROUP BY S2.docid
	ORDER BY sim
)
