import sys
sys.path.append('/Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/prototype_coding/hive-repo/prototype/GraphModeling/')

import operator

from sklearn import *
from sklearn.decomposition import *

import networkx as nx
from networkx import *

from graphDB import *

#import pygraphviz as pg
#from pygraphviz import *

from bluecoat_features import *

def extractNetworkByProbabilityRangeOfUserAgents(G, ua_col = 5, min_prob_thr = 0, max_prob_thr=0.1):
    Gu = nx.Graph()
    for ed in G.edges():
        if (G.node[ed[0]]['bipartite'] == 0):
            src = ed[0]
        else:
            src = ed[1]

        for pr in G.edge[ed[0]][ed[1]]['property']:
            ua = string.split(pr, '\t')[ua_col]
            Gu.add_edge(ua, src)
            Gu.add_node(ua, bipartite=0)
            Gu.add_node(src, bipartite=1)

    uas = getNodesOfGivenType(Gu, 0)
    uas = Gu.degree(uas)

    max_degree = (1.0*max(uas.values()))

    low_prob_uas = {}
    for key in uas.keys():
        if (uas[key]/max_degree < min_prob_thr or uas[key]/max_degree > max_prob_thr):
            
            del uas[key]

    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            ua = string.split(pr, '\t')[ua_col]
            if (uas.has_key(ua) == True):
                Gx.add_edge(ed[0], ed[1])
                Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
                Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
                if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                    Gx.edge[ed[0]][ed[1]]['property']  = {}

                Gx.edge[ed[0]][ed[1]]['property'][pr] = G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx, uas)
                

def extractNetworkByLowProbabilityMethods(G):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByHighFrequencyCommunications(G, thr):
    Gx = nx.Graph()
    count_distr = []
    for ed in G.edges():
        count = 0
        for pr in G.edge[ed[0]][ed[1]]['property']:
            count += G.edge[ed[0]][ed[1]]['property'][pr]['count']
        count_distr.append(count)
        if (count >  thr):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']
    return(Gx, count_distr)

def extractNetworkByHighBeaconing(G):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByKnownMalwareDownloads(G, fileNames):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByDownloadType(G, downLoadType='.dll'):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByMethodType(G, methodTye='GET'):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByDomain(G, domainType='.com'):
    Gx = nx.Graph()
    return(Gx)
                            
def extractNetworkByDenied(G):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByNewDestinationSites(G):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByLowAlexa(G, AlexaThr):
    Gx = nx.Graph()
    return(Gx)


def extractNetworkByLowTTLSites(G, ttlThr = 100):
    Gx = nx.Graph()
    return(Gx)


def extractNetworkByDomainFlux(G):
    Gx = nx.Graph()
    return(Gx)

def extractNetworkByFastFlux(G):
    Gx = nx.Graph()
    return(Gx)



def botnetDetectionByUserAgentOld(G):

    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G, 1)
    filterByWhiteList(G, wlist)

    vol_array = extract_volume_distr(G)
    mu = mean(vol_array)
    sigma = std(vol_array)

    vol_thr = mu+sigma

    print ('vol: ', mu, sigma, vol_thr)
    min_prob_thr = 0.0
    max_prob_thr = 0.3
    ua_col = 5
    
    [Gx, uas] = extractNetworkByProbabilityRangeOfUserAgents(G, ua_col, min_prob_thr, max_prob_thr)
    
    Gc = connected_component_subgraphs(Gx)
    for ii in range(len(Gc)):
        for ed in Gc[ii].edges():
            Gc[ii].add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gc[ii].add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gc[ii].edge[ed[0]][ed[1]]['property'] = Gx.edge[ed[0]][ed[1]]['property']


    vol_arr = array(vol_array)
    vol_thr = scoreatpercentile(vol_arr, 90)
    Gtv = extract_graph_by_high_volumes(Gx, vol_thr)

    # First identify botnets with multiple ips and single site
    Gc1 = connected_component_subgraphs(Gtv)

    Gts = extract_graph_by_edge_field_statuscode(Gx, sc_code= '3xx', col_num=1)
    Gc2 = connected_component_subgraphs(Gts)

    Gdx = extract_graph_by_edge_field(Gx, 'DENIED')
    sites = getNodesOfGivenType(Gdx,1)
    
    ips = getNodesOfGivenType(Gdx,0)
    Gpx = extract_graph_by_edge_field(Gx, 'PROXIED')
    Gd0  = extractGraphFromGivenNodes(Gpx, sites)
    Gd1  = extractGraphFromGivenNodes(Gpx, ips)

    Gc3 = connected_component_subgraphs(Gd0)
    Gc4 = connected_component_subgraphs(Gd1)


    bots = {}
    for ii in range(len(Gc3)):
        sites = getNodesOfGivenType(Gc3[ii],1)
        d = Gc3[ii].degree(sites)
        sorted_d = sorted(d.iteritems(), key=operator.itemgetter(1), reverse=True)
        bots[ii] = {}
        for jj in range(len(sorted_d)):
            bots[ii][jj] = {}
            bots[ii][jj]['sites'] = sorted_d[jj][0]
            bots[ii][jj]['ips'] = Gc3[ii].neighbors(sorted_d[jj][0])
    
    return(Gtv, Gc1, Gc2, Gc3, Gc4, Gtv, bots)




