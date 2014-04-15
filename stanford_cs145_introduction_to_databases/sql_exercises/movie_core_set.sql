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

/* Q6.
    For all cases where the same reviewer rated the same movie twice and gave 
    it a higher rating the second time, return the reviewer's name and the title
    of the movie. 
*/

select name, title
from Rating R1 
join Reviewer on Reviewer.rID = R1.rID
join Movie on Movie.mID = R1.mID
join Rating R2 on R1.mID = R2.mID and R1.rID = R2.rID
where R2.stars > R1.stars and R2.ratingDate > R1.ratingDate
group by R1.mID, R1.rID
having count(R1.mID) = 1

/* Q7.
    For each movie that has at least one rating, find the highest number of stars 
    that movie received. Return the movie title and number of stars. Sort by movie title.
*/
select title, max(stars)
from Movie, Rating
where Movie.mID = Rating.mID
group by Movie.mID
having count(Movie.mID) > 0
order by title

/* Q8.
    List movie titles and average ratings, from highest-rated to lowest-rated. If two or 
    more movies have the same average rating, list them in alphabetical order. 
*/
select title, avg(stars) as avgRating
from Movie, Rating
where Movie.mID = Rating.mID
group by Movie.mID
order by avgRating desc, title

/* Q9.
    Find the names of all reviewers who have contributed three or more ratings. 
    (As an extra challenge, try writing the query without HAVING or without COUNT.) 
*/
select name
from Reviewer, Rating
where Reviewer.rID = Rating.rID
group by Reviewer.rID 
having count(Reviewer.rID) >= 3

/* Without having */
select name
from (select name, count(*) as avgRev
      from Reviewer, Rating
      where Reviewer.rID = Rating.rID
      group by Reviewer.rID) S
where avgRev >= 3
