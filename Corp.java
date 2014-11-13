import java.util.*;

public class Corp {

    // ===========================================================Corp Resources
    private int c_creds = 5;
    private int c_displayCreds = 5;
    private int c_handLimit = 5;
    private int c_clicks = 3;
    private int c_maxClicks = 3;
    private int corpScore = 0;
    private Server rnd = null;
    private Server hq = null;
    private Server archives = null;
    private List<CorpCard> c_deck = new ArrayList<CorpCard>(49); 
    private List<CorpCard> c_hand = new ArrayList<CorpCard>(c_handLimit);
    private List<CorpCard> c_agendas = new ArrayList<CorpCard>(10);
    private Map<String, RunnerCard> runnerCards = new HashMap<String, RunnerCard>(); 
    private int DRAW_MONEY_CARD_PROB = 45;
    private List<Server> c_servers = new ArrayList<Server>();
    private List<Server> serversAccessed = new ArrayList<Server>();
    private String corpName = "HB1";
    private boolean usedAbility = false;
    private boolean debugMode = false;
    private Card current = null;

    // ===========================================================Initialize Corp
    public Corp(String identity, List<CorpCard> deck, boolean debugMode) {
        corpName = identity;
        setDeck(deck);
        this.debugMode = debugMode;
        Collections.shuffle(deck);
        Collections.shuffle(deck);
        c_servers.add(new Server("Archives"));
        archives = getServerByNumber(0);
        c_servers.add(new Server("RnD"));
        rnd = getServerByNumber(1);
        rnd.setAssets(deck);
        c_servers.add(new Server("HQ"));
        hq = getServerByNumber(2);
        drawCorpCard();
        drawCorpCard();
        drawCorpCard();
        drawCorpCard();
        drawCorpCard();

    }