def botnetDetectionByUserAgentOld(G):

    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G, 1)
    filterByWhiteList(G, wlist)

    vol_array = extract_volume_distr(G)
    mu = mean(vol_array)
    sigma = std(vol_array)

    vol_thr = mu+sigma

    print ('vol: ', mu, sigma, vol_thr)
    min_prob_thr = 0.0
    max_prob_thr = 0.3
    ua_col = 5
    
    [Gx, uas] = extractNetworkByProbabilityRangeOfUserAgents(G, ua_col, min_prob_thr, max_prob_thr)
    
    Gc = connected_component_subgraphs(Gx)
    for ii in range(len(Gc)):
        for ed in Gc[ii].edges():
            Gc[ii].add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gc[ii].add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gc[ii].edge[ed[0]][ed[1]]['property'] = Gx.edge[ed[0]][ed[1]]['property']


    vol_arr = array(vol_array)
    vol_thr = scoreatpercentile(vol_arr, 90)
    Gtv = extract_graph_by_high_volumes(Gx, vol_thr)

    # First identify botnets with multiple ips and single site
    Gc1 = connected_component_subgraphs(Gtv)

    Gts = extract_graph_by_edge_field_statuscode(Gx, sc_code= '3xx', col_num=1)
    Gc2 = connected_component_subgraphs(Gts)

    Gdx = extract_graph_by_edge_field(Gx, 'DENIED')
    sites = getNodesOfGivenType(Gdx,1)
    
    ips = getNodesOfGivenType(Gdx,0)
    Gpx = extract_graph_by_edge_field(Gx, 'PROXIED')
    Gd0  = extractGraphFromGivenNodes(Gpx, sites)
    Gd1  = extractGraphFromGivenNodes(Gpx, ips)

    Gc3 = connected_component_subgraphs(Gd0)
    Gc4 = connected_component_subgraphs(Gd1)


    bots = {}
    for ii in range(len(Gc3)):
        sites = getNodesOfGivenType(Gc3[ii],1)
        d = Gc3[ii].degree(sites)
        sorted_d = sorted(d.iteritems(), key=operator.itemgetter(1), reverse=True)
        bots[ii] = {}
        for jj in range(len(sorted_d)):
            bots[ii][jj] = {}
            bots[ii][jj]['sites'] = sorted_d[jj][0]
            bots[ii][jj]['ips'] = Gc3[ii].neighbors(sorted_d[jj][0])
    
    return(Gtv, Gc1, Gc2, Gc3, Gc4, Gtv, bots)




