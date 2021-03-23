# AmongSus #

PPS Course 19-20 project.

### Build status

![GitHub Workflow Status (branch)](https://github.com/OlegKonchenkov/PPS-AmongSus-Konchenkov-Pasqualini-Poggi/actions/workflows/scala.yml/badge.svg)
![GitHub contributors](https://img.shields.io/github/contributors/OlegKonchenkov/PPS-AmongSus-Konchenkov-Pasqualini-Poggi)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/OlegKonchenkov/PPS-AmongSus-Konchenkov-Pasqualini-Poggi)
![GitHub](https://img.shields.io/github/license/OlegKonchenkov/PPS-AmongSus-Konchenkov-Pasqualini-Poggi)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/OlegKonchenkov/PPS-AmongSus-Konchenkov-Pasqualini-Poggi)
[![codecov](https://codecov.io/gh/OlegKonchenkov/PPS-AmongSus-Konchenkov-Pasqualini-Poggi/branch/master/graph/badge.svg?token=4PY2JY91Q3)](https://codecov.io/gh/OlegKonchenkov/PPS-AmongSus-Konchenkov-Pasqualini-Poggi)

### Overview

AmongSus is a game based on the popular game Among Us, written in Scala.<br/>
The game is an "online multiplayer social deduction game" wherein up to ten players 
run around inside a virtual spaceship collect classic coin as a task while trying 
to figure out who the "Impostors" are before they kill everyone.

### How to compile

To Install the project, you need to execute the build through a shell, using the following command:

```shell script
sbt clean pack
```

### How to execute

Then you can execute the project, tap the following command to let Server to start:
- Windows:
```shell script
./server/target/pack/bin/amongsus-server
```
- Linux/Mac
```shell script
.\server\target\pack\bin\amongsus-server
```

And finally execute the command to start the Client of the game:
- Windows:
```shell script
./client/target/pack/bin/app-launcher
```
- Linux/Mac
```shell script
.\client\target\pack\bin\app-launcher
```

### Authors

- [Oleg Konchenkov](https://github.com/OlegKonchenkov)
- [Elia Pasqualini](https://github.com/eliapasqualini)
- [Giovanni Poggi](https://github.com/GiovanniPoggi)