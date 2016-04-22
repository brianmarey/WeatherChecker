package com.careydevelopment.weatherchecker;

import java.text.DecimalFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.careydevelopment.jsonparser.JSONArray;
import com.careydevelopment.jsonparser.JSONException;
import com.careydevelopment.jsonparser.JSONObject;
import com.careydevelopment.jsonparser.JSONParser;
import com.careydevelopment.propertiessupport.PropertiesFactory;
import com.careydevelopment.propertiessupport.PropertiesFactoryException;
import com.careydevelopment.propertiessupport.PropertiesFile;

public class WeatherChecker {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WeatherChecker.class);
	
	private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
	

	public static Weather getWeather(String searchTerm) throws WeatherCheckerException {
		Weather weather = new Weather();
		
		try {
			String appKey = getAppKey();
			String url = getUrl(appKey,searchTerm);
			LOGGER.info("url is " + url);
			JSONObject json = JSONParser.readJsonFromUrl(url);
			//LOGGER.info(json.toString());
			weather = getWeatherFromJson(json);
		} catch (JSONException je) {
			throw new WeatherCheckerException(je);
		}
		
		return weather;
	}
	
	
	private static Weather getWeatherFromJson(JSONObject json) throws JSONException {
		Weather weather = new Weather();
		
		JSONObject main = json.getJSONObject("main");
		JSONArray weatherArray = json.getJSONArray("weather");
		JSONObject wind = json.getJSONObject("wind");
		JSONObject sys = json.getJSONObject("sys");
		JSONObject clouds = json.getJSONObject("clouds");
		
		String cloudCover = clouds.getString("all");
		weather.setClouds(cloudCover);
		
		String windSpeed = wind.getString("speed");
		weather.setWind(windSpeed);
		
		String country = sys.getString("country");
		weather.setCountry(country);
		
		String city = json.getString("name");
		weather.setCity(city);
		
		String temp = main.getString("temp");
		temp = convertToFarenheit(temp);
		weather.setTemp(temp);
		
		String humidity = main.getString("humidity");
		weather.setHumidity(humidity);
		
		if (weatherArray.length() > 0) {
			String description = weatherArray.getJSONObject(0).getString("description");
			weather.setDescription(description);
		}
		
		
		return weather;
	}
	
	
	private static String convertToFarenheit(String kelvin) {
		String farenheit = "";
		
		Float kelvinF = Float.parseFloat(kelvin);
		Float farenheitF = ((kelvinF - 273.5f) * 1.8f) + 32;
		
		farenheit = farenheitF.toString();
		
		if (farenheit.indexOf(".") > -1) {
			farenheit = farenheit.substring(0, farenheit.indexOf("."));
		}
		
		
		
		return farenheit;
	}
	
	
	private static String getUrl(String appKey, String searchTerm) {
		StringBuilder builder = new StringBuilder();
		builder.append(BASE_URL);
		builder.append(searchTerm);
		builder.append("&APPID=");
		builder.append(appKey);
		
		return builder.toString();
	}
	
	
	private static String getAppKey() throws WeatherCheckerException {
		try {
			Properties props = PropertiesFactory.getProperties(PropertiesFile.OPEN_WEATHER_MAP_PROPERTIES);
			String apiKey = props.getProperty("api.key");
			return apiKey;
		} catch (PropertiesFactoryException pe) {
			throw new WeatherCheckerException(pe);
		}
	}

	public static void main(String[] args) {
		try {
			Weather weather = getWeather("27587");
			LOGGER.info("Temp is " + weather.getDescription());
		} catch(WeatherCheckerException we) {
			we.printStackTrace();
		}
	}

}
