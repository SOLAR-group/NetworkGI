# Improving Android Network usage

This repository is the accompaniment to the paper: Reducing the Network Usage of Android Applications with Genetic Improvement, for the SSBSE RENE Track 2023.

In this repository is the code to instrument http requests made by Android applications, a modified version of the GIDroid framework (https://github.com/SOLAR-group/GIDroid), and the results of our experiments.

## Profile Apps

Requires Java 11 and maven.

To instrument applications, simply build a jar using the command:

```clean compile assembly:single```

Next call the jar with arguments ```a``` (path to apk to instrument) and ```p``` (the name of the package in which the classes you wish to instrument are.

Once instrumented, simply install the apk onto a device or emulator, and run a random input generation tool, such as Monkey (https://developer.android.com/studio/test/other-testing-tools/monkey) on your code.
All http request outputs will be logged at the debug level.

## GIDroid

Full instructions for running GIDroid are available in the readme in the GIDroid directory.

## Results

Results from our experiments are available in the results directory, they are divided by which search algorithm was used and then the application that GIDroid ran on.
