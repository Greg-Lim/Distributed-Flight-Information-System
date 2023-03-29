# Distributed-Flight-Information-System

### Enviroment Set-Up
```
cd Distributed-Flight-Information-System\flight-info-system
mvn install
```

### Client (Java)
```
mvn exec:java -D exec.mainClass=com.sc4051.client.Client -D exec.args="<SERVER ADDRESS> <PORT> <MODE> <SEND PROBIBILITY> <SEND ATTEMPTS>"
```
#### Note:
```
<SERVER ADDRESS> is the address of the server
<PORT> is the port client would comunicate from
<MODE> is the invocation semantic. Possible values:
- 1: at-least-once
- 2: at-most-once
<SEND PROBIBILITY> is the probability of message succeeding
<SEND ATTEMPTS` is the number of attempts
```

### Server (Java)
```
mvn exec:java -D exec.mainClass=com.sc4051.server.Server -D exec.args="<PORT> <MODE> <SEND PROBIBILITY> <SEND ATTEMPTS>"
```
#### Note:
```
<PORT> is the port client would comunicate from
<MODE> is the invocation semantic. Possible values:
- 1: at-least-once
- 2: at-most-once
<SEND PROBIBILITY> is the probability of message succeeding
<SEND ATTEMPTS` is the number of attempts
```
