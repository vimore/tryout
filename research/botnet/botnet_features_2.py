import sys
sys.path.append('/Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/prototype_coding/hive-repo/prototype/GraphModeling/')

from sklearn import *
from sklearn.decomposition import *

from networkx import *

from graphDB import *

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
    # Note: we previously already removed highly connected destinations.

    # Get anomaly score for destinationHostOrIp, requestMethod, and requestClientApplication.
    b2s_host = readBeaconScoresByType(fileName, risk_col=2, anomaly_col=5 )
    b2s_method = readBeaconScoresByType(fileName, risk_col=3, anomaly_col=5 )
    b2s_ua = readBeaconScoresByType(fileName, risk_col=4, anomaly_col=5 )

    # Construct the graph of edges where risk and confidence scores are above a threshold.
    Gb_host = extractGraphByHighBeaconRiskAndConf(G, b2s_host, conf_thr=0.75, risk_thr = 0.75)
    Gb_method = extractGraphByHighBeaconRiskAndConf(G, b2s_method, conf_thr=0.75, risk_thr = 0.75)
    Gb_ua = extractGraphByHighBeaconRiskAndConf(G, b2s_ua, conf_thr=0.75, risk_thr = 0.75)
    
    bot = {}
    # Iterate through the graphs and eliminate nodes with degree 1 (no connected components possible).
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

    # Find the connected components of each graph.
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





