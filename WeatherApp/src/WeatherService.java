



import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.w3c.dom.*;

/**
 * WeatherService
 * - Uses OpenWeatherMap XML endpoints to avoid external JSON libraries.
 * - Current weather:  /data/2.5/weather?q={city}&mode=xml&units={metric|imperial}&appid={API_KEY}
 * - Forecast (3-hour): /data/2.5/forecast?q={city}&mode=xml&units={metric|imperial}&appid={API_KEY}
 *
 * IMPORTANT: Insert your API key below.
 */
public class WeatherService {

    // TODO: Put your OWM API key here (https://openweathermap.org/)
    public static final String API_KEY = "6ac1879a49fe98fdb8fd3dfa274ee01a";

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static CurrentWeather getCurrentWeather(String city, Units units) throws Exception {
        requireApiKey();
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&mode=xml&units=%s&appid=%s",
                encode(city), units.label, API_KEY);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            System.out.println("Error: " + resp.statusCode() + " " + resp.body());
            throw new ApiException("HTTP " + resp.statusCode() + " while fetching current weather.");

        }

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(resp.body().getBytes()));

        Element root = doc.getDocumentElement();
        if (!"current".equalsIgnoreCase(root.getNodeName())) {
            throw new ApiException("Unexpected response format for current weather.");
        }

        String cityName = getAttr(root, "city", "name");
        String desc = getAttr(root, "weather", "value");
        String icon = getAttr(root, "weather", "icon");

        double temp = getAttrDouble(root, "temperature", "value");
        double feels = getAttrDouble(root, "feels_like", "value", Double.NaN);
        if (Double.isNaN(feels)) {
            // fallback if not present
            feels = temp;
        }
        double hum = getAttrDouble(root, "humidity", "value");
        double press = getAttrDouble(root, "pressure", "value");
        double windSpeed = getAttrDouble(root, "speed", "value");

        CurrentWeather cw = new CurrentWeather();
        cw.cityName = cityName != null ? cityName : city;
        cw.description = desc != null ? desc : "-";
        cw.iconCode = icon != null ? icon : "01d";
        cw.temp = temp;
        cw.feelsLike = feels;
        cw.humidity = hum;
        cw.pressure = press;
        cw.windSpeed = windSpeed;
        return cw;
    }

    public static Forecast getForecast(String city, Units units) throws Exception {
        requireApiKey();
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?q=%s&mode=xml&units=%s&appid=%s",
                encode(city), units.label, API_KEY);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new ApiException("HTTP " + resp.statusCode() + " while fetching forecast.");
        }

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(resp.body().getBytes()));

        Element root = doc.getDocumentElement();
        if (!"weatherdata".equalsIgnoreCase(root.getNodeName())) {
            throw new ApiException("Unexpected response format for forecast.");
        }
        NodeList times = root.getElementsByTagName("time");

        Forecast f = new Forecast();
        for (int i = 0; i < times.getLength(); i++) {
            Element t = (Element) times.item(i);
            String fromIso = t.getAttribute("from"); // e.g., "2025-10-25T12:00:00"
            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.parse(fromIso + "Z"), ZoneId.systemDefault());

            Element symbol = firstChild(t, "symbol");
            String desc = symbol != null ? symbol.getAttribute("name") : "-";
            String icon = symbol != null ? symbol.getAttribute("var") : "01d";

            Element tempEl = firstChild(t, "temperature");
            double tv = tempEl != null ? parseDoubleSafe(tempEl.getAttribute("value")) : Double.NaN;

            ForecastItem item = new ForecastItem();
            item.time = ldt;
            item.description = desc != null ? desc : "-";
            item.iconCode = icon != null ? icon : "01d";
            item.temp = tv;
            f.items.add(item);
        }
        return f;
    }

    // ==== XML helpers ====

    private static Element firstChild(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        return nl.getLength() > 0 ? (Element) nl.item(0) : null;
    }

    private static String getAttr(Element root, String childTag, String attr) {
        Element el = firstChild(root, childTag);
        return el != null ? el.getAttribute(attr) : null;
    }

    private static double getAttrDouble(Element root, String childTag, String attr) {
        return getAttrDouble(root, childTag, attr, Double.NaN);
    }

    private static double getAttrDouble(Element root, String childTag, String attr, double def) {
        String s = getAttr(root, childTag, attr);
        if (s == null || s.isBlank()) return def;
        return parseDoubleSafe(s);
    }

    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return Double.NaN; }
    }

    private static String encode(String s) {
        try { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }

    private static void requireApiKey() {
        if (API_KEY == null || API_KEY.isBlank() || API_KEY.startsWith("REPLACE_")) {
            throw new ApiException("Missing API key. Edit WeatherService.API_KEY.");
        }
    }

    // ==== DTOs ====

    public static class CurrentWeather {
        public String cityName;
        public String description;
        public String iconCode;
        public double temp;
        public double feelsLike;
        public double humidity;
        public double pressure;
        public double windSpeed;
    }

    public static class ForecastItem {
        public LocalDateTime time;
        public String description;
        public String iconCode;
        public double temp;
    }

    public static class Forecast {
        public final List<ForecastItem> items = new ArrayList<>();
    }

    public static class ApiException extends RuntimeException {
        public ApiException(String msg) { super(msg); }
    }
}

