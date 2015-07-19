import java.util.*;

public class CardAbility {
    private static List<String> cardsForClickThree = Arrays.asList("Melange Mining Corp","Eliza's Toybox");
    private static List<String> preTurnAssets = Arrays.asList("Adonis Campaign","Pad Campaign","Mental Health Clinic");
    private static List<String> preAgendaCards = Arrays.asList("Trick of Light","Bioroid Efficiency Research");
    private static List<String> preAccessAssets = Arrays.asList("Caprice Nisei","Jackson Howard");
    private static List<String> hostedCardsToTrash = Arrays.asList("Rook","Knight","Bishop");
    private static List<String> conditionalAgendas = Arrays.asList("NAPD Contract","The Future Perfect");
    private static List<String> agendaConditionCards = Arrays.asList("Strongbox");
    private static List<String> doubleOperations = Arrays.asList("Celebrity Gift");
    private static List<String> moneyCards = Arrays.asList("Hedge Fund","Celebrity Gift","Mental Health Clinic","Sundew");
    private static List<String> moneyAssets = Arrays.asList("");
    private static CardAbility instance = null;
    private boolean debugMode = false;

    public CardAbility() {
    }

    public static CardAbility getInstance() {
        if (instance == null) {
            instance = new CardAbility();
        }
        return instance;
    }

    public boolean activate(CorpCard card, Corp corp) {
        return activate(card, corp, null, null, false);
    }

    public boolean activate(CorpCard card, Corp corp, boolean accessFromRunner) {
        return activate(card, corp, null, null, accessFromRunner);
    }

    public boolean activate(CorpCard card, Corp corp, Server server) {
        return activate(card, corp, server, null, false);
    }

    public boolean activate(CorpCard card, Corp corp, String location) {
        return activate(card, corp, null, location, false);
    }

