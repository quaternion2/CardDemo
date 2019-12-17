package com.carddemo;

/**
 * Main.java
 *
 * @brief This program simulates the shuffling, sorting and dealing of a set of
 * cards.
 *
 * @detials
 *
 */
class Main {

    public static void main(String[] args) {
        Deck deck = new Deck();

        System.out.print("\n=== Shuffle Deck ==============================\n");
        deck.shuffle(15); // use a seed value of 15
        deck.printDeck();

        System.out.print("\n=== Sorted Deck ==============================\n");
        deck.sort();
        deck.printDeck();

        System.out.print("\n=== Shuffled Again ==============================\n");
        deck.shuffle(34);  // use a seed value of 34
        deck.printDeck();

        System.out.print("\n=== Deal 3 sets of 5 cards ==============================\n");
        deck.dealHand(3, 5);

        System.out.print("\n=== Deal again ==============================\n");
        deck.dealHand(3, 5);

        System.out.print("\n=== Suffle and deal again ==============================\n");
        deck.shuffle(99);  // use a seed value of 99
        deck.dealHand(3, 5);

        System.out.println();
    }
}
