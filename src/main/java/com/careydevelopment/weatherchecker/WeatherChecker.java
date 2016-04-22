package com.careydevelopment.weatherchecker;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			LOGGER.info(json.toString());
			//weather = getWeatherFromJson(json);
		} catch (JSONException je) {
			throw new WeatherCheckerException(je);
		}
		
		return weather;
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
		} catch(WeatherCheckerException we) {
			we.printStackTrace();
		}
	}

}
