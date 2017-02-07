package soccerAnalytics;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyCombiner extends Reducer<Text, Text, Text, Text> {
	// Input format --
	// Key - Player ID
	// Value - AverageRating:MarketValue:PlayerName:Position

	// Combiner interface of the MapReduce job
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		float totalRating = 0;
		int marketValue = 0;
		int valueLength = 0;
		String name = "";
		String position = "";

		// Iterates till Combiner consumes all the values
		for (Text value : values) {

			// Split the input string with ":" as delimiter
			String[] splitData = value.toString().split(":");

			// Add the average ratings for all the games for a player
			totalRating += Float.parseFloat(splitData[0]);
			marketValue = Integer.parseInt(splitData[1]);
			position = splitData[3];
			name = splitData[2].toString();
			valueLength++;
		}

		// Context Output Write format --
		// Key - PlayerID
		// Value - TotalRating:MarketValue:PlayerName:NumberOfGames:Position
		context.write(key, new Text(totalRating + ":" + marketValue + ":"
				+ name + ":" + valueLength + ":" + position));

	}

}
