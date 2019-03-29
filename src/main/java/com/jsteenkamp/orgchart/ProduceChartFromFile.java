package com.jsteenkamp.orgchart;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.io.GmlExporter;
import org.jgrapht.io.IntegerComponentNameProvider;

public class ProduceChartFromFile 
{
	public static void main(String... args) throws Exception
	{
		if(args.length != 1)
		{
			System.err.println("You have not specified a grapth input file as the first parameter. For an example - use src/main/resources/graph-example.txt");
			System.exit(0);
		}
		
		final List<String> allLines = Files.readAllLines(Paths.get(args[0]));
		OrgNodeEntity rootNode = buildNodes(allLines);
		DirectedAcyclicGraph<OrgNodeEntity, DefaultEdge> directedGraph = new DirectedAcyclicGraph<OrgNodeEntity, DefaultEdge>(DefaultEdge.class);
		
		directedGraph.addVertex(rootNode);
		_recurivelyAddNodes(directedGraph, rootNode);
	
		GmlExporter<OrgNodeEntity, DefaultEdge> exporter = new GmlExporter<>(new IntegerComponentNameProvider<>(), v -> v.getName(), new IntegerComponentNameProvider<>(), null);

		exporter.setParameter(GmlExporter.Parameter.ESCAPE_STRINGS_AS_JAVA, true);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
		
		File outputFile = new File("target/ProduceChartFromFile-Output.gml");
		
		exporter.exportGraph(directedGraph, outputFile );
		
		System.out.println("Exported the GRAPH ! " + outputFile.getAbsolutePath() );
		System.exit(0);
	}
	
	private static void _recurivelyAddNodes(DirectedAcyclicGraph<OrgNodeEntity, DefaultEdge> directedGraph, OrgNodeEntity node)
	{
		Set<OrgNodeEntity> children = node.getChildren();
		for(OrgNodeEntity child : children)
		{
			directedGraph.addVertex(child);
			directedGraph.addEdge(node, child);
			_recurivelyAddNodes(directedGraph, child);
		}
	}
	
	private static OrgNodeEntity buildNodes(List<String> allLines)
	{
		OrgNodeEntity rootNode = null;
		Map<Integer, OrgNodeEntity> lastParentStack = new HashMap<>();
		
		int count = 0;
		for(String line : allLines)
		{
			count++;
			
			if(rootNode == null)
			{
				rootNode = OrgNodeEntity.parseFromLine(null, line);
				lastParentStack.put(0, rootNode);
				continue;
			}
			
			int currentLevel = OrgNodeEntity.getTabs(line);
			if(currentLevel == 0)
			{
				throw new RuntimeException("File format invalid - can only have 1 root.Line was : \n" + line + "\nLine Number: "+ count);
			}
			
			OrgNodeEntity lastParent = lastParentStack.get(currentLevel - 1);
			OrgNodeEntity entry = OrgNodeEntity.parseFromLine(lastParent, line);
			lastParentStack.put(currentLevel, entry);
		}
		return rootNode;
	}
}




class OrgNodeEntity
{
	private static final AtomicInteger idCounter = new AtomicInteger();
	
	private final int id;
	
	private final String name;
	private final int level;
	
	private final OrgNodeEntity parent;
	private final Set<OrgNodeEntity> children;
	
	public static final int getTabs(String line)
	{
		int tabCount = 0;
		char[] lineChars = line.toCharArray();
		for (int i = 0; i < lineChars.length; i++)
		{
			if(lineChars[i] == '\t') 
			{
				tabCount++;
			}
			else
			{
				break;
			}
		}
		return tabCount;
	}
	
	public static OrgNodeEntity parseFromLine(OrgNodeEntity parent, String line)
	{
		int tabs = getTabs(line);
		String name;
		if(tabs == 0)
		{
			name = line.trim();
		}
		else
		{
			name = line.substring(line.lastIndexOf('\t') + 1, line.length()).trim(); 
		}
		
		OrgNodeEntity orgE = new OrgNodeEntity(parent, name, tabs);
		if(parent != null)
		{
			parent.children.add(orgE);
		}
		return orgE; 
		
	}

	private OrgNodeEntity(OrgNodeEntity parent, 
		                  String namEP, 
		                  int level)
	{
		super();
		this.id = idCounter.getAndIncrement();
		if(namEP.contains("\""))
		{
			this.name = namEP.replace("\"", "`");
		}
		else if(namEP.trim().startsWith("#"))
		{
			this.name = "";
		}
		else
		{
			this.name = namEP;
		}
		this.level = level;
		this.parent = parent;
		this.children = new HashSet<>();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrgNodeEntity other = (OrgNodeEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public int getLevel()
	{
		return level;
	}

	public OrgNodeEntity getParent()
	{
		return parent;
	}

	public Set<OrgNodeEntity> getChildren()
	{
		return children;
	}
	
	
}















