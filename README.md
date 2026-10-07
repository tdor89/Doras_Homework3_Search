# Homework 3 - Search Algorithm Comparison

This Java program compares two methods for searching JSON data:

1. Regular Expressions (RegEx)
2. Plain Inverted Index

The program uses the same Metropolitan Museum of Art API and the same three search terms used in Homework 2.

## API Used

Metropolitan Museum of Art Collection API:

https://collectionapi.metmuseum.org/public/collection/v1/objects/436535

## Search Terms

The program searches for:

- artist
- object
- URL

Both search methods count how many times each term appears in the JSON data.

## RegEx Search

The RegEx search uses Java's `Pattern` and `Matcher` classes.

The search is case-insensitive and scans the JSON data for each search term.

## Plain Inverted Index

The second method uses a plain inverted index stored in a `HashMap`.

The program processes the JSON data and stores each word and its frequency in the index.

Lambda functions are used when building and searching the inverted index.

## Execution Time Comparison

The program uses `System.nanoTime()` to measure:

- RegEx search time
- Inverted index build time
- Inverted index search time

Each test is run 50 times and the average time is calculated.

To test larger input sizes, the original JSON response is repeated at:

- 1x
- 10x
- 20x
- 30x
- 40x

RegEx is faster for a single search because it does not require an index to be built first.

The inverted index takes additional time to build, but once it exists, searching the index is much faster.

## Big-O Comparison

- RegEx search: `O(n * m)`
- Inverted index build: `O(n)`
- Inverted index lookup: `O(1)` average

## Chart

The program uses XChart to create a line chart comparing:

- RegEx Search
- Inverted Index Build
- Inverted Index Search

The X-axis shows input size.

The Y-axis shows average execution time in nanoseconds.

The chart is also saved as:

```text
search_comparison.png
```

## Maven

This project uses Maven to manage the XChart library.

The XChart dependency is included in `pom.xml`, so it is downloaded automatically when the project is built.