/* SQLite: http://www.sqlite.org/sqlite.html */
/* PostgreSQL: http://www.postgresql.org/docs/9.0/static/app-psql.html */

/* Read a small data set:
   1) SQLite3
   % sqlite3
   sqlite> .read social.sql 

   2) PostgreSQL
   % psql
   ray=# \i social.sql
*/

/* Q1.
    It's time for the seniors to graduate. Remove all 12th graders from Highschooler. 
 */
delete from Highschooler
where grade = 12

/* Q2.
    If two students A and B are friends, and A likes B but not vice-versa, remove 
    the Likes tuple. 
*/
delete from Likes
where not exists (select * from Likes L2 where Likes.ID1 = L2.ID2 and Likes.ID2 = L2.ID1)
      and (exists (select * from Friend where Likes.ID1 = Friend.ID1 and Likes.ID2 = Friend.ID2)
           or exists (select * from Friend where Likes.ID1 = Friend.ID2 and Likes.ID2 = Friend.ID1))

/* Q3.
    For all cases where A is friends with B, and B is friends with C, add a new 
    friendship for the pair A and C. Do not add duplicate friendships, friendships
    that already exist, or friendships with oneself. (This one is a bit challenging; 
    congratulations if you get it right.) 
*/
insert into Friend
select F1.ID1, F2.FD2
from Friend F1, Friend F2
where F1.ID2 = F2.ID1 and F1.ID1 <> F2.ID2
except 
select * from Friend
