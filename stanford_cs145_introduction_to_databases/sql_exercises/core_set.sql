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
    Find the titles of all movies directed by Steven Spielberg. 
 */

select title
from Movie
where director = "Steven Spielberg"; 

/* Q1.
    Find all years that have a movie that received a rating of 4 or 5,
    and sort them in increasing order.
 */
select distinct year
from Movie, Rating
where Movie.mID = Rating.mID and stars >= 4
order by Movie.year asc;

/* Q3.
    Find the titles of all movies that have no ratings. 
*/
select title
from Movie
where not Movie.mID in (select Rating.mID from Rating);

/* Q4.
    Some reviewers didn't provide a date with their rating. Find the names of 
    all reviewers who have ratings with a NULL value for the date. 
*/
select distinct name
from Reviewer, Rating
where Reviewer.rID = Rating.rID and Rating.ratingDate is null;

/* Q5.
    Write a query to return the ratings data in a more readable format: 
    reviewer name, movie title, stars, and ratingDate. 
    Also, sort the data, first by reviewer name, then by movie title, 
    and lastly by number of stars. 
*/
select name, title, stars, ratingDate
from Reviewer, Movie, Rating
where Reviewer.rID = Rating.rID and Movie.mID = Rating.mID
order by name, title, stars;