def botnetDetectionByUserAgent(G):

    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G, 2)
    filterByWhiteList(G, wlist)

    vol_array = extract_volume_distr(G)
    mu = mean(vol_array)
    sigma = std(vol_array)

    vol_thr = mu+sigma

    print ('vol: ', mu, sigma, vol_thr)
    min_prob_thr = 0.0
    max_prob_thr = 0.5
    ua_col = 5
    
    [Gx, uas] = extractNetworkByProbabilityRangeOfUserAgents(G, ua_col, min_prob_thr, max_prob_thr)

    vol_arr = array(vol_array)
    vol_thr = scoreatpercentile(vol_arr, 70)
    print (vol_thr)
    Gtv = extract_graph_by_high_volumes(Gx, vol_thr)
    

    #Gtv = filterGraphByDegreeRange(Gtv, [5, 100], 1)
    #Gtv = filterGraphByDegreeRange(Gtv, [2, 100], 0)
    # First identify botnets with multiple ips and single site

    Gx = extractGraphOfSameNodetype(Gtv, 1, 3)
    Gy = extractGraphOfSameNodetype(Gtv, 0, 3)


    #Gc = connected_component_subgraphs(Gtv)


    return(Gtv, Gx, Gy)


def botnetDetectionByBeaconing(G):
    temp_beacon_scores = readBeaconScores('beacons-1.tsv')
    thr = 0.5
    beacon_scores = {}
    for key in temp_beacon_scores.keys():
        if (temp_beacon_scores[key][1] > thr):
            beacon_scores[key] = temp_beacon_scores[key]
            print (beacon_scores[key][0], beacon_scores[key][1])
                
    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G, 1)

    Gx = nx.Graph()
    for ed in G.edges():
        x = ed[0] + '\t' + ed[1]
        if (beacon_scores.has_key(x) == True):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']


    Gc = connected_component_subgraphs(Gx)
    return(Gc, beacon_scores)

def botnetDetectionByBeaconing2(G):
    beacon_scores = readBeaconScores2('beacon_results.txt')
    for key in beacon_scores.keys():
        print('------------------>  ', key, beacon_scores[key])
                
    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexaTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    #updateGraphWithAlexa(G, alexa, 1)
    #filterTopAlexaSites(G, 1)

    Gx = nx.Graph()
    for ed in G.edges():
        x = ed[0] + '\t' + ed[1]
        if (beacon_scores.has_key(x) == True):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']


    Gc = connected_component_subgraphs(Gx)
    return(Gc)

def botnetDetectionByStatusCode400(G):
    Gx = copyGraph(G)
    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')
    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G, 0)
    
    distr = extract_volume_distr(G)

    vol_thr = 50
    Gx = extract_graph_by_edge_field_statuscode(G, sc_code='4xx', col_num=1)
    Gtv = extract_graph_by_high_volumes(Gx, vol_thr)

    nds = getNodesOfGivenType(Gtv, 1)
    Gy = extractGraphFromGivenNodes(Gtv,nds)
    Gy = extract_graph_by_edge_field(Gy, 'PROXIED')
    Gc = connected_component_subgraphs(Gtv)

    for ii in range(len(Gc)):
        for ed in Gc[ii].edges():
            Gc[ii].add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gc[ii].add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gc[ii].edge[ed[0]][ed[1]]['property'] = Gtv.edge[ed[0]][ed[1]]['property']

    return(Gc)


def botnetDetectionByInternalScanning(G):
    # Read whois list
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    X = []
    for ed in G.edges():
        if (ed[0][0:5] == ed[1][0:5]):
            X.append(ed)
    return(X)
        

