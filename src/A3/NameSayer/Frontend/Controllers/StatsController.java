package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Databases.UserDatabase;
import A3.NameSayer.Backend.Items.Attempt;
import A3.NameSayer.Backend.Items.CustomName;
import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchTo;
import A3.NameSayer.Main;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class StatsController implements Initializable {
    @FXML
    private Tile timeSpentTile;
    @FXML
    private Tile mostAttemptsTile;
    @FXML
    private Tile longestNameTile;
    @FXML
    private Tile daysPracticedTile;
    @FXML
    private Button backButton;

    private static final String SUNDAY = "Sun";
    private static final String MONDAY = "Mon";
    private static final String TUESDAY = "Tue";
    private static final String WEDNESDAY = "Wed";
    private static final String THURSDAY = "Thu";
    private static final String FRIDAY = "Fri";
    private static final String SATURDAY = "Sat";



    private BufferedReader _br = null;
    private String _text = "";
    private UserDatabase _userDatabase = UserDatabase.getInstance();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTiles();
    }

    // This method populates all the tiles with the corresponding information
    private void populateTiles() {
        populateTimeTile();
        populateAttemptsTile();
        populateLongestNameTile();
        populateDaysPracticedTile();
    }

    // This method populates the day practiced tile
    private void populateDaysPracticedTile() {
        daysPracticedTile.setTitle("How many attempts per day");
        daysPracticedTile.setTitleAlignment(TextAlignment.CENTER);

        // Gets the custom names with attempts
        ObservableList<CustomName> statList = _userDatabase.getCustomNamesWithAttempts();

        if (statList.size() < 1) {
            daysPracticedTile.setText("Practice atleast 1 name to see these stats");
            daysPracticedTile.setTextAlignment(TextAlignment.CENTER);
            daysPracticedTile.setSkinType(Tile.SkinType.TEXT);
        } else {
            HashMap<String, Integer> storeCount = new HashMap<>();
            // Initializing all the fields of the map to 0
            storeCount.put(SUNDAY, 0);
            storeCount.put(MONDAY, 0);
            storeCount.put(TUESDAY, 0);
            storeCount.put(WEDNESDAY, 0);
            storeCount.put(THURSDAY, 0);
            storeCount.put(FRIDAY, 0);
            storeCount.put(SATURDAY, 0);


            // Gets all the attempts done corresponding to the day
            for (CustomName c : statList) {
                for (Attempt a : c.getListOfAttempts()) {
                    Date d = new Date(new File(a.getAttemptPath()).lastModified());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");
                    String day = simpleDateFormat.format(d);

                    switch (day) {
                        case SUNDAY:
                            storeCount.put(SUNDAY, storeCount.get(SUNDAY) + 1);
                            break;
                        case MONDAY:
                            storeCount.put(MONDAY, storeCount.get(MONDAY) + 1);
                            break;
                        case TUESDAY:
                            storeCount.put(TUESDAY, storeCount.get(TUESDAY) + 1);
                            break;
                        case WEDNESDAY:
                            storeCount.put(WEDNESDAY, storeCount.get(WEDNESDAY) + 1);
                            break;
                        case THURSDAY:
                            storeCount.put(THURSDAY, storeCount.get(THURSDAY) + 1);
                            break;
                        case FRIDAY:
                            storeCount.put(FRIDAY, storeCount.get(FRIDAY) + 1);
                            break;
                        case SATURDAY:
                            storeCount.put(SATURDAY, storeCount.get(SATURDAY) + 1);
                            break;
                        default:
                            break;
                    }
                }
            }

            // Setting the XY Chart
            XYChart.Series<String, Number> seriesData = new XYChart.Series();
            seriesData.setName("series");
            seriesData.getData().add(new XYChart.Data<>("MO", storeCount.get(MONDAY)));
            seriesData.getData().add(new XYChart.Data<>("TU", storeCount.get(TUESDAY)));
            seriesData.getData().add(new XYChart.Data<>("WE", storeCount.get(WEDNESDAY)));
            seriesData.getData().add(new XYChart.Data<>("TH", storeCount.get(THURSDAY)));
            seriesData.getData().add(new XYChart.Data<>("FR", storeCount.get(FRIDAY)));
            seriesData.getData().add(new XYChart.Data<>("SA", storeCount.get(SATURDAY)));
            seriesData.getData().add(new XYChart.Data<>("SU", storeCount.get(SUNDAY)));

            daysPracticedTile.setChartType(Tile.ChartType.LINE);
            daysPracticedTile.setSeries(seriesData);
            daysPracticedTile.setSkinType(Tile.SkinType.SMOOTHED_CHART);

        }


    }

    // This method populates the longest name tile
    private void populateLongestNameTile() {
        longestNameTile.setTitle("Longest name practiced");
        longestNameTile.setTitleAlignment(TextAlignment.CENTER);

        ObservableList<CustomName> statList = _userDatabase.getCustomNamesWithAttempts();

        if (statList.size() < 1) {
            longestNameTile.setText("Practice atleast 1 name to see these stats");
            longestNameTile.setTextAlignment(TextAlignment.CENTER);
        } else {
            for (CustomName c : statList) {
                BarChartItem barChartItem = new BarChartItem(c.getName(), c.getName().replaceAll("\\s+", "").length(), Tile.DARK_BLUE);
                // Adds all the stats to a bar chart item and adds the barchart item to the graph
                longestNameTile.addBarChartItem(barChartItem);
            }
        }

        longestNameTile.setSkinType(Tile.SkinType.BAR_CHART);
        longestNameTile.setAnimated(true);

    }

    // This method populates the attempts tile for how many attempts corresponding to a name
    private void populateAttemptsTile() {
        mostAttemptsTile.setTitle("Name with most attempts");
        mostAttemptsTile.setTitleAlignment(TextAlignment.CENTER);


        ObservableList<CustomName> statList = _userDatabase.getCustomNamesWithAttempts();


        // Makes the barchart and stats
        if (statList.size() < 1) {
            mostAttemptsTile.setText("Practice atleast 1 name to see these stats");
            mostAttemptsTile.setTextAlignment(TextAlignment.CENTER);
        } else {
            int index = 0;
            for (CustomName c : statList) {
                if (index > 4) {
                    break;
                }
                BarChartItem barChartItem = new BarChartItem(c.getName(), c.getCurrentAttemptNumber() - 1, Tile.BLUE);
                mostAttemptsTile.addBarChartItem(barChartItem);
                index++;
            }

        }

        mostAttemptsTile.setSkinType(Tile.SkinType.BAR_CHART);
        mostAttemptsTile.setAnimated(true);
    }

    // This method populates the time tile by setting the time
    private void populateTimeTile() {
        timeSpentTile.setTitle("Total time spent practicing");
        timeSpentTile.setTitleAlignment(TextAlignment.CENTER);
        getTime();
        timeSpentTile.setDescriptionAlignment(Pos.CENTER);
        timeSpentTile.setSkinType(Tile.SkinType.TEXT);
    }

    // Gets the time from the .txt file
    private void getTime() {
        try {
            _br = new BufferedReader(new FileReader("time.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        _text = null;
        try {
            _text = _br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Long textTime = Long.valueOf(_text);
        textTime = textTime * 60000000000L;
        long duration = System.nanoTime() + textTime - Main.startTime;
        String time = String.valueOf(duration / 60000000000L);

        timeSpentTile.setDescription("You have spent " + time + " minutes practicing" );



    }

    // For going back to main menu
    public void onBackClick(ActionEvent e) throws IOException {
        SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }
}