    public boolean activate(CorpCard card, Corp corp, Server server, String location, boolean accessFromRunner) {
        debugMode = corp.debugMode;

        if ("Melange Mining Corp".equals(card.getName()) && corp.getClicks() > 2 && useMelange(corp)) {
            System.out.println("Corp activates " + card.getName() + " for two extra  clicks and gains 7 credits");
            corp.gainCreds(7);
            corp.removeClick();
            corp.removeClick();
            return true;
        }
        if ("Eliza's Toybox".equals(card.getName()) && corp.getClicks() > 2) {
            int minCost = 6;
            debugPrint("22");
            if (location != null && "Money Asset".equals(location)) {
                debugPrint("33");
                minCost = 3;
            }
            CorpCard ice = useToybox(corp, minCost);
            if (ice != null) {
                corp.removeClick();
                corp.removeClick();
                corp.refundCreds(ice.getCost());
                ice.rez();
                System.out.println("Corp activates " + card.getName() + " for two extra  clicks and rezzes " + ice.getActualName() + " at no cost");
                
                return true;
            } else {
                return false;
            }
        }
        if ("Adonis Campaign".equals(card.getName())) {
            if (card.adonisCreds > 0) {
                System.out.println(card.getName() + " triggers and transfers 3 credits");
                corp.gainCreds(3);
                card.adonisCreds = card.adonisCreds - 3;
                if (card.adonisCreds == 0) {
                    return false;
                }
                return true;
            } 
        }
        if ("Pad Campaign".equals(card.getName())) {
            System.out.println(card.getName() + " triggers and gains 1 credit");
            corp.gainCreds(1);
            return true;
        }
        if ("Mental Health Clinic".equals(card.getName())) {
            System.out.println(card.getName() + " triggers and gains 1 credit");
            System.out.println("Runner's maximum hand size is increased by 1");
            corp.gainCreds(1);
            return true;
        }
        if ("Caprice Nisei".equals(card.getName())) {
            System.out.println(card.getName() + " triggers: let's play a game");
            System.out.println("How many credits are you playing with? (0, 1, or 2)");
            int runnerCreds = getIntFromUser(0,2);
            System.out.println("How many credits did you spend?");
            int runnerSpent = getIntFromUser(0,2);
            if (runnerCreds == 1 && corp.getDisplayCreds() >= 2) {
                System.out.println("Corp spends 2 credits");
                corp.spendCreds(2);
            } else if (runnerCreds == 0 && corp.getDisplayCreds() >= 1) {
                System.out.println("Corp spends 1 credit");
                corp.spendCreds(1);
            } else {
                Random rand = new Random();
                int creds = (corp.getDisplayCreds() >= 2) ? 2 : corp.getDisplayCreds();
                int value = rand.nextInt(creds+1); 
                corp.spendCreds(value);
                System.out.println("Corp spends "+ value + " credits");
                if (runnerSpent != value) {
                    System.out.println("Caprice wins and prevents server access");
                } else {
                    System.out.println("Caprice loses and server is accessable");
                }
            }
            return true;
        }
        if ("Beanstalk Royalties".equals(card.getName())) {
            System.out.println("Corp plays " + card.getName() + " for " + card.getCost() + " and gains 3 credits");
            corp.gainCreds(3);
            return true;
        }
        if ("Hedge Fund".equals(card.getName())) {
            System.out.println("Corp plays " + card.getName() + " for " + card.getCost() + " and gains 9 credits");
            corp.gainCreds(9);
            return true;
        }
        if ("Biotic Labor".equals(card.getName())) {
            corp.gainClicks(1);
            return true;
        }
        if ("Green Level Clearance".equals(card.getName())) {
            System.out.println("Corp plays " + card.getName() + " for " + card.getCost() + ", gains 3 credits, and draws a card");
            corp.gainCreds(3);
            corp.drawCorpCards(1);
            return true;
        }
        if ("Blue Level Clearance".equals(card.getName()) && corp.getClicks() > 1) {
            System.out.println("Corp plays " + card.getName() + " for " + card.getCost() + " and an extra click, gains 5 credits, and draws 2 cards");
            corp.gainCreds(5);
            corp.removeClick();
            corp.drawCorpCards(2);
            return true;
        }
        if ("Celebrity Gift".equals(card.getName()) && corp.getClicks() > 1 && corp.getHQ().getAssets().size() > 3) {
            System.out.println("Corp plays " + card.getName() + " for " + card.getCost() + " and an extra click, reveals: ");
            int totalCreds = 0;
            for (CorpCard c : corp.getHQ().getAssets()) {
                System.out.print(c.getActualName() + ", ");
                totalCreds += 2;
            }
            System.out.print("gains " + totalCreds + " credits");
            corp.gainCreds(totalCreds);
            corp.removeClick();
            return true;
        }
        if ("NAPD Contract".equals(card.getActualName())) {
            if (accessFromRunner) {
                System.out.println("Runner must pay an additional 4 credits to steal " + card.getActualName());
                System.out.println("Did runner pay credits?");
                String paidCreds = getStringFromUser();
                return paidCreds != null && ("yes".equals(paidCreds) || "y".equals(paidCreds));
            } else {
                return true;
            }
        }
        if ("Bioroid Efficiency Research".equals(card.getName())) {
            CorpCard ice = null;
            for (Server s : corp.getWeakServers()) {
                if (s.getIce().isEmpty() && !corp.getCorpCardsByType(corp.getHQ().getAssets(), "ICE").isEmpty()) {
                    return false;
                }
            }
            for (Server s : corp.getServers()) {
                List<CorpCard> iceList = s.getIce();
                for  (CorpCard c : iceList) {
                    if (ice == null || (ice.getCost() < c.getCost() && !c.isRezzed())) {
                        ice = c;
                    }
                }
            }
            if (ice != null && !ice.isRezzed()) {
                ice.rez();
                System.out.println("Bioroid Efficiency Research rezzes " + ice.getName() +" for no cost");
                System.out.println("If all subroutines are broken, derez this ICE");
                corp.refundCreds(ice.getCost());
                corp.getHQ().getAssets().remove(card);
                corp.trashCard(card);
                return true;
            }
        }
        if ("Mandatory Upgrades".equals(card.getName())) {
            corp.setMaxClicks((corp.getMaxClicks()+1));
            corp.gainClicks(1);
            System.out.println("Mandatory Upgrades gains the Corp 1 additional click per turn (" + corp.getMaxClicks() + " total)");
            return true;
        }
        if ("The Future Perfect".equals(card.getActualName())) {
            if (accessFromRunner && (server == null || !server.isRemote())) {
                System.out.println(card.getName() + " triggers: let's play a game");
                System.out.println("How many credits are you playing with? (0, 1, or 2)");
                int runnerCreds = getIntFromUser(0,2);
                System.out.println("How many credits did you spend?");
                int runnerSpent = getIntFromUser(0,2);
                if (runnerCreds == 1 && corp.getDisplayCreds() >= 2) {
                    System.out.println("Corp spends 2 credits and prevents agenda access");
                    corp.spendCreds(2);
                    return false;
                } else if (runnerCreds == 0 && corp.getDisplayCreds() >= 1) {
                    System.out.println("Corp spends 1 credit and prevents agenda access");
                    corp.spendCreds(1);
                    return false;
                } else {
                    Random rand = new Random();
                    int creds = (corp.getDisplayCreds() >= 2) ? 2 : corp.getDisplayCreds();
                    int value = rand.nextInt(creds+1); 
                    corp.spendCreds(value);
                    System.out.println("Corp spends "+ value + " credits");
                    if (runnerSpent != value) {
                        System.out.println("Corp wins and prevents agenda access");
                        return false;
                    } else {
                        System.out.println("Corp loses and agenda is stolen");
                        return true;
                    }
                }
            }
            return true;
        }
        if ("Jackson Howard".equals(card.getName())) {
            if (corp.getClicks() == 0) {                
                List<CorpCard> agendas = new ArrayList<CorpCard>();
                List<CorpCard> secondary = new ArrayList<CorpCard>();
                for (CorpCard c : corp.getServerByNumber(0).getAssets()) {
                    if (c.isAgenda()) {
                        agendas.add(c);
                    } else if (c.isMoneyAsset() || c.isMoneyCard() || c.isCardAsset()) {
                        secondary.add(c);
                    }
                }
                if (agendas.size() > 0 || (agendas.size() + secondary.size()) > 2) {
                    int j = 0;
                    for (int i=0; i<3; i++) {
                        if (agendas.size() > 0) {
                            j++;
                            CorpCard c = agendas.get(0);
                            corp.debugPrint("Moving " + c.getActualName() + " to RnD");
                            corp.getServerByNumber(1).getAssets().add(c);
                            corp.getServerByNumber(0).getAssets().remove(c);
                            agendas.remove(c);
                        } else if (secondary.size() > 0) {
                            j++;
                            CorpCard c = secondary.get(0);
                            corp.debugPrint("Moving " + c.getActualName() + " to RnD");
                            corp.getServerByNumber(1).getAssets().add(c);
                            corp.getServerByNumber(0).getAssets().remove(c);
                            secondary.remove(c);
                        }
                    }
                    System.out.println("Corp removes " + card.getName() + " from the game to put  " + j + " cards from Archives to RnD");
                    Collections.shuffle(corp.getServerByNumber(1).getAssets());
                    server.removeAsset();
                    server.getAssets().remove(card);
                }
            } else {
                System.out.println("Corp activates " + card.getName() + " to draw two cards");
                corp.drawCorpCards(1);
                corp.drawCorpCards(1);
                return true;
            }
        }
        if ("Priority Requisition".equals(card.getName())) {
            CorpCard ice = null;
            for (Server s : corp.getServers()) {
                List<CorpCard> iceList = s.getIce();
                for  (CorpCard c : iceList) {
                    if (ice == null || (ice.getCost() < c.getCost() && !c.isRezzed())) {
                        ice = c;
                    }
                }
            }
            if (ice != null) {
                ice.rez();
                corp.refundCreds(ice.getCost());
                System.out.println("Priority Requisition rezzes " + ice.getName() +" for no cost");
            }
            
            return true;
        }
        if ("Accelerated Beta Test".equals(card.getName())) {
            System.out.println("Accelerated Beta Test triggers");
            for (int i=0; i<3; i++) {
                CorpCard topCard = corp.getServerByNumber(1).getAssets().remove(0);
                if (!topCard.isIce()) {
                    System.out.println("Corp trashes card " + (i+1));
                    corp.trashCard(topCard);
                } else {
                    Server s = null;
                    topCard.rez();
                    if (!corp.getWeakServers().isEmpty()) {
                        s = corp.getWeakServers().get(0);
                    } else if (corp.getServerByNumber(0).getIce().size() == 0) {
                        s = corp.getServerByNumber(0);
                    } else {
                        s = (corp.getServerByNumber(1).getIce().size() < corp.getServerByNumber(2).getIce().size()) ? corp.getServerByNumber(1) : corp.getServerByNumber(2);
                    }
                    if (s != null) {
                        s.addCard(topCard);
                        System.out.println("Corp installs and rezzes " + topCard.getActualName() + " on " + s.getName());
                    }
                }
            }
            return true;
        }
        if ("Nisei MK II".equals(card.getName())) {
            System.out.println("Nisei MK II gets three agenda counters");
            card.setCounters(3);
            return true;
        }
        if ("Trick of Light".equals(card.getName())) {
            if (useTrickOfLight(corp)) {
                return true;
            }
        }
        if ("Sundew".equals(card.getName())) {   
            corp.gainCreds(2);
            System.out.println("Sundew triggers and the corp gains 2 credits.");
            return true;
        }
        if ("Strongbox".equals(card.getActualName())) {   
            System.out.println(card.getActualName() + " requires runner must pay an additional click to steal agenda in this server");
            System.out.println("Did runner pay click?");
            String paidCreds = getStringFromUser();
            return paidCreds != null && ("yes".equals(paidCreds) || "y".equals(paidCreds));
        }
        return false;
    }