    // ===========================================================Getter/Setters
    public RunnerCard getRunnerCardByName(String cardName) {
        return runnerCards.get(cardName);
    }
    public List<Server> getServers() {
        return c_servers;
    }
    public List<Server> getServersAccessed() {
        return serversAccessed;
    }
    public void setServersAccessed(List<Server> serversAccessed) {
        this.serversAccessed = serversAccessed;
    }
    public void addServerAccessed(Server server) {
        if (!serversAccessed.contains(server)) {
            serversAccessed.add(server);
        }
    }
    public int getClicks() {
        return c_clicks;
    }
    public int getMaxClicks() {
        return c_maxClicks;
    }
    public void setMaxClicks(int clicks) {
        c_maxClicks = clicks;
    }
    public int getCreds() {
        return c_creds;
    }
    public int getDisplayCreds() {
        return c_displayCreds;
    }
    public void setDeck(List<CorpCard> deck) {
        c_deck = deck;
    }
    public int getHandCount() {
        return hq.getAssets().size();
    }
    public int getHandLimit() {
        return c_handLimit;
    }
    public Server getHQ() {
        return hq;
    }
    public int getCorpScore() {
        return corpScore;
    }
    public void setCurrent(Card current) {
        this.current = current;
    }
    public Card getCurrent() {
        return current;
    }
    // ===========================================================Base Actions 
    public void preTurn() {
        debugPrint("debug preTurn");
        //reset identity ability
        usedAbility = false;
        //activate pre-turn assets
        for(Server server : c_servers) {
            CorpCard asset = server.getAsset();
            CardAbility ca = new CardAbility();
            List<String> preTurnAssets = ca.getPreTurnAssets();
            if (asset != null && !asset.isAgenda() && !asset.isTrap()) {
                if (!asset.isRezzed() && !(asset.getCost() > getDisplayCreds())) {
                    spendReservedCreds(asset.getCost());
                    asset.rez();
                    System.out.println("Corp rezzes " + asset.getName() + " for " + asset.getCost() + " creds");
                }
                
                if (preTurnAssets.contains(asset.getActualName())) {
                    if (!ca.activate(asset, this)) {
                        trashCardFromServer(asset, server);
                    }
                }
            }
        }
    }
    public boolean clearVirusCounters() {
        if (tooManyViruses()) {
            removeClick();
            removeClick();
            System.out.println("Corp clears virus counters");
            for (RunnerCard card : runnerCards.values()) {
                card.clearVirusCounters();
            }
            return true;
        }
        return false;
    }
    public void resetClicks(int clicks) {
        c_clicks = clicks;
    }
    public void removeClick() {
        c_clicks = c_clicks - 1;
    }
    public boolean gainCred() {
        System.out.println("Corp takes a credit");
        c_creds++;
        c_displayCreds++;
        return true;
    }
    public boolean refundCreds(int creds) {
        debugPrint("debug refundCreds");
        c_creds = c_creds + creds;
        return true;
    }
    public void spendCreds(int creds) {
        debugPrint("debug spendCreds");
        c_creds = c_creds - creds;
        c_displayCreds = c_displayCreds - creds;
    }
    public void spendReservedCreds(int creds) {
        debugPrint("debug spendReservedCreds");
        c_displayCreds = c_displayCreds - creds;
    }
    public void reserveCreds(int creds) {
        debugPrint("debug reserveCreds");
        c_creds = c_creds - creds;
    }
    public boolean drawCorpCard() {
        CorpCard card = rnd.getAssets().remove(0);
        System.out.println("Corp draws a card");
        hq.getAssets().add(card);
        return true;
    }
    public boolean advanceCorpCard(CorpCard card, int counters){
        System.out.println("Corp advances " + card.getName());
        if (card.isAdvanceable()) {
            spendCreds(counters);
            card.advance(counters);
            return true;
        } else {
            return false;
        }
        
    }
    public boolean installCard(Server server, CorpCard card) {
        debugPrint("debug installCard");
        if (card.isUnique()) {
            for (Server s : c_servers) {
                if (card.isIce()) {
                    for (CorpCard c : s.getIce()) {
                        if (c.getName().equals(card.getName())) {
                            return false;
                        }
                    }
                } else if (card.isAsset()) {
                    for (CorpCard c : s.getAssets()) {
                        if (c.getName().equals(card.getName())) {
                            return false;
                        }
                    }
                }
            }
        }
        if (card.isIce()) {
            if (server.getIce().size() != 0 && server.getIce().size() > c_creds) {
                return false;
            } else {
                spendCreds(server.getIce().size()) ;
            }
            reserveCreds(card.getCost());
        } else if (!card.isAgenda()) {
            reserveCreds(card.getCost());
        }
        if (card.isRegion() && server.hasRegion()) {
            return false;
        }
        server.addCard(card);
        hq.getAssets().remove(card);
        System.out.println("Corp installs " + card.getName() + " on " +server.getName());
        return true;
    }
    public boolean tryPlayingCurrent() {
        debugPrint("debug tryPlayingCurrent");
        for (CorpCard card : hq.getAssets()) {
            if (card.isCurrent() && (current == null || "Runner".equals(current.getSide())) && card.getCost() <= getCreds()) {
                current = card;
                hq.getAssets().remove(card);
                return true;
            }
        }
        return false;
    }
    public boolean playOperation(CorpCard card) {
        CardAbility ca = new CardAbility();
        if (ca.activate(card, this)) {
            spendCreds(card.getCost());
            hq.getAssets().remove(card);
            trashCard(card);
            return true;
        }
        return false;
    }
    public boolean createServer(CorpCard card) {
        debugPrint("debug createServer");
        if (card.isIce()) {
            reserveCreds(card.getCost());
        } else if (!card.isAgenda()) {
            reserveCreds(card.getCost());
        }
        Server newServer = new Server(card);
        hq.getAssets().remove(card);
        c_servers.add(newServer);
        System.out.println("Corp installs " + card.getName() + " on a new server");
        if ("Haas-Bioroid: Engineering the Future".equals(corpName) && !usedAbility) {
            usedAbility = true;
            gainCred();
        }
        return true;
    }
    public void discardDownToLimit() {
        debugPrint("debug discardDownToLimit");
        int i = 0;
        int j = 0;
        //eventually, sort by priority and discard lowest; for now, anything not an agenda
        while (hq.getAssets().size() > c_handLimit) {
            shuffleHand();
            debugPrint("Asset size " + hq.getAssets().size());
            debugPrint("hand limit " + c_handLimit);
            if (!hq.getAssets().get(0).isAgenda() || j>5) {
                trashCard(hq.getAssets().remove(0));
                i++;
            }
            j++;
        }
        if (i > 1) {
            System.out.println("Corp discards " + i + " cards");
        } else if (i == 1) {
            System.out.println("Corp discards a card");
        }
    }
    public void trashCardFromServer(CorpCard card, Server server) {
        debugPrint("debug trashCardFromServer");
        if (server.isRemote() && server.getAsset() != null && !server.getAsset().isAgenda() && !server.getAsset().isTrap() && !server.getAsset().isRezzed()) {
            refundCreds(server.getAsset().getCost());
        }
        server.getAssets().remove(card);
        server.removeAsset();
        trashCard(card);
    }
    public void stealCardFromServer(CorpCard card, Server server) {
        debugPrint("debug stealCardFromServer");
        server.getAssets().remove(card);
        server.removeAsset();
        if ("Other Jinteki".equals(corpName)) {
            System.out.println("Corp deals one net damage to runner");
        }
    }
    // =========================================================== CorpCard-Triggered Actions
    public void gainCreds(int creds) {
        debugPrint("debug gainCreds");
        c_creds = c_creds + creds;
        c_displayCreds = c_displayCreds + creds;
    }
    public void gainClicks(int clicks) {
        debugPrint("debug gainClicks");
        c_clicks = c_clicks + clicks;
    }
    public boolean useSpecialCard(List<CorpCard> playable) {
        debugPrint("debug useSpecialCard");
        CardAbility ca = new CardAbility();
        List<String> cardsForClickNumber = ca.getCardsForClickNumber(c_clicks);
        if (cardsForClickNumber == null) {
            return false;
        }
        for (Server server : c_servers) {
            CorpCard card = server.getAsset();
            if (card != null && cardsForClickNumber.contains(card.getName())) {
                return ca.activate(card, this);
            }
        }
        for (CorpCard card : playable) {
            if (card.isOperation() && cardsForClickNumber.contains(card.getName())) {
                return ca.activate(card, this);
            }
        }
        return false;
    }
    public boolean usePreAgendaSpecialCard() {
        debugPrint("debug usePreAgendaSpecialCard");
        CardAbility ca = new CardAbility();
        List<String> preAgendaCards = ca.getPreAgendaCards();
        if (preAgendaCards == null) {
            return false;
        }
        for (CorpCard card : hq.getAssets()) {
            if (card.isOperation() && preAgendaCards.contains(card.getActualName()) && card.getCost() <= getDisplayCreds()) {
                if (ca.activate(card, this)) {
                    spendCreds(card.getCost());
                    return true;
                }
            }
        }
        return false;
    }
    public boolean drawCorpCards(int n) {
        debugPrint("debug drawCorpCards");
        for (int i=0; i<n; i++) {
            CorpCard card = rnd.getAssets().remove(0);
            hq.getAssets().add(card);    
        }
        return true;
    }

