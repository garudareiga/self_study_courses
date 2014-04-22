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
    Find the names of all reviewers who rated Gone with the Wind. 
 */
select distinct name
from Reviewer, Movie, Rating
where Reviewer.rID = Rating.rID 
    and Movie.mID = Rating.mID 
    and title = "Gone with the Wind";

/* Q2.
    For any rating where the reviewer is the same as the director 
    of the movie, return the reviewer name, movie title, and number
    of stars. 
*/
select name, title, stars 
from Movie, Reviewer, Rating 
where Movie.mID = Rating.mID and Review.rID = Rating.rID and director = name;

/* Q3.
    Return all reviewer names and movie names together in a single list, 
    alphabetized. (Sorting by the first name of the reviewer and first word
    in the title is fine; no need for special processing on last names or
    removing "The".) 
*/
select name
from Reviewer
union
select title
from Movie

/* Q4
    Find the titles of all movies not reviewed by Chris Jackson. 
*/
select title
from Movie
where not Movie.mID in (select Rating.mID from Reviewer, Rating where Reviewer.rID = Rating.rID and Reviewer.name = "Chris Jackson")

/* Q5
    For all pairs of reviewers such that both reviewers gave a rating to the same 
    movie, return the names of both reviewers. Eliminate duplicates, don't pair 
    reviewers with themselves, and include each pair only once. For each pair, return 
    the names in the pair in alphabetical order. 

    Note: Your queries are executed using SQLite, so you must conform to the SQL constructs 
    supported by SQLite. 
*/
select V1.name, V2.name
from Rating R1 
     join Rating R2 on R1.mID = R2.mID and R1.rID <> R2.rID
     join Reviewer V1 on V1.rID = R1.rID
     join Reviewer V2 on V2.rID = R2.rID and V1.name < V2.name
group by V1.rID, V2.name

/* Q6
    For each rating that is the lowest (fewest stars) currently in the database, 
    return the reviewer name, movie title, and number of stars. 
*/

select name, title, stars 
from Movie, Reviewer, Rating
where Movie.mID = Rating.mID and Reviewer.rID = Rating.rID and
    stars in (select min(stars) from Rating);
