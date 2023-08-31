package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import java.nio.charset.StandardCharsets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import com.google.gson.annotations.SerializedName;
import javafx.scene.paint.Color;

/**
 * This app gets weather from postal code.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    HBox titleHbox;
    Label title;
    Label instructions;
    HBox dropdownContainer;
    Label dropdownLabel;
    ComboBox<String> dropdown;
    TextField zipField = new TextField();
    Button getWeather;
    String transferInfo;
    VBox information;
    HBox hbox1;
    HBox hbox2;
    HBox hbox3;
    HBox hbox4;
    HBox hbox5;
    Label postalCodeTitle;
    private Label postalCode;
    Label cityNameTitle;
    private Label cityName;
    Label temperatureTitle;
    private Label moonsetLabel;
    Label windTitle;
    private Label moonriseLabel;
    Label descriptionTitle;
    private Label description;

    private String moonrise;
    private String moonset;

    private String zipCodeValue = "";
    private String cityNameValue = "";
    private String temperatureValue = "";
    private String windValue = "";
    private String descriptionValue = "";
    private Alert alertError = new Alert (AlertType.ERROR);

    Image image = new Image("file:resources/waxing-gibbous-moon.jpg");
    ImageView imageView = new ImageView(image);

    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object
    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    private static final String ZIP_API = "http://ZiptasticAPI.com/";
    private String uri;
    private static final String WEATHER_API =
        "http://api.weatherapi.com/v1/astronomy.json?key=32073632b6f1475d809230605233004&q=";
    private String uri2;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox(20);
        information = new VBox();
        titleHbox = new HBox();
        hbox1 = new HBox();
        hbox2 = new HBox();
        hbox3 = new HBox();
        hbox4 = new HBox();
        hbox5 = new HBox();
        title = new Label("Moon information in the US!");
        title.setTextFill(Color.MIDNIGHTBLUE);
        instructions = new Label("Type in postal code and press the \"get information!\" button.");
        dropdownContainer = new HBox();
        dropdownLabel = new Label("Type in a US code (ex: 90210, 30609, 30040, 10123, etc.):");
        dropdown = new ComboBox<String>();
        getWeather = new Button("Get Information!");
        postalCodeTitle = new Label("Postal Code: ");
        postalCode = new Label();
        cityNameTitle = new Label("Location: ");
        cityName = new Label();
        temperatureTitle = new Label("Moonset Time: ");
        moonsetLabel = new Label();
        windTitle = new Label("Moonrise Time: ");
        moonriseLabel = new Label();
        descriptionTitle = new Label("Moon Phase: ");
        description = new Label();
        zipField = new TextField("30609");

    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        // setup scene
        root.getChildren().addAll(titleHbox, instructions, dropdownLabel,
            dropdownContainer, information, imageView);
        imageView.setFitWidth(250);
        imageView.setPreserveRatio(true);
        titleHbox.getChildren().addAll(title);
        dropdownContainer.getChildren().addAll(zipField, getWeather);
        information.getChildren().addAll(hbox1, hbox2, hbox3, hbox4, hbox5);
        hbox1.getChildren().addAll(postalCodeTitle, postalCode);
        hbox2.getChildren().addAll(cityNameTitle, cityName);
        hbox3.getChildren().addAll(temperatureTitle, moonsetLabel);
        hbox4.getChildren().addAll(windTitle, moonriseLabel);
        hbox5.getChildren().addAll(descriptionTitle, description);
        scene = new Scene(root, 500, 400);
        root.setStyle("-fx-background-color: lavender");
        title.setStyle("-fx-font-size: 20px");
        titleHbox.setAlignment(Pos.CENTER);

        // setup stage
        stage.setTitle("Moonometer!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
    } // start

    /** {@inheritDoc} */
    public void init() {
        Runnable task = () -> {
            getWeather.setDisable(true);
            ZipResponse zipResponse = getOne();
            MoonResponse moonResponse = getTwo();
            getWeather.setDisable(false);
        };
        EventHandler<ActionEvent> taskHandler = event -> runNow(task);
        getWeather.setOnAction(taskHandler);
    }

    /**
     * Connects to Ziptastic API to enter zip code and get city.
     * @return the zipresponse.
     */
    public ZipResponse getOne() {
        ZipResponse zipResponse = new ZipResponse();
        try {
            zipResponse = new ZipResponse(); //forms uri
            String zipCode = URLEncoder.encode(zipField.getText(), StandardCharsets.UTF_8);
            uri = ZIP_API + zipCode;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) { //checks if request built can send/recieve
                throw new IOException(response.toString());
            }
            String jsonString = response.body();//gets request of body
            zipResponse = GSON.fromJson
                (jsonString, cs1302.api.ZipResponse.class);//parses json using gson
            transferInfo = zipResponse.city;
            transferInfo = transferInfo.toLowerCase();
            transferInfo = transferInfo.replaceAll("\\s", "%20");
            zipCodeValue = zipField.getText();
            cityNameValue = zipResponse.city + ", " + zipResponse.state;
            Platform.runLater(() -> postalCode.setText(zipCodeValue));
            Platform.runLater(() -> cityName.setText(cityNameValue));
        } catch (IOException | InterruptedException | NullPointerException e) {
            alertError.setContentText("Invalid zipcode or search did not work.");
            Platform.runLater(() -> alertError.showAndWait());
        }
        return zipResponse;
    }

    /**
     * Connects to weatherapi by using the city from ziptastic api and getting the moon information.
     * @return the moonresponse.
     */
    public MoonResponse getTwo() {
        MoonResponse moonResponse = new MoonResponse();
        try {
            moonResponse = new MoonResponse();//forms uri
            String place = transferInfo;
            uri2 = WEATHER_API + place;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri2)).build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) { //checks if request built can send/recieve
                throw new IOException(response.toString());
            }
            String jsonString = response.body();//gets request of body
            moonResponse = GSON.fromJson
                (jsonString, MoonResponse.class);//parses json using gson
            moonrise = moonResponse.astronomy.astro.moonrise;
            moonset = moonResponse.astronomy.astro.moonset;
            descriptionValue = moonResponse.astronomy.astro.moonPhase;
            Platform.runLater(() -> moonsetLabel.setText(moonset));
            Platform.runLater(() -> moonriseLabel.setText(moonrise));
            Platform.runLater(() -> description.setText(descriptionValue));
        } catch (IOException | InterruptedException e) {
            alertError.setContentText("Invalid zipcode or search did not work.");
            Platform.runLater(() -> alertError.showAndWait());
        }
        return moonResponse;
    }

    /**
     * Response from weatherapi.com.
     */
    public class MoonResponse {
        public Astronomy astronomy;
    }

    /**
     * Response of astronomy from weatherapi.com.
     */
    public class Astronomy {
        public Astro astro;
    }

    /**
     * Response of astro variable from weatherapi.com/
     */
    public class Astro {
        @SerializedName("moon_phase") String moonPhase;
        public String moonrise;
        public String moonset;
    }

    /**
     * Thread method to make main clear while something else runs on another thread.
     * @param test is the runnable thing.
     */
    public static void runNow(Runnable test) {
        Thread t = new Thread(test);
        t.setDaemon(true);
        t.start();
    }

} // ApiApp
