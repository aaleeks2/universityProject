package cwiczenia.projekt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class project {

    public static void main(String[] args) throws IOException {
        Container c = new Container();
        ContainerShip ship = new ContainerShip("MSC");
        c.writeContainersToFile((int) ship.getCapacity());
        Container[] containersToLoad = c.readContainersToArray();
        ship.sortAndLoad(containersToLoad);
        writeManifestFile(ship);
    }

    static void writeManifestFile(ContainerShip containerShip) throws IOException{
        int levelIndex = 0;
        int rowIndex = 0;
        int contPos = 0;

        FileWriter fileWriter = new FileWriter("Manifest.txt");
        for(Container[][] containers1 : containerShip.getBoard()){
            for(Container[] container2 : containers1){
                for(Container container: container2){
                    if(container!=null){
                        fileWriter.write("ID:"+container.id+"\t"+
                                "Type: " + container.type+"\t\t"+
                                "L:"+levelIndex+"\t"+" R:"+rowIndex+"\t"+" P:"+contPos+"\t"+
                                "TotalMass:"+new BigDecimal(container.load+container.mass).setScale(2, RoundingMode.HALF_UP)+"t\t"+
                                container.product.name+"\n");
                        contPos++;
                    } else {
                        break;
                    }
                }
                contPos=0;
                rowIndex++;
            }
            rowIndex=0;
            levelIndex++;
        }
        fileWriter.flush();
        fileWriter.close();
    }

    static Container[] sortByMassDescending(Container[] containers){
        for(int i = 0; i < containers.length-1; i++){
            for(int j=0; j<containers.length-i-1; j++){
                if((containers[j].mass + containers[j].load)<(containers[j+1].mass + containers[j+1].load)){
                    Container tempCont = containers[j];
                    containers[j]=containers[j+1];
                    containers[j+1] = tempCont;
                }
            }
        }
        return containers;
    }
}

class  Container{
    protected long id = (long)(Math.random()*100000)+100000;
    protected double height, width, length, load, mass;
    protected String type;
    protected String[] validProductTypes;
    protected Product product;
    protected boolean isOpened=false;

