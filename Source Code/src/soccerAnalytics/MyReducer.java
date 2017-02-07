package soccerAnalytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyReducer extends Reducer<Text, Text, Text, Text> {
	// Input format --
	// Key - PlayerID
	// Value - TotalRating:MarketValue:PlayerName:NumberOfGames:Position
	
	// First Reduce interface of the MapReduce job
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		int valueLength = 0;
		float finalRating = 0.0f;
		float totalRating = 0.0f;
		int marketValue = 0;
		String name = "";
		String position = "";

		// Iterates till Reducer consumes all the values
		for (Text value : values) {
			// Split the input string with ":" as delimiter
			String[] splitData = value.toString().split(":");
			
			valueLength += Integer.parseInt(splitData[3]);
			totalRating += Float.parseFloat(splitData[0]);
			marketValue = Integer.parseInt(splitData[1]);
			name = splitData[2].toString();
			position = splitData[4].toString();
		}

		// Calculate final rating for a player across all the games
		finalRating = (totalRating) / (valueLength);

		// HashMap soccerMap is created with String key and Float value
		HashMap<String, Float> soccerMap = new HashMap<String, Float>();

		// Populate the HashMap for every new Player ID
		if (!(soccerMap.containsKey(key.toString())))

			// HashMap contents --
			// Key - Player ID
			// Value - Final Rating
			soccerMap.put(key.toString(), finalRating);

		// Get the HashMap set
		Set<Entry<String, Float>> set = soccerMap.entrySet();

		// Convert Map to List
		List<Entry<String, Float>> list = new ArrayList<Entry<String, Float>>(
				set);

		// Output the HashMap contents
		for (Map.Entry<String, Float> entry : list) {

			// Context Output Write format --
			// Key - "->"
			// Value - PlayerID,FinalRating:MarketValue:PlayerName:Position
			context.write(new Text("->"), new Text(key + ":"
					+ entry.getValue().toString() + ":" + marketValue + ":"
					+ name + ":" + position));
		}
	}
}
