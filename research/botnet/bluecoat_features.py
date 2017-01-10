import sys
sys.path.append('/Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/prototype_coding/hive-repo/prototype/GraphModeling/')

import string
from string import *
import scipy
from scipy.stats import *

from sklearn import *
from sklearn.decomposition import *

import networkx as nx
from networkx import *
from matplotlib import *
from pylab import *

from graphDB import *

#import pygraphviz as pg
#from pygraphviz import *

import time

from udfs import *


#import kcenter
#from kcenter import *
def cleanup_the_graph(G):
    for ed in G.edges():
        if (G.node[ed[0]]['bipartite'] == G.node[ed[1]]['bipartite']):
            G.remove_edge(ed[0], ed[1])
        
def cleanup_the_graphList(G):
    for id in G:
        for ed in G[id].edges():
            if (G[id].node[ed[0]]['bipartite'] == G[id].node[ed[1]]['bipartite']):
                G[id].remove_edge(ed[0], ed[1])
        

def extract_graph_based_on_lowprobability_fields(G, thr, field_col = 5):
    Gs2f = nx.Graph()
    for ed in G.edges():
        if (G.node[ed[0]]['bipartite'] == 0):
            src = ed[0]
        else:
            src = ed[1]
        Gs2f.add_node(src, bipartite=0)
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            arr = string.split(pr, '\t')
            Gs2f.add_node(arr[field_col], bipartite=1)
            Gs2f.add_edge(src, arr[field_col])
    f2d = {}
    max_degree = -1
    for nd in Gs2f.nodes():
        if (Gs2f.node[nd]['bipartite'] == 1):
            f2d[nd] = Gs2f.degree(nd)
            if (max_degree < f2d[nd]):
                max_degree = f2d[nd]
    max_degree = 1.0*max_degree

    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            arr = string.split(pr, '\t')
            if ((f2d[arr[field_col]]/max_degree) < thr):
                Gx.add_edge(ed[0], ed[1])
                Gx.add_node(ed[0], bipartite = G.node[ed[0]]['bipartite'])
                Gx.add_node(ed[1], bipartite = G.node[ed[1]]['bipartite'])
                if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                    Gx.edge[ed[0]][ed[1]]['property'] = {}
                Gx.edge[ed[0]][ed[1]]['property'][pr] =  G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx, f2d)

def extract_graph_by_edge_field(G, pattern):
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            if (string.find(pr, pattern) >= 0):
                val1 = G.node[ed[0]]['bipartite']
                val2 = G.node[ed[1]]['bipartite']

                Gx.add_node(ed[0], bipartite=val1)
                Gx.add_node(ed[1], bipartite=val2)
                Gx.add_edge(ed[0], ed[1])

                if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                    Gx.edge[ed[0]][ed[1]]['property'] = {}

                Gx.edge[ed[0]][ed[1]]['property'][pr] = G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)


def extract_graph_by_edge_field_col(G, pattern, col_num):
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            item = string.split(pr,'\t')[col_num]
            if (string.find(item, pattern) >= 0):
                val1 = G.node[ed[0]]['bipartite']
                val2 = G.node[ed[1]]['bipartite']

                Gx.add_node(ed[0], bipartite=val1)
                Gx.add_node(ed[1], bipartite=val2)
                Gx.add_edge(ed[0], ed[1])

                if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                    Gx.edge[ed[0]][ed[1]]['property'] = {}

                Gx.edge[ed[0]][ed[1]]['property'][pr] = G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)



def extract_graph_by_edgeprop_field_old(G, pattern):
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            if (string.find(G.edge[ed[0]][ed[1]]['property'][pr]['data'], pattern) >= 0):
                val1 = G.node[ed[0]]['bipartite']
                val2 = G.node[ed[1]]['bipartite']

                Gx.add_node(ed[0], bipartite=val1)
                Gx.add_node(ed[1], bipartite=val2)
                Gx.add_edge(ed[0], ed[1])

                if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                    Gx.edge[ed[0]][ed[1]]['property'] = {}

                Gx.edge[ed[0]][ed[1]]['property'][pr] = G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)


def extract_graph_by_edgeprop_field(G, pattern, col_num):
    l = len(pattern)
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            data_str = ''
            counter = 0
            for row in rows:
                arr = string.split(row,'\t')
                if (string.find(arr[col_num][-l:], pattern) >= 0):
                    data_str = data_str + row + '\n'
                    counter += 1

            if (counter > 0):
                if (Gx.has_edge(ed[0], ed[1]) == False):
                    Gx.add_edge(ed[0],ed[1])
                    Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
                    Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
                    Gx.edge[ed[0]][ed[1]]['property'] = {}
                Gx.edge[ed[0]][ed[1]]['property'][pr] = {}
                Gx.edge[ed[0]][ed[1]]['property'][pr]['data'] = data_str
                
    return(Gx)







    
def extract_graph_by_low_frequency_nodes(G, thr, node_type=1):
    Gx = nx.Graph()
    nds = getNodesOfGivenType(G,node_type)
    for nd in nds:
        count = 0
        for nb in G.neighbors(nd):
            for pr in G.edge[nd][nb]['property'].keys():
                count += G.edge[nd][nb]['property'][pr]['count']

        if (count < thr):
            for nb in G.neighbors(nd):
                Gx.add_edge(nd, nb)
                Gx.add_node(nd, bipartite=node_type)
                Gx.add_node(nd, bipartite=1-node_type)
    return(Gx)

def filter_graph_by_edge_field(G, pattern):
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            if (string.find(pr, pattern) < 0):
                val1 = G.node[ed[0]]['bipartite']
                val2 = G.node[ed[1]]['bipartite']

                Gx.add_node(ed[0], bipartite=val1)
                Gx.add_node(ed[1], bipartite=val2)
                Gx.add_edge(ed[0], ed[1])

                if (Gx.edge[ed[0]][ed[1]].has_key('property') == False):
                    Gx.edge[ed[0]][ed[1]]['property'] = {}

                Gx.edge[ed[0]][ed[1]]['property'][pr] = G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)

