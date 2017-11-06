import java.util.*;

/**
 * Created by austinzhang on 3/23/17.
 * <p>
 * This strategy collects data and uses the data to deduce what the cards are
 * This strategy does not make any guesses, all the information it uses in its decision making process
 * is fact and the strategy does not use probability in its decisions eg. Only accuses when it is positive of the cards
 * This strategy does not use any subterfuge to throw off opposing players. eg. suggest a card it already has to mess with others
 * <p>
 * <p>
 * <p>
 * For next strategy make a program that starts off by suggesting all the cards it has so dumb AI accuse early and are instantly eliminated
 * Once you figure out the best room, avoid it. Try to go to your own room. This is because when you go to the real room and start suggesting, people have a high chance of guessing the correct solution
 * TAKE INTO ACCOUNT WHO YOU'RE MOVING, SUGGEST PEOPLE CLOSE TO THE ROOMS THAT COULD POSSIBLY CONTAIN ANSWERS IF YOU'RE NOT NEAR THE ANSWER ROOM.
 */
public class AustinZOldStrat implements Strategy {
    private Suggestion firstSuggestion;
    private HashSet<Suggestion> allSuggestionsMade;

    private Suggestion currentBestSuggestion;
    private Game game;
    private int mySeat;
    private Card[] myCards;
    private HashSet<Card> fullDeck = new HashSet<Card>();

    private boolean accusing;

    private ArrayList<HashSet<Suggestion>> suggestionsDisproven;
    private ArrayList<HashSet<Card>> cardsPlayersHave;
    private ArrayList<HashSet<Card>> cardsPlayersDontHave;

    //The ratio of importance between the top and bottom
    private double roomCoeff;

    private AustinZDijkstraPathfinding pathfinder;

    public void setRoomCoeff(double roomCoeff) {
        this.roomCoeff = roomCoeff;
    }

    public AustinZOldStrat(double roomCoeff) {
        this.roomCoeff = roomCoeff;
    }

    public AustinZOldStrat() {
        roomCoeff = 10;
    }

    @Override
    public void init(Game game, int seat, Card[] cards) {
        this.game = game;
        this.mySeat = seat;
        this.myCards = cards;

        accusing = false;


        for (int people = 0; people < 6; people++) {
            fullDeck.add(new Card(Card.PERSON, people));
        }
        for (int weapons = 0; weapons < 6; weapons++) {
            fullDeck.add(new Card(Card.WEAPON, weapons));
        }
        for (int rooms = 0; rooms < 9; rooms++) {
            fullDeck.add(new Card(Card.ROOM, rooms));
        }


        //Initialize the lists
        suggestionsDisproven = new ArrayList<HashSet<Suggestion>>();
        cardsPlayersHave = new ArrayList<HashSet<Card>>();
        cardsPlayersDontHave = new ArrayList<HashSet<Card>>();


        //Construct a HashSet for each player in these lists
        for (int i = 0; i < game.getNumPlayers(); i++) {
            suggestionsDisproven.add(new HashSet<Suggestion>());
            cardsPlayersHave.add(new HashSet<Card>());
            cardsPlayersDontHave.add(new HashSet<Card>());
        }

        for (int i = 0; i < cards.length; i++) {
            cardsPlayersHave.get(seat).add(cards[i]);
        }

        for (Card card : fullDeck) {
            if (!cardsPlayersHave.get(seat).contains(card)) {
                cardsPlayersDontHave.get(seat).add(card);
            }
        }

        updateCardsPlayersDontHave();

        pathfinder = new AustinZDijkstraPathfinding();

        currentBestSuggestion = null;

        allSuggestionsMade = new HashSet<Suggestion>();
    }


