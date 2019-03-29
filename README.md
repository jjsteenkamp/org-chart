# Organisational Chart Generator

The author of this application and his associates had the need to be able to generate an organisational chart.

The first attempt was to create and maintain an organisational chart using Microsoft Office. However, it quickly became clear that this approach did not really scale to the size of organisation we wanted to present.

Hence this simple project was created and, after no thought whatsoever, made available on GitHub. As you image it comes with no warranty or restrictions. 

_Of course, the actual names of the organisational chart itself is not made public .... the example uses a number of mocked up names_

## Building the Project

This is a simple single module maven project. Clone the project and build it: 

```
mvn clean install
```

## Generating the Organisational Chart (as a GML file)

In the root of the project run:

```
java -cp "target/appassembler/repo/*" com.jsteenkamp.orgchart.ProduceChartFromFile <input-file-name>
```

If you don't have a file yet - you can use: 'graph-example.txt'

```
java -cp "target/appassembler/repo/*" com.jsteenkamp.orgchart.ProduceChartFromFile src/main/resources/graph-example.txt
```
 
## Opening the GML File - 'target/ProduceChartFromFile-Output.gml'

If all is well - the system will produce a Graph Modelling Language Output : https://en.wikipedia.org/wiki/Graph_Modelling_Language 

The best tool I found to open it was yEd : https://www.yworks.com/products/yed 


## Notes on the file format

This input file format is as naive and simple as you can get. Effectively it uses new lines and tabs to demarcate different organisational boxes (the new lines) and the levels in the organisation (the tabs).

If you want to have a 'placeholder' - you prefix it with a '#'. This means that while the box will be visible, it will have no label.

![Alt text](ProduceChartFromFile-Output.png?raw=true "Example Chart Output")

```
Bob TopManager
	John Secondton
	#Peter Placeholder
		Josef Level
		Mary Speller
		Joe Bloggs
		Peter Hay
			Phillip Intern
	Sue Binary
		James Thirdson
	Ben LastManager
		Cassandra Underling
			Stephen Smith
	Laura James
```