def extract_graph_by_low_in_out_bytes(G, cols, bytes_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        in_bytes = []
        out_bytes = []
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            for row in rows:
                arr = string.split(row,'\t')
                in_bytes.append(atoi(arr[cols[0]]))
                out_bytes.append(atoi(arr[cols[1]]))
        if (max(in_bytes) < bytes_thr[0] and max(out_bytes) < bytes_thr[1]):
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.add_edge(ed[0], ed[1])
            Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']
    return(Gx)
            


def extract_graph_by_low_in_out_avg_bytes(G, cols, bytes_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        in_bytes = []
        out_bytes = []
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            for row in rows:
                arr = string.split(row,'\t')
                in_bytes.append(atoi(arr[cols[0]]))
                out_bytes.append(atoi(arr[cols[1]]))
        if (mean(in_bytes) < bytes_thr[0] and mean(out_bytes) < bytes_thr[1]):
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.add_edge(ed[0], ed[1])
            Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']
    return(Gx)




def extract_graph_by_high_in_out_bytes(G, cols, bytes_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        in_bytes = []
        out_bytes = []
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            for row in rows:
                arr = string.split(row,'\t')
                in_bytes.append(atoi(arr[cols[0]]))
                out_bytes.append(atoi(arr[cols[1]]))
        if (max(in_bytes) > bytes_thr[0] and max(out_bytes) > bytes_thr[1]):
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            
            Gx.add_edge(ed[0], ed[1])
            Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']
    return(Gx)
            
def extract_graph_by_high_volumes(G, vol_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        total_vol = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            total_vol += G.edge[ed[0]][ed[1]]['property'][pr]['count']

        if (total_vol > vol_thr):
            val1 = G.node[ed[0]]['bipartite']
            val2 = G.node[ed[1]]['bipartite']

            Gx.add_node(ed[0], bipartite=val1)
            Gx.add_node(ed[1], bipartite=val2)
            Gx.add_edge(ed[0], ed[1])
            Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']
    return(Gx)



def extract_graph_by_volume_range(G, min_thr, max_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        total_vol = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            total_vol += G.edge[ed[0]][ed[1]]['property'][pr]['count']

        if (total_vol >= min_thr and total_vol < max_thr):
            val1 = G.node[ed[0]]['bipartite']
            val2 = G.node[ed[1]]['bipartite']

            Gx.add_node(ed[0], bipartite=val1)
            Gx.add_node(ed[1], bipartite=val2)
            Gx.add_edge(ed[0], ed[1])
            Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']
    return(Gx)



def extract_volume_distr(G):
    volume_arr = []
    for ed in G.edges():
        total_vol = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            total_vol += G.edge[ed[0]][ed[1]]['property'][pr]['count']
        volume_arr.append(total_vol)
    return(array(volume_arr))







def extract_graph_by_internalIpAsSite(G):
    Gx = nx.DiGraph()
    
    for ed in G.edges():
        if (G.node[ed[0]]['bipartite'] == 1):
            src = 1
            dst = 0
        else:
            src = 0
            dst = 1

        
        if (tldextract.extract(ed[dst]).domain == ed[dst] and string.find(ed[dst], '\xa3') < 0):
            Gx.add_node(ed[src], bipartite=0)
            Gx.add_node(ed[dst], bipartite=1)
            Gx.add_edge(ed[src], ed[dst])

    return(Gx)
            


def addByteSummary(G):
    for ed in G.edges():
        out_bytes = []
        in_bytes = []
        if (G.node[ed[0]]['bipartite'] == 1):
            nd = ed[0]
        else:
            nd = ed[1]
        if (G.node[nd].has_key('in_bytes') == False):
            G.node[nd]['in_bytes'] = []
            G.node[nd]['out_bytes'] = []

        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')
            for row in rows[:-1]:
                arr = string.split(row,'\t')[-2:]
                out_bytes.append(atoi(arr[1]))
                in_bytes.append(atoi(arr[0]))
                G.node[nd]['out_bytes'].append(atoi(arr[1]))
                G.node[nd]['in_bytes'].append(atoi(arr[0]))
        
        G.edge[ed[0]][ed[1]]['summary'] = {}
        G.edge[ed[0]][ed[1]]['summary']['byte_data'] = {}
        G.edge[ed[0]][ed[1]]['summary']['byte_data']['out'] = np.array(out_bytes)
        G.edge[ed[0]][ed[1]]['summary']['byte_data']['in'] = np.array(in_bytes)

    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 1):
            G.node[nd]['in_bytes'] = np.array(G.node[nd]['in_bytes'])
            G.node[nd]['out_bytes'] = np.array(G.node[nd]['out_bytes'])
    
def filter_high_degree_urls(G,thr):
    Gx = nx.Graph()
    for ed in G.edges():
        flag = 0
        if (G.node[ed[0]]['bipartite'] == 0 and G.node[ed[1]]['bipartite'] == 1):
            src = ed[0]
            dst = ed[1]
        else:
            src = ed[1]
            dst = ed[0]

        if (G.degree(dst) < thr):
            Gx.add_node(src, bipartite = 0)
            Gx.add_node(dst, bipartite = 1)
            Gx.add_edge(src,dst)
            Gx.edge[src][dst]['property'] = {}
            Gx.edge[src][dst]['property'] = G.edge[src][dst]['property']
    return(Gx)


def feature_relative_popularity_field(G, field_col = 5, nodeType = 0):
    Gf = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property']:
            field = string.split(pr,'\t')[field_col]
            if (G.node[ed[0]]['bipartite'] == nodeType):
                ip = ed[0]
            else:
                ip = ed[1]
            Gf.add_node(ip, bipartite=0)
            Gf.add_node(field, bipartite=1)
            Gf.add_edge(ip,field)

    field_vals = getNodesOfGivenType(Gf, 1)

        
    f2s = Gf.degree(field_vals)
    m = max(f2s.values())
    for nd in f2s.keys():
        f2s[nd] = (1.0* f2s[nd])/m

    range_arr = [0, 0.05, 0.6, 1]
    ips = getNodesOfGivenType(G, nodeType)

    ip2scores = {}
    for nd in ips:
        ip2scores[nd] = []
        counter = {}
        for ii in range(len(range_arr)-1):
            counter[ii] = 0
        for field in Gf.neighbors(nd):
            score = f2s[field]
            for ii in range(1,len(range_arr)):
                if (score > range_arr[ii-1] and score <= range_arr[ii]):
                    counter[ii-1] += 1
        for ii in range(len(range_arr)-1):
            ip2scores[nd].append(counter[ii])
    
    return(ip2scores)


def feature_relative_frequency_field(G, field_col = 5, nodeType=0):
    Gf = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property']:
            field = string.split(pr,'\t')[field_col]
            if (G.node[ed[0]]['bipartite'] == nodeType):
                ip = ed[0]
            else:
                ip = ed[1]
            Gf.add_node(ip, bipartite=0)
            Gf.add_node(field, bipartite=1)
            Gf.add_edge(ip,field)
            if (Gf.edge[ip][field].has_key('count') == False):
                Gf.edge[ip][field]['count'] = 0
            Gf.edge[ip][field]['count'] += G.edge[ed[0]][ed[1]]['property'][pr]['count']


    field_vals = getNodesOfGivenType(Gf, 1)
    f2s = Gf.degree(field_vals)
    m = max(f2s.values())
    for nd in f2s.keys():
        f2s[nd] = (1.0* f2s[nd])/m

    range_arr = [0, 0.05, 0.6, 1]
    ips = getNodesOfGivenType(G, nodeType)
    ip2scores = {}
    for nd in ips:
        ip2scores[nd] = []
        counter = {}
        for ii in range(len(range_arr)-1):
            counter[ii] = 0
        for field in Gf.neighbors(nd):
            score = f2s[field]
            for ii in range(1,len(range_arr)):
                if (score > range_arr[ii-1] and score <= range_arr[ii]):
                    counter[ii-1] += Gf.edge[nd][field]['count']

        for ii in range(len(range_arr)-1):
            ip2scores[nd].append(counter[ii])
    
    return(ip2scores)

def feature_relative_popularity_site(G, nodeType=1):
    sites = getNodesOfGivenType(G, nodeType)
    D = G.degree(sites)
    m = (1.0*max(D.values()))

    s2p = {}
    for site in D.keys():
        s2p[site] = D[site]/m

    range_arr =  [0.0, 0.05, 0.6, 1]
    if (nodeType == 1):
        ips = getNodesOfGivenType(G, 0)
    else:
        ips = getNodesOfGivenType(G, 1)
        
    ip2scores = {}
    for nd in ips:
        ip2scores[nd] = []
        counter = {}
        for ii in range(len(range_arr)-1):
            counter[ii] = 0
        for site in G.neighbors(nd):
            score = s2p[site]
            for ii in range(1,len(range_arr)):
                if (score > range_arr[ii-1] and score <= range_arr[ii]):
                    counter[ii-1] += 1
        for ii in range(len(range_arr)-1):
            ip2scores[nd].append(counter[ii])
    return(ip2scores)
    
def feature_relative_frequency_site(G, nodeType=1):
    sites = getNodesOfGivenType(G, nodeType)
    D = G.degree(sites)
    m = (1.0*max(D.values()))

    s2p = {}
    for site in D.keys():
        s2p[site] = D[site]/m

    range_arr =  [0.0, 0.05, 0.6, 1]

    if (nodeType == 1):
        ips = getNodesOfGivenType(G, 0)
    else:
        ips = getNodesOfGivenType(G, 1)
        
    ip2scores = {}
    for nd in ips:
        ip2scores[nd] = []
        counter = {}
        for ii in range(len(range_arr)-1):
            counter[ii] = 0
        for site in G.neighbors(nd):
            count = 0
            for pr in G.edge[nd][site]['property'].keys():
                count += G.edge[nd][site]['property'][pr]['count']
            score = s2p[site]
            for ii in range(1,len(range_arr)):
                if (score > range_arr[ii-1] and score <= range_arr[ii]):
                    counter[ii-1] += count
                    
        for ii in range(len(range_arr)-1):
            ip2scores[nd].append(counter[ii])
    return(ip2scores)
    


def feature_degree(G):
    ip2deg = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ip2deg[nd] = G.degree(nd)
    return(ip2deg)

def feature_distinct_props(G):
    ips = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ips[nd] = 1

    ip2props = {}
    tmp = {}
    for nd in ips.keys():
        prs = {}
        for nbr in G.neighbors(nd):
            for pr in G.edge[nd][nbr]['property'].keys():
                prs[pr] = 1
        ip2props[nd] = size(prs.keys())
    return(ip2props)


def feature_total_bytes(G, loc):
    ips = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ips[nd] = 1

    ip2bytes_in = {}
    ip2bytes_out = {}
    tmp = {}
    for nd in ips.keys():
        in_bytes = 0
        out_bytes = 0
        for nbr in G.neighbors(nd):
            for pr in G.edge[nd][nbr]['property'].keys():
                rows = string.split(G.edge[nd][nbr]['property'][pr]['data'],'\n')
                for item in rows[:-1]:
                    arr = string.split(item,'\t')[loc[0]:loc[1]]
                    in_bytes  += atoi(arr[0])
                    out_bytes += atoi(arr[1])

        ip2bytes_in[nd] = in_bytes
        ip2bytes_out[nd] = out_bytes
    return(ip2bytes_in, ip2bytes_out)


def feature_max_bytes(G, loc):
    ips = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ips[nd] = 1

    ip2bytes_in = {}
    ip2bytes_out = {}

    for nd in ips.keys():
        in_bytes = -1
        out_bytes = -1
        for nbr in G.neighbors(nd):
            for pr in G.edge[nd][nbr]['property'].keys():
                rows = string.split(Gx.edge[nd][nbr]['property'][pr]['data'],'\n')
                for item in rows[:-1]:
                    arr = string.split(item,'\t')[loc[0]:loc[1]]
                    if (in_bytes < atoi(arr[0])):
                        in_bytes = atoi(arr[0])
                    if (out_bytes < atoi(arr[1])):
                        out_bytes = atoi(arr[1])

        ip2bytes_in[nd] = in_bytes
        ip2bytes_out[nd] = out_bytes
    return(ip2bytes_in, ip2bytes_out)

def feature_min_bytes(G, loc):
    ips = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ips[nd] = 1

    ip2bytes_in = {}
    ip2bytes_out = {}

    for nd in ips.keys():
        in_bytes = 9999999999
        out_bytes = 9999999999
        for nbr in G.neighbors(nd):
            for pr in G.edge[nd][nbr]['property'].keys():
                rows = string.split(Gx.edge[nd][nbr]['property'][pr]['data'],'\n')
                for item in rows[:-1]:
                    arr = string.split(item,'\t')[loc[0]:loc[1]]
                    if (in_bytes > atoi(arr[0])):
                        in_bytes = atoi(arr[0])
                    if (out_bytes > atoi(arr[1])):
                        out_bytes = atoi(arr[1])

        ip2bytes_in[nd] = in_bytes
        ip2bytes_out[nd] = out_bytes
    return(ip2bytes_in, ip2bytes_out)




def feature_total_count(G):
    ips = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ips[nd] = 1

    ip2count = {}
    for nd in ips.keys():
        count = 0
        for nbr in G.neighbors(nd):
            for pr in G.edge[nd][nbr]['property'].keys():
                count += Gx.edge[nd][nbr]['property'][pr]['count']
        ip2count[nd] = count
    return(ip2count)


def feature_max_count(G):
    ips = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ips[nd] = 1

    ip2count = {}
    for nd in ips.keys():
        count = 0
        for nbr in G.neighbors(nd):
            for pr in G.edge[nd][nbr]['property'].keys():
                if (count < Gx.edge[nd][nbr]['property'][pr]['count']):
                    count = Gx.edge[nd][nbr]['property'][pr]['count']
        ip2count[nd] = count
    return(ip2count)

def feature_min_count(G):
    ips = {}
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ips[nd] = 1

    ip2count = {}
    for nd in ips.keys():
        count = 999999999
        for nbr in G.neighbors(nd):
            for pr in G.edge[nd][nbr]['property'].keys():
                if (count > Gx.edge[nd][nbr]['property'][pr]['count']):
                    count = Gx.edge[nd][nbr]['property'][pr]['count']
        ip2count[nd] = count
    return(ip2count)

def feature_field_count(G, field_pattern = "GET"):
    ip2count = {}
    Gx = extract_graph_by_edge_field(G, field_pattern)

    for nd in G.nodes.keys():
        count = 0
        if (Gx.has_node(nd) == True):
            for nb in Gx.neighbors(nd):
                for pr in Gx.edge[nd][nb]['property']:
                    count += G.edge[nd][nb]['property'][pr]['count']
        ip2count[nd] = count
    return(ip2count)

def getFeatures0(G, byte_loc):
    features = {}
    features[0] = feature_degree(G)
    features[1] = feature_distinct_props(G)
    [features[2], features[3]] = feature_total_bytes(G, byte_loc)
    [features[4], features[5]] = feature_max_bytes(G,byte_loc)
    [features[6], features[7]] = feature_min_bytes(G, byte_loc)
    features[8] = feature_total_count(G)
    features[9] = feature_max_count(G)
    features[10] = feature_min_count(G)
    return(features)

def getFeatures1(G):
    features = {}
    features[0] = feature_relative_popularity_field(G, field_col = 1, nodeType=0)
    features[1] = feature_relative_popularity_field(G, field_col = 2, nodeType=0)
    features[2] = feature_relative_popularity_field(G, field_col = 3, nodeType=0)
    features[3] = feature_relative_popularity_field(G, field_col = 4, nodeType=0)
    features[4] = feature_relative_popularity_field(G, field_col = 5, nodeType=0)

    features[5] = feature_relative_frequency_field(G, field_col = 1, nodeType=0)
    features[6] = feature_relative_frequency_field(G, field_col = 2, nodeType=0)
    features[7] = feature_relative_frequency_field(G, field_col = 3, nodeType=0)
    features[8] = feature_relative_frequency_field(G, field_col = 4, nodeType=0)
    features[9] = feature_relative_frequency_field(G, field_col = 5, nodeType=0)

    features[10] = feature_relative_popularity_site(G, nodeType=1)
    features[11] = feature_relative_frequency_site(G, nodeType=1)

    return(features)

def getFeatures2(G):
    features = {}
    features[0] = feature_relative_popularity_field(G, field_col = 1, nodeType=1)
    features[1] = feature_relative_popularity_field(G, field_col = 2, nodeType=1)
    features[2] = feature_relative_popularity_field(G, field_col = 3, nodeType=1)
    features[3] = feature_relative_popularity_field(G, field_col = 4, nodeType=1)
    features[4] = feature_relative_popularity_field(G, field_col = 5, nodeType=1)

    features[5] = feature_relative_frequency_field(G, field_col = 1, nodeType=1)
    features[6] = feature_relative_frequency_field(G, field_col = 2, nodeType=1)
    features[7] = feature_relative_frequency_field(G, field_col = 3, nodeType=1)
    features[8] = feature_relative_frequency_field(G, field_col = 4, nodeType=1)
    features[9] = feature_relative_frequency_field(G, field_col = 5, nodeType=1)

    features[10] = feature_relative_popularity_site(G, nodeType=0)
    features[11] = feature_relative_frequency_site(G, nodeType=0)
    return(features)






def getIp2Id(ipList):
    ip2id = {}
    id2ip = {}
    id = 0
    for key in sort(ipList):
        ip2id[key] = id
        id2ip[id] = key
        id += 1
    return(ip2id, id2ip)

def getFeatureMatrix(features,ip2id):
    M = numpy.zeros((size(ip2id.keys()), size(features.keys())))
    for ip in sort(ip2id.keys()):
        row = ip2id[ip]
        col = 0
        for key in sort(features.keys()):
            if (features[key].has_key(ip) == False):
                M[row,col] = 0
            else:
                M[row,col] = features[key][ip]
            col += 1
    return(M)

def matrixConcatenate(M1, M2):
    M = np.concatenate((M1, M2), axis=1)

def getClusterSizes(cl_indices):
    counts = numpy.zeros(max(cl_indices), dtype=int)
    for ii in range(max(cl_indices)):
        counts[ii] = size(where(cl_indices==ii))
    return(counts)

def spectralClustering(G):
    M = networkx.linalg.adj_matrix(G, sort(G.nodes()))
    L = scipy.sparse.csr_matrix(networkx.linalg.laplacian_matrix(G))
    [W, V] = scipy.sparse.linalg.eigs(L, k = 6, which = 'SM')
    p = argsort(V[:,2])
    Md = M[:,p]
    Md = Md[p,:]
    return(V)


def readData():
    files = []
    files.append('bluecoat_parsed.log')
    time_col = 0
    col_src = 3
    col_dst = 10
    event_attrs = [0, 4, 5,8]
    edge_attrs = [2, 6, 7]
    delim = '\t'
    line_limit = 1000000
    Gdb = build_graph_ip2url(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit)
    return(Gdb)


def updateProxyGraph(G):
    for ed in G.edges():
        for prop in G.edge[ed[0]][ed[1]]['property']:
            G.edge[ed[0]][ed[1]]['property'][prop]['info'] = {}
            G.edge[ed[0]][ed[1]]['property'][prop]['info']['in_bytes'] = []
            G.edge[ed[0]][ed[1]]['property'][prop]['info']['out_bytes'] = []
            G.edge[ed[0]][ed[1]]['property'][prop]['info']['timestamp'] = []
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][prop]['data'],'\n')
            for row in rows[:-1]:
                arr = string.split(row,'\t')
                timestamp = arr[0] + arr[1]
                timestamp = timestamp.replace('-','').replace(':','')
                G.edge[ed[0]][ed[1]]['property'][prop]['info']['timestamp'].append(atoi(timestamp))
                G.edge[ed[0]][ed[1]]['property'][prop]['info']['in_bytes'].append(atoi(arr[2]))
                G.edge[ed[0]][ed[1]]['property'][prop]['info']['out_bytes'].append(atoi(arr[3]))


def extractGraphByNeverSeeneEdges(Gdb, Gt):
    Gx = nx.Graph()
    for ed in Gt.edges():
        if (Gdb.has_edge(ed[0],ed[1]) == False):
            Gx.add_edge(ed[0],ed[1])
            Gx.edge[ed[0]][ed[1]] = Gt.edge[ed[0]][ed[1]]
    for nd in Gx.nodes():
        Gx.add_node(nd, bipartite = Gt.node[nd]['bipartite'])

    return(Gx)

def extractGraphByNeverSeenNodes(Gdb, Gt, node_type):
    Gx = nx.Graph()
    for ed in Gt.edges():
        if (Gt.node[ed[0]]['bipartite'] == node_type):
            nd = ed[0]
        else:
            nd = ed[1]
            
        if (Gdb.has_node(nd) == False):
            Gx.add_node(ed[0], bipartite=Gt.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=Gt.node[ed[1]]['bipartite'])
            Gx.add_edge(ed[0],ed[1])
            Gx.edge[ed[0]][ed[1]]['property'] = Gt.edge[ed[0]][ed[1]]['property']
    return(Gx)

def extractSubgraphFromNodes(G, nodes, node_type):
    Gx = nx.Graph()
    for nd in nodes:
        Gx.add_node(nd, bipartite=G.node[nd]['bipartite'])
        for nb in G.neighbors(nd):
            Gx.add_node(nb, bipartite=G.node[nb]['bipartite'])
            Gx.add_edge(nd, nb)
            Gx.edge[nd][nb]['property'] = G.edge[nd][nb]['property']
    return(Gx)
            

def getNodesOfGivenType(G, node_type=0):

    nodeList = []
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == node_type):
            nodeList.append(nd)
    return(nodeList)

def test0(Gx, thr):
    D = Gx.degree()
    count = 0
    for nd in Gx.nodes():
        if (Gx.node[nd]['bipartite'] == 1 and D[nd] > thr):
            count += 1
            print ('count: ', count, nd, D[nd])


def drawTmpGraphold(G, fileName):
    Gt = nx.to_agraph(G)
    Gt.layout(prog='sfdp')
    Gt.draw(fileName)

def drawTmpGraph(G, Gdb, fileName):
    #nx.drawing.nx_pylab.draw_networkx(G,fileName)
    Ga = nx.to_agraph(G)

    minClEpoch = 99999999999999999
    maxClEpoch = -1
    for ed in G.edges():
        minEpoch = 999999999999
        maxEpoch = -1
        numSessions = 0
        for pr in Gdb.edge[ed[0]][ed[1]]['property']:
            if (Gdb.edge[ed[0]][ed[1]]['property'][pr]['count'] >= 0 ):
                numSessions += Gdb.edge[ed[0]][ed[1]]['property'][pr]['count']
                rows = string.split(Gdb.edge[ed[0]][ed[1]]['property'][pr]['data'], '\n')
                for row in rows[:-1]:
                    date_time = row[0:19]
                    pattern = '%Y-%m-%d\t%H:%M:%S'
                    epoch = int(time.mktime(time.strptime(date_time, pattern)))
                    if (minEpoch > epoch):
                        minEpoch = epoch
                    if (maxEpoch < epoch):
                        maxEpoch = epoch
                        

        delta = maxEpoch-minEpoch

        hrs = delta/3600
        rem = delta%3600
        mins = rem/60
        sec = rem%60

        duration = '\(' + str(hrs) + ', ' + str(mins) + ', ' + str(sec) + '\)'
        
        diff_epoch = '\< ' + str(numSessions) + ',  ' +  duration + ' \>'
        if (numSessions < 20):
            Ga.add_edge(ed[0], ed[1], label=diff_epoch , color='blue')
        else:
            Ga.add_edge(ed[0], ed[1], label=diff_epoch,  color='red')

        if (minClEpoch >= minEpoch):
            minClEpoch = minEpoch
        if (maxClEpoch <= maxEpoch):
            maxClEpoch = maxEpoch

    Ga.draw(fileName, prog= 'dot', args= '-Gepsilon=1')
    #Ga.draw(fileName, prog = 'fdp')
    return(minClEpoch, maxClEpoch)
 

def drawProxyGraph(G, fileName):
    Gt = nx.to_agraph(G)
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            Gt.add_node(nd, color='red')
        else:
            Gt.add_node(nd, color='blue')

    #Gt.node_attr['shape'] = 'point'
    #Gt.node_attr['size'] = 0.1
    Gt.layout(prog='sfdp')
    Gt.draw(fileName)




def genScoresForIPsBasedOnFirstTimeSites(G,C):
    # G = Graph by day
    # C = Graph Clusters

    thr1 = 1
    thr2 = 5
    
    ip2count = {}
    ## Initialize conunt with -1
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 0):
            ip2count[nd] = 0


    print ('====>  ', size(ip2count.keys()))
    for Gi in C:
        count = 0
        for nd in Gi.nodes():
            if (Gi.node[nd]['bipartite'] == 0):
                count += 1
        for nd in Gi.nodes():
            if (Gi.node[nd]['bipartite'] == 0):
                ip2count[nd] = count
        

    ip2score = {}
    for nd in ip2count.keys():
        if (ip2count[nd] > thr1 and ip2count[nd] < thr2):
            ip2score[nd] = 1
        else:
            ip2score[nd] = 0

    print ('size = ', len(ip2count.keys()), len(ip2score.keys()))
    return(ip2count, ip2score)
        

    
