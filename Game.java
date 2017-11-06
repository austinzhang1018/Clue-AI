import java.util.*;

public class Game {
    public static void main(String[] args) {

        totGames = 0;
        numTurnsTot = 0;
/*
        Strategy[] strategies = new Strategy[]{new AustinZBasicStrategy(new int[]{2029, 489, 3, 31, 3}), new MrKeithRandomStrategy(), new MrKeithRandomStrategy()};

        //playGame(strategies);
        //watchGame(strategies);
        invisibleGame(strategies);
        */

        Strategy[] strategies = new Strategy[]{new AustinZBasicStrategy(new int[]{4244, 303, 416, 26, 3}), new AustinZBasicStrategy(new int[]{4244, 303, 416, 26, 3}), new AustinZBasicStrategy(new int[]{4244, 303, 416, 26, 3}), new AustinZBasicStrategy(new int[]{4244, 303, 416, 26, 3}), new AustinZBasicStrategy(new int[]{4244, 303, 416, 26, 3})};

        playGame(strategies);
        //watchGame(strategies);
        //invisibleGame(strategies);

        /*
        strategies = new Strategy[]{new AustinZBasicStrategy(new int[]{1449, 10, 3, 34, 4}), new MrKeithRandomStrategy(), new MrKeithRandomStrategy()};

        //playGame(strategies);
        //watchGame(strategies);
        invisibleGame(strategies);

        strategies = new Strategy[]{new AustinZBasicStrategy(new int[]{1449, 10, 3, 34, 5}), new AustinZOldStrat(), new MrKeithRandomStrategy()};

        //playGame(strategies);
        //watchGame(strategies);
        invisibleGame(strategies);

        strategies = new Strategy[]{new AustinZBasicStrategy(new int[]{1449, 10, 3, 34, 6}), new AustinZOldStrat(), new MrKeithRandomStrategy()};

        //playGame(strategies);
        //watchGame(strategies);
        invisibleGame(strategies);
        */
    }

    //user plays against given computer players
    public static void playGame(Strategy[] opponents) {
        Display display = new Display(new HumanView());

        HumanStrategy human = new HumanStrategy();

        Strategy[] strategies = new Strategy[opponents.length + 1];
        for (int i = 0; i < opponents.length; i++)
            strategies[i] = opponents[i];
        strategies[opponents.length] = human;

        Game game = new Game(strategies, display);

        human.setDisplay(display);

        game.play();
    }

    //computer players only; with display
    public static void watchGame(Strategy[] strategies) {
        Display display = new Display(new EmptyView());
        Game game = new Game(strategies, display);
        game.play();
    }

    //computer players only; no display
    public static void invisibleGame(Strategy[] strategies) {
        Map<Strategy, Integer> winMap = new HashMap<Strategy, Integer>();
        for (Strategy s : strategies)
            winMap.put(s, 0);
        for (int i = 0; i < 5000; i++) {
            Game game = new Game(strategies, null);
            Strategy winner = game.play();
            //System.out.println(winner.toString());
            winMap.put(winner, winMap.get(winner) + 1);
            if (i % 100 == 99) {
                System.out.println(winMap);
            }
        }
        System.out.println(winMap);
    }

    private static final boolean DEBUG = false;
    private static final int DELAY = 0;

    //given room index, returns room connected to it by secret passage
    public static final int[] SECRET_PASSAGES = {
            -1, Card.LOUNGE, -1,
            -1, Card.KITCHEN, -1,
            Card.CONSERVATORY, -1, Card.STUDY};

    private Player[] players;  //index is seat number
    private Board board;
    private Suggestion solution;
    private int currentSeat;
    private Display display;
    private int numTurns;
    private int humanSeat;
    public static int numTurnsTot;
    public static int totGames;

