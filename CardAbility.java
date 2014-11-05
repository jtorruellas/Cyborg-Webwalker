import java.util.*;

public class CardAbility {
    private static List<String> cardsForClickThree = Arrays.asList("Melange Mining Corp");
    private static List<String> preTurnAssets = Arrays.asList("Adonis Campaign","Pad Campaign");

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

// Card specific evaluation functions
    public boolean useMelange(Corp corp) {
        return (corp.getCreds() < 15);
    }

}