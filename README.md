# Prova Finale Ingegneria del Software 2022
## Gruppo AM52
**Davide Capobianco** [@CDCapobianco](https://github.com/CDCapobianco) <br> cirodavide.capobianco@mail.polimi.it <br>
**Leonardo De Clara** [@leonardodeclara](https://github.com/leonardodeclara) <br> leonardo.declara@mail.polimi.it <br>
**Marianna Dragonetti** [@mariannadragonetti](https://github.com/mariannadragonetti) <br>marianna.dragonetti@mail.polimi.it

## Eriantys Board Game
<img src="https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_scatolaFrontombra.png" width=200px height=200px align="right"/>

Eriantys Board Game is the final test of **"Software Engineering"**, held at Politecnico di Milano (2021/2022).<br>
Game rules are available [here](https://craniointernational.com/2021/wp-content/uploads/2021/06/Eriantys_rules_small.pdf).<br>

## Project specification
The following projects consists of the MVC (Model-View-Controller) implementation of a distributed system made up of a server and multiple clients.<br>
The server is able to manage multiples games and multiple clients connected through socket connections. <br>
The game can be played through a command line interface (CLI) or a graphical user interface (GUI). <br>



## Documentation

### UML
The UML diagrams linked below represent the first design of our model and the designs produced at the end of the project.
- [Initial UML](https://github.com/leonardodeclara/ingsw2022-AM52/blob/master/deliverables/Initial_model_UML.png)
- [Final UML](https://github.com/leonardodeclara/ingsw2022-AM52/tree/master/deliverables/Final%20UML)

### JavaDocs
The project's Javadocs can be found at the following [link]().

### Test Coverage report
JUnit's coverage report is available at the following [link]().

### Tools and Libraries
**Astah** - UML diagrams.<br>
**Intellij IDEA Ultimate** - IDE. <br>
**Maven** - Build automation tool used primarily for Java projects.<br>
**JavaFX** - Java library for graphical user interfaces.<br>
**JUnit** - Unit testing framework.<br>


### Features
- Complete rules
- CLI
- GUI
- Socket
- 2 AF (Advanced Features):
    - __Multiple games:__ server is able to handle multiple matches simultaneously.
    - __Character cards:__ all 12 character cards relative to expert mode have been implemented.


| Functionality    |                       State                        |
|:-----------------|:--------------------------------------------------:|
| Basic rules      | 🟢 |
| Complete rules   | 🟢 |
| Socket           | 🟢 |
| GUI              | 🟢 |
| CLI              | 🟢 |
| Character cards  | 🟢 |
| Multiple games   | 🟢 |
| Persistence      | 🔴 |
| Resilience       | 🔴 |

#### Legend
[🔴]() Not Implemented <br>
[🟢]() Implemented

## Execution

### Jars
Jars can be downloaded from the following link: [Jars]().

### Client

#### CLI
Eryantis CLI client can be executed by typing the following command:
```
java -jar AM52-Client.jar -cli
```
#### GUI
Eryantis GUI client can be executed in three ways:

- by double clicking on  ```AM52-Client.jar```
- by typing the following command:
```
java -jar AM52-Client.jar
```
- by typing the following command:
```
java -jar AM52-Client.jar -gui
```
### Server
In order to execute the game's server the following command is required:
```
java -jar AM52-Server.jar 
```
Then a port number has to be entered in order to start server's execution.