    // =========================================================== Utility Functions
    public void cleanupServers() {
        List<Server> serversToRemove = new ArrayList<Server>();
        for (Server server : c_servers) {
            if (server.isRemote() && server.getIce().isEmpty() && server.getAssets().isEmpty() && server.getAsset() == null) {
                serversToRemove.add(server);
            }
        }
        for (Server server : serversToRemove) {
            c_servers.remove(server);
        }
    }
    public boolean cleanupServer(int serverNumber) {
        Server server = getServerByNumber(serverNumber);
        if (server.isRemote() && server.getIce().isEmpty() && server.getAssets().isEmpty() && server.getAsset() == null) {
           c_servers.remove(server);
           return true;
        }
        return false;
    }
    public void trashCard(CorpCard card) {
        archives.getAssets().add(card);
    }
    public boolean mulligan() {
        return (getCorpCardsByType(getHQ().getAssets(), "ICE").size() < 2);
    }
    public void shuffleHand() {
        debugPrint("debug shuffleHand");
        Collections.shuffle(getHQ().getAssets());
    }
    public void millRnD() {
        debugPrint("debug milling RnD");
        trashCard(rnd.getAssets().remove(0));
    }
    public boolean rezCard(CorpCard card) {
        debugPrint("debug rezCard");
        if (c_displayCreds >= card.getCost()) {
            c_displayCreds = c_displayCreds - card.getCost();
            card.rez();
            return true;
        } else {
            return false;
        }
    }
    public void addRunnerCard(String cardName) {
        RunnerCard runnerCard = new RunnerCard();
        runnerCard.setName(cardName);
        runnerCards.put(cardName, runnerCard);
        System.out.println("Runner installs " + cardName);
    }
    public void removeRunnerCard(String cardName) {
        if (runnerCards.containsKey(cardName)) {
            runnerCards.remove(cardName);
            System.out.println("Runner trashes " + cardName);
        } else {
            System.out.println("No card with that name exists.");
        }
    }
    public boolean scoreOrAdvanceAgenda() {
        debugPrint("debug scoreOrAdvanceAgenda");
        for (Server server : c_servers) {
            CorpCard asset = server.getAsset();
            if (asset != null && asset.isAgenda()) {
                int advancementNeeded = asset.getCost() - asset.getAdvancement();
                if (advancementNeeded <= c_clicks && advancementNeeded <= c_creds) {
                    if (advancementNeeded == 1 && advanceCorpCard(asset, 1)) {
                        asset.rez();
                        System.out.println("Corp scores " + asset.getName() + " for " + asset.getScoreValue() + " points");
                        corpScore = corpScore + asset.getScoreValue();
                        server.getAssets().remove(asset);
                        server.removeAsset();
                        c_agendas.add(asset);
                        CardAbility ca = new CardAbility();
                        ca.activate(asset, this);
                        return true;
                    } else {
                        return advanceCorpCard(asset, 1);
                    }
                } 
                if (advancementNeeded > getMaxClicks() && c_creds >= advancementNeeded && c_clicks >= (advancementNeeded-getMaxClicks())) {
                    return advanceCorpCard(asset, 1);
                }
            }
        }
        return false;
    }
    public boolean advanceTrap() {
        debugPrint("debug advanceTrap");
        for (Server server : c_servers) {
            CorpCard asset = server.getAsset();
            if (asset != null && asset.isTrap() && asset.isAdvanceable()) {
                int advancementNeeded = 2 - asset.getAdvancement();
                if (advancementNeeded == 0) {
                    asset.trapCounter++;
                }
                if (asset.trapCounter == 5) {
                    trashCardFromServer(asset, server);
                }
                if (advancementNeeded <= c_clicks && advancementNeeded <= c_creds && advancementNeeded != 0) {
                    return advanceCorpCard(asset, 1);
                }
                /*
                if (advancementNeeded == 4 && c_creds >= 4 && c_clicks == 1) {
                    return advanceCorpCard(asset, 1);
                }
                if (advancementNeeded == 5 && c_creds >= 5 && c_clicks == 2) {
                    return advanceCorpCard(asset, 1);
                }
                */
            }
        }
        return false;
    }
    public Server getServerByNumber(int serverNumber) {
        debugPrint("debug getServerByNumber");
        return c_servers.get(serverNumber);
    }
    public CorpCard getMoneyAsset() {
        debugPrint("debug getMoneyAsset");
        CorpCard moneyAsset = null;
        for (Server s : c_servers) {
            if (s.getAsset() != null && s.getAsset().isMoneyAsset()) {
                moneyAsset = s.getAsset();
                break;
            }
        }
        return moneyAsset;
    }
    public CorpCard getCardAsset() {
        debugPrint("debug getCardAsset");
        CorpCard cardAsset = null;
        for (Server s : c_servers) {
            if (s.getAsset() != null && s.getAsset().isCardAsset()) {
                cardAsset = s.getAsset();
                break;
            }
        }
        return cardAsset;
    }
    public List<Server> getWeakServers() {
        ArrayList<Server> weakServers = new ArrayList<Server>();
        for (Server server : c_servers) {
            if ((server.isRnD() ||server.isHQ()) && server.getIce().isEmpty()) {
                weakServers.add(server);
            }
        }
        weakServers.addAll(serversAccessed);
        return weakServers;
    }
    public List<CorpCard> getCorpCardsByType(List<CorpCard> playable, String type) {
        debugPrint("debug getCorpCardsByType");
        List<CorpCard> cardList = new ArrayList<CorpCard>();
        
        for (CorpCard card : playable) {
            if (type.equals(card.getType())) {
                cardList.add(card);
            }
        }
        return cardList;
    }
    public List<CorpCard> getCorpAssetsByAttribute(List<CorpCard> playable, String attribute) {
        debugPrint("debug getCorpAssetsByAttribute");
        List<CorpCard> cardList = new ArrayList<CorpCard>();
        for (CorpCard card : playable) {
            if (card.hasAttribute(attribute)) {
                cardList.add(card);
            }
        }
        return cardList;
    }
    public List<CorpCard> getPlayableCorpCards() {
        debugPrint("debug getPlayableCorpCards");
        List<CorpCard> cardList = new ArrayList<CorpCard>();
        for (CorpCard card : hq.getAssets()) {
            int cost = card.getCost();
            if (card.isIce()){
                cardList.add(card);
            } else if (cost <= c_creds && c_clicks > 0) {
                cardList.add(card);
            }
        }
        return cardList;
    }
    
