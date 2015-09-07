import java.util.*;
import javax.swing.*;

public class CardAbility {
    private static ArrayList<String> cardsForClickThree = new ArrayList<String>();
    private static ArrayList<String> preTurnAssets = new ArrayList<String>();
    private static ArrayList<String> preAgendaCards = new ArrayList<String>();
    private static ArrayList<String> preAccessAssets  = new ArrayList<String>();
    private static ArrayList<String> hostedCardsToTrash = new ArrayList<String>();
    private static ArrayList<String> conditionalAgendas  = new ArrayList<String>();
    private static ArrayList<String> agendaConditionCards = new ArrayList<String>();
    private static ArrayList<String> doubleOperations = new ArrayList<String>();
    private static ArrayList<String> moneyCards = new ArrayList<String>();
    private static ArrayList<String> moneyAssets = new ArrayList<String>();
    private static CardAbility instance = null;
    private boolean debugMode = false;
    private static JFrame frame;

    public CardAbility() {
        cardsForClickThree.addAll(Arrays.asList("Melange Mining Corp","Eliza's Toybox"));
        preTurnAssets.addAll(Arrays.asList("Adonis Campaign","Pad Campaign","Mental Health Clinic"));
        preAgendaCards.addAll(Arrays.asList("Trick of Light","Bioroid Efficiency Research"));
        preAccessAssets.addAll(Arrays.asList("Caprice Nisei","Jackson Howard"));
        hostedCardsToTrash.addAll(Arrays.asList("Rook","Knight","Bishop"));
        conditionalAgendas.addAll(Arrays.asList("NAPD Contract","The Future Perfect"));
        agendaConditionCards.addAll(Arrays.asList("Strongbox"));
        doubleOperations.addAll(Arrays.asList("Celebrity Gift"));
        moneyCards.addAll(Arrays.asList("Hedge Fund","Celebrity Gift","Mental Health Clinic","Sundew"));
        moneyAssets.addAll(Arrays.asList(""));
    }

    public static CardAbility getInstance() {
        if (instance == null) {
            instance = new CardAbility();
        }
        return instance;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
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
            int runnerCreds = getIntFromUser(card.getName() + " triggers: let's play a game\nHow many credits are you playing with? (0, 1, or 2)",0,2);
            int runnerSpent = getIntFromUser("How many credits did you spend?",0,2);
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
                int paidCreds = getYesNoFromUser("Runner must pay an additional 4 credits to steal " + card.getActualName() + "\nDid runner pay credits?");
                return paidCreds == 0;
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
                ArrayList<CorpCard> iceList = s.getIce();
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
                int runnerCreds = getIntFromUser(card.getName() + " triggers: let's play a game\nHow many credits are you playing with? (0, 1, or 2)",0,2);
                int runnerSpent = getIntFromUser("How many credits did you spend?",0,2);
                if (runnerCreds == 1 && corp.getDisplayCreds() >= 2) {
                    JOptionPane.showMessageDialog(frame, "Corp spends 2 credits and prevents agenda access.");
                    corp.spendCreds(2);
                    return false;
                } else if (runnerCreds == 0 && corp.getDisplayCreds() >= 1) {
                    JOptionPane.showMessageDialog(frame, "Corp spends 1 credit and prevents agenda access.");
                    corp.spendCreds(1);
                    return false;
                } else {
                    Random rand = new Random();
                    int creds = (corp.getDisplayCreds() >= 2) ? 2 : corp.getDisplayCreds();
                    int value = rand.nextInt(creds+1); 
                    corp.spendCreds(value);
                    String resultString = "Corp spends "+ value + " credits.";
                    if (runnerSpent != value) {
                        resultString = resultString + ("\nCorp wins and prevents agenda access.");
                        System.out.println("Runner prevented from stealing " + card.getActualName());
                        JOptionPane.showMessageDialog(frame, resultString);
                        return false;
                    } else {
                        resultString = resultString + ("\nCorp loses and agenda is stolen.");
                        System.out.println("Runner steals " + card.getActualName());
                        JOptionPane.showMessageDialog(frame, resultString);
                        return true;
                    }
                }
            }
            return true;
        }
        if ("Jackson Howard".equals(card.getName())) {
            if (corp.getClicks() == 0) {                
                ArrayList<CorpCard> agendas = new ArrayList<CorpCard>();
                ArrayList<CorpCard> secondary = new ArrayList<CorpCard>();
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
                ArrayList<CorpCard> iceList = s.getIce();
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
            System.out.println("Nisei MK II gets an agenda counter");
            card.setCounters(1);
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
            if (card.isRezzed()) {
                int paidCreds = getYesNoFromUser(card.getActualName() + " requires runner must pay an additional click to\nsteal agenda in this server.\nDid runner pay click?");
                return paidCreds == 0;
            }
        }
        return false;
    }

    public ArrayList<String> getCardsForClickNumber(int click) {
        if (click == 3) {
            return cardsForClickThree;
        }
        return null;
    }
    public ArrayList<String> getPreTurnAssets() {
        return preTurnAssets;
    }
    public ArrayList<String> getPreAgendaCards() {
        return preAgendaCards;
    }
    public ArrayList<String> getPreAccessAssets() {
        return preAccessAssets;
    }
    public ArrayList<String> getMoneyCards() {
        return moneyCards;
    }
    public ArrayList<String> getHostedCardsToTrash() {
        return hostedCardsToTrash;
    }
    public ArrayList<String> getConditionalAgendas() {
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
                if (card.isRezzed() && !activate(card, corp)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getYesNoFromUser(String text) {
        return JOptionPane.showConfirmDialog(
            frame,
            text,
            "Input Required",
            JOptionPane.YES_NO_OPTION);
    }

    public static String getStringFromUser(String text) {
        try {
            String s = (String)JOptionPane.showInputDialog(
                    frame,
                    text,
                    "Input Required",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
            return s;
        } catch (Exception e) {
            System.out.println("Incorrect input");
        }
        return "";
    }

    public static int getIntFromUser(String text, int min, int max) {
        int intFromUser = -1;
        while (intFromUser == -1) {
            intFromUser = getIntFromUserSafe(text, min, max);
            if (intFromUser == -2) {
                return -2;
            }
        }
        return intFromUser;
    }

    public static int getIntFromUserSafe(String text, int min, int max) {
        try {
            //Scanner reader = new Scanner(System.in);
            //int val = reader.nextInt();
            int val = -1;
            String s = (String)JOptionPane.showInputDialog(
                    frame,
                    text,
                    "Input Required",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
            if (s == null) {
                return -2;
            }
            val = Integer.parseInt(s);
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