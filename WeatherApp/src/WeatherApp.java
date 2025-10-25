

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Weather Information App (JavaFX)
 * - Real-time API calls via WeatherService (OpenWeatherMap XML endpoints)
 * - City input + search
 * - Current weather (temp, humidity, wind, conditions)
 * - Short-term forecast
 * - Unit conversion (metric/imperial)
 * - Error handling
 * - Search history with timestamps
 * - Dynamic backgrounds (time of day)
 */
public class WeatherApp extends Application {

    // View model properties
    private final StringProperty location = new SimpleStringProperty("");
    private final StringProperty condition = new SimpleStringProperty("-");
    private final StringProperty temperature = new SimpleStringProperty("-");
    private final StringProperty humidity = new SimpleStringProperty("-");
    private final StringProperty wind = new SimpleStringProperty("-");
    private final StringProperty feelsLike = new SimpleStringProperty("-");
    private final StringProperty pressure = new SimpleStringProperty("-");
    private final StringProperty status = new SimpleStringProperty("Enter a city (e.g., London or New York) and press Search.");

    private final ObservableList<String> forecastItems = FXCollections.observableArrayList();
    private final ObservableList<String> historyItems = FXCollections.observableArrayList();

    private final ObjectProperty<Units> units = new SimpleObjectProperty<>(Units.METRIC);

    // UI Nodes we need to tweak dynamically
    private BorderPane root;

    @Override
    public void start(Stage stage) {
        TextField cityField = new TextField();
        cityField.setPromptText("City (e.g., London, Paris, New York)");
        cityField.setPrefColumnCount(22);

        ComboBox<Units> unitBox = new ComboBox<>();
        unitBox.getItems().addAll(Units.METRIC, Units.IMPERIAL);
        unitBox.setValue(units.get());
        units.bind(unitBox.valueProperty());

        Button searchBtn = new Button("Search");
        searchBtn.setDefaultButton(true);

        HBox topBar = new HBox(10, new Label("Location:"), cityField, new Label("Units:"), unitBox, searchBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // Current weather display
        Label conditionLbl = bigLabel("-");
        conditionLbl.textProperty().bind(condition);

        Label tempLbl = bigLabel("-");
        tempLbl.textProperty().bind(temperature);

        GridPane facts = new GridPane();
        facts.setHgap(16);
        facts.setVgap(6);
        facts.add(new Label("Feels Like:"), 0, 0);
        facts.add(bindLabel(feelsLike), 1, 0);
        facts.add(new Label("Humidity:"), 0, 1);
        facts.add(bindLabel(humidity), 1, 1);
        facts.add(new Label("Wind:"), 0, 2);
        facts.add(bindLabel(wind), 1, 2);
        facts.add(new Label("Pressure:"), 0, 3);
        facts.add(bindLabel(pressure), 1, 3);

        VBox currentBox = new VBox(6, conditionLbl, tempLbl, facts);
        currentBox.setPadding(new Insets(10));
        currentBox.setAlignment(Pos.TOP_LEFT);

        // Forecast list
        ListView<String> forecastList = new ListView<>(forecastItems);
        forecastList.setPrefHeight(180);

        TitledPane forecastPane = new TitledPane("Forecast (next periods)", forecastList);
        forecastPane.setCollapsible(false);

        // History list
        ListView<String> historyList = new ListView<>(historyItems);
        historyList.setPrefHeight(140);
        TitledPane historyPane = new TitledPane("Search History", historyList);
        historyPane.setCollapsible(false);

        Label statusBar = new Label();
        statusBar.textProperty().bind(status);
        statusBar.setPadding(new Insets(6));

        VBox center = new VBox(10, currentBox, forecastPane);
        center.setPadding(new Insets(10));

        VBox right = new VBox(10, historyPane);
        right.setPadding(new Insets(10));
        right.setPrefWidth(280);

        root = new BorderPane(center, topBar, right, statusBar, null);
        applyDynamicBackground(root);

        Scene scene = new Scene(root, 920, 560);
        stage.setTitle("Weather Information App (JavaFX)");
        stage.setScene(scene);
        stage.show();

        // Actions
        Runnable doSearch = () -> {
            String city = cityField.getText().trim();
            if (city.isEmpty()) {
                status.set("Please enter a city.");
                return;
            }
            fetchWeather(city, units.get());
        };
        searchBtn.setOnAction(e -> doSearch.run());
        cityField.setOnAction(e -> doSearch.run());

        // Update background periodically (optional nicety)
        startBackgroundRefresher();
    }

    private void fetchWeather(String city, Units unitMode) {
        status.set("Fetching weather for " + city + " (" + unitMode.label + ") ...");
        // Call service off the FX thread
        new Thread(() -> {
            try {
                WeatherService.CurrentWeather cw = WeatherService.getCurrentWeather(city, unitMode);
                WeatherService.Forecast fc = WeatherService.getForecast(city, unitMode);

                Platform.runLater(() -> {
                    // Bind current
                    location.set(cw.cityName);
                    condition.set(emojiFor(cw.iconCode) + " " + cw.description);
                    temperature.set(formatTemp(cw.temp, unitMode));
                    feelsLike.set(formatTemp(cw.feelsLike, unitMode));
                    humidity.set(String.format("%.0f%%", cw.humidity));
                    pressure.set(String.format("%.0f hPa", cw.pressure));
                    wind.set(formatWind(cw.windSpeed, unitMode));

                    // Forecast
                    forecastItems.clear();
                    int max = Math.min(6, fc.items.size()); // short list
                    for (int i = 0; i < max; i++) {
                        var it = fc.items.get(i);
                        String row = String.format("%s  %s  %s  %s",
                                it.time.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")),
                                emojiFor(it.iconCode),
                                padRight(it.description, 14),
                                formatTemp(it.temp, unitMode));
                        forecastItems.add(row);
                    }

                    // History
                    historyItems.add(0, String.format("%s  |  %s  |  %s",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            city,
                            formatTemp(cw.temp, unitMode)));

                    status.set("Updated: " + cw.cityName + " • " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    applyDynamicBackground(root);
                });
            } catch (WeatherService.ApiException ex) {
                Platform.runLater(() -> status.set("API error: " + ex.getMessage()));
            } catch (Exception ex) {
                Platform.runLater(() -> status.set("Error: " + ex.getMessage()));
            }
        }, "weather-fetcher").start();
    }

    // ===== Helpers =====

    private static Label bigLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(22));
        return l;
    }