    public Game(Strategy[] givenStrategies, Display display) {
        this.display = display;
        numTurns = 0;

        //create array here so we can use players.length. player objects created later.
        players = new Player[givenStrategies.length];

        Strategy[] strategies = new Strategy[players.length];
        fillArrayWith(strategies, givenStrategies);

        Integer[] people = new Integer[players.length];
        fillArrayWith(people, new Integer[]{
                Card.SCARLET, Card.MUSTARD, Card.WHITE,
                Card.GREEN, Card.PEACOCK, Card.PLUM});

        //pick solution
        solution = new Suggestion((int) (Math.random() * 6),
                (int) (Math.random() * 9),
                (int) (Math.random() * 6));

        ArrayList<Card> deck = new ArrayList<Card>();
        //people
        for (int i = 0; i < 6; i++) {
            if (solution.getPerson() != i)
                deck.add(new Card(Card.PERSON, i));
        }
        //rooms
        for (int i = 0; i < 9; i++) {
            if (solution.getRoom() != i)
                deck.add(new Card(Card.ROOM, i));
        }
        //weapons
        for (int i = 0; i < 6; i++) {
            if (solution.getWeapon() != i)
                deck.add(new Card(Card.WEAPON, i));
        }

        ArrayList<Card>[] hands = (ArrayList<Card>[]) new ArrayList[players.length];  //unavoidable warning
        for (int i = 0; i < hands.length; i++)
            hands[i] = new ArrayList<Card>();
        int current = 0;
        while (deck.size() > 0) {
            hands[current].add(deck.remove((int) (Math.random() * deck.size())));
            current = (current + 1) % players.length;
        }

        //first in array is starting location for first person (character), etc.
        Location[] startingLocs = {new Location(9, 24), new Location(14, 24), new Location(23, 17),
                new Location(23, 7), new Location(16, 0), new Location(0, 5), new Location(0, 18)};
        //one of these locs must be wrong, because should only be 6.

        //create Player objects
        for (int seat = 0; seat < players.length; seat++) {
            Card[] hand = new Card[hands[seat].size()];
            for (int i = 0; i < hand.length; i++)
                hand[i] = hands[seat].get(i);
            players[seat] = new Player(this, seat, strategies[seat], people[seat], startingLocs[people[seat]], hand);
        }

        //create board
        board = new Board();

        currentSeat = 0;

        for (int seat = 0; seat < players.length; seat++)
            players[seat].init();

        humanSeat = -1;
        for (int i = 0; i < strategies.length; i++) {
            if (strategies[i] instanceof HumanStrategy)
                humanSeat = i;
        }

        if (display != null) {
            Card[][] allCards = new Card[players.length][0];
            for (int i = 0; i < players.length; i++)
                allCards[i] = players[i].getCards();

            display.setGame(this, strategies, solution, allCards);
        }
    }

    //randomly stores elements of choices in given target array
    private static <E> void fillArrayWith(E[] target, E[] choices) {
        ArrayList<E> choiceList = new ArrayList<E>();
        for (E choice : choices)
            choiceList.add(choice);
        for (int i = 0; i < target.length; i++) {
            int r = (int) (Math.random() * choiceList.size());
            target[i] = choiceList.remove(r);
        }
    }

    public Strategy play() {
        while (true) {
            if (players[currentSeat].isAlive()) {
                numTurns++;

                if (numTurns == 2000) {
                    System.out.println("Infinite Looping");
                    return null;
                }

                debug(players[currentSeat] + "'s turn");
                if (display != null)
                    display.update();

                if (display != null && humanSeat == -1) {
                    //watching a game
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }

                boolean canStay = false;
                boolean canUseSecretPassage = false;

                if (players[currentSeat].getLocation().isRoom()) {
                    canStay = players[currentSeat].recentlyTransported();
                    int room = players[currentSeat].getLocation().getRoom();
                    canUseSecretPassage = (getSecretPassage(room) != -1);
                }

                //returns ACCUSE, STAY, ROLL, or SECRET_PASSAGE
                int action = players[currentSeat].getStrategy().getActionType(canStay, canUseSecretPassage);

                if (action == Strategy.ACCUSE) {
                    debug(players[currentSeat] + " chose to accuse");
                    int winner = handleAccusation(players[currentSeat].getStrategy().getAccusation());
                    if (winner != -1)
                        return players[winner].getStrategy();
                } else if (action == Strategy.STAY) {
                    if (!canStay)
                        throw new RuntimeException(players[currentSeat] + " cannot stay");
                    debug(players[currentSeat] + " chose to stay");
                    int winner = handleSuggestion();
                    if (winner != -1)
                        return players[winner].getStrategy();
                } else if (action == Strategy.SECRET_PASSAGE) {
                    if (!canUseSecretPassage)
                        throw new RuntimeException(players[currentSeat] + " cannot use secret passage");

                    int room = players[currentSeat].getLocation().getRoom();
                    int oppRoom = getSecretPassage(room);
                    debug(players[currentSeat] + " uses secret passage to " + Card.getRoomName(oppRoom));
                    players[currentSeat].setLocation(new Location(oppRoom));

                    if (display != null)
                        display.update();

                    int winner = handleSuggestion();
                    if (winner != -1)
                        return players[winner].getStrategy();
                } else if (action == Strategy.ROLL) {
                    int roll = (int) (Math.random() * 6) + 1;
                    debug(players[currentSeat] + " rolls a " + roll);
                    if (display != null) {
                        display.showDie(roll);
                    }

                    //ensure that it is possible for player to move
                    if (getDestinations(players[currentSeat].getLocation(), roll).size() != 0) {
                        //player can definitely move
                        Location loc = players[currentSeat].getStrategy().move(roll);  //assume everything is valid right now
                        debug(players[currentSeat] + " moves to " + loc);

                        if (!isValidMove(players[currentSeat].getLocation(), roll, loc))
                            throw new RuntimeException(players[currentSeat] + " tried to move to invalid location " + loc);

                        players[currentSeat].setLocation(loc);

                        if (display != null)
                            display.update();

                        // SUGGESTION
                        if (loc.isRoom()) {
                            int winner = handleSuggestion();
                            if (winner != -1)
                                return players[winner].getStrategy();
                        }
                    }
                } else
                    throw new RuntimeException(players[currentSeat] + " returned invalid action:  " + action);

                //indicate that current players was not recently transported anymore (even if just suggested self)
                players[currentSeat].setTransported(false);
            }

            currentSeat = (currentSeat + 1) % players.length;
        }
    }

