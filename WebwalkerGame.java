import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;

public class WebwalkerGame extends JFrame  {
    private static final long serialVersionUID = 123456789;
    private int beat;
    private boolean songOver;
    private boolean mp3Loaded = false;
    private static Corp corp = null;
    private static boolean debugMode = false;
    public int runnerClicks = 4;
    public int runnerPoints = 0;
    public boolean runnerTurn;
    public String status = "Welcome to Cyborg - Webwalker";
    public boolean menuShowing = false;
    public static JFrame frame;
    public JLabel creditsLabel;
    public JLabel clicksLabel;
    public JLabel hqLabel;
    public JLabel currentLabel;
    private static final Font SERIF_FONT = new Font("serif", Font.PLAIN, 24);
    //public static final Font smallFont = new Font("serif", Font.PLAIN, 20);
    //public static final Font bigFont = new Font("serif", Font.PLAIN, 30);

    public static void main (String [] args) throws IOException {

        
        frame = new WebwalkerGame(args);
    }

    public WebwalkerGame (String [] args) {


        //Window and layout setup - content, controls, and settings panels
        setLocation (100, 100);
        setSize (1600, 800);
        setDefaultCloseOperation (EXIT_ON_CLOSE);
        final Container content = getContentPane();
        content.setLayout (new BorderLayout());
        JPanel bottomPanel = new JPanel ();
        JPanel leftPanel = new JPanel ();
        JPanel rightPanel = new JPanel ();
        JPanel topPanel = new JPanel ();
        content.add (rightPanel, BorderLayout.EAST);
        content.add (topPanel, BorderLayout.NORTH);
        content.add (bottomPanel, BorderLayout.SOUTH);
        content.add (leftPanel, BorderLayout.WEST);
        //rightPanel.setBorder (new LineBorder(Color.BLACK, 2));
        leftPanel.setPreferredSize(new Dimension(20, 2000));
        bottomPanel.setPreferredSize(new Dimension(2000, 30));
        rightPanel.setPreferredSize(new Dimension(300, 2000));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        Font bigFont = null;
        Font smallFont = null;
        try {
            bigFont = Font.createFont(Font.TRUETYPE_FONT, new File("img\\font.ttf"));
            bigFont = bigFont.deriveFont(Font.PLAIN,30);
            smallFont = Font.createFont(Font.TRUETYPE_FONT, new File("img\\font.ttf"));
            smallFont = smallFont.deriveFont(Font.PLAIN,15);
            GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(bigFont);
            ge.registerFont(smallFont);
        } catch (Exception e) {

        }
       
        //topPanel.setBorder (new LineBorder(Color.BLACK, 2));
        topPanel.setLayout (new FlowLayout());

        creditsLabel = new JLabel("***Corp Stats***      ");
        creditsLabel.setFont(bigFont);
        topPanel.add(creditsLabel);
        creditsLabel = new JLabel("Credits: 5      ");
        creditsLabel.setFont(bigFont);
        topPanel.add(creditsLabel);
        clicksLabel = new JLabel("Clicks: 3      ");
        clicksLabel.setFont(bigFont);
        topPanel.add(clicksLabel);
        hqLabel = new JLabel("HQ: 5/5      ");
        hqLabel.setFont(bigFont);
        topPanel.add(hqLabel);
        currentLabel = new JLabel("Current: None");
        currentLabel.setFont(bigFont);
        topPanel.add(currentLabel);
        JTextArea statusLabel = new JTextArea (status);
        statusLabel.setMargin(new Insets(5,5,5,5));
        statusLabel.setEditable(false);
        statusLabel.setFont(smallFont);
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setForeground(Color.DARK_GRAY);
        statusLabel.setBackground(Color.WHITE);
        statusLabel.setForeground(Color.BLACK);
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setBackground(Color.LIGHT_GRAY);

        CardAbility.getInstance().setFrame(frame);

        //rightPanel.add (statusLabel);
        //JTextField consoleField = new JTextField ("");
        //consoleField.setFont(new Font("Agency FB", Font.BOLD, 20));

        JScrollPane scrollPane = new JScrollPane(statusLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(scrollPane);

        if (args.length == 0) {
            System.out.println("Please give a Corp deck file name");
            return;
        }
        String corpDeckFilename = args[0];
        if (args.length == 2) {
            debugMode = ("debug".equals(args[1]));
        }

        Map<String, ArrayList<CorpCard>> deckWithIdentity = buildDeck(corpDeckFilename);
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
        int turns = 0;

        content.add(corp, BorderLayout.CENTER);
        corp.setBorder (new LineBorder(Color.DARK_GRAY, 3));
        content.addMouseListener(new CardZoomer(corp));
        setVisible (true);
        refreshBoard();

        while (runnerPoints < 7 && corp.getCorpScore() < 7) {                
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            // IMPORTANT: Save the old System.out!
            PrintStream old = System.out;
            // Tell Java to use your special stream
            System.setOut(ps);
            status = "*** " + (turns++) + " ***\nCorp begins turn and draws";  
            corp.resetClicks(corp.getMaxClicks());
            corp.preTurn();
            corp.drawCorpCards(1);

            System.out.flush();
            System.setOut(old);
            status =  status + "\n" + baos.toString();
            statusLabel.setText(status);
            statusLabel.setCaretPosition(statusLabel.getCaretPosition()+statusLabel.getText().length());
            refreshBoard();
           
            while (corp.getClicks() > 0) {
                try {
                    Thread.sleep(2000);
                } 
                catch (InterruptedException e) {
                }

                baos = new ByteArrayOutputStream();
                ps = new PrintStream(baos);
                // IMPORTANT: Save the old System.out!
                old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);

                System.out.println("Click " + (corp.getMaxClicks()-corp.getClicks()+1) + ": ");
                boolean clickSpent = corp.spendClick();


                 // Create a stream to hold the output
            

                if (clickSpent) {
                    // Put things back
                    System.out.flush();
                    System.setOut(old);
                    status =  status + "\n" + baos.toString();
                    statusLabel.setText(status);
                    statusLabel.setCaretPosition(statusLabel.getCaretPosition()+statusLabel.getText().length());
                    updateCorpStatus();

                    corp.removeClick();
                    corp.cleanupServers();
                    Scanner reader = new Scanner(System.in);
                    refreshBoard();
                }
            }
            System.out.println("Corp ends turn");
            corp.discardDownToLimit();
            hqLabel.setText("HQ: " + corp.getHandCount() + "/" + corp.getHandLimit() + "      ");
            
            status = status + "\n\nRunner's turn begins\n";  
            statusLabel.setText(status);
            statusLabel.setCaretPosition(statusLabel.getCaretPosition()+statusLabel.getText().length());
            runnerClicks = 3;
            //renderCorpBoard(corp);

            for (Server s : corp.getServers()) {
                if (s.isRemote()) {
                    for (CorpCard asset : s.getAssets()) {
                        if ("Sundew".equals(asset.getActualName())) {
                            if (!asset.isRezzed() && !(asset.getCost() > corp.getDisplayCreds())) { 
                                asset.rez();
                            }
                            CardAbility.getInstance().activate(asset, corp, s);
                        }
                    }
                }
            }
            runnerTurn = true;
            while (runnerClicks > 0) {
                baos = new ByteArrayOutputStream();
                ps = new PrintStream(baos);
                // IMPORTANT: Save the old System.out!
                old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                try {
                    Thread.sleep(100);
                } 
                catch (InterruptedException e) {
                }
                    // Put things back
                    System.out.flush();
                    System.setOut(old);
                    if (!baos.toString().isEmpty()) {
                        status =  status + baos.toString();
                        statusLabel.setText(status);
                        statusLabel.setCaretPosition(statusLabel.getCaretPosition()+statusLabel.getText().length());
                    }
                    refreshBoard();
            }
            runnerTurn = false;
        }
        String winner = (runnerPoints > corp.getCorpScore()) ? "runner" : "corp";
        JOptionPane.showMessageDialog(frame, "***** The " + winner + " wins the match! *****");
    }

