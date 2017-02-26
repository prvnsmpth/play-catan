package models;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import exceptions.NotEnoughDevCardsException;
import models.constants.DevCardType;


public class DevCardStack {

  private final Multiset<DevCard> cards;

  public DevCardStack() {
    cards = HashMultiset.create();
  }

  public void add(DevCard card, int count) {
    cards.add(card, count);
  }

  public DevCard remove(DevCardType cardType) throws NotEnoughDevCardsException {
    DevCard card = new DevCard(cardType);
    if (cards.count(card) == 0) {
      throw new NotEnoughDevCardsException(card);
    }
    cards.remove(card, 1);
    return card;
  }

}