    public int getNumPlayers() {
        return players.length;
    }

    public int getNumCards(int seat) {
        return players[seat].getNumCards();
    }

    public int getNumPlayersAlive() {
        int count = 0;
        for (int seat = 0; seat < players.length; seat++) {
            if (players[seat].isAlive())
                count++;
        }
        return count;
    }

    public int getCurrentSeat() {
        return currentSeat;
    }

    public int getPerson(int seat) {
        return players[seat].getPerson();
    }

    //returns current location of player at given seat (null if in a room)
    public Location getLocation(int seat) {
        return players[seat].getLocation();
    }

    public boolean isValid(Location loc) {
        return board.isValid(loc);
    }

    //returns seat number of player at given valid location (-1 if empty)
    //note: this method is inefficient
    public int getSeatAtLocation(Location loc) {
        for (int seat = 0; seat < players.length; seat++) {
            Location playerLoc = players[seat].getLocation();
            if (playerLoc != null && playerLoc.equals(loc))
                return seat;
        }
        return -1;
    }

    //given person, returns their seat (-1 if not playing)
    public int getSeatForPerson(int person) {
        for (int seat = 0; seat < players.length; seat++) {
            if (players[seat].getPerson() == person)
                return seat;
        }
        return -1;
    }

    public int getSecretPassage(int room) {
        return SECRET_PASSAGES[room];
    }

    public boolean isValidMove(Location start, int roll, Location dest) {
        return getDestinations(start, roll).contains(dest);
    }

    //post: returns if player is in room specified by suggestion and if there is such person and weapon as specified by the suggestion
    private boolean isValidSuggestion(int seat, Suggestion s) {
        return s.getPerson() > -1 && s.getPerson() < getNumPlayers() &&
                players[seat].getLocation().isRoom() && s.getRoom() == players[seat].getLocation().getRoom()
                && s.getWeapon() > -1 && s.getWeapon() < 6;
    }

    //suggestion must include current room; returns winner (-1 if no winner yet)
    private int handleSuggestion() //Tells the correct player to reveal a card
    {
        Suggestion s = players[currentSeat].getStrategy().getSuggestion();
        debug(players[currentSeat] + " suggested " + s);

        //test if suggestion is valid
        if (s.getRoom() != players[currentSeat].getLocation().getRoom())
            throw new RuntimeException("Seat " + currentSeat + " is in room " +
                    players[currentSeat].getLocation().getRoom() +
                    " but suggested room " + s.getRoom());

        //move suggested player
        int suggestedSeat = getSeatForPerson(s.getPerson());
        if (suggestedSeat != -1) {
            Location oldLoc = players[suggestedSeat].getLocation();
            Location newLoc = new Location(s.getRoom());
            players[suggestedSeat].setLocation(newLoc);
            players[suggestedSeat].setTransported(!newLoc.equals(oldLoc));
            if (display != null)
                display.update();
        }

        int seat = (currentSeat + 1) % players.length;
        int revealer = -1;
        Card revealedCard = null;
        while (seat != currentSeat && revealer == -1) {
            ArrayList<Card> heldCards = new ArrayList<Card>();
            for (Card card : players[seat].getCards()) {
                if (s.contains(card))
                    heldCards.add(card);
            }
            if (heldCards.size() > 0) {
                revealer = seat;
                revealedCard = players[seat].getStrategy().revealCard(currentSeat, s);

                if (!players[seat].hasCard(revealedCard))
                    throw new RuntimeException(players[seat] + " attemped to show card they don't hold:  " + revealedCard);
                if (!s.contains(revealedCard))
                    throw new RuntimeException(players[seat] + " attemped to show card that doesn't match suggestion:  " + revealedCard);
            } else
                seat = (seat + 1) % players.length;
        }

        //already notified revealer. now notify everyone else.
        for (seat = 0; seat < players.length; seat++) {
            if (seat == currentSeat)
                players[seat].getStrategy().cardRevealed(s, revealer, revealedCard);
            else if (seat != revealer)
                players[seat].getStrategy().suggestionMade(currentSeat, s, revealer);
        }

        if (display != null)
            display.suggested(currentSeat, s, revealer, revealedCard);

        if (revealer == -1) {
            //no one revealed
            //current player now has opportunity to accuse
            Suggestion accusation = players[currentSeat].getStrategy().getAccusation();
            if (accusation != null) {
                int winner = handleAccusation(accusation);
                if (winner != -1)
                    return winner;
            }
        }
        return -1;
    }

