package soccerAnalytics;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper2 extends Mapper<LongWritable, Text, Text, Text> {
	// Input format --
	// Key - "\>"
	// Value - PlayerID:FinalRating:MarketValue:PlayerName:Position

	// Second Map interface of the MapReduce job
	@Override
	public void map(LongWritable key, Text values, Context context)
			throws IOException, InterruptedException {

		float finalRating = 0.0f;

		int marketValue = 0;
		String name = "";
		String position = "";

		// Split the input string with ":" as delimiter
		String[] splitData = values.toString().split(":");

		finalRating = Float.parseFloat(splitData[1]);
		marketValue = Integer.parseInt(splitData[2]);
		name = splitData[3].toString();
		position = splitData[4].toString();

		// Context Output Write format --
		// Key - Position
		// Value - AverageRating:MarketValue:PlayerName
		context.write(new Text(position), new Text(finalRating + ":"
				+ marketValue + ":" + name));

	}
}
