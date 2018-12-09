package com.tool.channels;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


@SpringBootApplication
public class ChannelsApplication {

    public static final String filePath = "/Users/p2766623/Desktop/";

    public static void main(String[] args) {
        SpringApplication.run(ChannelsApplication.class, args);
        getChannelsWithoutFeeds(filePath);
        //deleteChannels(filePath);
        System.out.println("Successfully completed");
    }

    private static void deleteChannels(String filePath) {
        String filePathChannels = filePath + "channels_no_feeds.json";
        JSONArray entitiesList = getChannels(filePathChannels);
        String cdvrCsBaseUrl = "http://localhost:25100/cs/v3";
        RestTemplate restTemplate = new RestTemplate();

        for (Object o : entitiesList) {
            JSONObject channel = (JSONObject) o;
            String channelId = (String) channel.get("id");
            String endpoint = cdvrCsBaseUrl + "/channels/" + channelId;
            restTemplate.delete(endpoint);
            System.out.println("Delete request for channel Id: " + endpoint);
        }
    }


    private static void getChannelsWithoutFeeds(String filePath) {
        JSONObject resultChannelsWithoutFeeds = new JSONObject();
        JSONArray channelsWithoutFeeds = new JSONArray();
        JSONObject resultChannelsWithFeeds = new JSONObject();
        JSONArray channelsWithFeeds = new JSONArray();
        try {
            String filePathChannels = filePath + "channels.json";
            JSONArray entitiesList = getChannels(filePathChannels);
            for (Object o : entitiesList) {
                JSONObject channel = (JSONObject) o;
                String strId = (String) channel.get("id");
                if (channel.containsKey("feeds")) {
                    JSONArray feedsArray = (JSONArray) channel.get("feeds");
                    if (feedsArray.size() == 0) {
                        channelsWithoutFeeds.add(channel);

                    } else {
                        channelsWithFeeds.add(channel);
                    }
                } else {
                    channelsWithoutFeeds.add(channel);
                }


            }
            resultChannelsWithFeeds.put("entities", channelsWithFeeds);
            resultChannelsWithoutFeeds.put("entities", channelsWithoutFeeds);
            System.out.println("Total Number of channels in the file " + entitiesList.size());
            System.out.println("Number of channels with feeds: " + channelsWithFeeds.size());
            System.out.println("Number of channels without feeds: " + channelsWithoutFeeds.size());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // writing JSON to file:"channel_with_no_feeds.json" in cwd
            PrintWriter pw = new PrintWriter("channel_with_no_feeds.json");
            pw.write(gson.toJson(resultChannelsWithoutFeeds));
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static JSONArray getChannels(String filePath) {

        JSONArray entitiesList = null;
        try {
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject inputChannels = (JSONObject) jsonParser.parse(reader);
            entitiesList = (JSONArray) inputChannels.get("entities");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return entitiesList;
    }
}
