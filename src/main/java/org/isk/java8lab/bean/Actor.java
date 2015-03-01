package org.isk.java8lab.bean;

import java.util.Map;

public class Actor {

  final private String firstname;
  final private String lastname;
  final private String number;

  public Actor(String firstname, String lastname, String number) {
    super();
    this.firstname = firstname;
    this.lastname = lastname;
    this.number = number;
  }

  public String getFirstname() {
    return this.firstname;
  }

  public String getLastname() {
    return this.lastname;
  }

  public String getNumber() {
    return this.number;
  }

  public String toCSV() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.firstname);
    sb.append('#');
    sb.append(this.lastname);
    sb.append('#');
    sb.append(this.number);
    return sb.toString();
  }

  public static Actor fromCSV(String actorAsStr, Map<Actor, Actor> actors) {
    final String[] arrayActor = actorAsStr.split("#");
    return fromCSV(arrayActor, actors);
  }

  public static Actor fromCSV(String[] arrayActor, Map<Actor, Actor> actors) {
    final Actor actor = new Actor(("null".equals(arrayActor[0]) ? null : arrayActor[0]), //
        ("null".equals(arrayActor[1]) ? null : arrayActor[1]), //
        ("null".equals(arrayActor[2]) ? null : arrayActor[2]));

    final Actor found = actors.get(actor);

    if (found != null) {
      return found;
    } else {
      actors.put(actor, actor);
      return actor;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.firstname == null) ? 0 : this.firstname.hashCode());
    result = prime * result + ((this.lastname == null) ? 0 : this.lastname.hashCode());
    result = prime * result + ((this.number == null) ? 0 : this.number.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Actor other = (Actor) obj;
    if (this.firstname == null) {
      if (other.firstname != null)
        return false;
    } else if (!this.firstname.equals(other.firstname))
      return false;
    if (this.lastname == null) {
      if (other.lastname != null)
        return false;
    } else if (!this.lastname.equals(other.lastname))
      return false;
    if (this.number == null) {
      if (other.number != null)
        return false;
    } else if (!this.number.equals(other.number))
      return false;
    return true;
  }
}
