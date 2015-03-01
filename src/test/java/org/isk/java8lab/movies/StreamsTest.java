package org.isk.java8lab.movies;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.isk.java8lab.bean.Actor;
import org.isk.java8lab.bean.ActorInMovie;
import org.isk.java8lab.bean.Movie;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class StreamsTest {
  final static Set<Movie> MOVIES = new HashSet<>();

  @BeforeClass
  public static void setup() throws Exception {
    final long start = System.nanoTime();

    final URL resource = Thread.currentThread().getContextClassLoader().getResource("movies.csv");
    final URI uri = resource.toURI();

    try (final Stream<String> stream = Files.lines(Paths.get(uri))) {
      final Map<Actor, Actor> actors = new HashMap<>();
      stream.onClose(() -> {
        long end = System.nanoTime();
        System.out.println("Done in " + ((end - start) / 1_000_000) + "ms");
      }).forEach(line -> {
        try {
          MOVIES.add(Movie.fromCSV(line, actors));
        } catch (Exception e) {
          System.out.println(e);
          System.out.println(line);
        }
      });
    }

    System.out.println("---------------------------------------------------");
  }

  @Test
  public void moviesWithHeroesInTheTitle() {
    MOVIES.stream().filter(m -> m.getName().toLowerCase().contains("heroes")) //
        .forEach(m -> System.out.println(m.getName()));
  }

  @Test
  public void moviesWithHeroesInTheTitleSorted() {
    MOVIES.stream().filter(m -> m.getName().toLowerCase().contains("heroes")) //
        .sorted(Comparator.comparing(Movie::getName)) //
        .forEach(m -> System.out.println(m.getName()));
  }

  @Test
  public void moviesWithHeroesInTheTitleSorted_theWrongWay() {
    MOVIES.stream() //
        .limit(1000) //
        .sorted((o1, o2) -> {
          System.out.println(o1.getName());
          return o1.getName().compareTo(o2.getName());
        }) //
        .filter(m -> m.getName().toLowerCase().contains("heroes")) //
        .forEach(m -> System.out.println(m.getName()));
  }

  @Test
  public void moviesWithHeroesInTheTitleSorted_TheGoodWay() {
    MOVIES.stream() //
        .filter(m -> m.getName().toLowerCase().contains("Heroes"))
        .sorted((o1, o2) -> {
          System.out.println(o1.getName());
          return o1.getName().compareTo(o2.getName());
        }).forEach(m -> System.out.println(m.getName()));
  }
  
  @Test
  public void moviesWithHeroesInTheTitleCount() {
    final long count = MOVIES.stream().filter(m -> m.getName().toLowerCase().contains("heroes")).count();
    Assert.assertEquals(8, count);
  }


  @Test
  public void oneHundredMovieTitlesOnly() {
    MOVIES.stream().limit(100).map(Movie::getName).forEach(System.out::println);
  }

  @Test
  public void oneHundredMovieTitlesOnlyInUppercase() {
    MOVIES.stream().limit(100).map(Movie::getName).map(String::toUpperCase).forEach(System.out::println);
  }

  @Test
  public void wasThereAJamesBondMovie() {
    final boolean jamesBondMovie = MOVIES.stream().anyMatch(m -> m.getName().toLowerCase().contains("james bond"));
    Assert.assertTrue(jamesBondMovie);
  }

  @Test
  public void wasThereAtLeastOneBatmanMovie() {
    final boolean batmanMovie = MOVIES.stream().anyMatch(m -> m.getName().toLowerCase().contains("batman"));
    Assert.assertTrue(batmanMovie);
  }
  
  @Test
  public void areAll1995Movies() {
    final boolean all1995 = MOVIES.stream().allMatch(m -> "1995".equals(m.getReleaseYear()));
    Assert.assertTrue(all1995);
  }

  @Test
  public void isTheShawshankRedemptionFrom1995() {
    final boolean isTheShawshankRedemptionFrom1995 = !MOVIES.stream() //
        .noneMatch(m -> m.getName().toLowerCase().contains("the shawshank redemption"));
    Assert.assertFalse(isTheShawshankRedemptionFrom1995);
  }

  @Test
  public void numberOfVotes_reduce() {
    final OptionalInt reduce = MOVIES.stream().mapToInt(m -> m.getVotes()).reduce((v1, v2) -> v1 + v2);
    int count = reduce.getAsInt();
    Assert.assertEquals(10_050_031, count);
    reduce.ifPresent(System.out::println);
  }

  @Test
  public void numberOfVotes_sum() {
    final int count = MOVIES.stream().mapToInt(m -> m.getVotes()).sum();
    Assert.assertEquals(10_050_031, count);
  }

  @Test
  public void findFirstMovieTitleSorted() {
    final Optional<String> optional = MOVIES.stream().map(m -> m.getName()).sorted().findFirst();
    Assert.assertEquals("'Doctor Zhivago': The Making of a Russian Epic", optional.get());
  }

  @Test
  public void findAnyTheShawshankRedemptionOrReturnMovieNotFound() {
    final Optional<String> optional = MOVIES.stream().map(m -> m.getName()) //
        .filter(s -> s.toLowerCase().contains("The Shawshank Redemption")) //
        .findAny();
    Assert.assertEquals("Movie not found", optional.orElse("Movie not found"));
  }

  @Test
  public void averageRatings() {
    final double average = MOVIES.stream() //
        .mapToDouble(Movie::getRating) //
        .filter(rating -> rating > 0) //
        .average() //
        .getAsDouble();

    Assert.assertEquals(6.2, average, 0.01);
  }

  @Test
  public void maxRating() {
    final String movie = MOVIES.stream().max(Comparator.comparing(Movie::getRating)).get().getName();
    Assert.assertEquals("Renegades 2", movie);
  }

  @Test
  public void minRating() {
    final String movie = MOVIES.stream()
                               .filter(m -> m.getRating() > 0)
                               .min(Comparator.comparing(Movie::getRating))
                               .get()
                               .getName();
    Assert.assertEquals("Dronningens nytårstale", movie);
  }

  @Test
  public void doubleSummaryStatistics() {
    final DoubleSummaryStatistics summary = MOVIES.stream()
                                                  .filter(m -> m.getRating() > 0)
                                                  .collect(Collectors.summarizingDouble(m -> m.getRating()));
    System.out.println(summary);
  }

  @Test
  public void allGenresToArray() {
    final String[] genres = MOVIES.stream()
                                  .flatMap(m -> m.getGenres().stream())
                                  .distinct()
                                  .sorted()
                                  .toArray(String[]::new);

    for (String s : genres) {
      System.out.println(s);
    }
  }

  @Test
  public void bestMovieOfKevinSpacey() {
    final String movie = MOVIES
        .stream()
        .filter(m -> m.getActors()
                      .stream()
                      .anyMatch(
                          ai -> ai.getActor().getFirstname().toLowerCase().equals("kevin")
                             && ai.getActor().getLastname().toLowerCase().equals("spacey")
                       )
               )
        .max(Comparator.comparing(Movie::getRating)).get().getName();

    Assert.assertEquals("Se7en", movie);
  }

  @Test
  public void numberOfMoviesByActors() {
    final Map<Actor, Long> numberOfMoviesByActors = MOVIES
         .stream()
        .flatMap(m -> m.getActors().stream())
        .collect(Collectors.groupingBy(ActorInMovie::getActor, HashMap::new, Collectors.counting()));
    numberOfMoviesByActors.forEach((k, v) -> System.out.println(k.getFirstname() + " " + k.getLastname() + ": " + v));
  }

  @Test
  public void numberOfMoviesByActorsSortedByActors() {
    final Map<String, Long> numberOfMoviesByActors = MOVIES
        .stream()
        .flatMap(m -> m.getActors().stream())
        .collect(Collectors.groupingBy(k -> k.getActor().getFirstname() + " " + k.getActor().getLastname(),
                                       TreeMap::new,
                                       Collectors.counting()
                                      )
                );

    numberOfMoviesByActors.forEach((k, v) -> System.out.println(k + ": " + v));
  }

  @Test
  public void actorWithMostMovies() {
    final Map<String, Long> numberOfMoviesByActors = MOVIES
        .stream()
        .flatMap(m -> m.getActors().stream())
        .collect(Collectors.groupingBy(k -> k.getActor().getFirstname() + " " + k.getActor().getLastname(),
                                       TreeMap::new,
                                       Collectors.counting()
                                      )
                );

    final Optional<Entry<String, Long>> optional = 
        numberOfMoviesByActors.entrySet().stream().max(Map.Entry.comparingByValue());
    Assert.assertEquals("Alex Sanders", optional.get().getKey());
    Assert.assertEquals(198, optional.get().getValue().longValue());
  }

  @Test
  public void allCountries() {
    final List<String> countries = MOVIES.stream()
                                         .flatMap(m -> m.getCountries().stream())
                                         .distinct()
                                         .sorted()
                                         .collect(Collectors.toList());
    countries.forEach(System.out::println);
  }

  @Test
  public void moviesByRating() {
    MOVIES.stream().collect(Collectors.groupingBy(m -> m.getRating(),
                                                  Collectors.mapping((Movie m) -> m.getName(),
                                                                     Collectors.toList()
                                                                    )
                                                  )
                           );
  }

  @Test
  public void numberOfMoviesByCountry_theGoodWay() {
    final Map<String, Long> countryMoviesNum = MOVIES.stream()
                                               .flatMap(m -> m.getCountries().stream()) //
                                               .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

    countryMoviesNum.forEach((k, v) -> System.out.println(k + ": " + v));
  }

  @Test
  public void numberOfMoviesByCountry_theWrongWay() {
    final Stream<String> countries = MOVIES.stream().flatMap(m -> m.getCountries().stream()).distinct();
    final Map<String, Long> countryMoviesNum = countries.collect(
        Collectors.toMap(country -> country, country -> MOVIES.stream()
                                                              .filter(m -> m.getCountries().contains(country))
                                                              .count()
                        )
    );
    countryMoviesNum.forEach((k, v) -> System.out.println(k + ": " + v));
  }

  @Test
  public void countryWithMostMovies() {
    final Optional<Entry<String, Long>> ll = MOVIES.stream().flatMap(m -> m.getCountries().stream())
        .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue());

    System.out.println(ll.get().getKey() + ": " + ll.get().getValue());
  }

  @Test
  public void moviesWithJamesBond() {
    final List<Movie> movies = MOVIES
        .stream()
        .filter(m -> m.getActors()
                       .stream()
                       .filter(ai -> ai.getCharacter() != null)
                      .anyMatch(ai -> ai.getCharacter().toLowerCase().contains("james bond"))
               )
        .collect(Collectors.toList());

    movies.forEach(m -> System.out.println(m.getName()));
  }

  @Test
  public void oneHundredBestNoneAdultOrShortMoviesSortedWithAtLeastOneThousandVotes() {
    MOVIES.stream().filter(m -> !m.getGenres().contains("ADULT")) //
        .filter(m -> !m.getGenres().contains("SHORT")) //
        .filter(m -> m.getVotes() >= 1000) //
        .sorted(Comparator.comparing(Movie::getRating).reversed()) //
        .limit(100)
        .map(m -> m.getName() + " : " + m.getRating()) //
        .forEach(System.out::println);
  }

  @Test
  @Ignore
  public void moviesByActors_theWrongWay() {
    final Stream<Actor> actors = MOVIES.stream().flatMap(m -> m.getActors().stream().map(ActorInMovie::getActor));
        //.limit(2);
    final Map<Actor, List<Movie>> actorMovies = actors
        .distinct()
        .parallel()
        .collect(Collectors.groupingBy(a -> a,
                                       Collectors.mapping(a -> MOVIES.stream()
                                                                     .filter(m -> m.getActors()
                                                                                   .stream()
                                                                                   .map(ActorInMovie::getActor)
                                                                                   .anyMatch(innerA -> innerA.equals(a))
                                                                            )
                                                                     .collect(Collectors.toList()),
                    Collectors.reducing(new ArrayList<Movie>(), (List<Movie> a, List<Movie> b) -> {
                      a.addAll(b);
                      return a;
                    }
                  )
                )
              )
            );

    actorMovies.entrySet().stream() //
        .filter(a -> a.getKey().getLastname().toLowerCase().equals("spacey")).map(e -> e.getValue()) //
        .findFirst() //
        .get() //
        .forEach(m -> System.out.println(m.getName()));
  }

  @Test
  public void moviesByActors_aBetterWay() {
    /* Example
    Collector<Person, StringJoiner, String> personNameCollector =
        Collector.of(
            () -> new StringJoiner(" | "),          // supplier
            (j, p) -> j.add(p.name.toUpperCase()),  // accumulator
            (j1, j2) -> j1.merge(j2),               // combiner
            StringJoiner::toString);                // finisher
     */

    final Map<Actor, List<Movie>> actorMovies = MOVIES.parallelStream().collect(this.getActorMoviesCollector());
    actorMovies.entrySet().stream() //
        .filter(a -> a.getKey().getLastname().toLowerCase().equals("spacey")).map(e -> e.getValue()) //
        .findFirst() //
        .get() //
        .forEach(m -> System.out.println(m.getName()));
  }

  private Collector<Movie, Map<Actor, List<Movie>>, Map<Actor, List<Movie>>> getActorMoviesCollector() {
    final Collector<Movie, Map<Actor, List<Movie>>, Map<Actor, List<Movie>>> collector =
        Collector.of(() -> new ConcurrentHashMap<Actor, List<Movie>>(),
            (map, movie) -> {
              movie.getActors().stream().map(ActorInMovie::getActor).forEach(actor -> {
                final List<Movie> movies = map.get(actor);
                if (movies != null) {
                  movies.add(movie);
                } else {
                  final List<Movie> list = new ArrayList<>();
                  list.add(movie);
                  map.put(actor, list);
                }
              });
            },
            (map1, map2) -> {
              map1.forEach((actor, movies) -> {
                final List<Movie> otherMovies = map2.remove(actor);
                if (otherMovies != null) {
                  movies.addAll(otherMovies);
                }
              });

              map1.putAll(map2);

              return map1;
            });
    return collector;
  }

  @Test
  public void actorWithBestAverageRatings() {
    final Map<Actor, List<Movie>> actorMovies = MOVIES.parallelStream().filter(m -> m.getVotes() > 1000).collect(this.getActorMoviesCollector());

    final Map<Actor, OptionalDouble> actorsRatings = actorMovies.entrySet().stream() //
      .collect(Collectors.toMap( //
                (Entry<Actor, List<Movie>> a) -> a.getKey(), //
                (Entry<Actor, List<Movie>> a) -> a.getValue().stream().mapToDouble(Movie::getRating).average() //,
              ) //
      );

    final Entry<Actor, OptionalDouble> max = actorsRatings.entrySet()
                                                          .stream()
                                                          .max((a, b) -> Double.compare(a.getValue().getAsDouble(),
                                                                                        b.getValue().getAsDouble()
                                                                                       )
                                                              )
                                                          .get();

    System.out.println(max.getKey().getFirstname() + " " + max.getKey().getLastname() + " " + max.getValue().getAsDouble());
  }

  public static class Person {
    String name;
  }
}