    public void setId(long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    protected boolean isValidProduct(String productCode){
        boolean result = false;
        for(String s : validProductTypes){
            if(productCode.startsWith(s)){
                result = true;
                break;
            }
        }
        return result;
    }

    Container generateRandomContainer(){
        int roll = (int)(Math.random()*6);
        Container container;
        switch (roll) {
            case 0 -> container = new Container20ft();
            case 1 -> container = new Container20ftHighCube();
            case 2 -> container = new Container20ftHardTop();
            case 3 -> container = new FlatRackContainer20ft();
            case 4 -> container = new RefrigeratedContainer20ft();
            case 5 -> container = new TankContainer();
            default -> throw new IllegalStateException("Unexpected value: " + roll);
        }
        return container;
    }

    protected void setRandomProduct(){
        Product product = new Product().generateRandomProduct();
        if(this.isValidProduct(product.productCode)){
            this.product= product;
        }else {
            setRandomProduct();
        }
    }

    void writeContainersToFile(int capacity) throws IOException{
        FileWriter fileWriter = new FileWriter("containers.txt");
        for (int i = 0; i < capacity; i++) {
            fileWriter.write(this.generateRandomContainer() + "\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

    @Override
    public String toString(){
        return "Container"+ "\t" + "type:" + type + "\t" + "productCode:" + product.productCode + "\t" + "id:" + id + "\t" + "height:" + height + "\t" + "width:" + width +
                "\t" + "length:" + length + "\t" + "capacity:" + load + "\t" +
                "mass:" + mass + "\t" ;
    }

    public Container() {
    }

    Container[] readContainersToArray() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("containers.txt"));
        String containerType ="";
        String containerProduct="";
        String containerId="";
        int i =0;
        Container[] containers = new Container[15000];
        String s;
        while((s = br.readLine()) != null) {
            String[] split = s.split("\t");
            for (String string : split) {
                if (string.startsWith("type:")) {
                    containerType = string.substring(5);
                } else if (string.startsWith("productCode:")) {
                    containerProduct = string.substring(12);
                } else if (string.startsWith("id:")) {
                    containerId = string.substring(3);
                }
            }
            switch (containerType) {
                case "Standard20ft" -> containers[i] = new Container20ft();
                case "Standard20ftHighCube" -> containers[i] = new Container20ftHighCube();
                case "Standard20ftHardTop" -> containers[i] = new Container20ftHardTop();
                case "FlatRack20ft" -> containers[i] = new FlatRackContainer20ft();
                case "Refrigerated20ft" -> containers[i] = new RefrigeratedContainer20ft();
                case "Tank20ft" -> containers[i] = new TankContainer();
                default -> throw new IllegalStateException("Unexpected value: " + containerType);
            }

            Product product = switch (containerProduct.substring(0, 2)) {
                case "FD" -> new Food();
                case "LS" -> new LooseProduct();
                case "MC" -> new Machine();
                case "LQ" -> new LiquidProduct();
                case "WD" -> new WoodenProduct();
                case "FT" -> new FrozenProduct();
                case "AG" -> new AGD();
                case "MT" -> new Material();
                default -> new Product();
            };

            product.productCode=containerProduct;
            String nameLetters = product.productCode.substring(product.productCode.length()-3);

            if(product instanceof Food){
                switch(nameLetters){
                    case "BA"->product.name = "banana";
                    case "ME"->product.name = "melon";
                    case "LE"->product.name = "lemon";
                    case "LI"->product.name = "lime";
                    case "GR"->product.name = "grapefruit";
                    case "KI"->product.name = "kiwi";
                }
            } else if(product instanceof LooseProduct){
                switch(nameLetters){
                    case "SA"->product.name = "sand";
                    case "QU"->product.name = "quartz";
                    case "CE"->product.name = "cement";
                    case "GR"->product.name = "gravel";
                    case "SC"->product.name = "scrap";
                }
            } else if(product instanceof Machine){
                switch(nameLetters){
                    case "CA"->product.name = "car";
                    case "TR"->product.name = "tractor";
                    case "FO"->product.name = "forklift";
                    case "RO"->product.name = "roller";
                    case "MO"->product.name = "motorcycle";
                }
            }else if(product instanceof LiquidProduct){
                switch(nameLetters){
                    case "JU"->product.name = "juice";
                    case "BE"->product.name = "beer";
                    case "CH"->product.name = "chemicals";
                    case "FU"->product.name = "fuel";
                    case "OI"->product.name = "oil";
                }
            } else if(product instanceof WoodenProduct){
                switch(nameLetters){
                    case "BE"->product.name = "bed";
                    case "DO"->product.name = "door";
                    case "CH"->product.name = "chair";
                    case "WA"->product.name = "wardrobe";
                    case "TA"->product.name = "table";
                    case "DE"->product.name = "desk";
                    case "BO"->product.name = "bookcase";
                }
            }else if(product instanceof FrozenProduct){
                switch(nameLetters){
                    case "FI"->product.name = "fish";
                    case "CA"->product.name = "calamari";
                    case "PR"->product.name = "prawns";
                    case "ME"->product.name = "meat";
                    case "CH"->product.name = "cheese";
                }
            }else if(product instanceof AGD){
                switch(nameLetters){
                    case "TV"->product.name = "TV";
                    case "PE"->product.name = "personalComputer";
                    case "MO"->product.name = "monitor";
                    case "PL"->product.name = "playStation";
                    case "WA"->product.name = "washMachine";
                    case "FR"->product.name = "fridge";
                }
            }else {
                switch(nameLetters){
                    case "CO"->product.name = "copper";
                    case "LE"->product.name = "lead";
                    case "MA"->product.name = "magnesium";
                    case "BR"->product.name = "bricks";
                    case "WO"->product.name = "wood";
                    case "CA"->product.name = "carbon";
                }
            }

            containers[i].setProduct(product);
            containers[i].setId(Integer.parseInt(containerId));
            i++;
        }
        return containers;
    }
}

class Container20ft extends Container{
    public Container20ft() {
        height = 2.6;
        width = 2.400;
        length = 6.1;
        load = (Math.random()*16)+5;
        mass = 1.972;
        type = "Standard20ft";
        validProductTypes = new String[]{"AGD", "MT", "LP", "WD"};
        setRandomProduct();
    }
}

class Container20ftHighCube extends Container{
    public Container20ftHighCube() {
        height = 2.6;
        width = 2.4;
        length = 6.1;
        load = (Math.random()*16)+5;
        mass = 2.797;
        type = "Standard20ftHighCube";
        validProductTypes = new String[]{"MCH", "AGD"};
        setRandomProduct();
    }
}

class Container20ftHardTop extends Container{
    public Container20ftHardTop() {
        height = 2.6;
        width = 2.400;
        length = 6.1;
        load = (Math.random()*17)+10;
        mass = 2.797;
        type = "Standard20ftHardTop";
        validProductTypes = new String[]{"LP", "WD", "AGD"};
        setRandomProduct();
    }
}

class FlatRackContainer20ft extends Container{
    public FlatRackContainer20ft() {
        height = 2.6;
        width = 2.4;
        length = 6.1;
        load = (Math.random()*21)+10;
        mass = 2.361;
        type = "FlatRack20ft";
        validProductTypes = new String[]{"MCH", "LP", "MT"};
        setRandomProduct();
        isOpened=true;
    }
}

class RefrigeratedContainer20ft extends Container{
    boolean isBatteryLoaded = false;
    public RefrigeratedContainer20ft() {
        height = 2.6;
        width = 2.400;
        length = 6.1;
        load = (Math.random()*15)+5;
        mass = 3.081;
        type = "Refrigerated20ft";
        validProductTypes = new String[]{"FT", "FD"};
        setRandomProduct();
        loadBattery();
    }

    void loadBattery(){
        this.isBatteryLoaded=true;
    }
}

class TankContainer extends Container{
    public TankContainer() {
        height = 2.6;
        width = 2.400;
        length = 6.1;
        load = (Math.random()*20)+10;
        mass = 3;
        type = "Tank20ft";
        validProductTypes = new String[]{"LQ"};
        setRandomProduct();
    }
}

class ContainerShip{
    private String name;
    private double capacity, length, width;
    private Container[][][] board;

    public Container[][][] getBoard() {
        return board;
    }

    public double getCapacity() {
        return capacity;
    }

    public ContainerShip(String name){
        this.name=name;
        capacity=15000;
        length=400;
        width=61;
    }

    void sortAndLoad(Container[] containers){
        int heavierIndex = 0;
        int lighterIndex = 0;
        int openedIndex = 0;

        for (Container container : containers) {
            if (container.isOpened) {
                openedIndex++;
            } else {
                if((container.mass + container.load) >= 20){
                    heavierIndex++;
                }else {
                    lighterIndex++;
                }
            }
        }

        Container[] lighterContainers = new Container[lighterIndex];
        Container[] heavierContainers = new Container[heavierIndex];
        Container[] openedContainers = new Container[openedIndex];

        heavierIndex = 0;
        lighterIndex = 0;
        openedIndex = 0;

        for (Container container : containers) {
            if (container.isOpened) {
                openedContainers[openedIndex]=container;
                openedIndex++;
            } else {
                if((container.mass + container.load) >= 20){
                    heavierContainers[heavierIndex]=container;
                    heavierIndex++;
                }else {
                    lighterContainers[lighterIndex]=container;
                    lighterIndex++;
                }
            }
        }

        Container[] sortedLighterContainers = project.sortByMassDescending(lighterContainers);
        Container[] sortedHeavierContainers = project.sortByMassDescending(heavierContainers);
        Container[] sortedOpenContainers = project.sortByMassDescending(openedContainers);

        heavierIndex = 0;
        lighterIndex = 0;
        openedIndex = 0;

        int numberOfRowsOnLevel = (int)((this.length-50)/7);
        int containersInRow = (int)((this.width-4)/2.4);
        int containersOnLevel = numberOfRowsOnLevel*containersInRow;
        int numberOfLevels = containers.length/containersOnLevel+1;

        this.board = new Container[numberOfLevels][numberOfRowsOnLevel][containersInRow];

        for(int i = 0; i < numberOfLevels; i++){
            for(int j = 0; j < numberOfRowsOnLevel; j++){
                for(int k = 0; k < containersInRow; k++){
                    if(heavierIndex<sortedHeavierContainers.length && (j<=numberOfRowsOnLevel-(numberOfRowsOnLevel/5) && j>=numberOfRowsOnLevel/5 && i<9)){
                        this.board[i][j][k]=sortedHeavierContainers[heavierIndex];
                        heavierIndex++;
                    }else{
                        if(lighterIndex<sortedLighterContainers.length){
                            this.board[i][j][k]=sortedLighterContainers[lighterIndex];
                            lighterIndex++;
                        }else {
                            if(openedIndex<sortedOpenContainers.length){
                                this.board[i][j][k]=sortedOpenContainers[openedIndex];
                                openedIndex++;
                            }else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ContainerShip " + name + " capacity: "+capacity+" TEU, length: "+length+", width: "+width;
    }
}

class Product {
    protected String productCode, name;

    public Product generateRandomProduct(){
        int roll = (int)(Math.random()*8);
        Product product;
        switch(roll){
            case 0 -> product = new Food();
            case 1 -> product = new Machine();
            case 2 -> product = new LooseProduct();
            case 3 -> product = new LiquidProduct();
            case 4 -> product = new WoodenProduct();
            case 5 -> product = new FrozenProduct();
            case 6 -> product = new AGD();
            case 7 -> product = new Material();
            default -> throw new IllegalStateException("Unexpected value: " + roll);
        }
        return product;
    }

    public Product() {
    }

    @Override
    public String toString() {
        return "Product= productCode:"+productCode+ "\t"+"name:"+name;
    }
}

class Food extends Product{
    private String[] stuffToEat = {"bananas", "melons", "lemon", "lime", "grapefruit", "kiwi"};
    public Food() {
        int roll = (int) (Math.random()*stuffToEat.length);
        name = stuffToEat[roll];
        productCode = "FD0" + roll+name.substring(0, 2).toUpperCase();
    }
}

class LooseProduct extends Product{
    private String[] looseStuff = {"sand", "quartz", "cement", "gravel", "scrap"};
    public LooseProduct(){
        int roll = (int) (Math.random()*looseStuff.length);
        name = looseStuff[roll];
        productCode = "LS0"+roll+name.substring(0, 2).toUpperCase();
    }
}

class Machine extends Product{
    private String[] machines = {"car", "tractor", "forklift", "roller", "motorcycle"};
    public Machine() {
        int roll = (int)(Math.random()*machines.length);
        name = machines[roll];
        productCode = "MCH0"+roll+name.substring(0, 2).toUpperCase();
    }
}

class LiquidProduct extends Product{
    private String[] liquids = {"juice", "beer", "chemicals", "fuel", "oil"};
    public LiquidProduct() {
        int roll = (int)(Math.random()*liquids.length);
        name = liquids[roll];
        productCode="LQ0"+roll+name.substring(0, 2).toUpperCase();
    }
}

class WoodenProduct extends Product{
    private String[] wdnPrd = {"bed", "door", "chair", "wardrobe", "table", "desk", "bookcase"};
    public WoodenProduct(){
        int roll = (int)(Math.random()*wdnPrd.length);
        name=wdnPrd[roll];
        productCode = "WD0"+roll+name.substring(0, 2).toUpperCase();
    }
}

class FrozenProduct extends Product{
    private String[] coolProducts = {"fish", "calamari", "prawns", "meat", "cheese"};
    public FrozenProduct() {
        int roll = (int)(Math.random()*coolProducts.length);
        name = coolProducts[roll];
        productCode = "FT0"+roll+name.substring(0, 2).toUpperCase();
    }
}

class AGD extends Product{
    private String[] agdProd = {"TV", "personalComputer", "monitor", "playStation", "washMachine", "fridge"};
    public AGD(){
        int roll = (int)(Math.random()*agdProd.length);
        name = agdProd[roll];
        productCode="AGD0"+roll+name.substring(0, 2).toUpperCase();
    }
}

class Material extends Product{
    private String[] materials = {"copper", "lead", "magnesium", "bricks", "wood", "carbon"};
    public Material(){
        int roll = (int)(Math.random()*materials.length);
        name = materials[roll];
        productCode="MT0"+roll+name.substring(0, 2).toUpperCase();
    }
}
