import sys
sys.path.append('/Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/prototype_coding/hive-repo/prototype/GraphModeling/')

from sklearn import *
from sklearn.decomposition import *

from networkx import *

#import pygraphviz as pg
#from pygraphviz import *

from bluecoat_features import *

def preFilterGraph(G):
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    alexa = readAlexaTop1M()
    wlist = readWhiteList("whitelist")
    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G,1)
    filterByWhiteList(G, wlist)

def filterGraphByHighDegreeDests(G, degree_thr):
    nds = getNodesOfGivenType(G, 1)
    for nd in nds:
        if (G.degree(nd) > degree_thr):
            G.remove_node(nd)


def botnetByBeaconing(G):
    fileName = 'beacon_results.txt'
    b2s_host = readBeaconScoresByType(fileName, risk_col=2, anomaly_col=5 )
    b2s_method = readBeaconScoresByType(fileName, risk_col=3, anomaly_col=5 )
    b2s_ua = readBeaconScoresByType(fileName, risk_col=4, anomaly_col=5 )

    Gb_host = extractGraphByHighBeaconRiskAndConf(G, b2s_host, conf_thr=0.75, risk_thr = 0.75)
    Gb_method = extractGraphByHighBeaconRiskAndConf(G, b2s_method, conf_thr=0.75, risk_thr = 0.75)
    Gb_ua = extractGraphByHighBeaconRiskAndConf(G, b2s_ua, conf_thr=0.75, risk_thr = 0.75)
    
    bot = {}
    nds = getNodesOfGivenType(Gb_host, 1)
    for nd in nds:
        if (Gb_host.degree(nd) < 2):
            Gb_host.remove_node(nd)

    nds = getNodesOfGivenType(Gb_method, 1)
    for nd in nds:
        if (Gb_method.degree(nd) < 2):
            Gb_method.remove_node(nd)

    nds = getNodesOfGivenType(Gb_ua, 1)
    for nd in nds:
        if (Gb_ua.degree(nd) < 2):
            Gb_ua.remove_node(nd)

    x = connected_component_subgraphs(Gb_host)
    y = connected_component_subgraphs(Gb_method)
    z = connected_component_subgraphs(Gb_ua)

    fpOut = open("data_to_display","w")
    out_str = 'src' + '\t' + 'dst' + '\t' + 'beaconing_score' + '\t' + 'Number of Sessions' + '\n'
    fpOut.write(out_str)
    for ed in x[0].edges():

        if (G.node[ed[0]]['bipartite'] == 0):
            src = ed[0]
            dst = ed[1]
        else:
            src = ed[1]
            dst = ed[0]

        ed_str = src + '\t' + dst
        count = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            count += G.edge[ed[0]][ed[1]]['property'][pr]['count']

        data_str = ed_str + '\t' + str(b2s_host[ed_str][0]) + '\t' + str(count) + '\n'
        fpOut.write(data_str)
    fpOut.close()

    return(x,y,z)

def botnetDetectionByDeniedTraffic(G):
    Gd = extract_graph_by_edge_field(G, 'DENIED')
    nds = getNodesOfGivenType(Gd, 0)
    for nd in nds:
        x = unicode(nd)
        if (is_internal(x) == False):
            Gd.remove_node(nd)

    for nd in Gd.nodes():
        if (Gd.degree(nd) == 0):
            Gd.remove_node(nd)

    vol_distr = getVolDistr(Gd)
    return(Gd, vol_distr)
    
def getVolDistr(G):
    vol_distr = []
    for ed in G.edges():
        count = 0
        for pr in G.edge[ed[0]][ed[1]]['property']:
            count += G.edge[ed[0]][ed[1]]['property'][pr]['count']
        vol_distr.append(count)
    return(np.array(vol_distr))


def botnetDetectionByLowProbUAs(G, field_col = 5, thr=0.005):
    uas = getFieldsOfGivenType(G, field_col)
    tot = 1.0*sum(uas.values())
    selected_uas = {}
    counter = 0
    for key in uas.keys():
        if (uas[key]/tot < thr):
            selected_uas[key] = uas[key]

    #Look for 
    Gx = extract_graph_by_edge_field(G,'\t4')


    return(selected_uas)


def getGraphFromGivenFieldValues(G, field_col, fields):
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            arr = string.split(pr,'\t')
            if (fields.has_key(arr[field_col]) == True):
                G.add_edge(ed[0],ed[1])
                Gx.edge[ed[0]][ed[1]]['property'][pr] =  G.edge[ed[0]][ed[1]]['property'][pr]
    for nd in Gx.nodes():
        Gx.node[nd] = G.node[nd]
    return(Gx)

def getRelativeRank(fields):
    tot = 1.0*max(fields.values())
    for key in fields.keys():
        fields[key] = fields[key]/tot

def getRelativeRankAndFilter(fields, thr):
    tot = 1.0*max(fields.values())
    selected_fields = {}
    for key in fields.keys():
        if (fields[key]/tot < thr):
            selected_fields[key] = fields[key]/tot
    return(selected_fields)

def getGraphByLowProbUAs(G, ua_field_col=5):
    uas = getFieldsOfGivenType(G, ua_field_col)
    uas = getRelativeRankAndFilter(uas,0.001)
    Gx = getGraphFromGivenFieldValues(G, ua_field_col, uas)
    return(Gx)