    // =========================================================== Probability Tests 
    public boolean oddsDrawMoneyCorpCard() {
        int prob = 51;
        if (prob > DRAW_MONEY_CARD_PROB){
            return true;
        } else {
            return false;
        }
    }

    // =========================================================== Evaluation functions
    public boolean isSuitableForAgenda(Server emptyServer) {
        debugPrint("debug isSuitableForAgenda");
        if (emptyServer.getIce().size() > 1 && !serversAccessed.contains(emptyServer)) {
            for (CorpCard ice : emptyServer.getIce()) {
                if (ice.hasAttribute("Agenda")) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }
    public Server getBestOpenServer() {
        debugPrint("debug getBestOpenServer");
         for (Server server : c_servers) {
            if (!server.getIce().isEmpty() && server.isRemote() && server.getAsset() == null) {
                return server;
            }
        }
        return null;
    }
    public Server getBestWeakServer(List<Server> weakServers, int agendasInHand) {
        debugPrint("debug getBestWeakServer");
        return weakServers.get(0);
    }
    public CorpCard getBestAgenda(List<CorpCard> playableAgendas) {
        debugPrint("debug getBestAgenda");
        return playableAgendas.get(0);
    }
    public CorpCard getBestIce(List<CorpCard> playableIce, Server server, String targetServer) {
        debugPrint("debug getBestIce");
        //takes into account if it can be rezzed
        String iceTypes = "";
        if (server != null) {
            List<CorpCard> ice = server.getIce();
            if (ice != null) {
                for (CorpCard card : ice) {
                    iceTypes = iceTypes + card.getSubType() + ",";
                }
            }
        }
        if (playableIce != null) {
            CorpCard[] topThree = new CorpCard[4];
            for (CorpCard card : playableIce) {
                if ("Agenda".equals(targetServer) && card.hasAttribute("Agenda")) {
                    topThree[0] = card;
                } else if ("RnD".equals(targetServer) && card.hasAttribute("RnD")) {
                    topThree[0] = card;
                } else if ("HQ".equals(targetServer) && card.hasAttribute("HQ")) {
                    topThree[0] = card;
                }
                if (!"".equals(iceTypes) && !iceTypes.contains(card.getSubType())) {
                    topThree[1] = card;
                }
                if ("Agenda".equals(targetServer) && card.hasAttribute("ETR")) {
                    topThree[2] = card;
                } else if ("RnD".equals(targetServer) && card.hasAttribute("DMG")) {
                    topThree[2] = card;
                } else if ("HQ".equals(targetServer) && card.hasAttribute("ETR")) {
                    topThree[2] = card;
                }
                if (card.getCost() < c_creds) {
                    topThree[3] = card;
                }
            }
            for (int i=0; i<4; i++) {
                if (topThree[i] != null) {
                    debugPrint("debug BestIce number " + i + " is " + topThree[i].getActualName());
                    return topThree[i];
                }
            }
            return playableIce.get(0);
        }
        return null;
    }
    public CorpCard getBestOperation(List<CorpCard> playableOperations) {
        for (CorpCard card : playableOperations) {
            if (!card.getActualName().equals("Trick of Light")) {
                return card;
            }
        }
        return null;
    }
    public CorpCard getBestAsset(List<CorpCard> playableAssets) {
        return playableAssets.get(0);
    }
    public boolean assetNeedsIce(CorpCard asset) {
        return asset.needsIce();
    }
    public boolean tooManyViruses() {
        int totalVirus = 0;
        for (RunnerCard card : runnerCards.values()) {
            if ("Darwin".equals(card.getName()) && card.getVirusCounters() > 3) {
                return true;
            } else if ("Medium".equals(card.getName()) && card.getVirusCounters() > 3) {
                return true;
            } else {
                totalVirus = totalVirus + card.getVirusCounters();
            }
        }
        return (totalVirus > 8);
    }
    public void reactToRunner() {

    }

// =========================================================== Spend Click functions
public boolean tryPlayingCard(List<CorpCard> playable) {
    debugPrint("debug tryPlayingCard");
    if (!playable.isEmpty()) {
            List<Server> weakServers = getWeakServers();
            List<CorpCard> iceCorpCards = getCorpCardsByType(playable, "ICE");
            List<CorpCard> assetCorpCards = getCorpCardsByType(playable, "Asset");
            List<CorpCard> agendaCorpCards = getCorpAssetsByAttribute(playable, "Advanceable");
            List<CorpCard> operationCorpCards = getCorpCardsByType(playable, "Operation");
            Server openServer = getBestOpenServer();
            debugPrint("debug weakServers " + weakServers.size());
            debugPrint("debug icecards " + iceCorpCards.size());
            if (c_clicks > weakServers.size()) {
                //Play operation 
                if (!operationCorpCards.isEmpty()) {
                    CorpCard bestOperation = getBestOperation(operationCorpCards);
                    if (bestOperation != null && playOperation(bestOperation)) {
                        return true;
                    }
                }
                //Play agenda
                if (!agendaCorpCards.isEmpty()) {
                    CorpCard bestAgenda = getBestAgenda(agendaCorpCards);
                    if (openServer == null && !iceCorpCards.isEmpty() && weakServers.isEmpty()) {
                        CorpCard bestIce = getBestIce(iceCorpCards, null, "Agenda");
                        if (bestIce != null && createServer(bestIce)) {
                            return true;
                        }
                    }
                    if (openServer != null && !isSuitableForAgenda(openServer) && !iceCorpCards.isEmpty() && weakServers.isEmpty()) {
                        CorpCard bestIce = getBestIce(iceCorpCards, openServer, "Agenda");
                        if (bestIce != null && installCard(openServer, bestIce)) {
                            return true;
                        }
                    }
                    if (openServer != null && isSuitableForAgenda(openServer) && installCard(openServer, bestAgenda)) {
                        return true;
                    } 
                }
                //Play asset
                if (!assetCorpCards.isEmpty()) {
                    CorpCard bestAsset = getBestAsset(assetCorpCards);
                    if (openServer != null && bestAsset != null && assetNeedsIce(bestAsset) && installCard(openServer, bestAsset)) {
                        return true;
                    } else if (assetNeedsIce(bestAsset) && !iceCorpCards.isEmpty() && weakServers.isEmpty()) {
                        CorpCard bestIce = getBestIce(iceCorpCards, null, "Asset");
                        if (bestIce != null && createServer(bestIce)) {
                            return true;
                        }
                    } else if (!assetNeedsIce(bestAsset)) { 
                        if (createServer(bestAsset)) {
                            return true;
                        }
                    }
                        /*
                    else { //////// this is controvertial - do you install a normally iced asset if you have no ice? safer choice above - can be done w/ some randomness
                        if (createServer(bestAsset)) {
                            return true;
                        }
                    }
                                            */
                }
                // Play ice (strengthen)
            } else if (!iceCorpCards.isEmpty()) {
                debugPrint("debug strengthen");
                Server weakServer = getBestWeakServer(weakServers, agendaCorpCards.size());
                CorpCard bestIce = getBestIce(iceCorpCards, weakServer, weakServer.getName());
                if (bestIce != null && installCard(weakServer, bestIce)) {
                    serversAccessed.remove(weakServer);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean spendClick() {
        debugPrint("debug spendClick");

        List<CorpCard> playable = getPlayableCorpCards();
        CorpCard moneyAsset = getMoneyAsset();
        CorpCard cardAsset = getCardAsset();

        if (usePreAgendaSpecialCard()) {
            return true;
        }

        //Score agenda
        if (scoreOrAdvanceAgenda()) {
            return true;
        }
        if (advanceTrap()) {
            return true;
        }

        reactToRunner();

        if (c_clicks > 2) {
            if (clearVirusCounters()) {
                return true;
            }
        }

        if (tryPlayingCurrent()) {
            return true;
        }

        //Look for special cards
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (useSpecialCard(playable)) {
            return true;
        }

        //Play a card
        if (tryPlayingCard(playable)) {
            return true;
        }

        //Nothing to play
        //Draw a card
         if (hq.getAssets().isEmpty() || (c_creds > 15) || (moneyAsset == null && oddsDrawMoneyCorpCard() && hq.getAssets().size() < c_handLimit)) {
            if (cardAsset == null) {
                if (drawCorpCard()) {
                    return true;
                }
            } else {
                CardAbility ca = new CardAbility();
                if (ca.activate(cardAsset, this)) {
                    return true;
                }
            }
        //Use money asset
        } 
        if (moneyAsset != null) {
            if (moneyAsset.activate()) {
                return true;
            }
        } 
        //Gain a money
        if (gainCred()) {
            return true;
        }
        return false; //error
    }
    public void debugPrint(String s) {
        if (debugMode) {
            System.out.println(s);
        }
    }
}