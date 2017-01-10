/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gephi.toolkit.demos;;

/**
 * This demo shows how to use the <code>AutoLayout</code> class to run layout
 * programmatically.
 * <p>
 * You can set a layout duration, and an execution ratio for several layout. For
 * instance you set 0.8 for a Yifan Hu algorithm and 0.2 for Label Adjust. If
 * execution time is 100 seconds, the first algorithm run for 80 seconds and the
 * second for 20 seconds. It also allows to change property values dynamically
 * (at a certain ratio or interpolated if values are numerical).
 *
 * @author Mathieu Bastian
 */

//Init a project - and therefore a workspace
var pc = Lookup.getDefault.lookup(classOf[ProjectController])
pc.newProject()
var workspace = pc.getCurrentWorkspace()

//Generate a new random graph into a container
var container = Lookup.getDefault().lookup(classOf[ContainerFactory]).newContainer()
RandomGraph randomGraph = new RandomGraph()
randomGraph.setNumberOfNodes(500)
randomGraph.setWiringProbability(0.005)
randomGraph.generate(container.getLoader())

//Append container to graph structure
var importController = Lookup.getDefault().lookup(classOf[ImportController])
importController.process(container, new DefaultProcessor(), workspace)

//See if graph is well imported
var graphModel = Lookup.getDefault().lookup(classOf[GraphController]).getModel()
var graph = graphModel.getDirectedGraph()
println("Nodes: " + graph.getNodeCount())
println("Edges: " + graph.getEdgeCount())

//Layout for 1 minute
var autoLayout = new AutoLayout(1, TimeUnit.MINUTES)
autoLayout.setGraphModel(graphModel)


var secondLayout = new ForceAtlasLayout(null);
var adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", true, 0.1f);//True after 10% of layout time
var repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 500d, 0f);//500 for the complete period
autoLayout.addLayout(firstLayout, 0.5f);
autoLayout.addLayout(secondLayout, 0.5f, Array(adjustBySizeProperty, repulsionProperty));

autoLayout.execute();

//Export
var ec = Lookup.getDefault().lookup(ExportController.class);
try {
    ec.exportFile(new File("autolayout.pdf"));
} catch (IOException ex) {
    ex.printStackTrace();
}

ForceAtlas2 fa2Layout = new ForceAtlas2(new ForceAtlas2Builder());
fa2Layout.setGraphModel(graphModel);
fa2Layout.resetPropertiesValues();
fa2Layout.setEdgeWeightInfluence(1.0);
fa2Layout.setGravity(1.0);
fa2Layout.setScalingRatio(2.0);
fa2Layout.setBarnesHutTheta(1.2);
fa2Layout.setJitterTolerance(0.1);

for (int i = 0; i < 100 && fa2Layout.canAlgo(); i++) 
fa2Layout.goAlgo();
