package org.isk.java8lab.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Movie {
  private String name;
  private double rating;
  private int votes;
  private String type;
  private String releaseYear;
  private String number;
  private boolean suspended;
  final private List<String> countries = new ArrayList<>();
  final private List<ActorInMovie> actors = new ArrayList<>();
  final private List<String> genres = new ArrayList<>();

  public Movie(String name) {
    this.name = name;
  }

  public Movie(String name, String year, String number, String type, boolean suspended) {
    this(name);
    this.releaseYear = year;
    this.number = number;
    this.type = type;
    this.suspended = suspended;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getReleaseYear() {
    return this.releaseYear;
  }

  public void setReleaseYear(String releaseYear) {
    this.releaseYear = releaseYear;
  }

  public String getNumber() {
    return this.number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public boolean isSuspended() {
    return this.suspended;
  }

  public void setSuspended(boolean suspended) {
    this.suspended = suspended;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getRating() {
    return this.rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public int getVotes() {
    return this.votes;
  }

  public void setVotes(int votes) {
    this.votes = votes;
  }

  public List<String> getCountries() {
    return this.countries;
  }

  public List<ActorInMovie> getActors() {
    return this.actors;
  }

  public List<String> getGenres() {
    return this.genres;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.suspended ? 0 : 1234);
    result = prime * result + ((this.number == null) ? 0 : this.number.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    result = prime * result + ((this.releaseYear == null) ? 0 : this.releaseYear.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    return result;
  }

  public String toCSV() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.name);
    sb.append((char) 0x1f);
    sb.append(this.rating);
    sb.append((char) 0x1f);
    sb.append(this.votes);
    sb.append((char) 0x1f);
    sb.append(this.type);
    sb.append((char) 0x1f);
    sb.append(this.releaseYear);
    sb.append((char) 0x1f);
    sb.append(this.number);
    sb.append((char) 0x1f);
    sb.append(this.suspended);
    sb.append((char) 0x1f);

    if (this.actors.size() > 0) {
      for (ActorInMovie actor : this.actors) {
        sb.append(actor.toCSV());
        sb.append('|');
      }
      sb.setLength(sb.length() - 1);
    } else {
      sb.append("null");
    }

    sb.append((char) 0x1f);

    if (this.countries.size() > 0) {
      for (String country : this.countries) {
        sb.append(country);
        sb.append('|');
      }
      sb.setLength(sb.length() - 1);
    } else {
      sb.append("null");
    }

    sb.append((char) 0x1f);

    if (this.genres.size() > 0) {
      for (String genre : this.genres) {
        sb.append(genre);
        sb.append('|');
      }
      sb.setLength(sb.length() - 1);
    } else {
      sb.append("null");
    }

    return sb.toString();
  }

  public static Movie fromCSV(final String s, Map<Actor, Actor> actors) {
    final String[] arrayMovie = s.split(String.valueOf((char) 0x1f));

    final Movie movie = new Movie(arrayMovie[0]);
    movie.setRating(Double.parseDouble(arrayMovie[1]));
    movie.setVotes(Integer.parseInt(arrayMovie[2]));
    movie.setType("null".equals(arrayMovie[3]) ? null : arrayMovie[3]);
    movie.setReleaseYear("null".equals(arrayMovie[4]) ? null : arrayMovie[4]);
    movie.setNumber("null".equals(arrayMovie[5]) ? null : arrayMovie[5]);
    movie.setSuspended(Boolean.valueOf(arrayMovie[6]));

    if (!"null".equals(arrayMovie[7])) {
      final String[] arrayActors = arrayMovie[7].split("\\|");
      for (String actorAsStr : arrayActors) {
        movie.getActors().add(ActorInMovie.fromCSV(actorAsStr, actors));
      }
    }

    if (!"null".equals(arrayMovie[8])) {
      final String[] arrayCountries = arrayMovie[8].split("\\|");
      for (String country : arrayCountries) {
        movie.getCountries().add(country);
      }
    }

    if (!"null".equals(arrayMovie[9])) {
      final String[] arrayGenres = arrayMovie[9].split("\\|");
      for (String genre : arrayGenres) {
        movie.getGenres().add(genre);
      }
    }

    return movie;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Movie other = (Movie) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.releaseYear == null) {
      if (other.releaseYear != null)
        return false;
    } else if (!this.releaseYear.equals(other.releaseYear))
      return false;
    if (this.number == null) {
      if (other.number != null)
        return false;
    } else if (!this.number.equals(other.number))
      return false;
    if (this.type == null) {
      if (other.type != null)
        return false;
    } else if (!this.type.equals(other.type))
      return false;
    if (this.suspended != other.suspended)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Movie [name=" + this.name + ", rating=" + this.rating + ", votes=" + this.votes + ", type=" + this.type
        + ", releaseYear=" + this.releaseYear + ", number=" + this.number + ", suspended=" + this.suspended
        + ", countries=" + this.countries + ", actors=" + this.actors + ", genres=" + this.genres + "]";
  }
}