    public void updateCorpStatus() {
        creditsLabel.setText("Credits: " + corp.getDisplayCreds() + "      ");
        clicksLabel.setText("Clicks: " + corp.getClicks() + "      ");
        hqLabel.setText("HQ: " + corp.getHandCount() + "/" + corp.getHandLimit() + "      ");

        Card current = corp.getCurrent();
        if (current != null) {
            currentLabel.setText("Current: " + current.getName() + " (" + current.getSide() + ")");
        }
    }

    //public void executeCommand(String command, Corp corp, int runnerClicks, int runnerPoints) {
    public void executeCommand(String command, Card card) {
        if ("rez ice".equals(command)) {
            CorpCard ice = (CorpCard) card;
            if (ice != null && !ice.isRezzed()) {
                //System.out.println();
                int extraRezCost = getIntFromUser("Additional Rez Cost: ", 0, 10);
                if (extraRezCost != -2) {
                    if (corp.getDisplayCreds() >= (ice.getCost() + extraRezCost)) {
                        corp.spendReservedCreds(ice.getCost());
                        corp.spendCreds(extraRezCost);
                        ice.rez();
                        //renderCorpBoard(corp);
                        System.out.println("Corp rezzes " + ice.getName() + "\n");
                    } else {
                        //renderCorpBoard(corp);
                        System.out.println("Corp does not rez ice\n");
                    }
                }
            }
        } else if ("derez ice".equals(command)) {
            CorpCard ice = (CorpCard) card;
            if (ice != null && ice.isRezzed()) {
                ice.derez();
                corp.reserveCreds(ice.getCost());
                //renderCorpBoard(corp);
                refreshBoard();
                System.out.println("Corp forced to derez " + ice.getActualName());
            }
        }else if ("trash ice".equals(command)) {
            int yn = getYesNoFromUser("Are you sure you want\nto trash this ICE?");
            if (yn == 0) {
                System.out.println("Corp forced to trash " + card.getName());
                corp.trashIceFromServer((CorpCard) card, corp.getServerByNumber(((CorpCard) card).getServerNumber()));
            }                
        } else if ("end turn".equals(command) || "end".equals(command)) {
            runnerClicks = 0;
        } else if ("access server".equals(command)) {
            CorpCard nisei = getCardByName(corp.getScoredAgendas(), "Nisei MK II");
            Server server = corp.getServerByNumber(((CorpCard)card).getServerNumber());
            if (nisei != null && nisei.getCounters() > 0 && CardAbility.getInstance().useNisei(corp, server)) {
                System.out.println("Corp ends run using Nisei agenda counter");
                nisei.setCounters(nisei.getCounters() - 1);
                if (nisei.getCounters() == 0) {
                    corp.removeAgenda(nisei);
                }
            } else {
                runnerPoints = accessCardsFromServer(corp.getServerByNumber(((CorpCard)card).getServerNumber()), runnerPoints);
            }
            boolean serverRemoved = corp.cleanupServer(((CorpCard)card).getServerNumber());
            int isSneakdoor = -11;
            if (((CorpCard)card).getServerNumber() == 3 && corp.getRunnerCardByName("Sneakdoor Beta") != null) {
                System.out.println();
                isSneakdoor = getYesNoFromUser("Was Sneakdoor Beta used?");
            }
            if (isSneakdoor == 0) {
                corp.addServerAccessed(corp.getServerByNumber(0)); //archives is weak
            } else if (!serverRemoved) {
                corp.addServerAccessed(corp.getServerByNumber(((CorpCard)card).getServerNumber()));
            }
            corp.cleanupServers();
        } else if ("expose asset".equals(command)) {
            if (corp.getServers().size() > 3) {
                System.out.println("Which server?");
                int serverNumber = getIntFromUser("Which server?", 4, corp.getServers().size());
                CorpCard asset = corp.getServerByNumber(serverNumber-1).getAsset();
                System.out.println("Corp forced to expose " + asset.getActualName());
            } else {
                System.out.println("No installed assets to expose");
            }
        } else if ("expose card".equals(command)) {
            //CorpCard ice = getIceCard(corp, false);
            ImageIcon icon = new ImageIcon("img\\" +  ((CorpCard) card).getActualName() + ".png");
            JOptionPane.showMessageDialog(
                        null,
                        "",
                        "Revealed Card", JOptionPane.INFORMATION_MESSAGE,
                        icon);
            System.out.println("Corp forced to expose " + ((CorpCard) card).getActualName());
        } else if ("trash top card".equals(command)) {
            corp.millRnD();
            System.out.println("Corp forced to trash top card of RnD");
        } else if ("adjust creds".equals(command)) {
            System.out.println("How many creds?");
            int creds = getIntFromUser("How many creds?",-10,10);
            corp.gainCreds(creds);
            System.out.println("Corp gains " + creds + " creds.");
        } else if ("install program".equals(command)) {
            corp.boardHeight = this.getContentPane().getHeight();
            String cardName = getStringFromUser("What program?");
            corp.addRunnerCard(cardName);
            refreshBoard();
        } else if ("trash program".equals(command)) {
            corp.removeRunnerCard(card);
        } else if ("add virus".equals(command)) {
            RunnerCard rc = (RunnerCard) card;
            rc.addVirusCounter();
            System.out.println(rc.getName() + " now has " + rc.getVirusCounters() + " virus counters");
        } else if ("play current".equals(command)) {
            String cardName = getStringFromUser("What current?");
            Card current = new Card();
            current.setSide("Runner");
            current.setName(cardName);
            corp.setCurrent(current);
            System.out.println("Runner plays current " + cardName);
            //renderCorpBoard(corp);
        } else if ("host program".equals(command)) {
            String cardName = getStringFromUser("What program?");
            if (cardName != null && !cardName.isEmpty()) {
                ((CorpCard) card).setHostedCard(cardName);
            }
            //renderCorpBoard(corp);
        } else if ("unhost program".equals(command)) {
            ((CorpCard) card).setHostedCard("");
            //renderCorpBoard(corp);
        } else if ("help".equals(command)) {
            System.out.println("\n****** Help Menu ******");
            System.out.println("Servers are numbered left to right, starting with 1.");
            System.out.println("ICE is numbered from top to bottom, starting with 1.");
            System.out.println("Valid commands:");
            //System.out.println("rez ice");
            System.out.println("derez ice");
            //System.out.println("trash ice");
            System.out.println("access server");
            //System.out.println("expose asset");
            //System.out.println("expose ice");
            //System.out.println("mill RnD");
            //System.out.println("adjust creds");
            System.out.println("install program");
            System.out.println("trash program");
            //System.out.println("add virus");
            //System.out.println("play current");
            System.out.println("host program");
            System.out.println("unhost program");
            //System.out.println("end turn\n");
        } else if ("^C".equals(command)) {
            System.out.println("Command \"" + command + "\" not recognized.  Type \"help\" for ArrayList of valid commands.");
        } 
        else if (!"".equals(command)) {
            System.out.println("Command \"" + command + "\" not recognized.  Type \"help\" for ArrayList of valid commands.");
        }
         updateCorpStatus();
    }