    private static Label bindLabel(StringProperty p) {
        Label l = new Label();
        l.textProperty().bind(p);
        return l;
    }

    private static String formatTemp(double t, Units u) {
        return String.format("%.1f °%s", t, (u == Units.METRIC ? "C" : "F"));
    }

    private static String formatWind(double v, Units u) {
        if (u == Units.METRIC) {
            return String.format("%.1f m/s", v);
        } else {
            return String.format("%.1f mph", v);
        }
    }

    private static String padRight(String s, int n) {
        if (s.length() >= n) return s;
        return s + " ".repeat(n - s.length());
    }

    /** Simple emoji mapping (no image assets needed). */
    private static String emojiFor(String iconCode) {
        if (iconCode == null) return "🌡️";
        // OWM icon codes like "01d", "02n", etc.
        char c = iconCode.charAt(0);
        switch (c) {
            case '0':
                return "🌡️";
            case '1':
                return "☀️";
            case '2':
                return "⛅";
            case '3':
                return "🌦️";
            case '4':
                return "☁️";
            case '9':
                return "🌧️";
            default:
                return "🌡️";
        }
    }

    /** Adjust background by local time (day, evening, night gradients). */
    private void applyDynamicBackground(Pane pane) {
        int hour = LocalDateTime.now().getHour();
        String gradient;
        if (hour >= 6 && hour < 17) {
            // day
            gradient = "linear-gradient(to bottom, #e3f2fd, #bbdefb)";
        } else if (hour >= 17 && hour < 20) {
            // evening
            gradient = "linear-gradient(to bottom, #FFCC80, #FF8A65)";
        } else {
            // night
            gradient = "linear-gradient(to bottom, #283593, #1A237E)";
        }
        pane.setStyle("-fx-background-color: " + gradient + ";");
    }

    /** Periodically refresh background style (every ~60s). */
    private void startBackgroundRefresher() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(60000);
                    Platform.runLater(() -> applyDynamicBackground(root));
                }
            } catch (InterruptedException ignored) {}
        }, "background-refresher");
        t.setDaemon(true);
        t.start();
    }

    public static void main(String[] args) {
        launch(args);
    }


}

