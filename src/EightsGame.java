// Zian Chen & Elysia Smyers, Period 3
// 12 December 2017
// EightsGame - class that plays Crazy Eights by allowing PLAYER to choose cards and has COMPUTER determine which is the "smartest" move.
import java.util.Scanner;

public class EightsGame {
	// precondition: none.
	// postcondition: moves each player until either the stock pile is gone or one player has no more cards. */
	public void playGame(Deck deck, Player player, Player computer) {
		boolean over = false;
		boolean yourTurn = true;
		boolean didComputerWin = false;
		boolean ranOutOfCards = false;
		Card cardPlayed = new Card(1, "");

		/* The first card displayed will be the top card dealt off the stock pile. */
		Card topCard = deck.deal();
		System.out.println("THE TOP CARD IS: " + topCard);

		/* Continue rotating players until the game is designated to be over. */
		while(!over) {

			/* Note that if a value of a card is 0, this means that cards ran out. */
			if(cardPlayed.getValue() != 0) {
				if(yourTurn) {
					cardPlayed = makeMove(player, computer, deck, topCard);
					topCard = cardPlayed;
					yourTurn = false;
					over = checkDone(player);
				}
				else {
					cardPlayed = computerPlay(computer, player, topCard, deck);
					topCard = cardPlayed;
					yourTurn = true;
					over = checkDone(computer);
					if(over == true)
						didComputerWin = true;
				}
				if(!over) {
					/* Display the top card for the next player. */
					System.out.println("-----------------------------------");
					System.out.println("THE TOP CARD IS: " + cardPlayed);
				}
			}

			/* In the case that all the cards in the deck run out, the player with less cards wins. */
			else {
				if(computer.getHighNum() < player.getHighNum()) {
					didComputerWin = true;
					ranOutOfCards = true;
				}
				over = true;
			}
		}
		printMessages(didComputerWin, ranOutOfCards, player, computer);
	}

	// precondition: none.
	// postcondition: prints message depending on win or loss.
	public void printMessages(boolean didComputerWin, boolean ranOutOfCards, Player player, Player computer) {
		if(!ranOutOfCards) {
			printPartOne(didComputerWin, player, computer);
		}
		else {
			printPartTwo(didComputerWin, player, computer);
		}
	}
	
	// precondition: none.
	// postcondition: prints out the message if you don't run out of cards.
	public void printPartOne(boolean didComputerWin, Player player, Player computer) {
		for(int i = 0; i < 15; i++)
			System.out.println();
		if(didComputerWin) {
			System.out.println("YOU LOST!");
			System.out.println("YOUR REMAINING HAND: " + '\n' + player);
			int points = player.calculatePoints();
			System.out.println("COMPUTER'S POINTS: " + points);
		}
		else {
			System.out.println("YOU WON!");
			System.out.println("COMPUTER'S REMAINING HAND: " + '\n' + computer);
			int points = computer.calculatePoints();
			System.out.println("YOUR POINTS: " + points);
		}
		for(int i = 0; i < 15; i++)
			System.out.println();
	}
	
	// precondition: none.
	// postcondition: prints out the message if you do run out of cards.
	public void printPartTwo(boolean didComputerWin, Player player, Player computer) {
		System.out.println("NO MORE CARDS LEFT. " + '\n');
		System.out.println("YOUR REMAINING HAND: " + '\n' + player);
		System.out.println();
		System.out.println("COMPUTER'S REMAINING HAND: " + '\n' + computer);
		System.out.println();
		int playerPoints = player.calculatePoints();
		int computerPoints = computer.calculatePoints();
		System.out.println("YOUR POINTS: " + computerPoints);
		System.out.println("COMPUTER'S POINTS: " + playerPoints + '\n');
		if(didComputerWin) 
			System.out.println("YOU LOST!");
		else
			System.out.println("YOU WON!");
	}

	// precondition: none.
	// postcondition: returns a boolean depending on if the player's hand is emptied.
	public boolean checkDone(Player player) {
		Card[] hand = player.getHand();
		if(hand[0] == null)
			return true;
		return false;
	}

	// precondition: none.
	// postcondition: plays a valid card for the player.
	private Card makeMove(Player player, Player computer, Deck deck, Card topCard) {
		Scanner keys = new Scanner(System.in);
		boolean done = false;
		Card cardPlayed = new Card(0, "");
		while(!done) {
			/* Display the top card and ask for which card the player wants to put down. */
			System.out.println("YOUR HAND IS: " + '\n' + player);
			System.out.println("WHICH CARD DO YOU WANT TO PLAY?" + '\n' + "ENTER 0 TO DRAW");
			int response = keys.nextInt();

			/* If they do not have any cards, draw cards from stock pile. */
			if(response == 0) {

				/* Make sure that the total number of cards does not surpass the deck. */
				if(deck.getTop() == 52)
					done = true;
				else 
					player.add(deck.deal());
			}

			/* Otherwise, check the validity of the card chosen. */
			else {
				boolean canPlay = checkValidity(player.getCard(response - 1), topCard);

				/* If the card is valid, then play that card by setting cardPlayed to valid card. */
				if(canPlay) {
					System.out.println("YOU PLAYED: " + player.getCard(response - 1));
					done = true;
					cardPlayed = player.getCard(response - 1);

					/* Take card out of your hand. */
					player.subtract(response - 1);
				}

				/* Otherwise, return an error message. */
				else
					System.out.println("YOU CANNOT PLAY THIS CARD. PICK NEW CARD. " + '\n');
			}
		}
		return cardPlayed;
	}

