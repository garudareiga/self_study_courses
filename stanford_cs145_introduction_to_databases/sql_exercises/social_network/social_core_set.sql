/* SQLite: http://www.sqlite.org/sqlite.html */
/* PostgreSQL: http://www.postgresql.org/docs/9.0/static/app-psql.html */

/* Read a small data set:
   1) SQLite3
   % sqlite3
   sqlite> .read rating.sql 

   2) PostgreSQL
   % psql
   ray=# \i rating.sql
*/

/* Q1.
    Find the names of all students who are friends with someone named Gabriel. 
 */
select H1.name
from Highschooler H1
where ID in (select ID2 from Friend, Highschooler H2 where ID1 = H2.ID and name = "Gabriel")

/* Q2.
    For every student who likes someone 2 or more grades younger than themselves, 
    return that student's name and grade, and the name and grade of the student they like. 
 */

select H1.name, H1.grade, H2.name, H2.grade
from Highschooler H1, Highschooler H2, Likes
where H1.ID = ID1 and H2.ID = ID2 and H1.grade - H2.grade >= 2

/* Q3.
    For every pair of students who both like each other, return the name and grade of both 
    students. Include each pair only once, with the two names in alphabetical order.
*/
select H1.name, H1.grade, H2.name, H2.grade 
from Highschooler H1, Highschooler H2
where H1.name < H2.name and 
    exists (select * from Likes L1 where H1.ID = L1.ID1 and H2.ID = L1.ID2) and 
    exists (select * from Likes L2 where H1.ID = L2.ID2 and H2.ID = L2.ID1) 
order by H1.name, H2.name

/* Q4.
    Find names and grades of students who only have friends in the same grade. Return the
    result sorted by grade, then by name within each grade. 
*/
select H1.name, H1.grade
from Highschooler H1
where not exists (select * from Highschooler H2, Friend where H1.ID = ID1 and H2.ID = ID2 and H1.grade != H2.grade)
order by H1.grade, H1.name

/* Q5
    Find the name and grade of all students who are liked by more than one other student.
*/
select name, grade
from Highschooler 
where ID in (select ID2 from Likes group by ID2 having count(ID2) > 1)
