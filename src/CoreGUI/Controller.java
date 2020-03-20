package CoreGUI;

import java.awt.*;

import java.io.IOException;
import java.util.*;

import ScraperApp.Scraper;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import DataAnalysis.Database;
import DataAnalysis.Product;
import DataAnalysis.Results;
import ScraperApp.JsonFileHandler;

import DataAnalysis.*;

public class Controller {
    // Properties

    private final int numberOfRows = 3;
    private final float contentNumOfCol = 3.0f;
    private int contentNumOfRows = 3;
    private int numberOfProducts = 0;
    private float productHeight = 600f;
    private float fontSize = 0.8f;

    private float alphaValue = 0.3f;

    private float screenPaddingConst = 160.0f;

    int numOfBrands = 0;
    String[] brands;

    @FXML
    private TextField searchText;

    @FXML
    private GridPane contentGrid;
    @FXML
    private GridPane statsGrid;

    @FXML
    private Text welcomeTxt;

    @FXML
    private Text invalidTxt;

    @FXML
    private ScatterChart priceToVolumeChart;
    @FXML
    private NumberAxis xAxisPTV;
    @FXML
    private NumberAxis yAxisPTV;

    @FXML
    private ScatterChart priceToMeanDeltaChart;
    @FXML
    private NumberAxis xAxisPTMD;
    @FXML
    private NumberAxis yAxisPTMD;

    @FXML
    private ScatterChart pricePerUnitChart;
    @FXML
    private NumberAxis xAxisPPU;
    @FXML
    private NumberAxis yAxisPPU;

    @FXML
    private Pane root;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane brandGrid;

    private GridPane[] prodPane = new GridPane[numberOfProducts];

    private Text keyword1 = new Text("Sanitiser, ");
    private Text keyword2 = new Text("Bodywash, ");
    private Text keyword3 = new Text("Wheat Biscuits, ");
    private Text keyword4 = new Text("Detergent");

    @FXML
    private TextFlow popularKeywords;

    private JsonFileHandler fileHandler;
    private Database database;
    private Results results;
    private Product[] products;

    private ArrayList<Product> productArrayList = new ArrayList<Product>();

    public void initialize() {
        System.out.println("Initialize ran!");

        popularKeywords.getChildren().addAll(keyword1, keyword2, keyword3, keyword4);

        fileHandler = new JsonFileHandler();
        database = new Database();
        results = new Results();
        products = null;

        initialiseEventListeners();
    }

    public void initialiseContentGrid(int rowsNeeded) {
        int currentNumOfRows = contentGrid.getRowCount();

        for(int i = currentNumOfRows; i < rowsNeeded; i++) {
            this.contentGrid.addRow(i);
        }
        this.contentGrid.setHgap(10);
        this.contentGrid.setVgap(20);

        this.contentGrid.setPadding(new Insets(5, 10, 10, 10));

        this.anchorPane.setPrefHeight((300 * rowsNeeded ) + (this.contentGrid.getVgap() * rowsNeeded) + screenPaddingConst);

        for(int i = 0; i < currentNumOfRows; i++) {
            this.contentGrid.getRowConstraints().add(new RowConstraints(300));
        }

    }

