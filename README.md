# distributedsystemsapp
Project written in Java for the lecture Principles of Distributed Systems


Principles of Distributed Systems - Winter Semester 2015/2016
Practical Assignment
Due date: 26.01.2016
Write a simple command line tool that does distributed read and write operation.
The tool requires the following functionalities:
1) Connectivity
a) It needs to be possible to interconnect several hosts to a logical network. The
implementation of a sophisticated overlay structure is not required. It is sufficient to have
all nodes connected to all other nodes.
b) It is not necessary to implement routing mechanisms. Since each node knows all others
nodes, messages can just be send to all nodes.
c) It is required to have a join operation that allows a new node to join the network of already
connected nodes.
d) A discovery mechanism is also not necessary. New machines join the network by sending
a join message to one of the machines already in the network. The address of the new host
is thereupon propagated in the network.
e) Hosts also need to be able to sign off from the network again.
f) One node in the network needs to be elected as master node. The master node stores a
string variable that is initially empty.
2) Distributed Read and Write Operations
a) Once the network is established any node can start a distributed read / write process by
sending a start message to all the other nodes in the network.
b) The process takes 20 seconds. During this time all the nodes in the network do the
following:
LOOP
a) Wait a random amount of time
b) Read the string variable from the master node
c) Append some random english word to this string
d) Write the updated string to the master node
END LOOP
c) After the process has ended all the nodes read the final string from the master node and
write it to the screen. Moreover they check if all the words they added to the string are
present in the final string. The result of this check is also written to the screen.
d) To make this work, the read and write operations need to get synchronised.
e) All hosts have to write all the actions they perform to the screen in order to be able to
retrace the process.
In order to implement this distributed process you need to have knowledge about distributed
synchronisation, which will be discussed in chapter 5 of the lecture. For this purpose you should
organise your work into two parts. At first implement the connectivity part, so that nodes can join
and leave the network and are able to send messages to each other. As soon as distributed
mutual exclusion mechanisms have been presented in the lecture you can start with the second
part, implementing the distributed calculations. You have to implement two different mutual
exclusion algorithms: Centralised Mutual Exclusion and Ricart & Agrawala.
The master node needs to be elected by the Bully algorithm. This algorithm will also be
discussed in chapter 5 of the lecture. In case the current master node signs off or fails a new
master has to be elected.
