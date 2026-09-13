# Algorithm Laboratory 1

## Overview

This project is a reusable Java framework for experimentally measuring and analyzing the performance of algorithms. It tests the supplied Selection Sort, Insertion Sort, Merge Sort, and `Arrays.sort` implementations using randomly generated integer arrays.

The framework performs JVM warm-up runs, repeated timed trials, statistical analysis, theoretical growth-model comparison, and CSV export.

## Requirements

* Java JDK installed
* No Maven, Gradle, Ant, or IDE-specific setup is required
* The project can be compiled and run using plain `javac` and `java`

## Project Files

The main framework classes are:

* `Algorithm.java` — interface implemented by algorithms being tested
* `InputGenerator.java` — interface for generating test inputs
* `Measurement.java` — performs warm-up runs and timed trials
* `PerformanceData.java` — stores timing statistics
* `Experiment.java` — runs an algorithm across multiple input sizes
* `Analysis.java` — compares measured data with theoretical growth models
* `Report.java` — prints results and exports CSV files
* `BenchmarkMain.java` — runs the full experiment

The supplied sorting algorithms are:

* `SelectionSort.java`
* `InsertionSort.java`
* `MergeSort.java`
* `ArraysSortWrapper.java`

The supplied input generator is:

* `RandomIntArrayGenerator.java`

`NaiveTimingDemo.java` is also included to demonstrate problems with reusing mutated inputs and performing only one un-warmed-up timing trial.

## Compile

Open a terminal in the directory containing the `.java` files.

For example:

```text
cd src
```

Compile all Java files with:

```text
javac *.java
```

If compilation succeeds, no output is normally displayed.

## Run the Naive Timing Demo

To run the provided timing demonstration:

```text
java NaiveTimingDemo
```

This demonstrates why reusing the same array and using only one timing measurement can produce misleading results.

## Run the Full Experiment

Run the complete benchmark with:

```text
java BenchmarkMain
```

The experiment uses:

* 5 untimed warm-up iterations per input size
* 12 timed trials per input size
* Fresh input for every warm-up and timed trial
* `System.nanoTime()` for timing
* Timing only around the algorithm's `execute()` method

Selection Sort and Insertion Sort use smaller input sizes because their expected \(O(n^2)\) running time grows quickly. Merge Sort and `Arrays.sort` use larger input sizes because their expected \(O(n \log n)\) growth allows them to handle larger inputs efficiently.

## Output

The program prints timing statistics and growth-model comparisons to the terminal.

CSV files are written to the `results` directory:

```text
results/
    Selection_Sort.csv
    Insertion_Sort.csv
    Merge_Sort.csv
    Arrays.sort_JDK_.csv
```

Each CSV contains:

* Input size
* Number of timed trials
* Mean execution time
* Median execution time
* Standard deviation
* Minimum execution time
* Maximum execution time
* Fitted \(n\) prediction
* Fitted \(n \log n\) prediction
* Fitted \(n^2\) prediction

These files can be opened in Excel, Google Sheets, or another plotting program to create the required execution-time graphs.

## Model Analysis

The program compares each algorithm against three candidate growth models:

* \(O(n)\)
* \(O(n \log n)\)
* \(O(n^2)\)

The models are compared using Mean Absolute Percentage Error (MAPE) and \(R^2\). The model with the lowest MAPE is reported as the best fit.

## Expected Theoretical Growth

* Selection Sort: \(O(n^2)\)
* Insertion Sort on random input: \(O(n^2)\)
* Merge Sort: \(O(n \log n)\)
* `Arrays.sort(int[])`: approximately \(O(n \log n)\)

Actual experimental results may differ because of JVM behavior, JIT compilation, CPU caching, garbage collection, operating-system scheduling, constant factors, and the tested input-size range.

## Automated Tool Use

ChatGPT was used to assist with framework design, code organization, timing methodology, model-fit analysis, and report organization. The assignment requirements and supplied starter code were provided as context, and the generated material was reviewed before being incorporated into the project.
