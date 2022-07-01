# Prova Finale Ingegneria del Software 2022
## Gruppo AM52
### Davide Capobianco [(@CDCapobianco)]<br> cirodavide.capobianco@mail.polimi.it <br>
### Leonardo De Clara [(@leonardodeclara)]<br> leonardo.declara@mail.polimi.it <br>
### Marianna Dragonetti [(@mariannadragonetti)] <br>marianna.dragonetti@mail.polimi.it

The following projects consists of the MVC (Model-View-Controller) implementation of a distributed system made up of a server and multiple clients.
The server is able to manage multiples games and multiple clients connected through socket connections.
The game can be played through a command line interface (CLI) or a graphical user interface (GUI).



## Documentation

### UML
The UML diagrams linked below represent the first design of our model and the designs produced at the end of the project.
- [Initial UML]()
- [Final UML]()

### JavaDocs
The project's Javadocs can be found at the following [link]().

### Test Coverage report
JUnit's coverage report is available at the following [link]().

### Libraries


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

## Compilation and execution

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
### Eriantys Server
In order to execute the game's server the following command is required:
```
java -jar AM52-Server.jar 
```
Then a port number has to be entered in order to start server's execution.