def getprop_distr(G, type='.exe'):
    Gx = extract_graph_by_edgeprop_field(G, type, 5)
    x2y = nx.Graph()
    for ed in Gx.edges():
        node = ed[0] + '\t' + ed[1]
        for pr in Gx.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(Gx.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            for row in rows:
                uri = string.split(row, '\t')[5]
                x2y.add_edge(uri, node)
                x2y.add_node(uri, bipartite=0)
                x2y.add_node(node, bipartite=1)

    nds = getNodesOfGivenType(x2y,0)
    d = x2y.degree(nds)
    
    sorted_d = sorted(d.iteritems(), key=operator.itemgetter(1), reverse=True)

    test_dll = sorted_d[1][0]
    
    return(x2y, sorted_d)


def botnetDetectionByDlls(G):
    Gl = copyGraph(G)

    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    updateGraphWithAlexa(Gl, alexa, 1)
    filterTopAlexaSites(Gl, 4)
    
    [dll2edge, sorted_dlls] = getprop_distr(Gl, '.dll')
    thr = 20
    dlls = []
    for ii in range(len(sorted_dlls)):
        if (sorted_dlls[ii][1] > thr):
            dlls.append(sorted_dlls[ii][0])

    G_list = {}
    for dll in dlls:
        Gx = extract_graph_by_edgeprop_field(Gl, dll, 5)
        nds = getNodesOfGivenType(Gx, 0)
        Gy = extract_graph_by_edge_field(Gl, 'PROXIED')
        Gz = extractGraphFromGivenNodes(Gy, nds)
        G_list[dll] = connected_component_subgraphs(Gz)
    return(G_list)
    

def botnetDetectionByExe(G):
    Gl = copyGraph(G)

    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')
    updateGraphWithAlexa(Gl, alexa, 1)
    filterTopAlexaSites(Gl, 4)
    
    [exe2edge, sorted_exes] = getprop_distr(Gl, '.exe')
    thr = 20
    exes = []
    for ii in range(len(sorted_exes)):
        if (sorted_exes[ii][1] > thr):
            exes.append(sorted_exes[ii][0])

    G_list = {}
    for exe in exes:
        Gx = extract_graph_by_edgeprop_field(Gl, exe, 5)
        nds = getNodesOfGivenType(Gx, 0)
        Gy = extract_graph_by_edge_field(Gl, 'PROXIED')
        Gz = extractGraphFromGivenNodes(Gy, nds)
        G_list[exe] = connected_component_subgraphs(Gz)
    return(G_list)


def test_botnetDetectionByUserAgent_old(G, prob_thr, vol_thr):
    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')


    alexa_table = {0:0.2, 1:0.3, 2:0.4, 3:0.5, 4:0.6, 5:0.7, 6:0.8, 7:0.9}    
    ua_col = 5
    [G1, uas] = extractNetworkByProbabilityRangeOfUserAgents(G, ua_col, prob_thr[0], prob_thr[1])
    Gx =  extract_graph_by_high_volumes(G1, vol_thr)
    

    Gs = {}
    Gs[0] = extractGraphOfSameNodetype_range(Gx, 1, 3, 5)
    Gs[1] = extractGraphOfSameNodetype_range(Gx, 1, 6, 10)
    Gs[2] = extractGraphOfSameNodetype_range(Gx, 1, 11, 1000)


    edLists = {}
    bots = {}
    for key in Gs.keys():
        updateGraphWithAlexa2(Gs[key], alexa)
        edLists[key] = []
        for nd in Gs[key].nodes():
            if (Gs[key].node[nd]['alexa_rank'] < 3):
                for nb in Gs[key].neighbors(nd):
                    edLists[key].append([nd,nb])
                    Gs[key].remove_edge(nd,nb)

        bots[key] = nx.Graph()
        for ed in Gs[key].edges():
            for nd in Gx.neighbors(ed[0]):
                bots[key].add_edge(ed[0],nd)
            for nd in Gx.neighbors(ed[1]):
                bots[key].add_edge(ed[1],nd)
        for nd in bots[key].nodes():
            if (bots[key].degree(nd) < 2):
                bots[key].remove_node(nd)

    return(bots)





def test_botnetDetectionByUserAgent(G, prob_thr, vol_thr):


    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    
    alexa_table = {0:0.2, 1:0.3, 2:0.4, 3:0.5, 4:0.6, 5:0.7, 6:0.8, 7:0.9}    
    ua_col = 5
    [G1, uas] = extractNetworkByProbabilityRangeOfUserAgents(G, ua_col, prob_thr[0], prob_thr[1])
    Gx =  extract_graph_by_high_volumes(G1, vol_thr)
    

    Gs = {}
    Gs[0] = extractGraphOfSameNodetype_range(Gx, 1, 3, 5)
    Gs[1] = extractGraphOfSameNodetype_range(Gx, 1, 6, 10)
    Gs[2] = extractGraphOfSameNodetype_range(Gx, 1, 11, 1000)


    edLists = {}
    bots = {}
    for key in Gs.keys():
        updateGraphWithAlexa2(Gs[key], alexa)
        edLists[key] = []
        for nd in Gs[key].nodes():
            if (Gs[key].node[nd]['alexa_rank'] < 3):
                for nb in Gs[key].neighbors(nd):
                    edLists[key].append([nd,nb])
                    Gs[key].remove_edge(nd,nb)

        bots[key] = nx.Graph()
        for ed in Gs[key].edges():
            for nd in Gx.neighbors(ed[0]):
                bots[key].add_edge(ed[0],nd)
            for nd in Gx.neighbors(ed[1]):
                bots[key].add_edge(ed[1],nd)
        for nd in bots[key].nodes():
            if (bots[key].degree(nd) < 2):
                bots[key].remove_node(nd)

    return(bots)







def extractBots(G):
    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist_1')
    filterByWhiteList(G, wlist)
    updateGraphWithAlexa2(G, alexa)
    patterns = ['/exchange/', '/exchweb/']
    filterout_graph_with_edgeprop_field(G, patterns, 5)
    filterout_internal_dest_ips(G)


    bot_results = {}
    bot_results['lowprob_ua'] = test_botnetDetectionByUserAgent(G, [0, 0.1], 10)
    bot_results['mediumprob_ua'] = test_botnetDetectionByUserAgent(G, [0.1, 0.5], 10)
    bot_results['lowprob_ua_pt1'] = test_botnetDetectionByUserAgent(G, [0.5, 1.1], 10)
    bot_results['anomalous_dll_downloads'] = botnetDetectionByDlls(G)
    bot_results['cs-status400'] = botnetDetectionByStatusCode400(G)
    bot_results['beaconing_activity'] = botnetDetectionByBeaconing2(G)
    
    return(bot_results)
    

def print_bot_result(bot_results, G, output_file):    
    fpOut = open(output_file,'w')
    ii = 0
    fpOut.write('------>    low_prob_ua_pt1' + '\n')
    for key in bot_results['lowprob_ua_pt1']:
        for ed in sort(bot_results['lowprob_ua_pt1'][key].edges()):
            print (ed, key)
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + '\t' + src + '\t' + dst + '\n'
            fpOut.write(row)
        ii += 1

    print ('djfldsjfldjslsdjl')
    xx = raw_input()
    fpOut.write('------>    mediumprob_ua_pt1' + '\n')
    for key in bot_results['mediumprob_ua']:
        for ed in sort(bot_results['mediumprob_ua'][key].edges()):
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + '\t' + src + '\t' + dst + '\n'
            fpOut.write(row)
        ii += 1



    fpOut.write('------>    beaconing_activity' + '\n')
    for key in range(len(bot_results['beaconing_activity'])):
        for ed in sort(bot_results['beaconing_activity'][key].edges()):
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + '\t' + src + '\t' + dst + '\n'
            fpOut.write(row)
        ii += 1



    
    fpOut.write('------>    anomalous_dll_downloads' + '\n')
    for key in bot_results['anomalous_dll_downloads']:
        for gr in bot_results['anomalous_dll_downloads'][key]:
            for ed in sort(gr.edges()):
                if (G.node[ed[0]]['bipartite'] == 0):
                    src = ed[0]
                    dst = ed[1]
                else:
                    src = ed[1]
                    dst = ed[0]
                row = str(ii) + '\t' + src + '\t' + dst + '\n'
                fpOut.write(row)
        ii += 1


    fpOut.write('------>    cs-status400' + '\n')
    for key in range(len(bot_results['cs-status400'])):
        for ed in sort(bot_results['cs-status400'][key].edges()):
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + '\t' + src + '\t' + dst + '\n'
            fpOut.write(row)
        ii += 1
    fpOut.close()

def print_bot_result2(bot_results, G, output_file):    

    fpOut = open(output_file,'w')
    ii = 0
    fpOut.write('------>    low_prob_ua_pt1' + '\n')
    for key in bot_results['lowprob_ua_pt1']:
        for ed in sort(bot_results['lowprob_ua_pt1'][key].edges()):
            print (ed, key)
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + ',' + src + ',' + dst + ',' + '0.8' + ',' + 'low_prob_ua' + '\n'
            fpOut.write(row)
        ii += 1


    for key in bot_results['mediumprob_ua']:
        for ed in sort(bot_results['mediumprob_ua'][key].edges()):
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + ',' + src + ',' + dst + ',' + '0.6' + ',' + 'med_prob_ua' + '\n'
            fpOut.write(row)
        ii += 1



    for key in range(len(bot_results['beaconing_activity'])):
        for ed in sort(bot_results['beaconing_activity'][key].edges()):
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + ',' + src + ',' + dst + ',' + '0.8' + ',' + 'beacon_activity' + '\n'
            fpOut.write(row)
        ii += 1

    

    for key in bot_results['anomalous_dll_downloads']:
        for gr in bot_results['anomalous_dll_downloads'][key]:
            for ed in sort(gr.edges()):
                if (G.node[ed[0]]['bipartite'] == 0):
                    src = ed[0]
                    dst = ed[1]
                else:
                    src = ed[1]
                    dst = ed[0]
                row = str(ii) + ',' + src + ',' + dst + ',' + '0.8' + ',' + 'anomalous_dll_downloads' + '\n'
                fpOut.write(row)
        ii += 1


    for key in range(len(bot_results['cs-status400'])):
        for ed in sort(bot_results['cs-status400'][key].edges()):
            if (G.node[ed[0]]['bipartite'] == 0):
                src = ed[0]
                dst = ed[1]
            else:
                src = ed[1]
                dst = ed[0]
            row = str(ii) + ',' + src + ',' + dst + ',' + '0.8' + ',' + 'cs-status400' + '\n'
            fpOut.write(row)
        ii += 1



def test_botnetDetectionByDestDegree(G):

    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    updateGraphWithAlexa(G, alexa, 1)

    nds = getNodesOfGivenType(G, 1)
    deg = G.degree(nds)

    sorted_deg = sorted(deg.iteritems(), key=operator.itemgetter(1), reverse=True)
    jj = 0
    nds = []
    for ii in range(len(sorted_deg)):
        if (G.node[sorted_deg[ii][0]]['alexa_rank'] > 3 and sorted_deg[ii][1] > 5 and sorted_deg[ii][1] < 10):
            print (jj, sorted_deg[ii], G.node[sorted_deg[ii][0]]['alexa_rank'])
            nds.append(sorted_deg[ii][0])
            jj += 1
    
    Gx = extractGraphFromGivenNodes(G, nds)
    Gx = extract_graph_by_high_volumes(Gx, 10)        
    return(sorted_deg, Gx)
        


def testA(G):
    temp_beacon_scores = readBeaconScores('beacons-1.tsv')
    thr = 0.5
    beacon_scores = {}
    for key in temp_beacon_scores.keys():
        if (temp_beacon_scores[key][1] > thr):
            beacon_scores[key] = temp_beacon_scores[key]
            print (beacon_scores[key][0], beacon_scores[key][1])
                
    # Read whoisist
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist')

    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G, 1)

    Gx = nx.Graph()
    for ed in G.edges():
        x = ed[0] + '\t' + ed[1]
        if (beacon_scores.has_key(x) == True):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']

    return(Gx)


