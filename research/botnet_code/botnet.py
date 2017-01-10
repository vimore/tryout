#!/usr/bin/python

import string
from string import *

from pylab import *

from networkx import *
from communityDetection import *


class Botnet:
    def __init__(self, fileName):
        xx = 0
    def __iter__(self):
        return self

    def combineListOfGraphs(self, Gs):
        Gx = nx.Graph()
        for key in Gs.keys():
            for ed in Gs[key].edges():
                Gx.add_edge(ed[0], ed[1])
                Gx.node[ed[0]] = Gs[key].node[ed[0]]
                Gx.node[ed[1]] = Gs[key].node[ed[1]]
        return(Gx)


    def combineListOfGraphsWithWeights(self, Gs, weights):
        Gx = nx.Graph()
        for key in Gs.keys():
            for ed in Gs[key].edges():
                Gx.add_edge(ed[0], ed[1])
                Gx.node[ed[0]] = Gs[key].node[ed[0]]
                Gx.node[ed[1]] = Gs[key].node[ed[1]]
                if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                    Gx.edge[ed[0]][ed[1]]['property'] = {}
                Gx.edge[ed[0]][ed[1]]['property'][key] = weights[key]
        return(Gx)



    def readBeaconScores(self, fileName):
        anomaly_col = 5
        host_col = 2
        method_col = 3
        ua_col = 4

        anomaly = {}
        host2risk = {}
        method2risk = {}
        ua2risk = {}

        fpIn = open(fileName, 'r')
        line = fpIn.readline()[:-1]
        line = fpIn.readline()[:-1]
        while line != "":
            arr = line.split('\t')
            arr1 = [arr[1], arr[0]]
            anomaly[join(arr[0:2],'\t')] = atof(arr[anomaly_col])
            host2risk[join(arr[0:2],'\t')] = atof(arr[host_col])
            method2risk[join(arr[0:2],'\t')] = atof(arr[method_col])
            ua2risk[join(arr[0:2],'\t')] = atof(arr[ua_col])

            anomaly[join(arr1,'\t')] = atof(arr[anomaly_col])
            host2risk[join(arr1,'\t')] = atof(arr[host_col])
            method2risk[join(arr1,'\t')] = atof(arr[method_col])
            ua2risk[join(arr1,'\t')] = atof(arr[ua_col])

            line = fpIn.readline()[:-1]
        fpIn.close()
        return(anomaly, host2risk, method2risk, ua2risk)


    def extractHighRiskBeaconGraph(self, anomaly, risk, conf_thr, risk_thr):
        Gx = nx.Graph()
        for edge_key in anomaly.keys():
            if (anomaly[edge_key] > conf_thr and risk[edge_key] > risk_thr):
                ed = string.split(edge_key,'\t')
                Gx.add_edge(ed[0], ed[1])
                Gx.add_node(ed[0], bipartite = 0)
                Gx.add_node(ed[1], bipartite = 1)
        return(Gx)


    def getNodesOfGivenType(self, G, node_type):
        nds = {}
        for nd in G.nodes():
            if (G.node[nd]['bipartite'] == node_type):
                nds[nd] = 1
        return(nds)

    def beaconing(self, fileName):
        [anomaly, risk_host, risk_method, risk_ua] = self.readBeaconScores(fileName)

        Gb = {}

        # Construct graph that consists of high risk hosts
        Gb['host'] = self.extractHighRiskBeaconGraph(anomaly, risk_host, conf_thr = 0.75, risk_thr = 0.75)

        # Construct graph that consists of high risk methods (posts instead of gets)
        Gb['method'] = self.extractHighRiskBeaconGraph(anomaly, risk_method, conf_thr = 0.75, risk_thr = 0.75)
        Gt = self.extractHighRiskBeaconGraph(anomaly, risk_method, conf_thr = 0.75, risk_thr = 0.75)

    
        # Construct graph that consists of high risk low probability user agents
        Gb['ua'] = self.extractHighRiskBeaconGraph(anomaly, risk_ua, conf_thr = 0.75, risk_thr = 0.75)
        
        Gb = self.combineListOfGraphs(Gb)

        # Remove domains that have only one source. 
        bot = {}
        nds = self.getNodesOfGivenType(Gb, 1)
        for nd in nds:
            if (Gb.degree(nd) < 2):
                Gb.remove_node(nd)

        # Get the connected components. Each connected component is a potential botnet
        C = connected_component_subgraphs(Gb)

        # Output each connected component (botnet) 
        out_file = "botnet_by_beaconing"
        fpOut = open(out_file, "w")
        arr = ['Source', 'Destination', 'Count', 'Beacon_Score', 'sld_risk', 'method_risk', 'ua_risk', 'aggreated_risk', 'BotnetIndex']
        out_str = join(arr, '\t')
        fpOut.write(out_str + '\n')
        fpOut.close()
        index = 1
        for ii in range(len(C)):
            Gx = C[ii]
            if (len(Gx.edges()) > 0):
                self.outputBotnetByBeacon(Gx, anomaly, risk_host, risk_method, risk_ua, index, out_file)
                index += 1

        return(Gb, C)

    def outputBotnetByBeacon(self, G, anomaly, risk_host, risk_method, risk_ua, index, fileName):
        fpOut = open(fileName, "a")
        for ed in G.edges():
            src = ed[0]
            dst = ed[1]
            if (G.node[ed[0]]['bipartite'] == 0):
                ed = [ed[0], ed[1]]
            else:
                ed = [ed[1], ed[0]]
            ed_str = join(ed,'\t')
            arr = []
            arr.append(ed_str)
            count= 0
            #for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            #count += G.edge[ed[0]][ed[1]]['property'][pr]['count']
            agg_risk = (1-((1.0 - risk_host[ed_str])*(1.0 - risk_method[ed_str])*(1.0 - risk_ua[ed_str])))*anomaly[ed_str]
            arr.append(str(count))
            arr.append(str(anomaly[ed_str]))
            arr.append(str(risk_host[ed_str]))
            arr.append(str(risk_method[ed_str]))
            arr.append(str(risk_ua[ed_str]))
            arr.append(str(agg_risk))
            arr.append(str(index))

            out_str = join(arr, '\t')
            fpOut.write(out_str + '\n')

        fpOut.close()


    def extractLowProbabilityGraph(self, traffic):
        Gx = nx.Graph()
        for edge_key in traffic.keys():
            ed = string.split(edge_key,'\t')
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite = 0)
            Gx.add_node(ed[1], bipartite = 1)
        return(Gx)


    def readLowProbabilityTraffic(self, fileName, trafficThr):
        host_col = 2
        method_col = 3
        ua_col = 4

        host2prob = {}
        method2prob = {}
        ua2prob = {}

        fpIn = open(fileName, 'r')
        line = fpIn.readline()[:-1]
        line = fpIn.readline()[:-1]
        while line != "":
            arr = line.split('\t')
            if (atof(arr[host_col]) > trafficThr['host']):
                host2prob[join(arr[0:2],'\t')] = atof(arr[host_col])

            if (atof(arr[method_col]) > trafficThr['method']):
                method2prob[join(arr[0:2],'\t')] = atof(arr[method_col])

            if (atof(arr[ua_col]) > trafficThr['ua']):
                ua2prob[join(arr[0:2],'\t')] = atof(arr[ua_col])


            line = fpIn.readline()[:-1]
        fpIn.close()
        return(host2prob, method2prob, ua2prob)


    def filterByAlexaRank(self, G, sld2alexa, alexaThr):
        slds = self.getNodesOfGivenType(G, 1)
        for sld in slds:
            if (sld2alexa[sld] < alexaThr):
                G.remove_node(sld)

    def filterEdgesByThr(self, G, thr=2):
        slds = self.getNodesOfGivenType(G, 1)
        for ed in G.edges():
            if (len(G.edge[ed[0]][ed[1]]['property'].keys()) < thr):
                G.remove_edge(ed[0],ed[1])


    def botnetsByProbabiltyTraffic(self, fileName):
        trafficThr = {}
        trafficThr['host'] = 0.95
        trafficThr['method'] = 0.95
        trafficThr['ua'] = 0.95
        weights = {}
        weights['host'] = 1.0
        weights['method'] = 1.0
        weights['ua'] = 1.0

        lowProbTraffic = {}
        [lowProbTraffic['trafficByHost'], lowProbTraffic['trafficByMethod'], lowProbTraffic['trafficByUserAgent']] = self.readLowProbabilityTraffic(fileName, trafficThr)

        Gs = {}
        Gs['host'] = self.extractLowProbabilityGraph(lowProbTraffic['trafficByHost'])
        Gs['method']  = self.extractLowProbabilityGraph(lowProbTraffic['trafficByMethod'])
        Gs['ua'] = self.extractLowProbabilityGraph(lowProbTraffic['trafficByUserAgent'])
        Gt = self.combineListOfGraphsWithWeights(Gs, weights)

        ## Filter High rank slds if they are popular in alexa
        sld2alexa = self.readAlexa(fileName)
        self.filterByAlexaRank(Gt, sld2alexa, 6)
        self.filterEdgesByThr(Gt, thr=2)

        C = nx.connected_component_subgraphs(Gt)

        outputFile = 'botnetGraphs'
        fpOut = open(outputFile,'w')
        fpOut.close()

        index = 0
        for Cx in C:
            if (len(Cx.nodes()) > 2):
                self.outputBotnetByLowProbabilityTraffic(Cx, index, outputFile, lowProbTraffic)
                index += 1
        return(Gt)

    
    def readAlexa(self, fileName):
        alexa_col = 7
        sld2alexa = {}

        fpIn = open(fileName, 'r')
        line = fpIn.readline()[:-1]
        line = fpIn.readline()[:-1]
        while line != "":
            arr = line.split('\t')
            arr1 = [arr[1], arr[0]]
            sld2alexa[arr[1]] = atoi(arr[alexa_col])
            line = fpIn.readline()[:-1]
            
        return(sld2alexa)


    def getBestPartitions(self, G):
        partition = best_partition(G)

        Gc = {}
        for ii in range(max(partition.values())):
            nds = {}
            for key in partition.keys():
                if (partition[key] == ii):
                    nds[key] = ii
            print (nds)
            Gc[ii] = self.extractGraphFromSet(G, nds)
        return(Gc, partition)

    def extractGraphFromSet(self, G, nds):
        Gx = nx.Graph()
        for ed in G.edges():
            if (nds.has_key(ed[0]) == True and nds.has_key(ed[1]) == True):
                Gx.add_edge(ed[0], ed[1])
                Gx.node[ed[0]] = G.node[ed[0]]  
                Gx.node[ed[1]] = G.node[ed[1]]  
        return(Gx)

        
    def outputBotnetByLowProbabilityTraffic(self, G, index, fileName, lowProbTraffic):
        fpOut = open(fileName, "a")
        out_vect = []
        for ed in G.edges():
            src = ed[0]
            dst = ed[1]
            if (G.node[ed[0]]['bipartite'] == 1):
                src = ed[1]
                dst = ed[0]
            out_vect = []
            out_vect.append(str(index))
            out_vect.append(src)
            out_vect.append(dst)

            ed_str = join([src,dst],'\t')

            for key in lowProbTraffic.keys():
                if (lowProbTraffic[key].has_key(ed_str) == False):
                    out_vect.append(str(-1))
                else:
                    out_vect.append(str(lowProbTraffic[key][ed_str]))
            
            output_str = join(out_vect,'\t')
            fpOut.write(output_str + '\n')
        fpOut.close()

if __name__ == '__main__':
    B = Botnet('tmpfile')
    fileName = 'beacon_results.txt'
    #[X,C] = B.beaconing(fileName)
    Gs = B.botnetsByProbabiltyTraffic(fileName)
    


    

