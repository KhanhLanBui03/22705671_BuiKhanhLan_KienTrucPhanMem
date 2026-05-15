package com.movie.movieservice.service;

import com.movie.movieservice.entity.Movie;
import com.movie.movieservice.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @PostConstruct
    public void initData() {
        if (movieRepository.count() == 0) {
            movieRepository.save(new Movie(null, "Avengers: Endgame", "Action/Sci-Fi", 150000.0));
            movieRepository.save(new Movie(null, "Inception", "Sci-Fi/Thriller", 120000.0));
            movieRepository.save(new Movie(null, "The Dark Knight", "Action/Drama", 130000.0));
        }
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }
}