    //returns winner (-1 if no winner yet)
    private int handleAccusation(Suggestion accusation) {
        debug(players[currentSeat] + " accuses:  " + accusation);
        if (accusation.equals(solution)) {
            numTurnsTot += numTurns;
            totGames++;
            //System.out.println(((double)numTurnsTot)/totGames);
            debug(players[currentSeat] + " wins");
            if (display != null)
                display.accused(currentSeat, accusation, true);
            return currentSeat;
        } else {
            debug(players[currentSeat] + " loses");
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("FALSE ACCUSATION " + players[currentSeat].toString());
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            if (display != null)
                display.accused(currentSeat, accusation, false);
            players[currentSeat].die();

            //transport dead player into the room they accused in, so they're out of the way
            players[currentSeat].setLocation(new Location(accusation.getRoom()));

            if (display != null)
                display.update();

            for (int seat = 0; seat < players.length; seat++)
                players[seat].getStrategy().accusationMade(currentSeat, accusation);

            if (getNumPlayersAlive() == 1) {
                for (int seat = 0; seat < players.length; seat++) {
                    if (players[seat].isAlive()) {
                        debug(players[seat] + " is only player alive");

                        if (display != null)
                            display.onePlayerAlive(seat);

                        return seat;
                    }
                }
                throw new RuntimeException("should not get here");
            }
        }
        return -1;
    }

    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
            try {
                Thread.sleep(DELAY);
            } catch (Exception e) {
            }
        }
    }

    public String toString() {
        return toString(new HashSet<Location>());
    }

    //returns string version of board, with given locations highlighted
    public String toString(Set<Location> locs) {
        String s = "";
        for (int y = 0; y < 25; y++) {
            for (int x = 0; x < 24; x++) {
                Location loc = new Location(x, y);
                String ch = null;
                if (locs.contains(loc))
                    ch = "*";
                else if (!board.isValid(loc))
                    ch = "X";
                else {
                    for (int seat = 0; seat < players.length; seat++) {
                        if (players[seat].getLocation().equals(loc)) {
                            ch = "" + seat;
                            break;
                        }
                    }
                    if (ch == null)
                        ch = ".";
                }
                s += ch;
            }
            s += "\n";
        }

        for (int room = 0; room < 9; room++) {
            String contents = "";
            Location roomLoc = new Location(room);
            for (int seat = 0; seat < players.length; seat++) {
                if (players[seat].getLocation().equals(roomLoc))
                    contents += seat;
            }
            if (locs.contains(roomLoc))
                contents += "*";
            if (contents.length() > 0)
                s += Card.getRoomName(room) + ":  " + contents + "\n";
        }

        return s;
    }

    public boolean isFull(Location loc) {
        if (loc.isRoom())
            return false;
        for (Player player : players) {
            if (player.getLocation().equals(loc))
                return true;
        }
        return false;
    }

    public Set<Location> getValidAdjacentLocations(Location loc) {
        return board.getValidAdjacentLocations(loc);
    }

    //returns set of adjacent locations that are available move to
    //includes empty hallway squares and rooms (regardless of whether room is occupied)
    public Set<Location> getAvailableAdjacentLocations(Location loc) {
        Set<Location> locs = board.getValidAdjacentLocations(loc);
        Iterator<Location> it = locs.iterator();
        while (it.hasNext()) {
            if (isFull(it.next()))
                it.remove();
        }
        return locs;
    }

    //returns all locations that can be moved to with given roll
    public Set<Location> getDestinations(Location loc, int roll) {
        Set<Location> destinations = new HashSet<Location>();
        Set<Location> visited = new HashSet<Location>();
        visited.add(loc);
        findDestinations(loc, roll, destinations, visited);
        return destinations;
    }

    //loc is already in destinations
    private void findDestinations(Location loc, int roll, Set<Location> destinations,
                                  Set<Location> visited) {
        if (roll == 0 || (loc.isRoom() && visited.size() > 1))
            destinations.add(loc);
        else {
            for (Location adjLoc : getAvailableAdjacentLocations(loc)) {
                if (visited.add(adjLoc)) {
                    findDestinations(adjLoc, roll - 1, destinations, visited);
                    visited.remove(adjLoc);
                }
            }
        }
    }

    public int getNumTurns() {
        return numTurns;
    }
}