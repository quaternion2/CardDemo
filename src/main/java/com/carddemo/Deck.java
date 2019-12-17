package com.carddemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Deck.java
 *
 * @brief A class that represents a deck of playing cards.
 *
 * @details This class is responsible for shuffling and dealing a deck of cards.
 * The deck should contain 52 cards A,2 - 10, J,Q K or four suits, but no
 * jokers.
 *
 * DESIGN WARNING: A standard deck does not have 4 Jokers (It only has two and 
 * while they must be visually distinct they they are not associated with a 
 * suit), since the jokers are to be removed (Standard 52 Card deck) the design 
 * of Card will be left as-is (It associates, incorrectly a Joker with each
 * suit) however should Jokers be needed in the future the 
 * design of both Card and Deck will likely need modification.
 */
class Deck {
    private final List<Card> cards;
/**
 * Creates a new deck, since a new deck should appear in its default 
 * order 
 * 
 */
    public Deck() {
        cards = new ArrayList(52);
        Card.Suit[] cardSuits = Card.Suit.values();
        Card.Value[] cardValues = Card.Value.values();
        for (Card.Suit suit : cardSuits) {
            for (int i = 1; i < cardValues.length; i++) {//Skip Jokers
                this.cards.add(new Card(suit, cardValues[i]));
            }
        }
        selectionSort(this.cards);//a new deck should appear in the decks order
    }
    
    /**
     * Set the state of the deck externally, to facilitate DI and 
     * in turn unit testing.
     * 
     * @param cards 
     */
    public Deck(List<Card> cards){
        this.cards = new ArrayList(cards);
    }
    
    /**
     * Required for unit testing. 
     * 
     * @return List of cards representing the internal state of the deck.
     */
    public List<Card> getCards(){
        return new ArrayList(this.cards);
    }
    

    public void dealHand(int sets, int cards) {
        final int total = sets * cards;
        for (int i = 0; i < total; i++) {
            if (i > 0 && i % cards == 0) {//add a new line after every hand
                System.out.print("\n");
            }
            this.cards.get(i).print();
        }
    }

    /**
     * Prints 4 rows of 13 cards.
     */
    public void printDeck() {
        final int NCOLS = 13;//print 13 columns
        for (int i = 0; i < this.cards.size(); i++) {
            if (i > 0 && i % NCOLS == 0) {//add a new line after every NCOLS
                System.out.print("\n");
            }
            cards.get(i).print();
        }
    }

    
    /**
     * This shuffles the deck in a non-secure way. ie: Unsuitable for online 
     * gambling.
     * 
     * Explanation: a Deck has 52 factorial (52!) possibilities (8.06e+67) 
     * Random takes a long reducing the possibilities to (1.84e+19) however 
     * this method takes a int further truncating the possibilities such that 
     * only 2^32-1 exist. This means as few as five drawn cards (1/52*1/51*1/50*...) 
     * etc would exhaust available entropy and lead to knowing 
     * the full ordering of the deck.
     * 
     * @param seed 
     */
    public void shuffle(int seed) {
        Collections.shuffle(cards, new Random(seed));
    }

    /**
     * A simple selection sort.
     * 
     * Sorts the deck from lowest to highest Suits will appear in order from
     * Diamonds, Clubs, Hearts, Spades and face value will be ascending with Ace
     * being low.
     * 
     * Aside: I quickly entertained several methods of sort. One requirement 
     * is that this be treated as 'production' grade however another
     * requirement prohibits the built in sort (which is _really_ good, it's 
     * a double pivot quick sort and its implementation is likely 
     * the fastest general purpose sort possible, without taking the nature of 
     * the data itself into consideration); finally there is the requirement
     * that this should be OO. 
     * - Should I just do a selection sort, 52 cards is unlikely to cause 
     *      performance issues, for a single users it is certainly moot. Also
     *      as explained later if performance is a concern sorting
     *      can be avoided entirely. Also this algorithm is visually verifiable
     *      (without testing we have high confidence).
     * - Could implement a standard general purpose single pivot quicksort. 
     *      The main down side is more complexity. It should be tested. 
     *      Also it isn't particularly OO to have a general purpose sorting 
     *      algorithm as part of Deck (really it is a concern of the collection
     *      or certainly an external utility concern). 
     * - If the sort is to be specifically implemented for Deck then 
     *      would it not make sense to take advantage of our knowledge of the 
     *      data? This would be OO in that the search is specifically tied 
     *      to this Objects data concerns...
     * - Since there is no requirement that the sort be performed in place
     *      and exploiting our knowledge of the data
     *      it is possible to create a new array and using a hash function 
     *      that maps suite and value into the right index we get O(n) 
     *      which is even faster than quicksort
     * - Finally the fastest solution be to sort the deck in the constructor
     *      using any sort method, even direct assignment using 'new' 52 times 
     *      then make the collection immutable (simply a good practice and helps 
     *      with reasoning) then create a second working 
     *      copy (a non-final class member). This reduces the complexity to a 
     *      direct copy removing the need for a sort all together. 
     *      Clearly not a 'sort' but 
     *      functionally identical with superior results. 
     * - Conclusion: This is just to share a though process that took about 2
     *  minutes, writing this out took longer! In the end I went with my 
     *  first thought because it is a traditional sort which seems to be 
     *  in the nature of the rules, it is trivially verifiable and _if_ 
     *  performance is a concern there are better ways to handle this.
     */
    public void sort() {
        this.selectionSort(cards);
    }
    
    public final void selectionSort(List<Card> list){
        for (int i = 0; i < list.size(); i++) {
            for (int j = i; j < list.size(); j++) {
                Card current = list.get(i);
                Card target = list.get(j);
                if (this.compareDeckCards(current, target) > 0) {//swap if target card is less than the current card
                    list.set(i, target);
                    list.set(j, current);
                }
            }
        } 
    }

    /**
     * This compares cards for deck ordering (the desired order the deck 
     * should appear in), not to be confused with a cards relative value 
     * which is defined by Card class's is_* methods. 
     * 
     * A comparison function, follows the same definition as {@link https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#compare-T-T-}
     * @param c1 Card 1
     * @param c2 Card 2
     * @return Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     * @See Card
     */
    private int compareDeckCards(Card c1, Card c2) {
        //We must define the order we want since this order differs from 
        //higher index means higher priority
        //Cards.Suit default ordinals 
        final List<Card.Suit> suitPriority = new ArrayList<Card.Suit>() {
            {
                add(Card.Suit.DIAMONDS);
                add(Card.Suit.CLUBS);
                add(Card.Suit.HEARTS);
                add(Card.Suit.SPADES);
            }
        };

        int c1SuitPriority = suitPriority.indexOf(c1.get_suit());
        int c2SuitPriority = suitPriority.indexOf(c2.get_suit());
        int difference = c1SuitPriority - c2SuitPriority;
        if (difference != 0) {
            return difference;
        }
        //Card.Value (face values) has ordinals in desired order (ace low)
        //it is better to make this explicit here rather than use, Cards
        //internal comparisons (Card.is_*) since the decks order may in 
        //the future be different than the cards, as is already the case with 
        //the Suits
        return c1.getValue().ordinal() - c2.getValue().ordinal();
    }

}
