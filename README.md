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
I seguenti diagrammi delle classi rappresentano rispettivamente il modello iniziale sviluppato durante la fase di progettazione e i diagrammi del prodotto finale nelle parti critiche riscontrate.
- [Initial UML]()
- [Final UML]()

### JavaDocs


### Test Coverage report


### Libreries


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
java -jar EriantysClient -cli
```
#### GUI
Eryantis GUI client can be executed in three ways:

- by double clicking on  ```EriantysClient.jar```
- by typing the following command:
```
java -jar EriantysClient.jar
```
- by typing the following command:
```
java -jar EriantysClient.jar -gui
```
### Santorini Server
In order to execute the game's server the following command is required:
```
java -jar EriantysServer.jar -port <port_number>
```
where ``` <port_number> ``` represents the server port. By default its value is 1024.