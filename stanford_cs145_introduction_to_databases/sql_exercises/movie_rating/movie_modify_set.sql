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
    Add the reviewer Roger Ebert to your database, with an rID of 209. 
 */
insert into Reviewer
(rID, name)
values
(209, "Roger Ebert")

/* Q2.
    Insert 5-star ratings by James Cameron for all movies in the database. 
    Leave the review date as NULL.
*/
insert into Rating
(rID, mID, stars, ratingDate)
select rID, mID, 5, null
from Movie, Reviewer
where name = "James Cameron";

/* Q3.
    For all movies that have an average rating of 4 stars or higher, add 25 
    to the release year. (Update the existing tuples; don't insert new tuples.)
*/
update Movie
set year = year + 25
where Movie.mID in (select Rating.mID from Rating group by Rating.mID having avg(stars) >= 4)

/* Q4.
    Remove all ratings where the movie's year is before 1970 or after 2000, 
    and the rating is fewer than 4 stars.
*/
delete from Rating
where stars < 4 and Rating.mID in (select Movie.mID from Movie where year < 1970 or year > 2000)