    //Best action to take
    @Override
    public int getActionType(boolean canStay, boolean canUseSecretPassage) {
        currentBestSuggestion = bestSuggestion();

        //System.out.println("Best Suggestion: " + currentBestSuggestion.toString());
        //System.out.println("possible answers");
        //printPossibleAnswers();
        //System.out.println("disproven");
        //System.out.println(suggestionsDisproven.toString());
        //System.out.println("cardsPlayersHave");
        //System.out.println(cardsPlayersHave.toString());
        //System.out.println("don't have");
        //System.out.println(cardsPlayersDontHave.toString());


        //System.out.println(totSize);
        //System.out.println("Best Suggestion: " + suggestion.toString());

        if (shouldAccuse()) {
            accusing = true;
            return Strategy.ACCUSE;
        }

        //Currently always stay when you can - being able to
        if (canStay && currentBestSuggestion.getRoom() == game.getLocation(mySeat).getRoom()) {
            return Strategy.STAY;
        }

        boolean shouldUseSecretPassage = false;

        List<Location> path = pathfinder.findPath(game, mySeat, game.getLocation(mySeat), new Location(currentBestSuggestion.getRoom()));

        if (path != null && path.size() > 1 && path.get(1).isRoom()) {
            shouldUseSecretPassage = true;
        }

        if (canUseSecretPassage && shouldUseSecretPassage) {
            return Strategy.SECRET_PASSAGE;
        }

        return Strategy.ROLL;

    }


    //Returns the location of the move that's closest to the target room.
    @Override
    public Location move(int roll) {
        Set<Location> possibleMoves = game.getDestinations(game.getLocation(mySeat), roll);
        //System.out.println(game.getLocation(mySeat).toString());
        //System.out.println(game.getDestinations(game.getLocation(mySeat), roll).toString());
        //System.out.println("Target Room: " + Card.getRoomName(bestSuggestion().getRoom()));
        Location bestLocation;
        //int leastMoves = Integer.MAX_VALUE;

        /** SPEED UP **/ /*
        for (Location startLoc : possibleMoves) {
            int testedLocationNumMoves = pathfinder.shortestDistance(game, mySeat, startLoc, new Location(currentBestSuggestion.getRoom()));
            //System.out.println(startLoc.toString() + " " + testedLocationNumMoves);
            if (testedLocationNumMoves < leastMoves) {
                bestLocation = startLoc;
                leastMoves = testedLocationNumMoves;
            }
        }
        */

        bestLocation = pathfinder.bestMove(game, mySeat, game.getDestinations(game.getLocation(mySeat), roll), currentBestSuggestion.getRoom());

        if (bestLocation == null) {

            bestLocation = (Location) game.getDestinations(game.getLocation(mySeat), roll).toArray()[0];

            /*
            System.out.println(game.getDestinations(game.getLocation(mySeat), roll).toString());
            for (Location loc : game.getDestinations(game.getLocation(mySeat), roll)) {
                System.out.println(pathfinder.shortestDistance(game, mySeat, loc, new Location(currentBestSuggestion.getRoom())));
            }
            throw new RuntimeException("MOVE METHOD IN STRATEGY IS BROKEN FIX IT");
            */
        }


        return bestLocation;


    }


    //first differentiate between accusations and suggestions
    //decide suggestion based on what cards you don't yet know and what card will answer the most suggestions
    @Override
    public Suggestion getSuggestion() {
        //Have to suggest room you're in so that part's easy

        Suggestion suggestion = new Suggestion(currentBestSuggestion.getPerson(), game.getLocation(mySeat).getRoom(), currentBestSuggestion.getWeapon());

        //System.out.println("Real Suggestion: " + suggestion.toString());

        allSuggestionsMade.add(suggestion);

        return suggestion;
    }


