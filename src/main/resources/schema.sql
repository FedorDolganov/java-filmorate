CREATE TABLE IF NOT EXISTS films (
  id integer PRIMARY KEY,
  name varchar,
  description varchar,
  releaseDate date,
  duration integer,
  mpa integer
);

CREATE TABLE IF NOT EXISTS users (
  id integer PRIMARY KEY,
  email varchar,
  login varchar,
  name varchar,
  birthday date
);

CREATE TABLE IF NOT EXISTS filmGenres (
  film_id integer,
  genre_id integer
);

CREATE TABLE IF NOT EXISTS genres (
  id integer PRIMARY KEY,
  genre varchar
);

CREATE TABLE IF NOT EXISTS mpas (
  id integer PRIMARY KEY,
  mpa varchar
);

CREATE TABLE IF NOT EXISTS likes (
  film_id integer,
  user_id integer,
  PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id integer,
  friend_id integer,
  PRIMARY KEY (user_id, friend_id)
);