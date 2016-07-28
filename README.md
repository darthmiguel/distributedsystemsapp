# distributedsystemsapp
Project written in Java for the lecture Principles of Distributed Systems


Principles of Distributed Systems - Winter Semester 2015/2016

###### Practical Assignment

Write a simple command line tool that does distributed read and write operation.
The tool requires the following functionalities:
* Connectivity
  * It needs to be possible to interconnect several hosts to a logical network. The implementation of a sophisticated overlay structure is not required. It is sufficient to have all nodes connected to all other nodes.
  * It is not necessary to implement routing mechanisms. Since each node knows all others nodes, messages can just be send to all nodes.
  * It is required to have a join operation that allows a new node to join the network of already connected nodes.
  * A discovery mechanism is also not necessary. New machines join the network by sending a join message to one of the machines already in the network. The address of the new host is thereupon propagated in the network.
  * Hosts also need to be able to sign off from the network again.
  * One node in the network needs to be elected as master node. The master node stores a string variable that is initially empty.
* Distributed Read and Write Operations
  * Once the network is established any node can start a distributed read / write process by sending a start message to all the other nodes in the network.
  * The process takes 20 seconds. During this time all the nodes in the network do the following:
    * LOOP
      * Wait a random amount of time
      * Read the string variable from the master node
      * Append some random english word to this string
      * Write the updated string to the master node
    * END LOOP
  * After the process has ended all the nodes read the final string from the master node and write it to the screen. Moreover they check if all the words they added to the string are present in the final string. The result of this check is also written to the screen.
  * To make this work, the read and write operations need to get synchronised.
  * All hosts have to write all the actions they perform to the screen in order to be able to retrace the process.
