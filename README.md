<!-- PROJECT SHIELDS -->
[![Build Status](https://www.travis-ci.com/radjavi/patsch.svg?token=rmuutsnipHQVjuvXdgR4&branch=main)](https://travis-ci.com/radjavi/patsch) [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/radjavi/patsch">
    <img src="images/PatschLogo2.png" alt="Logo" width="240">
  </a>

  <h1 align="center">Patsch</h1>

  <p align="center">
    This repository contains the implementation that was part of our master's thesis <br>
    <a href="https://odr.chalmers.se/handle/20.500.12380/304106">Critical Patrolling Schedules for Two Robots on a Line</a> at Chalmers University of Technology.
    <br />
    <br />
  </p>
</p>

## Abstract

Patrolling with unbalanced frequencies (PUF) involves scheduling mobile robots that continuously visit a finite number of fixed stations, with known individual maximal waiting times, placed on a line. Recent work demonstrates that PUF is a remarkably challenging problem. To advance current research in the best-known solution, we searched for *critical instances* of the integer patrolling problem (IntPUF). A solvable instance is critical if it becomes impossible to schedule the visits when any station's waiting time is decremented.

Formulating IntPUF as a specific graph problem enabled several algorithms and heuristics to be developed, mainly for solving any instance of IntPUF and searching for critical instances.
The algorithms were proved to be correct and implemented in a Java program to collect the critical instances. 
Benchmarking the implementation shows that solving instances work well when the number of stations is relatively small while searching for critical instances turned out to be extremely difficult.
However, numerous critical instances were found, which reveal interesting patterns that may be further analyzed and utilized to improve the search for additional critical instances. 

## Getting Started

This project has been packaged using ***Gradle***, which runs on all major operating systems. To run our implementation, the only requirement is ***Java JDK*** version 11 or higher. On Windows, the following commands are run using `gradlew.bat` in cmd instead of `./gradlew`.

### Search

To run our application that searches for all critical instances for a given integer m > 1, roof value r > 0, using t > 0 threads:

`./gradlew search --args="m r t"`

For example: `./gradlew search --args="7 14 4"`

### Solve

To run our application that solves an instance (t_0,...,t_m), where every t_i is an integer > 0:

`./gradlew solve --args="(t_0,...,t_m)"`

For example: `./gradlew solve --args="(6,4,1,4,6)"`

### Other tasks

To test, compile and assemble the project, run the following command:

`./gradlew build`

To list all available gradle tasks:

`./gradlew tasks`

### Logs

After running Search or Solve, log and result files will be created in a `logs/` directory.

<!-- LICENSE -->
## License
Distributed under the GPL-3.0 License. See [LICENSE](LICENSE) for more information.

<!-- CONTACT -->
## Contact

- Iman Radjavi: radjavi@hotmail.com
- Anton Gustafsson: antong95@gmail.com
