package soccerAnalytics;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//Class to implement the Mapper interface
public class MyMapperGoalkeeper extends Mapper<LongWritable, Text, Text, Text> {

	// First Map interface of the MapReduce job
	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		try {
			// Get the current line
			String input = value.toString();

			// Split the string with "," as delimiter
			String[] lineItems = input.split(",");

			// Define the attribute columns for Goalkeeper
			int[] attributeNumber = { 6, 7, 12, 13, 14 };

			int i = 0;

			// Create Array List for to consolidate attribute ratings of
			// individual games
			ArrayList<String> gameRating = new ArrayList<String>();
			for (i = 0; i < attributeNumber.length; i++) {
				gameRating.add(lineItems[attributeNumber[i]]);

			}

			String playerID = lineItems[0];
			String position = lineItems[3];

			float totalRating = 0;

			// Add attribute ratings for individual game
			for (int j = 0; j < gameRating.size(); j++) {
				totalRating += Float.parseFloat(gameRating.get(j));
			}

			float avgRating = 0;

			// Find average rating for single game
			avgRating = totalRating / gameRating.size();

			// Collect Market Value for each player from input data
			int marketValue = Integer.parseInt(lineItems[18]);

			// Concatenate Player First Name and Last Name
			String playerName = lineItems[1] + " " + lineItems[2];

			// Filter input data for Goalkeeper
			if (position.equalsIgnoreCase("Goal_keeper")) {

				// Context Output Write format --
				// Key - Player ID
				// Value - AverageRating:MarketValue:PlayerName:Position
				context.write(new Text(playerID), new Text(avgRating + ":"
						+ marketValue + ":" + playerName + ":" + position));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
