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
select title, (max(stars) - min(stars)) as spread 
from Movie, Rating
where Movie.mID = Rating.mID
group by Movie.mID
order by RatingSpread desc, title;

/* Q2.
    Find the difference between the average rating of movies released before 
    1980 and the average rating of movies released after 1980. (Make sure to
    calculate the average rating for each movie, then the average of those 
    averages for movies before 1980 and movies after. Don't just calculate the 
    overall average rating before and after 1980.) 
 */
select avg(ratingPre1980.avgRating) - avg(ratingPost1980.avgRating)
from (select avg(Rating.stars) as avgRating 
      from Movie, Rating 
      where Movie.mID = Rating.mID and Movie.year < 1980 
      group by Movie.mID) ratingPre1980, 
     (select avg(Rating.stars) as avgRating 
      from Movie, Rating 
      where Movie.mID = Rating.mID and Movie.year > 1980 
      group by Movie.mID) ratingPost1980

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

/* Q4
    Find the movie(s) with the highest average rating. Return the movie title(s) 
    and average rating. (Hint: This query is more difficult to write in SQLite 
    than other systems; you might think of it as finding the highest average 
    rating and then choosing the movie(s) with that average rating.) 

    Note: Your queries are executed using SQLite, so you must conform to the SQL 
    constructs supported by SQLite.
*/
select title, maxAvgStars
from (select title, avg(stars) as maxAvgStars 
      from Rating, Movie
      where Rating.mID = Movie.mID
      group by Rating.mID)
where maxAvgStars in (select max(avgStars)
                      from (select avg(stars) as avgStars from Rating group by Rating.mID));

/* Q5
    Find the movie(s) with the lowest average rating. Return the movie title(s) 
    and average rating. (Hint: This query may be more difficult to write in SQLite 
    than other systems; you might think of it as finding the lowest average rating 
    and then choosing the movie(s) with that average rating.)
*/
select title, maxAvgStars
from (select title, avg(stars) as maxAvgStars 
      from Rating, Movie
      where Rating.mID = Movie.mID
      group by Rating.mID)
where maxAvgStars in (select min(avgStars)
                      from (select avg(stars) as avgStars from Rating group by Rating.mID));

/* Q6  
    For each director, return the director's name together with the title(s) of the 
    movie(s) they directed that received the highest rating among all of their movies, 
    and the value of that rating. Ignore movies whose director is NULL. 
*/

select distinct director, title
from Movie M1, Rating R1
where M1.mID = R1.mID and 
      stars in (select max(stars)
        from Movie M2, Rating R2
        where M2.mID = R2.mID and not M2.director is NULL and M1.director = M2.director);
