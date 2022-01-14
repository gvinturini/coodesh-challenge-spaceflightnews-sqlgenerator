package main;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {

		try {

			String plainTextJSON = getJSON();
			JSONArray parsedJSON = createJSONObject(plainTextJSON);
			FileWriter newsSQLFile = new FileWriter(new File("insert_news.sql"));
			FileWriter launchesSQLFile = new FileWriter(new File("insert_launches.sql"));
			FileWriter eventsSQLFile = new FileWriter(new File("insert_events.sql"));
			PrintWriter prNews = new PrintWriter(newsSQLFile);
			PrintWriter prLaunches = new PrintWriter(launchesSQLFile);
			PrintWriter prEvents = new PrintWriter(eventsSQLFile);
			String newsSQLTemplate = "INSERT INTO tb_news VALUES ('%ID%', '%TITLE%', '%URL%', '%IMAGEURL%', '%NEWSSITE%', '%SUMMARY%', '%PUBLISHEDAT%', '%UPDATEDATE%', %FEATURED%);";
			String launchesSQLTemplate = "INSERT INTO tb_launches VALUES (null, '%ID%', '%PROVIDER%', '%NEWS_ID%');";
			String eventsSQLTemplate = "INSERT INTO tb_events VALUES (null, '%ID%', '%PROVIDER%', '%NEWS_ID%');";

			List<JSONObject> objList = new ArrayList<JSONObject>();

			for (int i = 0; i < parsedJSON.size(); i++) {

				objList.add((JSONObject) parsedJSON.get(i));

				String newsSQL = newsSQLTemplate.replace("%ID%", objList.get(i).get("id").toString())
						.replace("%TITLE%", objList.get(i).get("title").toString().replace("'", "\\'"))
						.replace("%URL%", objList.get(i).get("url").toString().replace("'", "\\'"))
						.replace("%IMAGEURL%", objList.get(i).get("imageUrl").toString().replace("'", "\\'"))
						.replace("%NEWSSITE%", objList.get(i).get("newsSite").toString())
						.replace("%SUMMARY%", objList.get(i).get("summary").toString().replace("'", "\\'"))
						.replace("%PUBLISHEDAT%", objList.get(i).get("publishedAt").toString())
						.replace("%UPDATEDATE%", objList.get(i).get("updatedAt").toString())
						.replace("%FEATURED%", objList.get(i).get("featured").toString());

				prNews.println(newsSQL);

			}

			for (int j = 0; j < objList.size(); j++) {
				JSONArray launchesArray = (JSONArray) objList.get(j).get("launches");
				if (launchesArray.size() != 0) {
					for (int k = 0; k < launchesArray.size(); k++) {
						JSONObject currentLaunch = (JSONObject) launchesArray.get(k);
						String launchesSQL = launchesSQLTemplate
								.replace("%ID%", currentLaunch.get("id").toString())
								.replace("%PROVIDER%", currentLaunch.get("provider").toString())
								.replace("'%NEWS_ID%'", objList.get(j).get("id").toString());
						prLaunches.println(launchesSQL);
					}
				}

			}

			// Events SQL scripts generation
			for (int j = 0; j < objList.size(); j++) {
				JSONArray eventsArray = (JSONArray) objList.get(j).get("events");

					for (int k = 0; k < eventsArray.size(); k++) {
						JSONObject currentEvent = (JSONObject) eventsArray.get(k);
						
						String eventsSQL = eventsSQLTemplate
								.replace("%ID%", currentEvent.get("id").toString())
								.replace("%PROVIDER%", currentEvent.get("provider").toString())
								.replace("'%NEWS_ID%'", objList.get(j).get("id").toString());

						prEvents.println(eventsSQL);
						
					}

				}

			prNews.close();
			newsSQLFile.close();
			prLaunches.close();
			launchesSQLFile.close();
			prEvents.close();
			eventsSQLFile.close();

			System.out.println("Creation finished");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getJSON() {

		int responsecode = 0;

		try {

			URL arcticlesURL = new URL("https://api.spaceflightnewsapi.net/v3/articles?_limit=50000");

			HttpURLConnection connection = (HttpURLConnection) arcticlesURL.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			responsecode = connection.getResponseCode();

			if (responsecode != 200) {
				throw new RuntimeException("Response: " + responsecode);
			} else {

				String inputLine = "";
				Scanner scanner = new Scanner(arcticlesURL.openStream());

				while (scanner.hasNext()) {
					inputLine += scanner.nextLine();
				}

				scanner.close();

				return inputLine;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static JSONArray createJSONObject(String textJSON) throws ParseException {

		JSONParser parser = new JSONParser();
		JSONArray dataArray = (JSONArray) parser.parse(textJSON);

		return dataArray;

	}

}
