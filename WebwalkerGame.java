import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class WebwalkerGame {


    private static Corp corp = null;
    private static boolean debugMode = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please give a Corp deck file name");
            return;
        }
        String corpDeckFilename = args[0];
        if (args.length == 2) {
            debugMode = ("debug".equals(args[1]));
        }

        Map<String, List<CorpCard>> deckWithIdentity = buildDeck(corpDeckFilename);
        String identity = "";
        for (String s :deckWithIdentity.keySet()) {
            identity = s;
        }
        System.out.println("Corp is playing " + identity);
        corp = new Corp(identity, deckWithIdentity.get(identity), debugMode);
        if(corp.mulligan()) {
            System.out.println("Corp takes a mulligan");
            corp = new Corp(identity, deckWithIdentity.get(identity), debugMode);
        }

        int runnerClicks = 4;
        int runnerPoints = 0;
        int turns = 0;

        while (runnerPoints < 7 && corp.getCorpScore() < 7) {
            System.out.println("\n\n*** " + (turns++) + " ***\n"); 
            corp.resetClicks(corp.getMaxClicks());
            corp.preTurn();
            corp.drawCorpCards(1);
            System.out.println("Corp begins turn and performs mandatory draw");  
            while (corp.getClicks() > 0) {
                renderCorpBoard(corp);
                System.out.println("Click " + (corp.getMaxClicks()-corp.getClicks()+1) + ": ");
                if (corp.spendClick()) {
                    corp.removeClick();
                    corp.cleanupServers();
                    Scanner reader = new Scanner(System.in);
                    System.out.println("\nPress enter to continue");
                    reader.nextLine();
                }
            }
            System.out.println("Corp ends turn");
            corp.discardDownToLimit();
            
            
            runnerClicks = 3;
            renderCorpBoard(corp);
            while (runnerClicks > 0) {
                System.out.print("run>");
                String command = getStringFromUser();
                if ("rez ice".equals(command)) {
                    CorpCard ice = getIceCard(corp, false);
                    if (ice != null && !ice.isRezzed()) {
                        System.out.println("Additional Rez Cost: ");
                        int extraRezCost = getIntFromUser(0, 10);
                        if (corp.getDisplayCreds() >= (ice.getCost() + extraRezCost)) {
                            corp.spendReservedCreds(ice.getCost());
                            corp.spendCreds(extraRezCost);
                            ice.rez();
                            renderCorpBoard(corp);
                            System.out.println("Corp rezzes " + ice.getName() + "\n");
                        } else {
                            renderCorpBoard(corp);
                            System.out.println("Corp does not rez ice\n");
                        }
                    }
                } else if ("derez ice".equals(command)) {
                    CorpCard ice = getIceCard(corp, false);
                    if (ice != null && ice.isRezzed()) {
                        ice.derez();
                        corp.reserveCreds(ice.getCost());
                        renderCorpBoard(corp);
                        System.out.println("Corp forced to derez " + ice.getName());
                    }
                }else if ("trash ice".equals(command)) {
                    CorpCard ice = getIceCard(corp, true);
                } else if ("end turn".equals(command) || "end".equals(command)) {
                    runnerClicks = 0;
                } else if ("access server".equals(command)) {
                    System.out.println("Which server?");
                    int serverNumber = getIntFromUser(1, corp.getServers().size());
                    runnerPoints = accessCardsFromServer(corp.getServerByNumber(serverNumber - 1), runnerPoints);
                    boolean serverRemoved = corp.cleanupServer(serverNumber - 1);
                    String isSneakdoor = null;
                    if (serverNumber == 3 && corp.getRunnerCardByName("Sneakdoor Beta") != null) {
                        System.out.println("Was Sneakdoor Beta used?");
                        isSneakdoor = getStringFromUser();
                    }
                    if (isSneakdoor != null && "yes".equals(isSneakdoor)) {
                        corp.addServerAccessed(corp.getServerByNumber(0)); //archives is weak
                    } else if (!serverRemoved) {
                        corp.addServerAccessed(corp.getServerByNumber(serverNumber-1));
                    }
                    renderCorpBoard(corp);
                } else if ("expose asset".equals(command)) {
                    if (corp.getServers().size() > 3) {
                        System.out.println("Which server?");
                        int serverNumber = getIntFromUser(4, corp.getServers().size());
                        CorpCard asset = corp.getServerByNumber(serverNumber-1).getAsset();
                        System.out.println("Corp forced to expose " + asset.getActualName());
                    } else {
                        System.out.println("No installed assets to expose");
                    }
                } else if ("expose ice".equals(command)) {
                    CorpCard ice = getIceCard(corp, false);
                    System.out.println("Corp forced to expose " + ice.getActualName());
                } else if ("mill RnD".equals(command) || "mill rnd".equals(command)) {
                    corp.millRnD();
                    System.out.println("Corp forced to trash top card of RnD");
                } else if ("adjust creds".equals(command)) {
                    System.out.println("How many creds?");
                    int creds = getIntFromUser(-10,10);
                    corp.gainCreds(creds);
                    System.out.println("Corp gains " + creds + " creds.");
                } else if ("install program".equals(command)) {
                    System.out.println("What program?");
                    String cardName = getStringFromUser();
                    corp.addRunnerCard(cardName);
                } else if ("trash program".equals(command)) {
                    System.out.println("What program?");
                    String cardName = getStringFromUser();
                    corp.removeRunnerCard(cardName);
                } else if ("add virus".equals(command)) {
                    System.out.println("On which card?");
                    String cardName = getStringFromUser();
                    RunnerCard card = corp.getRunnerCardByName(cardName);
                    if (card == null) {
                        System.out.println("No card with that name exists.");
                    } else {
                        card.addVirusCounter();
                        System.out.println(card.getName() + " now has " + card.getVirusCounters() + " virus counters");
                    }
                } else if ("help".equals(command)) {
                    System.out.println("\n****** Help Menu ******");
                    System.out.println("Servers are numbered left to right, starting with 1.");
                    System.out.println("ICE is numberes from top to bottom, starting with 1.");
                    System.out.println("Valid commands:");
                    System.out.println("rez ice");
                    System.out.println("derez ice");
                    System.out.println("trash ice");
                    System.out.println("access server");
                    System.out.println("expose asset");
                    System.out.println("expose ice");
                    System.out.println("mill RnD");
                    System.out.println("adjust creds");
                    System.out.println("install program");
                    System.out.println("trash program");
                    System.out.println("add virus");
                    System.out.println("end turn\n");
                } else if ("^C".equals(command)) {
                    System.out.println("Command \"" + command + "\" not recognized.  Type \"help\" for list of valid commands.");
                } 
                else if (!"".equals(command)) {
                    System.out.println("Command \"" + command + "\" not recognized.  Type \"help\" for list of valid commands.");
                } 
            }
        }
        String winner = (runnerPoints > corp.getCorpScore()) ? "runner" : "corp";
        System.out.println("***** The " + winner + " wins the match! *****");
    }

    public static int getIntFromUser(int min, int max) {
        int intFromUser = -1;
        while (intFromUser == -1) {
            intFromUser = getIntFromUserSafe(min, max);
        }
        return intFromUser;
    }

    public static int getIntFromUserSafe(int min, int max) {
        try {
            Scanner reader = new Scanner(System.in);
            int val = reader.nextInt();
            if (max == 0) {
                return 0;
            }
            if (val > max || val < min) {
                System.out.println("Incorrect input: out of range " + min + " to " + max);
                return -1;
            } else {
                return val;
            }
        } catch (Exception e) {
            System.out.println("Incorrect input: must be integer");
        }
        return -1;
    }

    public static String getStringFromUser() {
        try {
            Scanner reader = new Scanner(System.in);
            String val = reader.nextLine();
            return val;
        } catch (Exception e) {
            System.out.println("Incorrect input");
        }
        return "";
    }

    public static int accessCardsFromServer(Server server, int runnerPoints) {
        System.out.println("How many cards?");
        int numberAccessed = getIntFromUser(0,99);

        if (!server.isRnD()) {
            Collections.shuffle(server.getAssets());
        }
        List<CorpCard> serverAssets = server.getAssets();
        List<CorpCard> cardsToTrash = new ArrayList<CorpCard>();
        List<CorpCard> cardsToSteal = new ArrayList<CorpCard>();
        numberAccessed = (numberAccessed > serverAssets.size()) ? serverAssets.size() : numberAccessed;
        System.out.println("Accessing " + numberAccessed + " cards.");
        if (server.isArchives()) {
            for (int i=0; i<numberAccessed;i++) {
                CorpCard card = serverAssets.get(i);
                if (card.isAgenda()) {
                    System.out.println("Accessing: " + card.getActualName() + ". Steal or leave?");
                } else if (card.isTrap(server.getName())) {
                    System.out.println("Accessing: " + card.getActualName() + ".");
                    if (!(card.getCost() > corp.getDisplayCreds())) {
                        corp.spendReservedCreds(card.getCost());
                        System.out.println("Corp spends " + card.getCost() + " to trigger trap.");
                    }
                    System.out.println("Trash " + card.getActualName() + " for " + card.getTrashCost() + " or leave?");
                } else {
                    System.out.println("Accessing: " + card.getActualName() + ". Press enter to continue.");
                }
                String command = getStringFromUser();
                if ("steal".equals(command)) {
                    if (card.isAgenda()) {
                        runnerPoints = runnerPoints + card.getScoreValue();
                        cardsToSteal.add(card);
                        System.out.println("Runner steals agenda and has " + runnerPoints + " points");
                    } else {
                        System.out.println("Cannot steal card");
                        i--;
                    }
                }
            }
        } else {
            for (int i=0; i<numberAccessed;i++) {
                CorpCard card = serverAssets.get(i);
                if (card.isAgenda()) {
                    System.out.println("Accessing: " + card.getActualName() + ". Trash for 0, steal, or leave?");
                } else if (card.isTrap(server.getName())) {
                    System.out.println("Accessing: " + card.getActualName() + ".");
                    if (!(card.getCost() > corp.getDisplayCreds()) && (!card.isAdvanceable() || (card.isAdvanceable() && card.getAdvancement() > 0))) {
                        corp.spendReservedCreds(card.getCost());
                        System.out.println("Corp spends " + card.getCost() + " to trigger trap.");
                    }
                    System.out.println("Trash " + card.getActualName() + " for " + card.getTrashCost() + " or leave?");
                } else if (card.getTrashCost() < 99) {
                    System.out.println("Accessing: " + card.getActualName() + ". Trash for " + card.getTrashCost() +" or leave?");
                } else {
                    System.out.println("Accessing: " + card.getActualName() + ". Leave or trash with special ability?");
                }
                String command = getStringFromUser();
                if ("trash".equals(command)) {
                    cardsToTrash.add(card);
                } else if ("steal".equals(command)) {
                    if (card.isAgenda()) {
                        runnerPoints = runnerPoints + card.getScoreValue();
                        cardsToSteal.add(card);
                        System.out.println("Runner steals agenda and has " + runnerPoints + " points");
                    } else {
                        System.out.println("Cannot steal card");
                        i--;
                    }
                } else if ("jack out".equals(command)) {
                    break;
                }
            }
        }
        for (int i = cardsToTrash.size()-1; i>=0; i--) {
            corp.trashCardFromServer(cardsToTrash.get(i), server);
        }
        for (int i = cardsToSteal.size()-1; i>=0; i--) {
            corp.stealCardFromServer(cardsToSteal.get(i), server);
        }
        return runnerPoints;
    }

    public static CorpCard getIceCard(Corp corp, boolean trash) {
        System.out.println("Which server?");
        int serverNumber = getIntFromUser(1, corp.getServers().size());
        Server server = corp.getServers().get(serverNumber-1);
        System.out.println("Which position?");
        int iceNumber = getIntFromUser(1, server.getIce().size());
        if (server != null && iceNumber != 0) {
            CorpCard ice = server.getIce().get(iceNumber-1);
            if (trash) {
                System.out.println("Corp forced to trash " + ice.getName());
                if (!ice.isRezzed()) {
                    corp.refundCreds(ice.getCost());
                }
                server.getIce().remove(ice);
            } else {
                return ice;
            }
        }
        System.out.println("Incorrect ICE location");
        return null;
    }

    public static void renderCorpBoard(Corp corp) {
        int columnWidth = 12;

        //Render Board
        String serverNumberLayer = "|| ";
        String layer0 = "|| ";
        String layer1 = "|| ";
        String layer2 = "|| ";
        String layer3 = "|| ";
        String dividerLayer = "==";
        String spacingLayer = "|| ";
        int i = 0;
        for (Server server : corp.getServers()) {
            i++;
            String name = server.getName();
            if (server.getAsset() != null) {
                CorpCard asset = server.getAsset();
                name = (asset.getAdvancement() > 0) ? "(" + (asset.getAdvancement() + ") " + asset.getName()) : asset.getName();  
            }
            List<CorpCard> ice = server.getIce();

            layer0 = layer0 + padToN(name, columnWidth) + " || ";
            
            if (ice != null && ice.size() > 0) {
                layer1 = layer1 + padToN(ice.get(0).getName(), columnWidth) + " || ";
            } else {
                layer1 = layer1 + "             || ";
            }
            if (ice != null && ice.size() > 1) {
                layer2 = layer2 + padToN(ice.get(1).getName(), columnWidth) + " || ";
            } else {
                layer2 = layer2 + "             || ";
            }
            if (ice != null && ice.size() > 2) {
                layer3 = layer3 + padToN(ice.get(2).getName(), columnWidth) + " || ";
            } else {
                layer3 = layer3 + "             || ";
            }
            dividerLayer = dividerLayer + "================";
            spacingLayer = spacingLayer + "             || ";
            serverNumberLayer = serverNumberLayer + "" + padToN(i + " ", 2) + "           || ";
        }
        System.out.println("\n" + dividerLayer);
        System.out.println(serverNumberLayer);
        System.out.println(layer0);
        System.out.println(dividerLayer);
        System.out.println(layer1);
        System.out.println(layer2);
        System.out.println(layer3);
        System.out.println(dividerLayer);
        String creds = "|| Back-end Credits: " + corp.getCreds();
        String displayCreds = "|| Credits: " + corp.getDisplayCreds();
        String clicks = "|| Clicks: " + corp.getClicks();
        String handSize = "|| HQ: " + corp.getHandCount() + "/" + corp.getHandLimit();
        debugPrint(padToN(creds, dividerLayer.length() - 2) + "||"); 
        System.out.println(padToN(displayCreds, dividerLayer.length() - 2) + "||"); 
        System.out.println(padToN(clicks, dividerLayer.length() - 2) + "||"); 
        System.out.println(padToN(handSize, dividerLayer.length() - 2) + "||"); 
        List<CorpCard> hq = corp.getHQ().getAssets();
        for (CorpCard card : hq) {
            debugPrint((padToN("|| Card : " + card.getActualName(), dividerLayer.length() - 2) + "||")); 
        }
        List<Server> servers = corp.getWeakServers();
        for (Server server : servers) {
            debugPrint((padToN("|| Weak Server : " + server.getName(), dividerLayer.length() - 2) + "||")); 
        }
        System.out.println(dividerLayer);
        System.out.println("\n");
    }

    public static String padToN(String s, int n) {
        if (s.length() > n) {
                    s = s.substring(0, n);
        } else {
            for (int i=(n-s.length()); i > 0; i--) {
                s = s + " ";
            }
        }
        return s;
    }
    public static boolean verifyIdentity(String identity) {
        List<String> identities = Arrays.asList("Haas-Bioroid: Engineering the Future","Haas-Bioroid: Stronger Together","Jinteki - Replicating Perfection","Other Jinteki");
        return identities.contains(identity);
    }
    public static Map<String, List<CorpCard>> buildDeck(String corpDeckFilename) {
        List<CorpCard> deck = new ArrayList<CorpCard>();
        String identity = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(corpDeckFilename));
            identity = in.readLine();
            String str;
            if (!verifyIdentity(identity)) {
                System.out.println("Incorrect identity name.  Please verify deck and run again.");
                return null;
            }
            while ((str = in.readLine()) != null) {
                String[] parts = str.split(",");
                int quantity = Integer.parseInt(parts[0]);
                for (int i=0;i<quantity;i++) {
                    CorpCard card = new CorpCard();
                    card.setType(parts[5]);
                    card.setName(parts[1]);
                    card.setCost(Integer.parseInt(parts[2]));
                    if (card.isIce()) {
                        card.setSubroutines(Integer.parseInt(parts[3]));
                        card.setStrength(Integer.parseInt(parts[4]));
                    } else {
                        card.setScoreValue(Integer.parseInt(parts[3]));
                        card.setTrashCost(Integer.parseInt(parts[4]));
                    }
                    card.setSubType(parts[6]);
                    card.setAttributes(parts[7]);
                    
                    deck.add(card);
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("debug error building deck: " + e.getMessage());
        }
        Map<String, List<CorpCard>> identityMap = new HashMap<String, List<CorpCard>>();
        identityMap.put(identity, deck);
        return identityMap;
    }

    public static void debugPrint(String s) {
        if (debugMode) {
            System.out.println(s);
        }
    }

}