#def getGraphsOfPathLengths(G, node_type = 0):
def extractGraphOfSameNodetype(G, node_type = 0, thr = 2):
    if (node_type == 0):
        nds = getNodesOfGivenType(G, 1)
    else:
        nds = getNodesOfGivenType(G, 0)

    Gx = nx.Graph()
    for nd in nds:
        nbs = G.neighbors(nd)
        ii = 0
        for nb in nbs:
            G.add_node(nb)

        for nb1 in nbs[0:-1]:
            for nb2 in nbs[ii+1:]:
                if (Gx.has_edge(nb1,nb2) == False):
                    Gx.add_edge(nb1,nb2)
                    Gx.edge[nb1][nb2]['count'] = 1
                else:
                    Gx.edge[nb1][nb2]['count'] += 1
                ii += 1

    for ed in Gx.edges():
        if (Gx.edge[ed[0]][ed[1]]['count'] < thr):
            Gx.remove_edge(ed[0],ed[1])
    for nd in Gx.nodes():
        if (Gx.degree(nd) < 1):
            Gx.remove_node(nd)
    
    return(Gx)



def extractGraphOfSameNodetype_range(G, node_type = 0, min_thr=1, max_thr=2):
    if (node_type == 0):
        nds = getNodesOfGivenType(G, 1)
    else:
        nds = getNodesOfGivenType(G, 0)

    Gx = nx.Graph()
    for nd in nds:
        nbs = G.neighbors(nd)
        ii = 0
        for nb in nbs:
            G.add_node(nb)

        for nb1 in nbs[0:-1]:
            for nb2 in nbs[ii+1:]:
                if (Gx.has_edge(nb1,nb2) == False):
                    Gx.add_edge(nb1,nb2)
                    Gx.edge[nb1][nb2]['count'] = 1
                else:
                    Gx.edge[nb1][nb2]['count'] += 1
                ii += 1

    for ed in Gx.edges():
        if (Gx.edge[ed[0]][ed[1]]['count'] < min_thr or Gx.edge[ed[0]][ed[1]]['count'] > max_thr):
            Gx.remove_edge(ed[0],ed[1])
    for nd in Gx.nodes():
        if (Gx.degree(nd) < 1):
            Gx.remove_node(nd)
    
    return(Gx)


