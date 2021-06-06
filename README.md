<!-- PROJECT SHIELDS -->
[![Build Status](https://www.travis-ci.com/radjavi/patsch.svg?token=rmuutsnipHQVjuvXdgR4&branch=main)](https://travis-ci.com/radjavi/patsch) [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/radjavi/patsch">
    <img src="images/PatschLogo2.png" alt="Logo" width="240">
  </a>

  <h1 align="center">Patsch</h1>

  <h5 align="center">
    Critical Patrolling Schedules for Two Robots on a Line
  </h5>

  <p align="center">
    About the project...
    <br />
    <br />
  </p>
</p>

## Getting Started
To get started with the project you will need ***JDK 11***. The project has been packaged using ***Gradle***. To test, compile and assemble the project, run the following command:

`./gradlew build`

To only run the tests:

`./gradlew test`

To run our application that searches for all critical instances for a given integer m, where m > 1 (defaults to m=4 if args not specified), using t threads:

`./gradlew run --args="m t"`

To list all available gradle tasks:

`./gradlew tasks`

<!-- LICENSE -->
## License
Distributed under the GPL-3.0 License. See [LICENSE](LICENSE) for more information.



<!-- CONTACT -->
## Contact

- Iman Radjavi: radjavi@hotmail.com
- Anton Gustafsson: antong95@gmail.com