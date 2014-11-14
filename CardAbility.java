import java.util.*;

public class CardAbility {
    private static List<String> cardsForClickThree = Arrays.asList("Melange Mining Corp");
    private static List<String> preTurnAssets = Arrays.asList("Adonis Campaign","Pad Campaign");
    private static List<String> preAgendaCards = Arrays.asList("Trick of Light","Bioroid Efficiency Research");
    private static List<String> preAccessAssets = Arrays.asList("Caprice Nisei");

    public CardAbility() {
    }

    public boolean activate(CorpCard card, Corp corp) {

        if ("Melange Mining Corp".equals(card.getName()) && corp.getClicks() > 2 && useMelange(corp)) {
            System.out.println("Corp activates " + card.getName() + " for two extra  clicks and gains 7 credits");
            corp.gainCreds(7);
            corp.removeClick();
            corp.removeClick();
            return true;
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
        if ("Caprice Nisei".equals(card.getName())) {
            System.out.println(card.getName() + " triggers: let's play a game");
            System.out.println("How many credits are you playing with? (0, 1, or 2)");
            int runnerCreds = getIntFromUser(0,2);
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
        if ("Bioroid Efficiency Research".equals(card.getName())) {
            CorpCard ice = null;
            for (Server server : corp.getWeakServers()) {
                if (server.getIce().isEmpty() && !corp.getCorpCardsByType(corp.getHQ().getAssets(), "ICE").isEmpty()) {
                    return false;
                }
            }
            for (Server server : corp.getServers()) {
                List<CorpCard> iceList = server.getIce();
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
        if ("Jackson Howard".equals(card.getName())) {
            System.out.println("Corp activates " + card.getName() + " to draw two cards");
            corp.drawCorpCards(1);
            corp.drawCorpCards(1);
            return true;
        }
        if ("Priority Requisition".equals(card.getName())) {
            CorpCard ice = null;
            for (Server server : corp.getServers()) {
                List<CorpCard> iceList = server.getIce();
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
                    Server server = null;
                    topCard.rez();
                    if (!corp.getWeakServers().isEmpty()) {
                        server = corp.getWeakServers().get(0);
                    } else if (corp.getServerByNumber(0).getIce().size() == 0) {
                        server = corp.getServerByNumber(0);
                    } else {
                        server = (corp.getServerByNumber(1).getIce().size() < corp.getServerByNumber(2).getIce().size()) ? corp.getServerByNumber(1) : corp.getServerByNumber(2);
                    }
                    if (server != null) {
                        server.addCard(topCard);
                        System.out.println("Corp installs and rezzes " + topCard.getActualName() + " on " + server.getName());
                    }
                }
            }
            return true;
        }
        if ("Trick of Light".equals(card.getName())) {
            if (useTrickOfLight(corp)) {
                return true;
            }
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

// Card specific evaluation functions
    public boolean useMelange(Corp corp) {
        return (corp.getCreds() < 15);
    }
    public boolean useTrickOfLight(Corp corp) {
        CorpCard trap = null;
        CorpCard agenda = null;
        int advancementNeeded = 99;
        int trapAdvancement = 0;
        boolean agendaInstalled = false;
        for (Server server : corp.getServers()) {
            CorpCard asset = server.getAsset();
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

}