	// precondition: the card must not hold a null value (player cannot choose number greater than those displayed.
	// postcondition: return a true or false depending on whether or not the card matches suit or value.
	public static boolean checkValidity(Card card, Card topCard) {
		if(card.getValue() == topCard.getValue() || card.getSuit().equals(topCard.getSuit()) || card.getValue() == 8)
			return true;
		else
			return false;
	}

	// precondition: none.
	// postcondition: computer returns the "best" card move. 
	public Card computerPlay(Player computer, Player player, Card topCard, Deck deck) {
		/* Note: this method will first determine which cards are playable in the 
		 * computer's hand. Playable cards can be played due to EITHER suit or number. 
		 * Among a selection of multiple suits or numbers, choosing a card with multiple
		 * suits is more advantageous than choosing a card with multiple numbers.
		 */
		Card cardPlayed = new Card(0, "Marina Rogers supplied a few ideas in this portion of the game (and wants recognition and maybe some extra credit)");
		Card[] hand = computer.getHand();
		boolean over = false;
		int dealtCards = 0;

		while(!over) {
			/* New array that will hold the locations of playable cards in my hand,
			 * so that later, if playable[i] holds a value greater than 0, this means
			 * hand[i] will be a playable card. Playable card locations will have 1 
			 * in the location of playableTracker.
			 */
			int[] playableTracker = trackPlayableCards(hand, topCard);

			/* We then need to confirm which suits are the most abundant in the hand.
			 * To do this, we use a new array which each spot corresponding to a suit.
			 * 0 - clubs
			 * 1 - spades
			 * 2 - hearts 
			 * 3 - diamonds
			 */
			int[] suitTracker = trackSuits(hand);
			/* Save which suit appears the most. */
			String mostSuit = getMostSuits(suitTracker, hand);
			int cardPlayedLocation = 0;

			if(checkPlayable(playableTracker)) {
				/* If the suit that appears the most frequently is also the suit of the top card, then go ahead and play the first card that has that suit. */
				if(mostSuit.equals(topCard.getSuit())) {
					for(int i = 0; i < playableTracker.length; i++) {
						if(hand[i] != null) {
							if(playableTracker[i] == 1 && hand[i].getSuit().equals(mostSuit)) {
								cardPlayed = hand[i];
								cardPlayedLocation = i;
								over = true;
							}
						}
					}
				}

				/* If not, then just play the first playable card that appears. */
				else {
					for(int i = 0; i < playableTracker.length; i++) {
						if(hand[i] != null && playableTracker[i] == 1) {
							cardPlayed = hand[i];
							cardPlayedLocation = i;
							over = true;
						}
					}
				}

				/* Take this card out from the computer's hand. */
				computer.subtract(cardPlayedLocation);
			}
			else {
				/* Make sure that the total number of cards does not surpass the deck. */
				if(deck.getTop() == 52)
					over = true;
				else {
					computer.add(deck.deal());
					dealtCards++;
				}
			}
		}
		if(dealtCards != 0)
			System.out.println("COMPUTER DREW " + dealtCards + " CARD(S)");
		System.out.println("COMPUTER PLAYED: " + cardPlayed);
		System.out.println("COMPUTER HAS " + computer.getHighNum() + " CARDS LEFT.");
		return cardPlayed;
	}


	// precondition: none.
	// postcondition: return a true or false to check if there are any playable cards.
	public boolean checkPlayable(int[] playableTracker) {
		for(int i = 0; i < playableTracker.length; i++) {
			if(playableTracker[i] > 0)
				return true;
		}
		return false;
	}

	// precondition: none.
	// postcondition: returns an int[] array storing the locations of playable cards.
	public int[] trackPlayableCards(Card[] hand, Card topCard) {
		int[] playableTracker = new int[hand.length];
		for(int i = 0; i < hand.length; i++) {
			if(hand[i] != null && checkValidity(hand[i], topCard)) {
				playableTracker[i]++;
			}
		}
		return playableTracker;
	}

	// precondition: none.
	// postcondition: return an int[] array tracking the number of each suit.
	public int[] trackSuits(Card[] hand) {
		int[] suitTracker = new int[hand.length];
		for(int i = 0; i < hand.length; i++) {
			if(hand[i] != null) {
				if(hand[i].getSuit().equals("clubs")) {
					suitTracker[0]++;
				}
				else if(hand[i].getSuit().equals("spades")) {
					suitTracker[1]++;
				}
				else if(hand[i].getSuit().equals("hearts")) {
					suitTracker[2]++;
				}
				else if(hand[i].getSuit().equals("diamonds")) {
					suitTracker[3]++;
				}
			}
		}
		return suitTracker;
	}

	// precondition: none.
	// postcondition: returns the suit that appears most frequently.
	public String getMostSuits(int[] suitTracker, Card[] hand) {
		int greatest = 0;
		int tracker = 5;
		String mostSuit = " ";

		/* Store the location of the most frequently occurring suit in an array. */
		for(int i = 0; i < suitTracker.length; i++) {
			if(suitTracker[i] != 0 && suitTracker[i] >= greatest) {
				tracker = i;
				greatest = suitTracker[i];
			}
		}

		/* Return the most frequently occurring suit according to the tracking array's corresponding suit. */
		if(tracker == 0) {
			mostSuit = "clubs";
		}
		else if(tracker == 1) {
			mostSuit = "spades";
		}
		else if(tracker == 2) {
			mostSuit = "hearts";
		}
		else if(tracker == 3) {
			mostSuit = "diamonds";
		}

		return mostSuit;
	}
}
© 2017 GitHub, Inc.
Terms
Privacy
Security
Status
Help
Contact GitHub
API
Training
Shop
Blog
About