def testB(G):
    Gx = copyGraph(G)
    # Read whoisist
    distr = extract_volume_distr(G)
    vol_thr = 50
    Gx = extract_graph_by_edge_field_statuscode(G, sc_code='4xx', col_num=1)
    Gtv = extract_graph_by_high_volumes(Gx, vol_thr)
    nds = getNodesOfGivenType(Gtv, 1)
    Gy = extractGraphFromGivenNodes(Gtv,nds)
    Gy = extract_graph_by_edge_field(Gy, 'PROXIED')

    return(Gtv, Gy)

def testCase0(G):
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists

    updateGraphWithAlexa(G, alexa, 1)
    wlist = readWhiteList('whitelist_1')
    filterByWhiteList(G, wlist)
    updateGraphWithAlexa2(G, alexa)

    filterTopAlexaSites(G, 2)
    Gx = testA(G)
    [Gy, X] = testB(G)
    
    Gi = getIntersectionGraph(Gx,Gy)
    return(Gx, Gy, Gi)
            
            

def testCase1(G):
    whois = readBluecoatWhois("bluecoat_whois_out.tsv")
    # Read Alexa
    alexa = readAlexTop1M()
    # read WhiteLists
    wlist = readWhiteList('whitelist_1')
    updateGraphWithAlexa(G, alexa, 1)
    filterTopAlexaSites(G, 2)
    
    Gx = testA(G)
    [Gy, X] = testB(G)
    
    Gi = getIntersectionGraph(Gx,Gy)
    return(Gi)
            

