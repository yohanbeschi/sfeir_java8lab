package org.isk.java8lab.bean;

import java.util.Map;

public class ActorInMovie {
  final private Actor actor;
  final private String character;
  final private String billingNumber;

  public ActorInMovie(Actor actor, String character, String billingNumber) {
    super();
    this.actor = actor;
    this.character = character;
    this.billingNumber = billingNumber;
  }

  public Actor getActor() {
    return this.actor;
  }

  public String getCharacter() {
    return this.character;
  }

  public String getBillingNumber() {
    return this.billingNumber;
  }

  public String toCSV() {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.actor.toCSV());
    sb.append('#');
    sb.append(this.character);
    sb.append('#');
    sb.append(this.billingNumber);
    return sb.toString();
  }

  public static ActorInMovie fromCSV(String actorAsStr, Map<Actor, Actor> actors) {
    final String[] arrayActor = actorAsStr.split("#");
    if (actorAsStr.length() == 5) {
      return new ActorInMovie(Actor.fromCSV(arrayActor, actors), //
          ("null".equals(arrayActor[3]) ? null : arrayActor[3]), //
          ("null".equals(arrayActor[4]) ? null : arrayActor[4]));
    } else {
      return new ActorInMovie(Actor.fromCSV(arrayActor, actors), //
          ("null".equals(arrayActor[3]) ? null : arrayActor[3]), //
          null);
    }
  }
}