def createLowDegreeGraph(G, node_type, deg_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
        Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
        Gx.add_edge(ed[0],ed[1])
        Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']

    for nd in Gx.nodes():
        if (Gx.node[nd]['bipartite'] == node_type and Gx.degree(nd) > deg_thr):
            Gx.remove_node(nd)
    return(Gx)

def createHighDegreeGraph(G, node_type, deg_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
        Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
        Gx.add_edge(ed[0],ed[1])
        Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']

    for nd in Gx.nodes():
        if (Gx.node[nd]['bipartite'] == node_type and Gx.degree(nd) < deg_thr):
            Gx.remove_node(nd)

    for nd in Gx.nodes():
        if (Gx.degree(nd) == 0):
            Gx.remove_node(nd)            
            
    return(Gx)


def getConnectedComponentClusters(G, Gc, useragent_col = 5):
    ua_col = useragent_col
    Cl = {}
    cl_num = 0
    for C in Gc:
        Cl[cl_num] = {}
        e_num = 0
        for ed in C.edges():
            # Check if ed[0] and ed[1] are the same due to change of IP
            commonSites = {}
            for ip in ed:
                for site in G.neighbors(ip):
                    if (commonSites.has_key(site) == False):
                        commonSites[site] = 1
                    else:
                        commonSites[site] += 1

            for site in commonSites.keys():
                if (commonSites[site] == 1):
                    del commonSites[site]

            ua1 = {}
            ua2 = {}
            for site in commonSites.keys():
                for pr in G.edge[ed[0]][site]['property'].keys():
                    ua1[string.split(pr, '\t')[ua_col]] = 1
                for pr in G.edge[ed[1]][site]['property'].keys():
                    ua2[string.split(pr, '\t')[ua_col]] = 1
            flag = 0
            for ua in ua1.keys():
                if (ua2.has_key(ua) == True):
                    flag = 1
            if (flag == 0):
                Cl[cl_num][e_num] = {}
                Cl[cl_num][e_num]['ips'] = ed
                Cl[cl_num][e_num]['sites'] = commonSites.keys()
                Cl[cl_num][e_num]['ua1'] = ua1.keys()
                Cl[cl_num][e_num]['ua2'] = ua2.keys()
                e_num += 1
        cl_num += 1
    return(Cl)
                        


def getConnectedComponentClusters_old(G, Gc):
    cluster = {}
    c_num = 0
    for C in Gc:
        for ed in C.edges():
            commonSites = {}
            for site in G.neighbors(ed[0]):
                if (G.has_edge(ed[1],site) == True):
                    commonSites[site] = 1
            ua2ip = {}
            ua = {}
            data = {}
            data[ed[0]] = {}
            data[ed[1]] = {}
            ua[ed[0]] = {}
            ua[ed[1]] = {}
            for site in commonSites.keys():
                data[ed[0]][site] = {}
                ua[ed[0]][site] = {}
                
                count1 = 0
                for key in G.edge[ed[0]][site]['property'].keys():
                    ua[ed[0]][site][string.split(key,'\t')[3]] = 1
                    data[ed[0]][site][count1] = G[ed[0]][site]['property'][key]['data']
                    count1 += 1

                
                count2 = 0
                data[ed[1]][site] = {}
                ua[ed[1]][site] = {}
                for key in G.edge[ed[1]][site]['property'].keys():
                    ua[ed[1]][site][string.split(key,'\t')[3]] = 1
                    data[ed[1]][site][count2] = G[ed[1]][site]['property'][key]['data']
                    count2 += 1
                    

            flag = 0
            for site in commonSites.keys():
                for ua_key in ua[ed[0]][site]:
                    if (ua[ed[1]][site].has_key(ua_key) == True):
                        flag = 1

            if flag == 0:
                cluster[c_num] = {}
                for item in ed:
                    cluster[c_num][item] = {}
                    for site in commonSites:
                        cluster[c_num][item][site] = {}
                        cluster[c_num][item][site]['ua'] = ua[item][site]
                        cluster[c_num][item][site]['data'] = data[item][site]
                c_num += 1
    return(cluster)
                    
                              
def getPopularSites(G, thr = 0.75):
    N = getNodesOfGivenType(G, 1)
    D = G.degree(N)
    max_degree = (1.0*max(D.values()))
    popularSites = {}
    for nd in N:
        if ((D[nd]/max_degree) > thr):
            popularSites[nd] = D[nd]/max_degree
    return(popularSites)

def detectWateringHole(G, Gh, pop_thr, deg_thr):
    X = extractGraphOfSameNodetype(G, 1, deg_thr)
    return(X)
        


def test1(G):
    X = extract_graph_by_edge_field(G,'GET')
    Y = extract_graph_by_edge_field(G,'POST')
    Z = nx.Graph()
    for ed in X.edges():
        if (Y.has_edge(ed[0],ed[1]) == True):
            Z.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Z.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Z.add_edge(ed[0], ed[1])
            Z.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']

    E = []
    for ed in Z.edges():
        g_count = 0
        p_count = 0
        for pr in Z.edge[ed[0]][ed[1]]['property']:
            if (string.find(pr, 'GET') >= 0):
                g_count += Z.edge[ed[0]][ed[1]]['property'][pr]['count']
            else:
                p_count += Z.edge[ed[0]][ed[1]]['property'][pr]['count']
        if  (p_count > 10):
            E.append(ed)
    return(Z, E)
        
def getClusterStats(C,G):
    for Cx in C:
        ips = C[Cx].keys()
        sites = C[Cx][ips[0]]

        for ip in ips:
            for site in sites:
                in_count = []
                out_count = []
                for ii in C[Cx][ip][site]['data']:
                    rows = string.split(C[Cx][ip][site]['data'][ii], '\n')
                    for row in rows[:-1]:
                        arr = string.split(row,'\t')[-2:]
                        in_count.append(atoi(arr[0]))
                        out_count.append(atoi(arr[1]))

                in_cout = np.array(in_count)
                out_count = 1.0*np.array(out_count)
                C[Cx][ip][site]['summary'] = {}
                C[Cx][ip][site]['summary']['out'] = out_count
                C[Cx][ip][site]['summary']['in'] = in_count
                C[Cx][ip][site]['summary']['in_total'] = sum(in_count)
                C[Cx][ip][site]['summary']['out_total'] = sum(out_count)
                C[Cx][ip][site]['summary']['avg_ratio'] = mean(out_count/in_count)
                C[Cx][ip][site]['summary']['min_ratio'] = min(out_count/in_count)
                C[Cx][ip][site]['summary']['max_ratio'] = max(out_count/in_count)
                C[Cx][ip][site]['summary']['count'] = len(in_count)
                post_count = 0
                get_count = 0
                for pr in G.edge[ip][site]['property'].keys():
                    if (string.find(pr, 'POST') >= 0):
                        post_count += G.edge[ip][site]['property'][pr]['count']
                    if (string.find(pr, 'GET') >= 0):
                        get_count += G.edge[ip][site]['property'][pr]['count']

                C[Cx][ip][site]['summary']['post_count'] = post_count
                C[Cx][ip][site]['summary']['get_count'] = get_count
                
def test2(C):
    for id in C:
        Cx = C[id]
        post_list = []
        get_list = []
        flag = 0
        for ip in Cx.keys():
            for site in Cx[ip]:
                if (Cx[ip][site]['summary']['post_count'] > 0):
                    post_list.append(Cx[ip][site]['summary']['post_count'])
                    get_list.append(Cx[ip][site]['summary']['get_count'])                    
                    flag = 1

        if (flag  > 0):
            ips = Cx.keys()
            sites= Cx[ips[0]].keys()
            print (id, ips, sites, post_list, get_list)
        
    
    
def getPopularityScore(G, node_type =1):
    nds = getNodesOfGivenType(G, node_type)
    pScore = G.Degree(nds)
    max_score = max(pScore.values())
    
    for nd in D.keys():
        pScore[nd] = (1.0*pScore[nd])/max(pScore.values())

def getBotnetGraph(C):
    G = nx.DiGraph()
    ips = {}
    for id in C:
        for ip in C[id]['ips']:
            for site in  C[id]['sites']:
                print ('---->', site)
                G.add_edge(ip, site)
                G.add_node(ip, bipartite=0)
                G.add_node(site, bipartite=1)
    return(G)

                
def BotnetAnomaly(C, G):
    nds = getNodesOfGivenType(C, node_type=1)
    pScore = array(G.degree(nds).values())
    max_score = max(G.degree().values())

    aScore = (1.0*pScore)/max_score
    results = [mean(aScore), max(aScore), min(aScore), std(aScore)]
    return(results)


def getBotPatterns(Gb, G):
    data = {}
    count = 0
    get_count = 0
    post_count = 0

    for ed in Gb.edges():
        epochs = []
        for pr in G.edge[ed[0]][ed[1]]['property']:
            if (string.find(pr,'GET') >= 0):
                get_count += G.edge[ed[0]][ed[1]]['property'][pr]['count']
                
            if (string.find(pr,'POST') >= 0):
                post_count += G.edge[ed[0]][ed[1]]['property'][pr]['count']


            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')
            for row in rows[:-1]:
                date_time = row[0:19]
                pattern = '%Y-%m-%d\t%H:%M:%S'
                epoch = int(time.mktime(time.strptime(date_time, pattern)))
                epochs.append(epoch)
        epochs = sort(array(epochs))
        xx = ed[0] + '\t' + ed[1]
        data[xx] = sort(epochs[1:]-epochs[0:-1])
        count +=1 

    return(data, post_count, get_count)
    

def test3(G, target_site='google.com'):
    nbs = G.neighbors(target_site)
    data = {}
    for nb in nbs:
        epochs = []
        for pr in G.edge[target_site][nb]['property']:
            rows = string.split(G.edge[nb][target_site]['property'][pr]['data'],'\n')
            for row in rows[:-1]:
                date_time = row[0:19]
                pattern = '%Y-%m-%d\t%H:%M:%S'
                epoch = int(time.mktime(time.strptime(date_time, pattern)))
                epochs.append(epoch)
        epochs = sort(array(epochs))
        data[nb] = sort(epochs[1:]-epochs[0:-1])

    return(data)


def test4(G, Gs, l):
    fpOut = open('tmpdata','w')
    nds = getNodesOfGivenType(G, 1)
    
    for nd in sort(nds):    
        if (nd[0] == l):
            xx = 0
            if (string.find(nd, 'mich') >= 0):
                G.remove_node(nd)
        else:
            yy = 0
            #G.remove_node(nd)

    for nd in G.nodes():
        fpOut.write(nd + '\n')
    fpOut.close()

    drawTmpGraph(G, Gs, 'tmp0.pdf')
    

def getTrafficByfld(G, node_type = 1, domain_ext = '.com'):
    Gt = nx.Graph()
    nds = getNodesOfGivenType(G, node_type)

    for nd in nds:
        if (string.find(nd, domain_ext) >= 0):
            for nb in G.neighbors(nd):
                Gt.add_edge(nd, nb)
                Gt.add_node(nd, bipartite=G.node[nd]['bipartite'])
                Gt.add_node(nb, bipartite=G.node[nb]['bipartite'])
                Gt.edge[nd][nb]['property'] = G.edge[nd][nb]['property']
    return(Gt)
            
            
            
    

def test5(G, thr):
    Gp = extract_graph_by_edge_field(G,'POST')
    nds = getNodesOfGivenType(Gp,1)

    for nd in nds:
        if (Gp.degree(nd) > thr):
            Gp.remove_node(nd)

    for nd in Gp.nodes():
        if (Gp.degree(nd) == 0):
            Gp.remove_node(nd)

    drawTmpGraph(Gp, G, 'post_graph.pdf')
    
def readBeaconScores(fileName):
    data2score = {}
    fpIn = open(fileName,'r')
    line = fpIn.readline()
    line = fpIn.readline()[:-1]
    while line != "":
        arr = string.split(line,'\t')
        x1 = arr[0] + '\t' + arr[2]
        x2 = arr[2] + '\t' + arr[0]
        conf = atof(arr[3])
        risk = atof(arr[4])

        data2score[x1] = [conf, risk]
        data2score[x2] = [conf, risk]
        line = fpIn.readline()[:-1]
    
    return(data2score)

def readBeaconScoresByType(fileName, risk_col = 2, anomaly_col=5):
    data2score = {}
    fpIn = open(fileName,'r')
    line = fpIn.readline()
    line = fpIn.readline()[:-1]
    while line != "":
        arr = string.split(line,'\t')
        x1 = arr[0] + '\t' + arr[1]
        x2 = arr[1] + '\t' + arr[0]
   
        risk = atof(arr[risk_col])
        conf = atof(arr[anomaly_col])

        data2score[x1] = [conf, risk]
        data2score[x2] = [conf, risk]
        line = fpIn.readline()[:-1]
    
    return(data2score)


def readBeaconConfScores(fileName):
    data2score = {}
    fpIn = open(fileName,'r')
    line = fpIn.readline()
    line = fpIn.readline()[:-1]
    while line != "":
        arr = string.split(line,'\t')
        x1 = arr[0] + '\t' + arr[2]
        x2 = arr[2] + '\t' + arr[0]
        conf = atof(arr[3])
        risk = atof(arr[4])

        data2score[x1] = conf
        data2score[x2] = conf
        line = fpIn.readline()[:-1]
    
    return(data2score)



def readBeaconScores2(fileName):
    data2score = {}
    fpIn = open(fileName,'r')
    line = fpIn.readline()
    line = fpIn.readline()[:-1]
    while line != "":
        arr = string.split(line,'\t')
        x1 = arr[0] + '\t' + arr[1]
        x2 = arr[1] + '\t' + arr[0]
        risk = atof(arr[6])
        
        data2score[x1] = risk
        data2score[x2] = risk
        line = fpIn.readline()[:-1]
    
    return(data2score)

        
def getfield2popularity(G, field_col):
    field2num = {}
    for ed in G.edges():
        fields = {}
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            field = string.split(pr, '\t')[field_col]
            fields[field] = 1

        for field in fields.keys():
            if (field2num.has_key(field) == False):
                field2num[field] = 0
            field2num[field] += 1
            
    return(field2num)


def test6(G):
    for ed in G.edges():
        if (G.node[ed[0]]['bipartite'] == 0 and G.node[ed[1]]['bipartite'] == 0):
            print ('1:   ', ed)

        if (G.node[ed[0]]['bipartite'] == 1 and G.node[ed[1]]['bipartite'] == 1):
            print ('2:   ', ed)
    
            
def getDataMatrix(features):
    id2ip = {}
    D = np.zeros((len(features[0].keys()), 4*len(features.keys())))
    row = 0
    for key in features[0].keys():
        kk = 0
        for ii in features.keys():
            for jj in range(len(features[ii][key])):
                D[row,kk] = features[ii][key][jj]
                kk += 1
        id2ip[row] = key
        row += 1
    return(D, id2ip)

def getFieldsOfGivenType(G, field_col):
    field2count = {}
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            field = string.split(pr,'\t')[field_col]
            if (field2count.has_key(field) == False):
                field2count[field] = 0
            field2count[field] += 1
    return(field2count)

def getFieldFrequency(G, field_col):
    field2vol = {}
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            field = string.split(pr, '\t')[field_col]
            if (field2vol.has_key(field) == False):
                field2vol[field] = 0
            field2vol[field] += G.edge[ed[0]][ed[1]]['property'][pr]['count']
    return(field2vol)


def getedge2fieldFrequency(G, field_col, field_vals):
    field2vol = {}
    for ed in G.edges():
        field2vol[ed] = {}
        for key in field_vals:

            field2vol[ed][key] = 0
        
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            field = string.split(pr, '\t')[field_col]
            field2vol[ed][field] += G.edge[ed[0]][ed[1]]['property'][pr]['count']
    return(field2vol)
    

def extractGraphFromEdges(G, edges):
    Gx = nx.Graph()
    for ed in edges:
        Gx.add_edge(ed[0], ed[1])
        Gx.add_node(ed[0], bipartite=0)
        Gx.add_node(ed[1], bipartite=1)
        Gx.edge[ed[0]][ed[1]] = G.edge[ed[0]][ed[1]]
    return(Gx)

def extractEdgesWithLowProbNon(G, thr, fieldArr, field_col=3):
    edList = []
    for ed in G.edges():
        total1 = 0
        total2 = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            arr = string.split(pr,'\t')
            if (fieldArr.has_key(arr[field_col]) == True):
                total1 +=  G.edge[ed[0]][ed[1]]['property'][pr]['count']
            else:
                total2 +=  G.edge[ed[0]][ed[1]]['property'][pr]['count']

        if (total2/(total1+total2) > thr):
            edList.append(ed)
        
    return(edList)

def readBeaconScores_xx(fileName):
    fpIn = open(fileName,'r')
    line = fpIn.readline()[:-1]
    scores = {}
    while line != "":
        arr = string.split(line,'\t')
        x = (arr[0], arr[1])
        y = (arr[1], arr[0])
        if (atof(arr[2]) > 0.5):
            scores[x] = atof(arr[2])
            scores[y] = atof(arr[2])
        line = fpIn.readline()[:-1]

    return(scores)

def getHighRiskUas(G, field_col, thr):
    U = getFieldsOfGivenType(G, field_col)
    m = 1.0*max(U.values())
    print (m)
    Uh = {}
    for key in U.keys():
        if (1-(U[key]/m) > thr):
            Uh[key] = (1-(U[key]/m))

    return(Uh)

def getHighRiskMethods(G, field_col, thr):
    M = getFieldsOfGivenType(G, field_col)
    m_val = 1.0*max(M.values())
    Mh = {}
    for key in M.keys():
        if (1-(M[key]/max_val) > thr):
            Mh[key] = (1-(M[key]/max_val))

    return(Mh)

def filterGraphByGivenFieldValues(G, field_col, field_values):
    for ed in G.edges():
        flag = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            field = string.split(pr, '\t')[field_col]
            if (field_values.has_key(field) == True):
                flag = 1
        if (flag == 0):
            G.remove_edge(ed[0],ed[1])

def readAlexaTop1M():
    fileName = 'top-1m.csv'

    rank_range = [100, 1000, 10000, 50000, 100000, 1000000]


    bucket = {}
    for ii in range(len(rank_range)):
        bucket[rank_range[ii]] = ii

    fpIn = open(fileName,'r')
    line = fpIn.readline()[:-1]

    ii = 0
    alexa = {}
    while line != "":
        arr = string.split(line, ',')
        if (atoi(arr[0])/rank_range[ii] == 0):
            alexa[arr[1]] = bucket[rank_range[ii]]
        else:
            alexa[arr[1]] = bucket[rank_range[ii]]
            ii = ii+1
        line = fpIn.readline()[:-1]

    return(alexa)

def updateGraphWithAlexa(G,alexa, nodeType=1):
    nds = getNodesOfGivenType(G, nodeType)
    count1 = 0
    count2 = 0
    x = {}
    y = {}
    for nd in nds:
        domain_info = tldextract.extract(nd)
        sld = domain_info.domain + '.' + domain_info.suffix
        if (alexa.has_key(sld) == True):
            G.node[nd]['alexa_rank'] = alexa[sld]
            x[sld] = 1
            count1 += 1 
        else:
            G.node[nd]['alexa_rank'] = 7
            y[sld] = 1
            count2 += 1

def filterTopAlexaSites(G, rank=2):
    nds = getNodesOfGivenType(G, 1)
    for nd in nds:
        nbs = G.neighbors(nd)
        if (G.node[nd]['alexa_rank'] >= 0 and G.node[nd]['alexa_rank'] < rank):
            print (nd)
            G.remove_node(nd)
        for nb in nbs:
            if (G.degree(nb) == 0):
                G.remove_node(nb)



    

def getTimeRange(Gx, G):
    time_stamps = []
    for ed in Gx.edges():
        min_time = -1
        max_time = 9999999999
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            for row in rows:
                item = atoi(row[0:19].replace('-','').replace(':','').replace('\t','')[0:-2])
                time_stamps.append(item)
    return(time_stamps)

        
def getRawData(inFile, botnet):
    eds = []
    fpIn = open(inFile, 'r')
    line = fpIn.readline()[:-1]
    while line != "":
        line = fpIn.readline()[:-1]
        
def readBluecoatWhois(fileName = "bluecoat_whois_out.tsv"):
    ip2company = {}
    fpIn = open(fileName, 'r')
    line = fpIn.readline()[:-1]
    while line != "":
        arr = string.split(line,'\t')
        ip2company[arr[0]] = arr[1]
        line = fpIn.readline()[:-1]

    return(ip2company)


def feature_relative_popularity_site_using_whois(G, ip2company, nodeType=1):
    sites = getNodesOfGivenType(G, nodeType)
    D = G.degree(sites)
    m = (1.0*max(D.values()))
    site2sld = {}

    for site in sites:
        if (ip2company.has_key(site) == True):
            site2sld[site] = ip2company[site]
            print ('---> ', site, site2sld[site])
        else:
            domain = tldextract.extract(site)
            sld = domain.domain + '.' + domain.suffix
            site2sld[site] = sld


    Gx = nx.Graph()
    for ed in G.edges():
        if (G.edge[0]['bipartite'] == 0):
            src = ed[0]
            dst = sit2sld[ed[1]]
        else:
            src = ed[1]
            dst = site2sld[ed[0]]
        Gx.add_edge(src, dst)
        Gx.add_node(src, bipartite=0)
        Gx.add_node(dst, bipartite=1)
    return(site2sld)
            


def get_ip2site_counts(Gx):
    ed2f = {}
    for ed in Gx.edges():
        xx = (ed[0], ed[1])
        ed2f[xx] = 0
        for pr in Gx.edge[ed[0]][ed[1]]['property'].keys():
            ed2f[xx] += Gx.edge[ed[0]][ed[1]]['property'][pr]['count']
    return(ed2f)
    
def get_ip2site_inout_bytes(Gx):
    ed2in = {}
    ed2out = {}
    for ed in Gx.edges():
        xx = (ed[0], ed[1])
        ed2in[xx] = 0
        ed2out[xx] = 0
        for pr in Gx.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(Gx.edge[ed[0]][ed[1]]['property'][pr]['data'], '\n')[:-1]
            for row in rows:
                arr = string.split(row, '\t')
                ed2in[xx] += atoi(arr[3])
                ed2out[xx] += atoi(arr[4])
    return(ed2in, ed2out)

def get_ip2site_method(Gx):
    ed2get = {}
    ed2nonget = {}
    for ed in Gx.edges():
        xx = (ed[0], ed[1])
        ed2get[xx] = 0
        ed2nonget[xx] = 0
        for pr in Gx.edge[ed[0]][ed[1]]['property'].keys():
            if (string.find(pr, 'GET') >= 0):
                ed2get[xx] += Gx.edge[ed[0]][ed[1]]['property'][pr]['count']
            else:
                ed2nonget[xx] += Gx.edge[ed[0]][ed[1]]['property'][pr]['count']

    return(ed2get, ed2nonget)


def convert2list(data):
    ed2x = {}
    for hr in data.keys():
        for ed in data[hr]:
            if (ed2x.has_key(ed) == False):
                ed2x[ed] = []
            ed2x[ed].append(data[hr][ed])
    return(ed2x)

def edge2iqrfeatures(data, percents):
    distr = []
    ed2id = {}
    id2ed = {}

    id = 0
    count = 0
    for key in data.keys():
        ed2id[key] = id
        id2ed[id] = key
        id += 1
        for item in data[key]:
            distr.append(item)
        count += 1
        
    distr = array(distr)
    r = np.zeros((len(percents)-1,2))
    for ii in range(1, len(percents)):
        r[ii-1][0] = scoreatpercentile(distr, percents[ii-1])
        r[ii-1][1] = scoreatpercentile(distr, percents[ii])

    M = np.zeros((count, len(r)))
    for key in data.keys():
        id = ed2id[key]
        for item in data[key]:
            for ii in range(r.shape[0]):
                if (item >= r[ii][0] and item < r[ii][1]):
                    M[id][ii] += 1
                    break
    return(M, ed2id, id2ed)


def genedgeFeatureMatrix(G):
    hours = []
    for key in G.keys():
        if (string.find(str(key),'20050502') >= 0):
            hours.append(key)

    counts = {}
    in_bytes = {}
    out_bytes = {}
    get_method = {}
    nonget_method = {}
    for hr in hours:
        counts[hr] = get_ip2site_counts(G[hr])
        [in_bytes[hr], out_bytes[hr]] = get_ip2site_inout_bytes(G[hr])
        [get_method[hr], nonget_method[hr]] = get_ip2site_method(G[hr])
    
    ed2count = {}
    ed2getmethod = {}
    ed2nongetmethod = {}
    ed2inbytes = {}
    ed2outbytes = {}

    ed2count = convert2list(counts)
    ed2getmethod = convert2list(get_method)
    ed2nongetmethod = convert2list(nonget_method)
    ed2inbytes = convert2list(in_bytes)
    ed2outbytes = convert2list(out_bytes)

    count_percents = [0, 40, 60, 75, 90, 95, 99, 100]
    get_percents = [0, 20, 40, 60, 80, 90, 95, 100]
    nonget_percents  = [0, 20, 40, 60, 80, 90, 95, 100]
    inbytes_percents  = [0, 20, 40, 60, 80, 90, 95, 100]
    outbytes_percents = [0, 20, 40, 60, 80, 90, 95, 100]

    [M1, x, y] = edge2iqrfeatures(ed2count, count_percents)
    [M2, x, y] = edge2iqrfeatures(ed2getmethod, get_percents)
    [M3, x, y] = edge2iqrfeatures(ed2nongetmethod, nonget_percents)
    [M4, x, y] = edge2iqrfeatures(ed2inbytes, inbytes_percents)
    [M5, x, y] = edge2iqrfeatures(ed2outbytes, outbytes_percents)

    M = np.concatenate((M1,M2), axis=1)
    M = np.concatenate((M,M3), axis=1)
    M = np.concatenate((M,M4), axis=1)
    M = np.concatenate((M,M5), axis=1)

    return(M, x, y)



def extract_graph_by_edge_field_statuscode(G, sc_code='2xx', col_num=1):
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            arr = string.split(pr,'\t')
            if (arr[col_num][0] == sc_code[0]):
                if (Gx.has_edge(ed[0], ed[1]) == False):
                    Gx.add_edge(ed[0], ed[1])
                    Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
                    Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
                    Gx.edge[ed[0]][ed[1]]['property'] = {}                    
                Gx.edge[ed[0]][ed[1]]['property'][pr] = G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)


def extractGraphFromGivenNodes_old(G, nds, node_type):
    Gx = nx.Graph()
    for ed in G.edges():
        if (G.node[ed[0]]['bipartite'] == (1-node_type)):
            x = ed[1-node_type]
            y = ed[node_type]
        else:
            x = ed[node_type]
            y = ed[1-node_type]

        if (nds.has_key(y) == True):
            Gx.add_edge(x,y)
            Gx.add_node(x, bipartite=G.node[x]['bipartite'])
            Gx.add_node(y, bipartite=G.node[y]['bipartite'])
            Gx.edge[x][y]['property'] = G.edge[x][y]['property']

    return(Gx)

            
def readWhiteList(fileName):
    wList = {}
    fpIn = open(fileName,'r')
    line = fpIn.readline()[:-1]
    while line != "":
        wList[line] = 1
        line = fpIn.readline()[:-1]
    fpIn.close()
    return(wList)


def filterByWhiteList(G, wlist):
    for nd in wlist.keys():
        if (G.has_node(nd)==True):
            nbs = G.neighbors(nd)
            G.remove_node(nd)
            for nb in nbs:
                if (G.degree(nb) == 0):
                    G.remove_node(nb)
    for nb in G.nodes():
        if (G.degree(nd) == 0):
            G.remove(nd)

            
    
def extractGraphOfHighFrequency(G, freq_thr):
    Gx = nx.Graph()
    for ed in G.edges():
        total = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            total += G.edge[ed[0]][ed[1]]['property'][pr]['count']
        if (total > freq_thr):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']
    return(Gx)

            
            
def getIntersectionGraph(G1, G2):
    Gx = nx.Graph()
    for ed in G1.edges():
        if (G2.has_edge(ed[0], ed[1]) == True):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G1.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G1.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G1.edge[ed[0]][ed[1]]['property']
    return(Gx)

def getIntersectionGraph_comp(G1, G2):
    Gx = nx.Graph()
    for ed in G1.edges():
        if (G2.has_edge(ed[0], ed[1]) == False):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G1.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G1.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G1.edge[ed[0]][ed[1]]['property']
    return(Gx)

def getCommonNodesOfGivenType(G1, G2, node_type):
    common_nds = {}
    nds = getNodesOfGivenType(G1, node_type)
    for nd in nds:
        if (G2.has_node(nd) == True):
            common_nds[nd] = 1

    return(common_nds)



    
def extractGraphByHighBeaconRiskAndConf(G, b2s, conf_thr, risk_thr):
    Gx = nx.Graph()
    count1 = 0
    count2 = 0
    for ed in G.edges():
        ed_str = ed[0] + '\t' + ed[1]
        if (b2s.has_key(ed_str) == True and b2s[ed_str][0] > conf_thr and b2s[ed_str][1] > risk_thr):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite = G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite = G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']
    return(Gx)

def extractHighBeaconingGraph(G, b2s, thr):
    Gx = nx.Graph()
    count1 = 0
    count2 = 0
    for ed in G.edges():
        ed_str = ed[0] + '\t' + ed[1]
        if (b2s.has_key(ed_str) == True and b2s[ed_str] > thr):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite = G.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite = G.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] =  G.edge[ed[0]][ed[1]]['property']
    return(Gx)

