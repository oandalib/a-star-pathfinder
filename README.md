# A* Pathfinder

### Purpose
The purpose of this project is to perform the [A* Search Algorithm](https://en.wikipedia.org/wiki/A*_search_algorithm) on
a 20x20 grid that the user can customize the start and end positions with, along with any barriers. This implementation 
uses the Manhattan Distance for determining cell H scores.<br>
This entire project could've been done in Javascript, but I wanted the additional challenge of getting two separate 
languages to work together, and specifically play around with a messaging queue (RabbitMQ).

### Languages/Communication
* Javascript/Node: The simple front end of the site is made with HTML/CSS/Javascript. The front end makes a request to the
  back-end through a REST API, which then starts a Java app through command-line. Node then retrieves the result from
  RabbitMQ and updates the front-end.
* Java: Java does the actual work of the path-finding algorithm and sends the result to RabbitMQ.

### Running
Simply clone this repo and:
```sh
$ docker-compose up
```
* Please wait 5-10 seconds after the Docker containers start (not just build) before accessing the site.
* Access site at [localhost](http:/localhost).
