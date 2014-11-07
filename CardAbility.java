import java.util.*;

public class CardAbility {
    private static List<String> cardsForClickThree = Arrays.asList("Melange Mining Corp");
    private static List<String> preTurnAssets = Arrays.asList("Adonis Campaign","Pad Campaign");
    private static List<String> preAgendaCards = Arrays.asList("Trick of Light");

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
        if ("Beanstalk Royalties".equals(card.getName())) {
            System.out.println(" and gains 3 credits");
            corp.gainCreds(3);
            return true;
        }
        if ("Hedge Fund".equals(card.getName())) {
            System.out.println(" and gains 9 credits");
            corp.gainCreds(9);
            return true;
        }
        if ("Biotic Labor".equals(card.getName())) {
            corp.gainClicks(1);
            return true;
        }
        if ("Green Level Clearance".equals(card.getName())) {
            System.out.println(", gains 3 credits, and draws a card");
            corp.gainCreds(3);
            corp.drawCorpCards(1);
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
            trap.unadvance();
            trap.unadvance();
            agenda.advance();
            agenda.advance();
            System.out.println("Corp plays Trick of Light to move " + trapAdvancement + " counters from one asset to another");
            return true;
        } else if (trap != null && advancementNeeded == 4 && !agendaInstalled) {
            Server openServer = corp.getBestOpenServer();
            if (openServer != null && corp.isSuitableForAgenda(openServer) && corp.installCard(openServer, agenda)) {
                return true;
            } 
        } else if (trap != null && advancementNeeded == 3 && corp.getClicks() == 3 && corp.getCreds() > 2 && !agendaInstalled) {
            corp.createServer(agenda);
            corp.removeClick();
            corp.spendCreds(1);
            trap.unadvance();
            trap.unadvance();
            agenda.advance();
            agenda.advance();
            System.out.println("Corp plays Trick of Light to move " + trapAdvancement + " counters from one asset to another");
            return true;
        }
        return false;
    }

}