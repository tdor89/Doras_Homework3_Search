import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.knowm.xchart.ChartEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class APICall {

    public static void main(String[] args) {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://collectionapi.metmuseum.org/public/collection/v1/objects/436535"))
                    .GET()
                    .build();
        }
        catch (URISyntaxException e) {
            System.out.println(
                    "There was a problem with your URL: " + e);
        }

        if (request == null) {
            return;
        }

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() > 299) {

                System.out.println(
                        "The URL is correct but there is no data, because: "
                                + response.statusCode());

            }
            else {

                System.out.println(
                        "This is the data in that API:"
                                + response.body());

                String file = "data.json";

                try (FileWriter writer =
                             new FileWriter(file)) {

                    writer.write(response.body());
                }

                // Read the saved JSON file
                String fileContents =
                        Files.readString(Path.of(file));

                // Same search words used in Homework 2
                String[] searchWords =
                        {"artist", "object", "URL"};

                // Search original data using RegEx
                Map<String, Integer> regexResults =
                        regexSearch(
                                fileContents,
                                searchWords);

                // Build the inverted index
                Map<String, Integer> invertedIndex =
                        buildInvertedIndex(fileContents);

                // Search the completed inverted index
                Map<String, Integer> indexResults =
                        indexSearch(
                                invertedIndex,
                                searchWords);

                // Print RegEx results
                System.out.println(
                        "\nRegEx Search Results:");

                for (String word : searchWords) {

                    System.out.println(
                            word + ": "
                                    + regexResults.get(word)
                                    + " times");
                }

                // Print inverted index results
                System.out.println(
                        "\nInverted Index Results:");

                for (String word : searchWords) {

                    System.out.println(
                            word + ": "
                                    + indexResults.get(word)
                                    + " times");
                }

                // Test increasing input sizes
                int[] multipliers =
                        {1, 10, 20, 30, 40};

                double[] inputSizes =
                        {1, 10, 20, 30, 40};

                double[] regexTimes =
                        new double[multipliers.length];

                double[] indexBuildTimes =
                        new double[multipliers.length];

                double[] indexSearchTimes =
                        new double[multipliers.length];

                // Run each test several times and average
                int trials = 50;

                for (int i = 0;
                     i < multipliers.length;
                     i++) {

                    String testData =
                            fileContents.repeat(
                                    multipliers[i]);

                    long regexTotal = 0;
                    long indexBuildTotal = 0;
                    long indexSearchTotal = 0;

                    for (int trial = 0;
                         trial < trials;
                         trial++) {

                        // Time RegEx search
                        long regexStart =
                                System.nanoTime();

                        regexSearch(
                                testData,
                                searchWords);

                        long regexEnd =
                                System.nanoTime();

                        regexTotal +=
                                regexEnd - regexStart;


                        // Time building the inverted index
                        long indexBuildStart =
                                System.nanoTime();

                        Map<String, Integer> testIndex =
                                buildInvertedIndex(
                                        testData);

                        long indexBuildEnd =
                                System.nanoTime();

                        indexBuildTotal +=
                                indexBuildEnd - indexBuildStart;


                        // Time searching the completed index
                        long indexSearchStart =
                                System.nanoTime();

                        indexSearch(
                                testIndex,
                                searchWords);

                        long indexSearchEnd =
                                System.nanoTime();

                        indexSearchTotal +=
                                indexSearchEnd - indexSearchStart;
                    }

                    // Calculate average times
                    regexTimes[i] =
                            (double) regexTotal
                                    / trials;

                    indexBuildTimes[i] =
                            (double) indexBuildTotal
                                    / trials;

                    indexSearchTimes[i] =
                            (double) indexSearchTotal
                                    / trials;
                }


                // Print timing results
                System.out.println(
                        "\nAverage Response Time:");

                for (int i = 0;
                     i < multipliers.length;
                     i++) {

                    System.out.println(
                            "\n" + multipliers[i]
                                    + "x input:");

                    System.out.println(
                            "RegEx search: "
                                    + (long) regexTimes[i]
                                    + " ns");

                    System.out.println(
                            "Inverted index build: "
                                    + (long) indexBuildTimes[i]
                                    + " ns");

                    System.out.println(
                            "Inverted index search: "
                                    + (long) indexSearchTimes[i]
                                    + " ns");
                }


                // Print Big-O comparison
                System.out.println(
                        "\nBig-O Comparison:");

                System.out.println(
                        "RegEx search: O(n * m)");

                System.out.println(
                        "Inverted index build: O(n)");

                System.out.println(
                        "Inverted index lookup: O(1) average");


                // Create line chart
                XYChart chart =
                        new XYChartBuilder()
                                .width(800)
                                .height(600)
                                .title(
                                        "RegEx vs Plain Inverted Index")
                                .xAxisTitle(
                                        "Input Size (x original JSON)")
                                .yAxisTitle(
                                        "Response Time (nanoseconds)")
                                .build();


                // Compare search times after index is built
                chart.addSeries(
                        "RegEx Search",
                        inputSizes,
                        regexTimes);

                chart.addSeries(
                        "Inverted Index Build",
                        inputSizes,
                        indexBuildTimes);

                chart.addSeries(
                        "Inverted Index Search",
                        inputSizes,
                        indexSearchTimes);


                // Display the chart
                new SwingWrapper<>(chart)
                        .displayChart();


                // Save chart as PNG
                ChartEncoder.saveChart(
                        chart,
                        "search_comparison",
                        "png");

                System.out.println(
                        "\nChart saved as search_comparison.png");
            }
        }

        catch (InterruptedException e) {

            System.out.println(
                    "The server connection was interrupted: "
                            + e);
        }

        catch (IOException e) {

            System.out.println(
                    "There was a network or file error: "
                            + e);
        }
    }


    // Search using RegEx
    public static Map<String, Integer> regexSearch(
            String text,
            String[] searchWords) {

        Map<String, Integer> results =
                new HashMap<>();

        for (String word : searchWords) {

            Pattern pattern =
                    Pattern.compile(
                            word,
                            Pattern.CASE_INSENSITIVE);

            Matcher matcher =
                    pattern.matcher(text);

            int count = 0;

            while (matcher.find()) {
                count++;
            }

            results.put(
                    word,
                    count);
        }

        return results;
    }


    // Build a plain inverted index
    public static Map<String, Integer> buildInvertedIndex(
            String text) {

        Map<String, Integer> invertedIndex =
                new HashMap<>();

        // Separate camelCase words and underscores
        String normalizedText =
                text.replaceAll(
                                "([a-z0-9])([A-Z])",
                                "$1 $2")
                        .replace("_", " ")
                        .toLowerCase();

        // Break the text into individual words
        String[] words =
                normalizedText.split(
                        "[^a-z0-9]+");

        // Add each word using a lambda
        Arrays.stream(words)
                .filter(word -> !word.isEmpty())
                .forEach(word ->
                        invertedIndex.merge(
                                word,
                                1,
                                Integer::sum));

        return invertedIndex;
    }


    // Search the inverted index using a lambda
    public static Map<String, Integer> indexSearch(
            Map<String, Integer> invertedIndex,
            String[] searchWords) {

        Map<String, Integer> results =
                new HashMap<>();

        Arrays.stream(searchWords)
                .forEach(word ->
                        results.put(
                                word,
                                invertedIndex.getOrDefault(
                                        word.toLowerCase(),
                                        0)));

        return results;
    }
}