def extractBeaconingAndHighRiskSLD(G):
    fileName = 'beacon_results.txt'
    b2s = readBeaconScoresByType(fileName, risk_col=2, anomaly_col=5 )
    Gb = extractGraphByHighBeaconRiskAndConf(G, b2s, conf_thr=0.75, risk_thr = 0.75)
    return(Gb)

def extractBeaconingAndHighRiskMethod(G):
    fileName = 'beacon_results.txt'
    b2s = readBeaconScoresByType(fileName, risk_col=3, anomaly_col=5 )
    Gb = extractGraphByHighBeaconRiskAndConf(G, b2s, conf_thr=0.75, risk_thr = 0.75)
    return(Gb)

def extractBeaconingAndHighRiskUA(G):
    fileName = 'beacon_results.txt'
    b2s = readBeaconScoresByType(fileName, risk_col=4, anomaly_col=5 )
    Gb = extractGraphByHighBeaconRiskAndConf(G, b2s, conf_thr=0.75, risk_thr = 0.75)
    return(Gb)


def mergeGraphs(GList):
    Gx = nx.Graph()
    for G in GList:
        for ed in G.edges():
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.add_edge(ed[0],ed[1])
            if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                Gx.edge[ed[0]][ed[1]]['property'] = {}
            for pr in G.edge[ed[0]][ed[1]]['property'].keys():
                Gx.edge[ed[0]][ed[1]]['property'][pr] = G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)
                


def extractBeaconingGraphByHighRisk(G, anomaly, risk, conf_thr, risk_thr):
    Gx = nx.Graph()
    count1 = 0
    count2 = 0
    for ed in G.edges():
        ed_str = join(ed, '\t')
        if (anomaly.has_key(ed_str) == True and anomaly[ed_str] > conf_thr and risk[ed_str] > risk_thr):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite = G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite = G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']
    return(Gx)


def extractBeaconingGraphByHighRisk(G, anomaly, risk, conf_thr, risk_thr):
    Gx = nx.Graph()
    count1 = 0
    count2 = 0
    for ed in G.edges():
        ed_str = join(ed, '\t')
        if (anomaly.has_key(ed_str) == True and anomaly[ed_str] > conf_thr and risk[ed_str] > risk_thr):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite = G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite = G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']
    return(Gx)

                
def botnetByBeaconing_simplifiled_model(G):
    fileName = 'beacon_results.txt'

    
    [anomaly, risk_host, risk_method, risk_ua] = readBeaconScores(fileName)

    Gb = {}

    # Construct graph that consists of high risk hosts
    Gb['host'] = extractBeaconingGraphByHighRisk(G, anomaly, risk_host, conf_thr = 0.75, risk_thr = 0.75)

    # Construct graph that consists of high risk methods (posts instead of gets)
    Gb['method'] = extractBeaconingGraphByHighRisk(G, anomaly, risk_method, conf_thr = 0.75, risk_thr = 0.75)

    
    # Construct graph that consists of high risk low probability user agents
    Gb['ua'] = extractBeaconingGraphByHighRisk(G, anomaly, risk_ua, conf_thr = 0.75, risk_thr = 0.75)

    # Combine these three graphs into one graph
    Gb = combineListOfGraphs(Gb)


    # Remove domains that have only one source. 
    bot = {}
    nds = getNodesOfGivenType(Gb, 1)
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
            outputBotnetByBeacon(Gx, anomaly, risk_host, risk_method, risk_ua, index, out_file)
            index += 1

    return(C)

def extractBeaconingGraphByHighRisk(G, anomaly, risk, conf_thr, risk_thr):
    Gx = nx.Graph()
    count1 = 0
    count2 = 0
    for ed in G.edges():
        ed_str = join(ed, '\t')
        if (anomaly.has_key(ed_str) == True and anomaly[ed_str] > conf_thr and risk[ed_str] > risk_thr):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite = G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite = G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']
    return(Gx)



def readBeaconScores(fileName):
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
    

def botnetByBeaconing_simplified2():
    fileName = 'beacon_results.txt'
    [anomaly, risk_host, risk_method, risk_ua] = readBeaconScores(fileName)

def botnetByBeaconing_simplifiled_modelx(G):
    fileName = 'beacon_results.txt'

    
    [anomaly, risk_host, risk_method, risk_ua] = readBeaconScores(fileName)

    Gb = {}

    # Construct graph that consists of high risk hosts
    Gb['host'] = extractBeaconingGraphByHighRisk(G, anomaly, risk_host, conf_thr = 0.75, risk_thr = 0.75)

    # Construct graph that consists of high risk methods (posts instead of gets)
    Gb['method'] = extractBeaconingGraphByHighRisk(G, anomaly, risk_method, conf_thr = 0.75, risk_thr = 0.75)

    
    # Construct graph that consists of high risk low probability user agents
    Gb['ua'] = extractBeaconingGraphByHighRisk(G, anomaly, risk_ua, conf_thr = 0.75, risk_thr = 0.75)

    # Combine these three graphs into one graph
    Gb = combineListOfGraphs(Gb)


    # Remove domains that have only one source. 
    bot = {}
    nds = getNodesOfGivenType(Gb, 1)
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
            outputBotnetByBeacon(Gx, anomaly, risk_host, risk_method, risk_ua, index, out_file)
            index += 1

    return(C)