    public void initialiseEventListeners()
    {

        searchText.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {

                String input = searchText.getText().toLowerCase();

                if(input.isBlank() || input.isEmpty() || input == " " || input == ""){
                    if(!invalidTxt.isVisible()) {
                        invalidTxt.setVisible(true);
                    }
                    if(welcomeTxt.isVisible()) {
                        welcomeTxt.setVisible(false);
                    }
                    if(!contentGrid.getChildren().isEmpty()) {
                        contentGrid.getChildren().clear();
                        brandGrid.getChildren().clear();
                        pricePerUnitChart.getData().clear();
                        priceToMeanDeltaChart.getData().clear();
                        priceToVolumeChart.getData().clear();
                    }
                }
                else if(!input.isEmpty() || !input.isBlank()){

                    if(invalidTxt.isVisible()) {
                        invalidTxt.setVisible(false);
                    }
                    if(welcomeTxt.isVisible()) {
                        welcomeTxt.setVisible(false);
                    }
                    System.out.println("Input in lowercase: " + input);

                    try {
                        Scraper.scrape(input);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    products = fileHandler.readFile(input);
                    if (products == null) {
                        if(!invalidTxt.isVisible()) {
                            invalidTxt.setVisible(true);
                        }
                        if(welcomeTxt.isVisible()) {
                            welcomeTxt.setVisible(false);
                        }

                        contentGrid.getChildren().clear();
                        brandGrid.getChildren().clear();
                        pricePerUnitChart.getData().clear();
                        priceToMeanDeltaChart.getData().clear();
                        priceToVolumeChart.getData().clear();
                    }
                    else {
                        if(!results.isNull())
                        {
                            System.out.println("Resetting");
                            results = new Results();
                            database = new Database();
                            contentGrid.getChildren().clear();
                        }

                        products = database.update(products, results);
                        numberOfProducts = products.length;

                        // Calculate number of rows needed to display products
                        contentNumOfRows = (int)Math.ceil(numberOfProducts/contentNumOfCol);

                        this.prodPane = new GridPane[numberOfProducts];

                        setProductPane(prodPane);

                        Product[] top3Products = results.getTop3();

                        if(!productArrayList.isEmpty())
                        {
                            productArrayList.clear();
                        }

                        Arrays.sort(products, new Comparator<Product>() {
                            public int compare(Product product1, Product product2) {
                                if (product2.getPricePerUnit() >= product1.getPricePerUnit())
                                {
                                    return -1;
                                }
                                else if (product2.getPricePerUnit() < product1.getPricePerUnit()){
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        });

                        for (Product x :products) {
                            productArrayList.add(x);
                        }

                        // convert query into lowercase letters
                        insertItemsIntoPane(prodPane, productArrayList);
                        setGridPane(prodPane);
                        initialiseContentGrid(contentNumOfRows);

                        // Initialise charts section
                        numOfBrands = database.getUniqueBrands();
                        brands = database.getBrands();

                        plotPriceToVolume();
                        plotPriceToMeanDelta();
                        plotPricePerUnit();
                        setBrandGrid();
                    }



                }
            }
        });
    }

    // Initialise grid panel for products to be stored in.
    public void setProductPane(GridPane[] prodPane) {
        for(int i = 0; i < numberOfProducts; i++) {
            GridPane panel = new GridPane();
            panel.setAlignment(Pos.CENTER);

            for(int j = 0; j < numberOfRows; j++) {
                RowConstraints rowConst = new RowConstraints();

                rowConst.setValignment(VPos.CENTER);
                panel.getRowConstraints().add(rowConst);

            }
            ColumnConstraints columnConst = new ColumnConstraints();
            columnConst.setHalignment(HPos.CENTER);
            panel.getColumnConstraints().add(columnConst);

            panel.setMaxHeight(productHeight);
            panel.getStyleClass().add("productPane");
            panel.setVgap(5.0);
            panel.setPrefHeight(360);
            prodPane[i] = panel;
        }
    }

    // Adds images and texts according to the products received from scraping
    public void insertItemsIntoPane(GridPane[] prodPane, ArrayList<Product> testProducts) {

        int counter = 0;
        for(int i = 0; i < numberOfProducts; i++) {
            for(int j = 0; j < numberOfRows; j++) {
                TextFlow newTitle;
                Text toPushText;

                switch (j)
                {
                    case 0:
                        if(testProducts.get(counter).getImage() != null || !testProducts.get(counter).getImage().isEmpty() || !testProducts.get(counter).getImage().isBlank()) {
                            Image image = new Image(testProducts.get(counter).getImage(), 0, productHeight / numberOfRows-1, true, true, true);
                            ImageView iv1 = new ImageView();
                            iv1.setImage(image);
                            prodPane[i].add(iv1, 0, j);
                        }

                        break;
                    case 1:
                        toPushText = new Text(testProducts.get(counter).getTitle());
                        newTitle = new TextFlow(toPushText);
                        newTitle.setTextAlignment(TextAlignment.CENTER);

                        prodPane[i].add(newTitle, 0, j);
                        break;
                    case 2:
                        toPushText = new Text(String.format("$%.2f (Price Per Unit: $%.2f)",
                                testProducts.get(counter).getPrice(),
                                testProducts.get(counter).getPricePerUnit()));
                        newTitle = new TextFlow(toPushText);
                        newTitle.setTextAlignment(TextAlignment.CENTER);
                        prodPane[i].add(newTitle, 0, j);
                        break;
                }

            }
            counter++;
        }

    }

    // Adds product panels into Grid Pane
    public void setGridPane(GridPane[] prodPane) {
        int k = 0;
        for(int i = 0; i < contentNumOfRows; i++) {
            for(int j = 0; j < contentNumOfCol; j++) {
                if(k == numberOfProducts) {
                    break;
                }
                contentGrid.add(prodPane[k], j, i);
                k++;
            }
        }
    }

    // ---------------Plot points on the scatter chart-----------------
    public void plotPriceToVolume() {

        if(!priceToVolumeChart.getData().isEmpty())
        {
            priceToVolumeChart.getData().clear();
        }


        Arrays.sort(products, new Comparator<Product>() {
            public int compare(Product product1, Product product2) {
                if (product2.getVolume() > product1.getVolume())
                {
                    return 1;
                }
                else if (product2.getVolume() < product1.getVolume()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        xAxisPTV.setUpperBound(Math.ceil(Math.ceil(products[0].getVolume()) + (Math.ceil(products[0].getVolume())/10)));
        if(products[0] instanceof aboveOneKilogram) {
            xAxisPTV.setLowerBound(0);
            xAxisPTV.setTickUnit(products[0].getVolume() % 10);
        }
        else if(products[0] instanceof belowOneKilogram) {
            xAxisPTV.setLowerBound(Math.floor(products[products.length-1].getVolume()) / 2);
            xAxisPTV.setTickUnit(Math.ceil(products[0].getVolume() / 10));
        }

        xAxisPTV.setLabel("Volume");
        xAxisPTV.setAutoRanging(false);

        Arrays.sort(products, new Comparator<Product>() {
            public int compare(Product product1, Product product2) {
                if (product2.getPrice() >= product1.getPrice())
                {
                    return 1;
                }
                else if (product2.getPrice() < product1.getPrice()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        yAxisPTV.setLowerBound(0);
        yAxisPTV.setUpperBound(Math.ceil(products[0].getPrice()) + Math.ceil(products[0].getPrice() / 8));
        yAxisPTV.setTickUnit(Math.ceil(products[0].getPrice() / 10));
        yAxisPTV.setLabel("Price");
        yAxisPTV.setAutoRanging(false);

        priceToVolumeChart.setTitle("Price To Volume");

        for(int i = 0; i < numOfBrands; i++) {
            Product[] brandProduct = database.filterBrand(products, brands[i]);

            XYChart.Series chartSeries = new XYChart.Series();

            chartSeries.setName(brands[i]);

            for(Product prod : brandProduct) {
                chartSeries.getData().add(new XYChart.Data(prod.getVolume(), prod.getPrice()));
            }

            priceToVolumeChart.getData().add(chartSeries);
        }
        priceToVolumeChart.applyCss();
        setStyle(numOfBrands, priceToVolumeChart);
        priceToVolumeChart.setLegendVisible(false);


    }

    // Plot price to mean delta scatter chart
    public void plotPriceToMeanDelta() {
        if(!priceToMeanDeltaChart.getData().isEmpty()){
            priceToMeanDeltaChart.getData().clear();
        }

        priceToMeanDeltaChart.setTitle("Price to Mean Delta");


        Arrays.sort(products, new Comparator<Product>() {
            public int compare(Product product1, Product product2) {
                if (product2.getMeanDelta() >= product1.getMeanDelta())
                {
                    return 1;
                }
                else if (product2.getMeanDelta() < product1.getMeanDelta()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        yAxisPTMD.setUpperBound(Math.ceil(products[0].getMeanDelta()) + (Math.ceil(products[0].getMeanDelta()/8)));
        yAxisPTMD.setLowerBound(-Math.floor(products[0].getMeanDelta()) - (Math.ceil(products[0].getMeanDelta()/8)));
        yAxisPTMD.setTickUnit(Math.ceil(products[0].getMeanDelta() / 10));
        yAxisPTMD.setAutoRanging(false);
        yAxisPTMD.setLabel("Mean Delta");

        Arrays.sort(products, new Comparator<Product>() {
            public int compare(Product product1, Product product2) {
                if (product2.getPrice() >= product1.getPrice())
                {
                    return 1;
                }
                else if (product2.getPrice() < product1.getPrice()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        xAxisPTMD.setUpperBound(Math.ceil(products[0].getPrice()) + Math.ceil(products[0].getPrice() / 8));
        xAxisPTMD.setLowerBound(Math.floor(products[products.length-1].getPrice())/2);
        xAxisPTMD.setTickUnit(Math.ceil(products[0].getPrice() / 10));
        xAxisPTMD.setAutoRanging(false);
        xAxisPTMD.setLabel("Price");

        for(int i = 0; i < numOfBrands; i++) {
            Product[] brandProduct = database.filterBrand(products, brands[i]);

            XYChart.Series chartSeries = new XYChart.Series();

            chartSeries.setName(brands[i]);

            for(Product prod : brandProduct) {
                chartSeries.getData().add(new XYChart.Data(prod.getPrice(), prod.getMeanDelta()));
            }
            priceToMeanDeltaChart.getData().add(chartSeries);
        }
        priceToMeanDeltaChart.applyCss();

        setStyle(numOfBrands, priceToMeanDeltaChart);

        priceToMeanDeltaChart.setLegendVisible(false);

    }

    private void plotPricePerUnit(){
        if(!pricePerUnitChart.getData().isEmpty()) {
            pricePerUnitChart.getData().clear();
        }

        pricePerUnitChart.setTitle("Price Per Unit");

        Arrays.sort(products, new Comparator<Product>() {
            public int compare(Product product1, Product product2) {
                if (product2.getPricePerUnit() >= product1.getPricePerUnit())
                {
                    return 1;
                }
                else if (product2.getPricePerUnit() < product1.getPricePerUnit()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });


        xAxisPPU.setUpperBound(Math.ceil(products[0].getPricePerUnit()) + Math.ceil(products[0].getPricePerUnit() / 8));
        xAxisPPU.setLowerBound(Math.floor(products[products.length-1].getPricePerUnit() / 2));
        xAxisPPU.setTickUnit(Math.ceil(products[0].getPricePerUnit() % 10));
        xAxisPPU.setAutoRanging(false);

        xAxisPPU.setLabel("Price Per Unit");

        yAxisPPU.setUpperBound(2);
        yAxisPPU.setLowerBound(0);
        yAxisPPU.setTickUnit(1);
        yAxisPPU.setAutoRanging(false);

        yAxisPPU.setLabel("Unit");

        for(int i = 0; i < numOfBrands; i++) {
            Product[] brandProduct = database.filterBrand(products, brands[i]);

            XYChart.Series chartSeries = new XYChart.Series();

            chartSeries.setName(brands[i]);

            for(Product prod : brandProduct) {
                chartSeries.getData().add(new XYChart.Data(prod.getPricePerUnit(), 1));
            }

            pricePerUnitChart.getData().add(chartSeries);
        }

        System.out.println("Number of brands: " + numOfBrands);

        double[] statResults = results.getStatistics();
        double mean = statResults[0];
        double sd = statResults[1];

        XYChart.Series statsSeries = new XYChart.Series();

        statsSeries.setName("Intervals");

        statsSeries.getData().add(new XYChart.Data(mean - sd, 1));
        statsSeries.getData().add(new XYChart.Data(mean + sd, 1));
        statsSeries.getData().add(new XYChart.Data(mean, 1));

        pricePerUnitChart.getData().add(statsSeries);

        pricePerUnitChart.applyCss();
        setStyle(numOfBrands, pricePerUnitChart);
        setStatsStyle(numOfBrands, pricePerUnitChart);

        pricePerUnitChart.setLegendVisible(true);

    }

    private void setStyle(int numberOfBrands, ScatterChart chart) {
        for(int i = 0; i < numberOfBrands; i++) {
            Set<Node> nodes = chart.lookupAll(".series" + i);
            Product[] brandProduct = database.filterBrand(products, brands[i]);
            Color color = brandProduct[0].getColor();
            String colorString = String.format("%d, %d, %d", color.getRed(), color.getGreen(), color.getBlue());
            for(Node n : nodes) {
                n.setStyle(
                        "-fx-background-color: rgba(" + colorString + ", " + alphaValue + ");\n"
//                                "-fx-background-radius: 5px;\n" +
//                                "-fx-padding: 5px;\n"
                );
            }
        }

        chart.setStyle(
                "-fx-font-size: " + fontSize +"em;"
        );
    }

    private void setStatsStyle(int numOfBrands , ScatterChart chart) {
        Set<Node> nodes = chart.lookupAll(".series" + numOfBrands);
        String colorString = String.format("%d, %d, %d", 0, 0, 0);
        for(Node n : nodes) {
            n.setStyle(
                    "-fx-background-color: rgba(" + colorString + ", " + 1 + ");\n"+
                    "-fx-background-radius: 0;\n" +
                    "-fx-background-insets: 0;\n" +
                    "-fx-shape: \"M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,\n" +
                    " 10 L0,10 L0,8 L4,5 L0,2 L0,0 Z\";"
            );
        }
    }

    private void setBrandGrid() {
        GridPane grid = new GridPane();

        for(int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.CENTER);
            rowConstraints.setPrefHeight(30/2);

            grid.getRowConstraints().add(rowConstraints);
        }

        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setHalignment(HPos.CENTER);

        grid.getColumnConstraints().add(colConstraints);

        Text brandName = new Text(results.getEconomicalBrand());
        double lowestPrice = results.getLowestEconomical();
        double highestPrice = results.getHighestEconomical();
        String priceFormat = String.format("%.2f to %.2f", lowestPrice, highestPrice);

        Text priceRange = new Text("Adjusted price range of economical brand is: " + priceFormat);

        grid.add(brandName, 0, 0);
        grid.add(priceRange, 0, 1);
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(20);
        grid.getStyleClass().add("brandPane");

        brandGrid.add(grid, 0, 0);

        grid = new GridPane();

        for(int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.CENTER);
            rowConstraints.setPrefHeight(30/2);

            grid.getRowConstraints().add(rowConstraints);
        }

        colConstraints = new ColumnConstraints();
        colConstraints.setHalignment(HPos.CENTER);

        grid.getColumnConstraints().add(colConstraints);

        brandName = new Text(results.getExpensiveBrand());
        lowestPrice = results.getLowestExpensive();
        highestPrice = results.getHighestExpensive();
        priceFormat = String.format("%.2f to %.2f", lowestPrice, highestPrice);

        priceRange = new Text("Adjusted price range of expensive brand is: " + priceFormat);

        grid.add(brandName, 0, 0);
        grid.add(priceRange, 0, 1);
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(20);
        grid.getStyleClass().add("brandPane");

        brandGrid.add(grid, 0, 1);
    }

}