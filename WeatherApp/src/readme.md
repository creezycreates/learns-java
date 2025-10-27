# Weather Information App (JavaFX)

A GUI app that shows **real-time weather** using the OpenWeatherMap API.

## Features
- City input + Search
- Current weather: temperature, feels-like, humidity, wind, pressure, conditions
- Short-term forecast (next periods)
- Unit switch: Celsius ↔ Fahrenheit (and wind m/s ↔ mph)
- Search history with timestamps
- Error handling (invalid city, network/API issues)
- Dynamic backgrounds (day/evening/night)
- No external libraries (uses OWM **XML** mode + Java 11 `HttpClient`)

## Setup
1. Get a free API key from https://openweathermap.org/
2. Open `src/app/WeatherService.java` and set:
   ```java
   public static final String API_KEY = "YOUR_KEY_HERE";
3. Build & Run (On macOS/Linux replace %PATH_TO_FX% with the JavaFX SDK lib path.): 

    
    javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.graphics src/app/*.java -d out
    java  --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.graphics -cp out app.WeatherApp

4. Usage
   - Enter a city (e.g., London, New York) and click Search
   - Switch Units to Metric (°C) or Imperial (°F)
   - View Forecast list and History on the right
   - Status bar shows messages and errors