def filterGraphByDegreeRange(G, deg_range, node_type):
    Gx = nx.Graph()
    nds = getNodesOfGivenType(G, node_type)
    for nd in nds:
        if (G.degree(nd) >= deg_range[0] and G.degree(nd) <= deg_range[1]):
            for nb in G.neighbors(nd):
                Gx.add_edge(nd, nb)
                Gx.add_node(nd, bipartite=G.node[nd]['bipartite'])
                Gx.add_node(nb, bipartite=G.node[nb]['bipartite'])
                Gx.edge[nd][nb]['property'] = G.edge[nd][nb]['property']
    return(Gx)
                
        
def get_edgeprop_field_counts(G, field_col):
    G_f2s = nx.Graph()
    G_f2d = nx.Graph()
    for ed in G.edges():
        if (G.node[ed[0]]['bipartite'] == 0):
            src = ed[0]
            dst = ed[1]
        else:
            src = ed[1]
            dst = ed[0]

        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            for row in rows:
                arr = string.split(row,'\t')
                G_f2s.add_edge(src, arr[field_col])
                G_f2s.add_node(src, bipartite=0)
                G_f2s.add_node(arr[field_col], bipartite=1)

                G_f2d.add_edge(dst, arr[field_col])
                G_f2d.add_node(dst, bipartite=0)
                G_f2d.add_node(arr[field_col], bipartite=1)
                
    return(G_f2s, G_f2d)

