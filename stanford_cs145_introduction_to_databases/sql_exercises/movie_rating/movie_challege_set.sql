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
    For each movie, return the title and the 'rating spread', that is, the 
    difference between highest and lowest ratings given to that movie. Sort 
    by rating spread from highest to lowest, then by movie title. 
 */
select title, (max(stars) - min(stars)) as RatingSpread
from Movie, Rating
where Movie.mID = Rating.mID
group by Movie.mID
order by RatingSpread, title

/* Q2.
    Find the difference between the average rating of movies released before 
    1980 and the average rating of movies released after 1980. (Make sure to
    calculate the average rating for each movie, then the average of those 
    averages for movies before 1980 and movies after. Don't just calculate the 
    overall average rating before and after 1980.) 
 */
select avg(ratingPre1980.avgRating) - avg(ratingPost1980.avgRating)
from (select avg(Rating.stars) as avgRating from Movie, Rating where Movie.mID = Rating.mID and Movie.year < 1980 group by Movie.mID) ratingPre1980,
     (select avg(Rating.stars) as avgRating from Movie, Rating where Movie.mID = Rating.mID and Movie.year > 1980 group by Movie.mID) ratingPost1980

/* Q3
    Some directors directed more than one movie. For all such directors, return 
    the titles of all movies directed by them, along with the director name. Sort 
    by director name, then movie title. (As an extra challenge, try writing the 
    query both with and without COUNT.
*/
select title, M1.director
from Movie M1
where M1.director in (select director from Movie group by director having count(*) > 1)
order by M1.director, title

select title, M1.director
from Movie M1
where M1.director in (select M2.director from Movie M2 join Movie M3 on M2.mID != M3.mID and M2.director = M3.director)
order by M1.director, title