    public static int getIntFromUser(String text, int min, int max) {
        int intFromUser = -11;
        while (intFromUser == -11) {
            intFromUser = getIntFromUserSafe(text, min, max);
            if (intFromUser == -12) {
                return -12;
            }
        }
        return intFromUser;
    }

    public static int getIntFromUserSafe(String text, int min, int max) {
        try {
            //Scanner reader = new Scanner(System.in);
            //int val = reader.nextInt();
            int val = -11;
            String s = (String)JOptionPane.showInputDialog(
                    frame,
                    text,
                    "Input Required",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
            if (s == null) {
                return -12;
            }
            val = Integer.parseInt(s);
            if (max == 0) {
                return 0;
            }
            if (val > max || val < min) {
                System.out.println("Incorrect input: out of range " + min + " to " + max);
                return -11;
            } else {
                return val;
            }
        } catch (Exception e) {
            System.out.println("Incorrect input: must be integer");
        }
        return -11;
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

    public static int getYesNoFromUser(String text) {
        return JOptionPane.showConfirmDialog(
            frame,
            text,
            "Input Required",
            JOptionPane.YES_NO_OPTION);
    }

    public static int accessCardsFromServer(Server server, int runnerPoints) {
        ArrayList<String> preAccessAssets = CardAbility.getInstance().getPreAccessAssets();
        ArrayList<CorpCard> serverAssets = server.getAssets();
        for (int i=serverAssets.size()-1; i<=0; i--) {
            if(serverAssets.size() > 0 && i >= 0) {
                CorpCard c = serverAssets.get(i);
                if (!server.isRnD() && preAccessAssets.contains(c.getActualName())) {
                    if (!c.isRezzed() && c.getCost() <= corp.getDisplayCreds()) {
                        corp.spendCreds(c.getCost());
                        c.rez();
                    }
                    CardAbility.getInstance().activate(c, corp, server);
                }
            } else {
                break;
            }
        }
        int success = getYesNoFromUser("Access successful?");
        if (success == 1) {
            return 0;
        }

        System.out.println("How many cards?");
        int numberAccessed = getIntFromUser("How many cards?",0,99);

        if (!server.isRnD()) {
            Collections.shuffle(server.getAssets());
        }
        ArrayList<CorpCard> cardsToTrash = new ArrayList<CorpCard>();
        ArrayList<CorpCard> cardsToSteal = new ArrayList<CorpCard>();
        numberAccessed = (numberAccessed > serverAssets.size()) ? serverAssets.size() : numberAccessed;
        System.out.println("Accessing " + numberAccessed + " cards.");
            for (int i=0; i<numberAccessed;i++) {
                CorpCard card = serverAssets.get(i);

                if (card.isAgenda()) {
                    System.out.println("Accessing: " + card.getActualName() + ". Steal or leave?");
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
                ImageIcon icon = new ImageIcon("img\\" +  ((CorpCard) card).getActualName() + ".png");
                Object[] possibilities = null;
                if (server.isArchives()) {
                    if (card.isAgenda()) {
                        Object[] agendaCommands = {"steal","continue","jack out"};
                        possibilities = agendaCommands;
                    } else {
                        Object[] otherCommands = {"continue", "jack out"};
                        possibilities = otherCommands;
                    }
                } else {
                    if (card.isAgenda()) {
                        Object[] agendaCommands = {"steal","trash (with special ability)","continue","jack out"};
                        possibilities = agendaCommands;
                    } else if (card.isAsset()) {
                        Object[] assetCommands = {"trash for " + card.getTrashCost(),"continue","jack out"};
                        possibilities = assetCommands;
                    } else {
                        Object[] otherCommands = {"trash (with special ability)", "continue", "jack out"};
                        possibilities = otherCommands;
                    }
                }
                String command = (String)JOptionPane.showInputDialog(
                    frame,
                    null,
                    "Accessed Card",
                    JOptionPane.PLAIN_MESSAGE,
                    icon,
                    possibilities,
                    "continue");
                //String command = getStringFromUser("Access command");
                if (command != null && command.contains("trash")) {
                    cardsToTrash.add(card);
                } else if (command != null && "steal".equals(command)) {
                    boolean meetsAdditionalAgendaConditions = CardAbility.getInstance().meetsAdditionalAgendaConditions(corp, server);
                    if (card.isAgenda() && meetsAdditionalAgendaConditions &&  (!CardAbility.getInstance().getConditionalAgendas().contains(card.getActualName()) || (CardAbility.getInstance().getConditionalAgendas().contains(card.getActualName()) && CardAbility.getInstance().activate(card, corp, server, null, true)))) {
                        runnerPoints = runnerPoints + card.getScoreValue();
                        cardsToSteal.add(card);
                        System.out.println("Runner steals agenda and has " + runnerPoints + " points");
                    } else {
                        System.out.println("Cannot steal card");
                        if (!CardAbility.getInstance().getConditionalAgendas().contains(card.getActualName())) {
                            i--;
                        }
                    }
                } else if ("jack out".equals(command)) {
                    break;
                }
       //     }
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
        int serverNumber = getIntFromUser("Which server?", 1, corp.getServers().size());
        Server server = corp.getServers().get(serverNumber-1);
        System.out.println("Which position?");
        int iceNumber = getIntFromUser("Which position?",1, server.getIce().size());
        if (server != null && iceNumber != 0) {
            CorpCard ice = server.getIce().get(iceNumber-1);
            if (trash) {
                System.out.println("Corp forced to trash " + ice.getName());
                corp.trashIceFromServer(ice, server);
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
        int maxAssetLayer = 0;
        String[] assetLayers = new String[10];
        String assetLayer1 = "|| ";
        String assetLayer2 = "|| ";
        String assetLayer3 = "|| ";
        String assetLayer4 = "|| ";
        String layer1 = "|| ";
        String layer2 = "|| ";
        String layer3 = "|| ";
        String dividerLayer = "==";
        String spacingLayer = "|| ";
        int i = 0;
        for (Server server : corp.getServers()) {
            i++;
            String name = server.getName();
            
            /*
            if (server.getAsset() != null) {
                CorpCard asset = server.getAsset();
                name = (asset.getAdvancement() > 0) ? "(" + (asset.getAdvancement() + ") " + asset.getName()) : asset.getName();  
            } else if (server.getAssets != null()) {

            }
            assetLayer1 = assetLayer1 + padToN(name, columnWidth) + " || ";

*/

            ArrayList<CorpCard> ice = server.getIce();
            ArrayList<CorpCard> assets = null;

            if (server.isRemote()) {
                assets = server.getAssets();
            } else {
                assets = server.getUpgrades();
            }

            if (assets != null && assets.size() > 0) {
                name = (assets.get(0).getAdvancement() > 0) ? "(" + (assets.get(0).getAdvancement() + ") " + assets.get(0).getName()) : assets.get(0).getName();  
                assetLayer1 = assetLayer1 + padToN(name, columnWidth) + " || ";
                maxAssetLayer++;
            } else {
                assetLayer1 = assetLayer1 + "             || ";
            }
            if (assets != null && assets.size() > 1) {
                name = (assets.get(1).getAdvancement() > 0) ? "(" + (assets.get(1).getAdvancement() + ") " + assets.get(1).getName()) : assets.get(1).getName();  
                assetLayer2 = assetLayer2 + padToN(name, columnWidth) + " || ";
                maxAssetLayer++;
            } else {
                assetLayer2 = assetLayer2 + "             || ";
            }
            if (assets != null && assets.size() > 2) {
                name = (assets.get(2).getAdvancement() > 0) ? "(" + (assets.get(2).getAdvancement() + ") " + assets.get(2).getName()) : assets.get(2).getName();  
                assetLayer3 = assetLayer3 + padToN(name, columnWidth) + " || ";
                maxAssetLayer++;
            } else {
                assetLayer3 = assetLayer3 + "             || ";
            }
            if (assets != null && assets.size() > 3) {
                name = (assets.get(3).getAdvancement() > 0) ? "(" + (assets.get(3).getAdvancement() + ") " + assets.get(3).getName()) : assets.get(3).getName();  
                assetLayer4 = assetLayer4 + padToN(name, columnWidth) + " || ";
                maxAssetLayer++;
            } else {
                assetLayer4 = assetLayer4 + "             || ";
            }
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
            serverNumberLayer = serverNumberLayer + padToN(i + " - " + server.getName(), columnWidth) + " || ";
        }
        System.out.println("\n" + dividerLayer);
        if (maxAssetLayer == 4) {
            System.out.println(assetLayer4);
        }
        if (maxAssetLayer >= 3) {
            System.out.println(assetLayer3);
        }
        if (maxAssetLayer >= 2) {
            System.out.println(assetLayer2);
        }
        if (maxAssetLayer >= 1) {
            System.out.println(assetLayer1);
        }
        System.out.println(serverNumberLayer);
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
        ArrayList<CorpCard> hq = corp.getHQ().getAssets();
        for (CorpCard card : hq) {
            debugPrint((padToN("|| Card : " + card.getActualName(), dividerLayer.length() - 2) + "||")); 
        }
        ArrayList<Server> servers = corp.getWeakServers();
        for (Server server : servers) {
            debugPrint((padToN("|| Weak Server : " + server.getName(), dividerLayer.length() - 2) + "||")); 
        }
        Card current = corp.getCurrent();
        if (current != null) {
            String currentText = "|| Current: " + current.getName() + " (" + current.getSide() + ")";
            System.out.println(padToN(currentText, dividerLayer.length() - 2) + "||"); 
        }
        String identityText = "|| Identity: " + corp.getName();
        System.out.println(padToN(identityText, dividerLayer.length() - 2) + "||"); 
        if (corp.getName().contains("Replicating Perfection")) {
            identityText = "|| Reminder - RP requires run on a central server before a remote. ";
            System.out.println(padToN(identityText, dividerLayer.length() - 2) + "||"); 
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
        ArrayList<String> identities =  new ArrayList<String>();
        identities.addAll(Arrays.asList("Haas-Bioroid: Engineering the Future","Haas-Bioroid: Stronger Together","Jinteki - Replicating Perfection","Other Jinteki"));
        return identities.contains(identity);
    }
    public static Map<String, ArrayList<CorpCard>> buildDeck(String corpDeckFilename) {
        ArrayList<CorpCard> deck = new ArrayList<CorpCard>();
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
                    CorpCard card = new CorpCard(parts[1], parts[5]);
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
                    card.setSide("Corp");
                    
                    deck.add(card);
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("debug error building deck: " + e + " " + e.getMessage());
        }
        Map<String, ArrayList<CorpCard>> identityMap = new HashMap<String, ArrayList<CorpCard>>();
        identityMap.put(identity, deck);
        return identityMap;
    }

    public static void debugPrint(String s) {
        if (debugMode) {
            System.out.println(s);
        }
    }

    public static CorpCard getCardByName(ArrayList<CorpCard> cardList, String s) {
        for (CorpCard c : cardList) {
            if (s.equals(c.getActualName())) {
                return c;
            }
        }
        return null;
    }

        public class CardZoomer implements MouseListener{
            Corp corp = null;
            Server server = null;
            public CardZoomer(Corp corp) {
                this.corp = corp;
            }
            public void mouseClicked(MouseEvent e){

                /*
                for (int i=0; i<newSong.numStrings(); i++){
                    tuners[i] = x;
                    x+=100;
                }
                for (int i=0; i<newSong.numStrings(); i++){
                    if (e.getX() < tuners[i]+10 && e.getX() > tuners[i]-10 && e.getY() < 585 && e.getY() > 565){
                        newSong.setTuning(newSong.numStrings()-1 - i, -1); //1 is uptune, -1 is downtune
                        newSong.repaint();
                    }
                    else if (e.getX() < tuners[i]+10 && e.getX() > tuners[i]-10 && e.getY() < 525 && e.getY() > 510){
                        newSong.setTuning(newSong.numStrings()-1 - i, 1); //1 is uptune, -1 is downtune
                        newSong.repaint();
                    }
                }
                */
            }
            private void doPop(MouseEvent e){
                Card card = corp.getCardFromCoord(e.getX(), e.getY());
                RunnerContextMenu menu = new RunnerContextMenu(card);
                if (runnerTurn) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
                refreshBoard();
            }
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menuShowing = true;
                    doPop(e);
                }
                if(e.getButton() == MouseEvent.BUTTON1 && !menuShowing) {
                    int xCoord = e.getX();
                    int yCoord = e.getY();
                    int serverNumber = (e.getX()-50) / 150;
                    //System.out.println("hey server number " + serverNumber);
                    server = corp.getServerByNumber(serverNumber);
                    if (server != null) {
                        server.toggleZoom(xCoord, yCoord, true);
                    }
                    for (RunnerCard rc : corp.getRunnerCards()) {
                        rc.checkClickLocation(xCoord, yCoord, true);
                    }
                    refreshBoard();
                }
                menuShowing = false;
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menuShowing = true;
                    doPop(e);
                }
                if(e.getButton() == MouseEvent.BUTTON1 && !menuShowing) {
                    int xCoord = e.getX();
                    int yCoord = e.getY();
                    if (server != null) {
                        server.toggleZoom(xCoord, yCoord, true);
                    }
                    for (RunnerCard rc : corp.getRunnerCards()) {
                        rc.checkClickLocation(xCoord, yCoord, true);
                    }
                    refreshBoard();
                    server = null;
                }
            }
            public void mouseEntered(MouseEvent e) {};
            public void mouseExited(MouseEvent e) {};
    }
    public class RunnerContextMenu extends JPopupMenu {
    JMenuItem trashIceItem;
    JMenuItem endTurnItem;
    JMenuItem playCurrentItem;
    JMenuItem rezIceItem;
    JMenuItem derezIceItem;
    JMenuItem exposeCardItem;
    JMenuItem installProgramItem;
    JMenuItem trashProgramItem;
    JMenuItem trashTopCardItem;
    JMenuItem adjustCredsItem;
    JMenuItem addVirusItem;
    JMenuItem accessServerItem;
    JMenuItem hostProgramItem;
    JMenuItem unhostProgramItem;
        public RunnerContextMenu(Card card){
            trashIceItem = new JMenuItem("trash ice");
            endTurnItem = new JMenuItem("end turn");
            playCurrentItem = new JMenuItem("play current");
            rezIceItem = new JMenuItem("rez ice");
            derezIceItem = new JMenuItem("derez ice");
            exposeCardItem = new JMenuItem("expose card");
            installProgramItem = new JMenuItem("install program");
            trashProgramItem = new JMenuItem("trash program");
            trashTopCardItem = new JMenuItem("trash top card");
            adjustCredsItem = new JMenuItem("adjust creds");
            addVirusItem = new JMenuItem("add virus");
            accessServerItem = new JMenuItem("access server");
            hostProgramItem = new JMenuItem("host program");
            unhostProgramItem = new JMenuItem("unhost program");
            trashIceItem.addActionListener(new MenuActionListener(card));
            endTurnItem.addActionListener(new MenuActionListener(card));
            playCurrentItem.addActionListener(new MenuActionListener(card));
            rezIceItem.addActionListener(new MenuActionListener(card));
            derezIceItem.addActionListener(new MenuActionListener(card));
            exposeCardItem.addActionListener(new MenuActionListener(card));
            installProgramItem.addActionListener(new MenuActionListener(card));
            trashProgramItem.addActionListener(new MenuActionListener(card));
            trashTopCardItem.addActionListener(new MenuActionListener(card));
            adjustCredsItem.addActionListener(new MenuActionListener(card));
            addVirusItem.addActionListener(new MenuActionListener(card));
            accessServerItem.addActionListener(new MenuActionListener(card));
            hostProgramItem.addActionListener(new MenuActionListener(card));
            unhostProgramItem.addActionListener(new MenuActionListener(card));
            if (card == null) {
                add(playCurrentItem);
                add(installProgramItem);
                add(adjustCredsItem);
            }
            if (card != null && card.getType().isEmpty()) {
                add(addVirusItem);
                add(trashProgramItem);
            } else {
                if (card != null && "RnD".equals(((CorpCard)card).getActualName())) {
                    add(trashTopCardItem);
                }
                if (card != null && ((CorpCard)card).isIce()) {
                    add(trashIceItem);
                    if (((CorpCard)card).getHostedCard().isEmpty()) {
                        add(hostProgramItem);
                    } else {
                        add(unhostProgramItem);
                    }                 
                    if (((CorpCard)card).isRezzed()) {
                        add(derezIceItem);
                    } else {
                        add(rezIceItem);
                    }
                } 
                if (card != null && !((CorpCard)card).isIce()) {
                    add(accessServerItem);
                }
                if (card != null && !"RnD".equals(((CorpCard)card).getActualName()) && !"HQ".equals(((CorpCard)card).getActualName()) && !"Archives".equals(((CorpCard)card).getActualName())) {
                    add(exposeCardItem);
                }
            }
            add(endTurnItem);
        }
    }
    public class MenuActionListener implements ActionListener {
        Card card = null;
        public MenuActionListener (Card card) {
            this.card = card;
        }
        public void actionPerformed(ActionEvent e) {
            executeCommand(e.getActionCommand(), card);
        }
    }
    /*
    private static Font getFont(String name) {
        Font font = null;
        if (name == null) {
            return SERIF_FONT;
        }
        try {
            // load from a cache map, if exists
            if (fonts != null && (font = fonts.get(name)) != null) {
                return font;
            }
            File fontFile = new File("img\\" + getName() + ".png");
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            GraphicsEnvironment ge = GraphicsEnvironment
                    .getLocalGraphicsEnvironment();

            ge.registerFont(font);

            fonts.put(name, font);
        } catch (Exception ex) {
            System.out.println(name + " not loaded.  Using serif font.");
            font = SERIF_FONT;
        }
        return font;
    }
    */
    public void refreshBoard() {
        corp.boardHeight = this.getContentPane().getHeight();
        corp.repaint();
    }
}