def extract_graph_from_given_list_of_field_values(G, fieldInfo, field_col):
    Gx = nx.Graph()
    for ed in G.edges():
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            field_val = string.split(pr, '\t')[field_col]
            if (fieldInfo.has_key(field_val) == True):
                if (Gx.has_edge(ed[0],ed[1])==False):
                    Gx.add_edge(ed[0], ed[1])
                    Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
                    Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
                    Gx.edge[ed[0]][ed[1]]['property'] = {}
                Gx.edge[ed[0]][ed[1]]['property'][pr] =  G.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)

def extractGraphFromGivenNodes(G, nodeList):
    Gx = nx.Graph()
    for nd in nodeList:
        if (G.has_node(nd) == True):
            for nb in G.neighbors(nd):
                Gx.add_edge(nd, nb)
                Gx.add_node(nd, bipartite=G.node[nd]['bipartite'])            
                Gx.add_node(nb, bipartite=G.node[nb]['bipartite'])
                Gx.edge[nd][nb]['property'] = G.edge[nd][nb]['property']

    return(Gx)


def copyGraph(G):
    Gx = nx.Graph()
    for ed in G.edges():
        Gx.add_edge(ed[0], ed[1])
        Gx.add_node(ed[0], bipartite=G.node[ed[0]]['bipartite'])
        Gx.add_node(ed[1], bipartite=G.node[ed[1]]['bipartite'])
        Gx.edge[ed[0]][ed[1]]['property'] = G.edge[ed[0]][ed[1]]['property']
    return(Gx)



    