    public List<String> getCardsForClickNumber(int click) {
        if (click == 3) {
            return cardsForClickThree;
        }
        return null;
    }
    public List<String> getPreTurnAssets() {
        return preTurnAssets;
    }
    public List<String> getPreAgendaCards() {
        return preAgendaCards;
    }
    public List<String> getPreAccessAssets() {
        return preAccessAssets;
    }
    public List<String> getMoneyCards() {
        return moneyCards;
    }
    public List<String> getHostedCardsToTrash() {
        return hostedCardsToTrash;
    }
    public List<String> getConditionalAgendas() {
        return conditionalAgendas;
    }
// Card specific evaluation functions
    public boolean useMelange(Corp corp) {
        return (corp.getCreds() < 15);
    }
    public boolean useNisei(Corp corp, Server server) {
        return ((server.isHQ() || server.isArchives()) && corp.getCorpCardsByType(server.getAssets(), "Agenda").size() > 1) || (server.isRemote() && server.getAsset() != null && server.getAsset().isAgenda());
    }
    
    public CorpCard useToybox(Corp corp, int minCost) {
        CorpCard bestIce = null;
        for (Server server : corp.getWeakServers()) {
            for (CorpCard ice : server.getIce()) {
                if ((bestIce == null || ice.getCost() > bestIce.getCost()) && !ice.isRezzed()) {
                    bestIce = ice;
                }
            }
        }
        if (bestIce != null && (bestIce.getCost() > corp.getDisplayCreds() || bestIce.getCost() > 4)) {
            return bestIce;
        } else if (bestIce == null) {
            for (Server server : corp.getServers()) {
                for (CorpCard ice : server.getIce()) {
                    if ((bestIce == null || ice.getCost() > bestIce.getCost()) && !ice.isRezzed()) {
                        bestIce = ice;
                    }
                }
            }
            if (bestIce != null && bestIce.getCost() > minCost) {
                return bestIce;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    public boolean useTrickOfLight(Corp corp) {
        CorpCard trap = null;
        CorpCard agenda = null;
        int advancementNeeded = 99;
        int trapAdvancement = 0;
        boolean agendaInstalled = false;
        for (Server s : corp.getServers()) {
            CorpCard asset = s.getAsset();
            if (asset != null && asset.isTrap() && asset.getAdvancement() > trapAdvancement) {
                trap = asset;
                trapAdvancement = trap.getAdvancement();
            } else if (asset != null && asset.isAgenda() && (asset.getCost() - asset.getAdvancement()) == (corp.getClicks() + 1)) {
                agenda = asset;
                advancementNeeded = asset.getCost() - asset.getAdvancement();
                agendaInstalled = true;
            }
        }
        if (agenda == null) {
            for (CorpCard card : corp.getHQ().getAssets()) {
                if (card.isAgenda() && (card.getCost() < advancementNeeded)) {
                    agenda = card;
                    advancementNeeded = card.getCost();
                }
            }
        }
        if (trapAdvancement > 2) {
            trapAdvancement = 2;
        } else if (trapAdvancement < 2) {
            return false;
        }
        if (trap != null && advancementNeeded == 4 && agendaInstalled) {
            CorpCard card = null;
            for (CorpCard c : corp.getHQ().getAssets()) {
                if ("Trick of Light".equals(c.getActualName())) {
                    card = c;
                }
            }
            if (card == null) {
                return false;
            }
            trap.unadvance();
            trap.unadvance();
            agenda.advance();
            agenda.advance();
            corp.getHQ().getAssets().remove(card);
            corp.trashCard(card);
            System.out.println("Corp plays Trick of Light to move " + trapAdvancement + " counters from one asset to another");
            return true;
        } else if (trap != null && advancementNeeded == 4 && !agendaInstalled) {
            Server openServer = corp.getBestOpenServer();
            if (openServer != null && corp.isSuitableForAgenda(openServer) && corp.installCard(openServer, agenda)) {
                return true;
            } 
        } else if (trap != null && advancementNeeded == 3 && corp.getClicks() == 3 && corp.getCreds() > 2 && !agendaInstalled) {
            CorpCard card = null;
            for (CorpCard c : corp.getHQ().getAssets()) {
                if ("Trick of Light".equals(c.getActualName())) {
                    card = c;
                }
            }
            if (card == null) {
                return false;
            }
            corp.createServer(agenda);
            corp.removeClick();
            corp.spendCreds(1);
            trap.unadvance();
            trap.unadvance();
            agenda.advance();
            agenda.advance();
            corp.getHQ().getAssets().remove(card);
            corp.trashCard(card);
            System.out.println("Corp plays Trick of Light to move " + trapAdvancement + " counters from one asset to another");
            return true;
        }
        return false;
    }

    public boolean isDouble(CorpCard card) {
        return doubleOperations.contains(card.getActualName());
    }

    public boolean meetsAdditionalAgendaConditions(Corp corp, Server server) {
        for (CorpCard card : server.getAssets()) {
            if (agendaConditionCards.contains(card.getActualName())) {
                if (!activate(card, corp)) {
                    return false;
                }
            }
        }
        return true;
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
    public void debugPrint(String s) {
        if (debugMode) {
            System.out.println(s);
        }
    }
}