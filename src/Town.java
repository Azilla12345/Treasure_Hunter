/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    boolean searched = false;
    boolean gameOver = false;
    boolean easy = false;

    boolean hasDug = false;


    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        searched = false;


        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        hasDug = false;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        hasDug = false;
        int i = (int)((Math.random() *4));
        if (i == 1) {
            treasure = "crown";
        } else if (i == 2) {
            treasure = "trophy";
        } else if (i == 3) {
            treasure = "gem";
        } else {
            treasure = "dust";
        }

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
            if (!(easy)) {
                if (checkItemBreak()) {
                    hunter.removeItemFromKit(item);
                    if (item.equals("horse") || (item.equals("rope"))) {
                        printMessage += "\nUnfortunately, you lost your " + item;
                    } else {
                        printMessage += "\nUnfortunately, your " + item + " broke";
                    }
                }
            }
            hasDug = false;
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        printMessage = "You exit the shop";
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    public void dig() {
        if ((!hasDug)) {
            if (hunter.hasItemInKit("shovel")) {
                int foundAnything = (int) (Math.random() * 2) + 1;
                if (foundAnything == 1) {
                    int goldFound = (int) (Math.random() * 20) + 1;
                    System.out.println("You dug up " + goldFound + " gold!");
                    hunter.changeGold(goldFound);
                } else {
                    System.out.println("You dug but only found dirt");
                }
                hasDug = true;
            } else {
                System.out.println("You can not dig without a shovel");
            }
        } else {
            System.out.println("You already dug for gold in this town!");
        }
    }

    public void huntForTreasure() {
        if (!(searched)) {
            System.out.println("You found a " + treasure);
            if (!(hunter.hasItemInKit(treasure))) {
                if (!(treasure.equals("dust"))) {
                    hunter.addItems(treasure);
                    if (hunter.hasItemInKit("crown") && (hunter.hasItemInKit("trophy") && (hunter.hasItemInKit("gem")))) {
                        System.out.println("Congratulations, you have found the last of the three treasures, you win!");
                        gameOver = true;
                    }
                }
            } else {
                System.out.println("you have already collected this item.  ");
            }
            searched = true;
        } else {
            System.out.println("You have already searched this town");
        }
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < (1/6)) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < (2/6)) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < (3/6)) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < (4/6)) {
            return new Terrain("Desert", "Water");
        } else if  (rnd < (5/6)){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }

    }
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
    public boolean isGameOver() {
        return gameOver;
    }
}