def updateGraphWithAlexa2(G,alexa):
    nds = G.nodes()
    for nd in nds:
        domain_info = tldextract.extract(nd)
        sld = domain_info.domain + '.' + domain_info.suffix
        if (alexa.has_key(sld) == True):
            G.node[nd]['alexa_rank'] = alexa[sld]
        else:
            G.node[nd]['alexa_rank'] = 7


def filterout_graph_with_edgeprop_field(G, patterns, col_num=5):
    for ed in G.edges():
        flag = 0
        for pr in G.edge[ed[0]][ed[1]]['property'].keys():
            rows = string.split(G.edge[ed[0]][ed[1]]['property'][pr]['data'],'\n')[:-1]
            for row in rows:
                field = string.split(row,'\t')[col_num]
                for pattern in patterns:
                    if (string.find(field, pattern) >= 0):
                        flag = 1
        if (flag > 0):
            G.remove_edge(ed[0], ed[1])


def filterout_internal_dest_ips(G):
    for nd in G.nodes():
        if (G.node[nd]['bipartite'] == 1):
            try:
                if (is_reserved(unicode(nd)) == True or is_internal(unicode(nd)) == True):
                    G.remove_node(nd)
                    print (nd)
            except:
                xx = 0
    
    
def combineGraphs(G1, G2):
    Gx = copyGraph(G1)
    for ed in G2.edges():
        if (Gx.has_edge(ed[0], ed[1]) == False):
            Gx.add_edge(ed[0], ed[1])
            Gx.add_node(ed[0], bipartite=G2.node[ed[0]]['bipartite'])
            Gx.add_node(ed[1], bipartite=G2.node[ed[1]]['bipartite'])
            Gx.edge[ed[0]][ed[1]]['property'] = {}
        for pr in G2.edge[ed[0]][ed[1]]['property']:
            Gx.edge[ed[0]][ed[1]]['property'][pr] = G2.edge[ed[0]][ed[1]]['property'][pr]
    return(Gx)
            