    /**
     * For now just try to reveal a random non-room cards first since rooms are the hardest to figure out.
     * In the future we'll keep track of the what other players know and try to withhold as much information from them as possible.
     */
    @Override
    public Card revealCard(int suggester, Suggestion suggestion) {
        allSuggestionsMade.add(suggestion);

        ArrayList<Card> possibleCardsToReveal = new ArrayList<Card>();
        for (Card myCard : myCards) {
            if (myCard.equals(new Card(Card.PERSON, suggestion.getPerson()))
                    || myCard.equals(new Card(Card.WEAPON, suggestion.getWeapon()))
                    || myCard.equals(new Card(Card.ROOM, suggestion.getRoom()))) {
                possibleCardsToReveal.add(myCard);
            }
        }

        if (Math.random() > .1) {
            for (Card aPossibleCardToReveal : possibleCardsToReveal) {
                if (aPossibleCardToReveal.isRoom()) {
                    return aPossibleCardToReveal;
                }
            }
        }

        ArrayList<Integer> playersPassed = playersBetweenSuggesterAndRevealer(suggester, mySeat);

        //System.out.println("Players Passed" + playersPassed.toString());

        for (int playerPassed : playersPassed) {
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.ROOM, suggestion.getRoom()));
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.WEAPON, suggestion.getWeapon()));
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.PERSON, suggestion.getPerson()));
        }
        boolean keepLooping = true;

        int counter = 0;

        while (keepLooping) {
            keepLooping = updateCardsPlayersDontHave() || updateCardsPlayersHave();

            counter++;
            if (counter > 20) {
                System.out.println("STUCK IN INFINITE LOOP IN SUGGESTION MADE");
            }
        }


        return possibleCardsToReveal.get(0);

    }

    @Override
    public Suggestion getAccusation() {
        if (accusing) {
            return accusation();
        } else {

            Suggestion toBeSuggested = new Suggestion(currentBestSuggestion.getPerson(), game.getLocation(mySeat).getRoom(), currentBestSuggestion.getWeapon());

            boolean ownCard = false;
            for (Card card : myCards) {
                if (toBeSuggested.contains(card)) {
                    ownCard = true;
                } else {
                    for (HashSet<Card> aCardPlayersDontHave : cardsPlayersDontHave) {
                        aCardPlayersDontHave.add(card);
                    }
                }
            }

            if (ownCard) {
                return null;
            }

            return toBeSuggested;
        }
    }

    @Override
    public void suggestionMade(int suggester, Suggestion suggestion, int revealer) {
        allSuggestionsMade.add(suggestion);

        ArrayList<Integer> playersPassed = playersBetweenSuggesterAndRevealer(suggester, revealer);

        //System.out.println("Players Passed" + playersPassed.toString());

        for (int playerPassed : playersPassed) {
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.ROOM, suggestion.getRoom()));
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.WEAPON, suggestion.getWeapon()));
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.PERSON, suggestion.getPerson()));
        }

        if (revealer != -1) {
            suggestionsDisproven.get(revealer).add(suggestion);
        }

        //System.out.println("revealer" + revealer);

        boolean keepLooping = true;

        int counter = 0;

        while (keepLooping) {
            keepLooping = updateCardsPlayersDontHave() || updateCardsPlayersHave();

            counter++;
            if (counter > 20) {
                System.out.println("STUCK IN INFINITE LOOP IN SUGGESTION MADE");
            }
        }
    }

    @Override
    public void cardRevealed(Suggestion suggestion, int revealer, Card card) {
        allSuggestionsMade.add(suggestion);
        ArrayList<Integer> playersPassed = playersBetweenSuggesterAndRevealer(mySeat, revealer);

        for (int playerPassed : playersPassed) {
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.ROOM, suggestion.getRoom()));
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.WEAPON, suggestion.getWeapon()));
            cardsPlayersDontHave.get(playerPassed).add(new Card(Card.PERSON, suggestion.getPerson()));
        }

        if (revealer != -1) {
            cardsPlayersHave.get(revealer).add(card);
        }

        boolean keepLooping = true;

        int counter = 0;

        while (keepLooping) {
            keepLooping = updateCardsPlayersDontHave() || updateCardsPlayersHave();

            counter++;
            if (counter > 20) {
                //System.out.println("STUCK IN INFINITE LOOP IN CARDREVEALED");
            }
        }
    }

    @Override
    public void accusationMade(int accuser, Suggestion accusation) {
        //suggestionsDisproven.get(accuser).add(accusation);
    }

    /**
     * HELPER CLASSES
     **/


    //returns array of seat int values that are between the suggester and the revealer
    private ArrayList<Integer> playersBetweenSuggesterAndRevealer(int suggester, int revealer) {
        //System.out.println("Suggester: " + suggester);
        //System.out.println("Revealer: " + revealer);

        int start = suggester;

        ArrayList<Integer> playersBetween = new ArrayList<Integer>();

        if (revealer == -1) {
            for (int i = 0; i < game.getNumPlayers(); i++) {
                if (i != suggester) {
                    playersBetween.add(i);
                }
            }
        } else {

            start = (start + 1) % game.getNumPlayers();

            while (start != revealer) {
                playersBetween.add(start);
                start = (start + 1) % game.getNumPlayers();
            }
        }

        //System.out.println(playersBetween.toString());
        return playersBetween;
    }

    //Loop between updateCardsPlayersDontHave and updateCardsPlayersHave until neither one continues to change

    //1st

    //If a player has a specific card another player cannot have the same card, so add that card to the other player's cardsPlayersDontHave deck.
    private boolean updateCardsPlayersDontHave() {
        boolean anythingChanged = false;

        for (int p = 0; p < cardsPlayersDontHave.size(); p++) {
            for (int q = 0; q < cardsPlayersHave.size(); q++) {
                if (p != q) {
                    for (Card card : cardsPlayersHave.get(q)) {
                        if (cardsPlayersDontHave.get(p).add(card)) {
                            anythingChanged = true;
                        }
                    }
                }
            }
        }
        return anythingChanged;
    }

    //2nd
    //If a player is known to not have 2 cards that are a part of a 3 card suggestion that the player disproved then
    // the player must have the third card in the suggestion that they disproved.

    //returns true if anything changed in the decks
    private boolean updateCardsPlayersHave() {
        boolean anythingChanged = false;

        for (int i = 0; i < suggestionsDisproven.size(); i++) {

            HashSet<Suggestion> suggestionsForPlayer = suggestionsDisproven.get(i);

            Iterator<Suggestion> suggestionIterator = suggestionsForPlayer.iterator();

            while (suggestionIterator.hasNext()) {
                Suggestion currentSuggestion = suggestionIterator.next();

                Card room = new Card(Card.ROOM, currentSuggestion.getRoom());
                Card person = new Card(Card.PERSON, currentSuggestion.getPerson());
                Card weapon = new Card(Card.WEAPON, currentSuggestion.getWeapon());

                if (cardsPlayersHave.get(i).contains(room) || cardsPlayersHave.get(i).contains(person) || cardsPlayersHave.get(i).contains(weapon)) {
                    suggestionIterator.remove();
                } else {

                    boolean noRoom = false;
                    boolean noPerson = false;
                    boolean noWeapon = false;

                    if (cardsPlayersDontHave.get(i).contains(room)) {
                        noRoom = true;
                    }

                    if (cardsPlayersDontHave.get(i).contains(person)) {
                        noPerson = true;
                    }

                    if (cardsPlayersDontHave.get(i).contains(weapon)) {
                        noWeapon = true;
                    }

                    if (noRoom && noPerson && noWeapon) {
                        System.out.println(currentSuggestion.toString());
                        System.out.println(cardsPlayersDontHave.get(i).toString());
                        System.out.println(cardsPlayersHave.get(i).toString());
                        throw new RuntimeException("UPDATE CARDS PLAYERS HAVE IS BROKEN");
                    }

                    if (!noRoom && noPerson && noWeapon) {
                        //System.out.println("Deduced " + room.toString());
                        anythingChanged = cardsPlayersHave.get(i).add(room);
                        suggestionIterator.remove();
                    }

                    if (noRoom && !noPerson && noWeapon) {
                        //System.out.println("Deduced " + person.toString());
                        anythingChanged = cardsPlayersHave.get(i).add(person);
                        suggestionIterator.remove();
                    }

                    if (noRoom && noPerson && !noWeapon) {
                        //System.out.println("Deduced " + weapon.toString());
                        anythingChanged = cardsPlayersHave.get(i).add(weapon);
                        suggestionIterator.remove();
                    }
                }
            }
        }

        return anythingChanged;
    }

    //Returns true if the player can deduce the answer to the game using the information it has.
    private boolean shouldAccuse() {
        int numberOfKnownCards = 0;

        for (HashSet<Card> cardsPlayerHas : cardsPlayersHave) {
            numberOfKnownCards += cardsPlayerHas.size();
        }

        //there are 21 total cards so if you know 18 than there are only 3 you don't know and you can deduce the answer.
        return numberOfKnownCards == 18;
    }

    //return the accusation you make
    private Suggestion accusation() {
        HashSet<Card> allKnownCards = new HashSet<Card>();

        for (HashSet<Card> player : cardsPlayersHave) {
            allKnownCards.addAll(player);
        }


        //Declare and Initialize all cards and add all possible values to allCards
        HashSet<Card> allCards = fullDeck;
        for (Card knownCard : allKnownCards) {
            allCards.remove(knownCard);
        }

        Card room = null;
        Card weapon = null;
        Card person = null;

        for (Card card : allCards) {

            if (card.isRoom()) {
                room = card;
            } else if (card.isWeapon()) {
                weapon = card;
            } else if (card.isPerson()) {
                person = card;
            }
        }

        return new Suggestion(person.getValue(), room.getValue(), weapon.getValue());
    }

    private boolean isCardInSetList(Card card, ArrayList<HashSet<Card>> setList) {
        for (HashSet<Card> set : setList) {
            if (set.contains(card)) {
                return true;
            }
        }

        return false;
    }

    private Suggestion bestSuggestion() {
        Card person = null;
        Card weapon = null;
        Card room;
        //First you pick the cards that are unknown

        //Create deck of unknown cards
        HashSet<Card> unknownCards = fullDeck;

        for (HashSet<Card> hashSetOfCards : cardsPlayersHave) {
            for (Card card : hashSetOfCards) {
                unknownCards.remove(card);
            }
        }


        //First pick the room card, this one is the most complicated.


        //find how far each room is away from you personally and assign it a value

        //indexes are the ints of rooms ex. ballroom = 0
        int[] distanceToRooms;

        /*
        //First find the distance to each of the rooms
        for (int i = 0; i < distanceToRooms.length; i++) {
            distanceToRooms[i] = pathfinder.shortestDistance(game, mySeat, game.getLocation(mySeat), new Location(i));
        }

        */

        distanceToRooms = pathfinder.shortestDistanceToRooms(game, mySeat, game.getLocation(mySeat));


        //System.out.println(Arrays.toString(distanceToRooms));

        //then find out how far each room card is away from you turn wise
        //0 = your card 1 = the person that goes after you etc. 6 = unknown card
        double[] distanceToCards = new double[9];

        for (int i = 0; i < 9; i++) {

            for (Card card1 : unknownCards) {
                if (card1.isRoom() && card1.getValue() == i) {
                    distanceToCards[i] = cardsPlayersHave.size();
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int indexOfKnownCard = 0; indexOfKnownCard < cardsPlayersHave.size(); indexOfKnownCard++) {
                for (Card cardInHashSet : cardsPlayersHave.get(indexOfKnownCard)) {
                    if (cardInHashSet.isRoom() && cardInHashSet.getValue() == i) {
                        int distanceFromMe = indexOfKnownCard - mySeat - 1;
                        if (distanceFromMe < 0) {
                            distanceFromMe = game.getNumPlayers() - mySeat + indexOfKnownCard - 1;
                        }

                        if (distanceFromMe == -1) {
                            distanceFromMe = game.getNumPlayers() - 1;
                        }

                        distanceToCards[i] = distanceFromMe;

                    }
                }
            }
        }

        /** THIS IS WHAT NEEDS TO BE OPTIMIZED **/
        double[] scoresForRooms = new double[9];

        for (int i = 0; i < 9; i++) {
            /** EQUATION FOR CALCULATING HOW GOOD A ROOM IS, roomCoeff needs to be optimized
             * Room coeff basically changes the balance between how much the distance to cards and distance to rooms matter
             * **/
            scoresForRooms[i] = roomCoeff * distanceToCards[i] - distanceToRooms[i];
        }

        int indexOfHighestScore = 0;
        double highestScore = scoresForRooms[0];

        double highestCardDist = 0;
        int highestCardIndex = 0;

        for (int i = 0; i < scoresForRooms.length; i++) {
            if (distanceToCards[i] > highestCardDist) {
                highestCardIndex = i;
                highestCardDist = distanceToCards[i];
            }

            if (scoresForRooms[i] > highestScore) {
                indexOfHighestScore = i;
                highestScore = scoresForRooms[i];
            }
        }

        //System.out.println("Scores for rooms");
        //System.out.println(Arrays.toString(scoresForRooms));

        room = new Card(Card.ROOM, indexOfHighestScore);


        HashMap<Card, Integer> cardNumPairs = new HashMap<Card, Integer>();
        for (HashSet<Suggestion> suggestions : suggestionsDisproven) {
            for (Suggestion suggestion : suggestions) {
                Card p = new Card(Card.PERSON, suggestion.getPerson());
                Card r = new Card(Card.ROOM, suggestion.getRoom());
                Card w = new Card(Card.WEAPON, suggestion.getWeapon());

                if (!cardNumPairs.containsKey(p)) {
                    cardNumPairs.put(p, 1);
                } else {
                    cardNumPairs.put(p, cardNumPairs.get(p) + 1);
                }

                if (!cardNumPairs.containsKey(w)) {
                    cardNumPairs.put(w, 1);
                } else {
                    cardNumPairs.put(w, cardNumPairs.get(w) + 1);
                }

                if (!cardNumPairs.containsKey(r)) {
                    cardNumPairs.put(r, 1);
                } else {
                    cardNumPairs.put(r, cardNumPairs.get(r) + 1);
                }
            }
        }
        int mostSuggestionsPerson = Integer.MIN_VALUE;
        int mostSuggestionsWeapon = Integer.MIN_VALUE;
        //First look through the cards you don't yet know
        for (Card card : unknownCards) {
            Integer mostSuggestions = cardNumPairs.get(card);

            if (card.getType() == Card.PERSON) {
                if (mostSuggestions != null && mostSuggestions > mostSuggestionsPerson) {
                    person = card;
                    mostSuggestionsPerson = mostSuggestions;
                } else {
                    if (person == null) {
                        person = card;
                    }
                }
            } else if (card.getType() == Card.WEAPON) {
                if (mostSuggestions != null && mostSuggestions > mostSuggestionsWeapon) {
                    weapon = card;
                    mostSuggestionsWeapon = mostSuggestions;
                } else {
                    if (weapon == null) {
                        weapon = card;
                    }
                }
            }
        }

        if (person != null && weapon != null) {
            //System.out.println(new Suggestion(person.getValue(), room.getValue(), weapon.getValue()).toString());
            return new Suggestion(person.getValue(), room.getValue(), weapon.getValue());
        }

        //Then look through cards starting from the person who's turn is right before yours

        int startingSeat = (mySeat + 1) % cardsPlayersHave.size();

        for (int i = startingSeat; i != mySeat; i = (i + 1) % cardsPlayersHave.size()) {
            for (Card card : cardsPlayersHave.get(i)) {
                if (person == null && card.getType() == Card.PERSON) {
                    person = card;
                }
                if (weapon == null && card.getType() == Card.WEAPON) {
                    weapon = card;
                }
            }
        }

        if (person != null && weapon != null) {
            //System.out.println(new Suggestion(person.getValue(), room.getValue(), weapon.getValue()).toString());
            return new Suggestion(person.getValue(), room.getValue(), weapon.getValue());
        }

        //If you still can't find the cards you need, (this basically means that you know
        //everything except for the rooms, start suggesting your own cards for weapons and people)

        for (Card card : cardsPlayersHave.get(mySeat)) {
            if (person == null && card.getType() == Card.PERSON) {
                person = card;
            }
            if (weapon == null && card.getType() == Card.WEAPON) {
                weapon = card;
            }

            if (person != null && weapon != null) {
                //System.out.println(new Suggestion(person.getValue(), room.getValue(), weapon.getValue()).toString());
                return new Suggestion(person.getValue(), room.getValue(), weapon.getValue());
            }
        }

        throw new RuntimeException("BEST SUGGESTION CODE IS BROKEN IN BASIC STRATEGY");

    }


    private int cardsOfTypeInSetList(ArrayList<HashSet<Card>> cardList, int type) {
        int numCards = 0;
        for (HashSet<Card> cardSet : cardList) {
            for (Card card : cardSet) {
                if (card.getType() == type) {
                    numCards++;
                }
            }
        }
        return numCards;
    }

    private void printPossibleAnswers() {
        HashSet<Card> allAnswers = fullDeck;
        for (HashSet<Card> cards : cardsPlayersHave) {
            for (Card card : cards) {
                allAnswers.remove(card);
            }
        }

        System.out.println(allAnswers.toString());
    }

    @Override
    public String toString() {
        return "Austin: Old";// + Card.getPersonName(game.getPerson(mySeat));
    }

}
