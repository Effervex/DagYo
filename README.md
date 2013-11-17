DAGYo
=====

WHAT:
A simple graph implementation (not necessarily a Directed Acyclic Graph). This implementation is a socket-based, parallelizable, primarily Command-Line based tool. Each graph basically consists of a number of nodes and n-ary edges, with a number of optional modules that can be added.

WHY:
This was created for multiple purposes: to provide a simple graph based structure for a multitude of projects, and to form the basis of the OpenCyc reimplementation. This was spurred by the need to reduce memory size, (hopefully) increase inference speed, and improve stability. The reimplementation can be found at https://github.com/Effervex/CycDAG.git

HOW:
To run as a command-line interface, simply compile the java files and launch DAGCommandLineInterface. Optional arguments include -p <portNumber> -r <rootDirectory> -n <numCachedNodes> -e <numCachedEdges>. The various config files define additional aspects such as which commands and modules are in use (many are defined within the java files).

To connect to DagYo, run 'telnet localhost 2425' (or whatever port number is used). Type 'list' to see all available commands and type 'help <command>' to see more information on each command.

WHO & WHERE:
The DAG was initially developed by Dr Sam Sarjant at the University of Waikato, New Zealand. It was built upon Dr Craig Taube-Schock's CLI wrapper for WikipediaMiner (GenericCLI.jar). The idea came about as a solution for both OpenCyc's shortcomings, and a framework for future work with Wikipedia and other projects.

Contact: Sam Sarjant sarjant@waikato.ac.nz
