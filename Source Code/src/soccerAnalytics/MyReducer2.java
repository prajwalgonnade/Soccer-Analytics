package soccerAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyReducer2 extends Reducer<Text, Text, Text, Text> {
	// Input format --
	// Key - Position
	// Value - AverageRating:MarketValue:PlayerName

	// Second Reduce interface of the MapReduce job
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		float finalRating = 0.0f;
		int marketValue = 0;
		String name = "";

		// HashMap soccerMap2 is created with String key and Float value
		HashMap<String, Float> soccerMap2 = new HashMap<String, Float>();

		// Iterates till Reducer consumes all the values
		for (Text value : values) {

			// Split the input string with ":" as delimiter
			String[] splitData = value.toString().split(":");

			finalRating = Float.parseFloat(splitData[0]);
			marketValue = Integer.parseInt(splitData[1]);
			name = splitData[2].toString();

			soccerMap2.put(name + ":" + marketValue,
					(float) (Math.floor(finalRating * 100) / 100));
		}

		// Get the HashMap set
		Set<Entry<String, Float>> set = soccerMap2.entrySet();

		// Convert Map to List
		List<Entry<String, Float>> list = new ArrayList<Entry<String, Float>>(
				set);

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> o1,
					Map.Entry<String, Float> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Write System Output to local file
		File file = new File("C:/Users/soccerAnalyticsOutput.txt");
		FileOutputStream fis = new FileOutputStream(file);
		PrintStream out = new PrintStream(fis);
		System.setOut(out);
		String pn = String.format("%-20s", "Player Name");
		String mv = String.format("%-10s", "Market Value");
	//	String or = String.format("%-20s", "Overall Rating");
		//System.out.println("Player name\tMarket Value\t\tOverall Rating");
		System.out.println(pn + "\t" + mv + "\t\t" + "Overall Rating");
		System.out
				.println("--------------------------------------------------------------");

		int counter = 0;

		// Iterate over the List
		for (Map.Entry<String, Float> entry : list) {

			// Limit the output to top 5 players
			if (counter < 5) {

				String concat[] = entry.getKey().split(":");

				String str1 = String.format("%-20s", concat[0]);
				String str2 = String.format("%-10s", concat[1]);
				Float str3 = entry.getValue();

				// Context Output Write format --
				// Key - PlayerName:MarketValue
				// Value - Overall Rating
				context.write(
						new Text(
								"Player name\t\t\tMarket Value\tOverall Rating\n"
										+ "-------------------------------------------------------------\n"
										+ str1 + "\t"), new Text(str2 + "\t"
								+ str3 + "\n"));

				System.out.println(str1 + "\t" + str2 + "\t\t" + str3);

			} else {
				break;
			}
			counter++;

		}
	}

}
