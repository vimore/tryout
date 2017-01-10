import sys
sys.path.append('/Users/sdoddi/Library/Python/2.7/lib/python/site-packages/')
sys.path.append('/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/site-packages/')

#import pygraphviz as pgv
#from pygraphviz import *
import networkx as nx
from networkx import *
from networkx.readwrite import *

import string
from string import *

from matplotlib.pylab import *

import tldextract
from tldextract import *


def build_graph_db_by_hour_old(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, src_port_col, line_limit):
    G = {}
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = -1
        tm = -1
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            if (tm != arr[time_col][0:10]):
                last_tm = tm
                tm =  arr[time_col][0:10]
                
            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            if (atoi(arr[src_port_col]) > 1024):
                arr[src_port_col] = '1025'

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]



            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            G[tm].add_edge(arr[col_src], arr[col_dst])

            if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('property')==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}

            if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('createTime')==False):
                try:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['createTime'] =  G[last_tm].edge[arr[col_src]][arr[col_dst]]['createTime']
                except:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['createTime'] =  atoi(tm)

            if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1

                try:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime'] = G[last_tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime']
                except:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime'] = atoi(tm)
            else:
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G, count)

def build_graph_db_by_hour(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, src_port_col, line_limit):
    G = {}
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = []
        #last_tm = -1
        tm = -1
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            if (tm != arr[time_col][0:10]):
                last_tm.append(tm)
                tm =  arr[time_col][0:10]

            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            if (atoi(arr[src_port_col]) > 1024):
                arr[src_port_col] = '1025'

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            if (G[tm].has_edge(arr[col_src], arr[col_dst]) == False):
                G[tm].add_edge(arr[col_src], arr[col_dst])
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
            else:
                if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
                else:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G, count)



def build_graph_db_by_hour_v2(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, src_port_col, line_limit):
    G = {}
    count = 0
    tmList = []
    offset = 10
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            tm = arr[time_col][0:offset]

            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            if (atoi(arr[src_port_col]) > 1024):
                arr[src_port_col] = '1025'

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            if (G[tm].has_edge(arr[col_src], arr[col_dst]) == False):
                G[tm].add_edge(arr[col_src], arr[col_dst])
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
            else:
                if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                    #G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
                else:
                    #G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G, count)

def build_graph_db_by_day(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, src_port_col, line_limit):
    G = {}
    count = 0
    offset = 8
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = -1
        tm = -1
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            if (tm != arr[time_col][0:offset]):
                last_tm = tm
                tm =  arr[time_col][0:offset]
                
            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            if (atoi(arr[src_port_col]) > 1024):
                arr[src_port_col] = '1025'

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]


            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            G[tm].add_edge(arr[col_src], arr[col_dst])

            if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('property')==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}

            if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('createTime')==False):
                try:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['createTime'] =  G[last_tm].edge[arr[col_src]][arr[col_dst]]['createTime']
                except:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['createTime'] =  atoi(tm)

            if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1

                try:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime'] = G[last_tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime']
                except:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime'] = atoi(tm)
            else:
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G, count)

def build_graph_db_by_hour_unlabel(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, src_port_col, line_limit):
    G = {}

    count = 0
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = -1
        tm = -1
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            if (tm != arr[time_col][0:10]):
                last_tm = tm
                tm =  arr[time_col][0:10]
                
            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            if (atoi(arr[src_port_col]) > 1024):
                arr[src_port_col] = '1025'

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]


            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            if (string.find(e_val, 'Attack') < 0):
                G[tm].add_edge(arr[col_src], arr[col_dst])

                if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('property')==False):
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}

                if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('createTime')==False):
                    try:
                        G[tm].edge[arr[col_src]][arr[col_dst]]['createTime'] =  G[last_tm].edge[arr[col_src]][arr[col_dst]]['createTime']
                    except:
                        G[tm].edge[arr[col_src]][arr[col_dst]]['createTime'] =  atoi(tm)

                if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1

                    try:
                        G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime'] = G[last_tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime']
                    except:
                        G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime'] = atoi(tm)
                else:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G, count)




def appendInternalExternalLabels(G, prefix):
    for key in G.keys():
        for nd in G[key].nodes():
            if (string.find(nd,prefix) >= 0):
                G[key].node[nd]['type'] = 'internal'
            else:
                G[key].node[nd]['type'] = 'external'

def appendFirstTimeDates(G):
    node2time = {}
    for key in sort(G.keys()):
        for nd in G[key].nodes():
            if (node2time.has_key(nd) == False):
                node2time[nd] = atoi(key)
            G[key].node[nd]['time'] = node2time[nd]


def appendEdgeCreateTimes(G):
    ii = 0
    key = sort(G.keys())[0]
    for ed in G[key].edges():
        G[key].edge[ed[0]][ed[1]]['createTime'] = key

    ii = 1
    for key1 in sort(G.keys())[1:]:
        for ed in G[key1].edges():
            createTime = key1
            for key2 in sort(G.keys())[0:ii]:
                if (G[key2].has_edge(ed[0], ed[1]) == True):
                    createTime = key2
                    break
            G[key1].edge[ed[0]][ed[1]]['createTime'] = createTime
        ii += 1

def appendEdgePropertyCreateTimes(G):
    ii = 0
    key = sort(G.keys())[0]
    for ed in G[key].edges():
        for pr in G[key].edge[ed[0]][ed[1]]['property'].keys():
            G[key].edge[ed[0]][ed[1]]['property'][pr]['createTime'] = key

    ii = 1
    for key1 in sort(G.keys())[1:]:
        for ed in G[key1].edges():
            for pr in G[key1].edge[ed[0]][ed[1]]['property'].keys():
                createTime = key1
                for key2 in sort(G.keys())[0:ii]:
                    if (G[key2].has_edge(ed[0], ed[1]) == True):
                        if (G[key2].edge[ed[0]][ed[1]]['property'].has_key(pr) == True):
                            createTime = key2
                            break
                G[key1].edge[ed[0]][ed[1]]['property'][pr]['createTime'] = createTime
        ii += 1




def buildIdentityGraph(files, time_col, node_cols, edge_attrs, delim):
    G = nx.Graph()
    for file in files:
        fp = open(file,'r')
        line = fp.readline()[:-1]
        while line != "":
            arr = string.split(line, delim)

            nodeA = arr[node_cols[0]]
            nodeB = arr[node_cols[1]]

            G.add_edge(nodeA, nodeB)
            prop = ''
            for col in edge_attrs:
                prop += arr[col] + delim

            prop = prop[:-1]

            print ('---> ', prop)
            if (G.edge[nodeA][nodeB].has_key('propery') == False):
                G.edge[nodeA][nodeB]['propery'] = {}

            if (G.edge[nodeA][nodeB]['propery'].has_key(prop) == False):
                G.edge[nodeA][nodeB]['propery'][prop] = {}
                G.edge[nodeA][nodeB]['propery'][prop]['data'] = arr[time_col][0:10]
            else:
                G.edge[nodeA][nodeB]['propery'][prop]['data']  += arr[time_col][0:10] + '\t'
            
            line = fp.readline()[:-1]
    return(G)





def build_graph_url2ip_by_hour(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = {}
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = []
        tm = -1
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            if (tm != arr[time_col][0:10]):
                last_tm.append(tm)
                tm =  arr[time_col][0:10]

            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            G[tm].add_edge(arr[col_src], arr[col_dst])

            if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('property')==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}


            if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
            else:
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)




def buildDNSGraphByHour(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = {}
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = []
        tm = -1
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            if (tm != arr[time_col][0:10]):
                last_tm.append(tm)
                tm =  arr[time_col][0:10]

            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            G[tm].add_edge(arr[col_src], arr[col_dst])

            if (G[tm].edge[arr[col_src]][arr[col_dst]].has_key('property')==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}


            if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
            else:
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)



def buildDNSGraph(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = nx.DiGraph()
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = []
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            tm =  arr[time_col][0:10]

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            G.add_edge(arr[col_src], arr[col_dst])

            if (G.edge[arr[col_src]][arr[col_dst]].has_key('property')==False):
                G.edge[arr[col_src]][arr[col_dst]]['property'] = {}
                G.edge[arr[col_src]][arr[col_dst]]['createTime'] = tm


            if (G.edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['createTime'] = tm
            else:
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)


        
        print print_string
    return(G)

def buildDNSDomain2IPByHour(files, time_col, col_src, col_dst, delim, line_limit):
    G = {}
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        last_tm = []
        tm = -1
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            if (tm != arr[time_col][0:10]):
                last_tm.append(tm)
                tm =  arr[time_col][0:10]

            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            G[tm].add_edge(arr[col_src], arr[col_dst])
            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)


def mergeGraphs(G1, G2):
    Gx = nx.DiGraph()
    for ed in G1.edges():
        Gx.add_edge(ed[0],ed[1])
        Gx.edge[ed[0]][ed[1]]['createTime'] =  G1.edge[ed[0]][ed[1]]['createTime']
        Gx.edge[ed[0]][ed[1]] = {}
        Gx.edge[ed[0]][ed[1]]['createTime'] =  G1.edge[ed[0]][ed[1]]['createTime']
        Gx.edge[ed[0]][ed[1]]['property'] = {}
        for prop in G1.edge[ed[0]][ed[1]]['property']:
            Gx.edge[ed[0]][ed[1]]['property'][prop] = {}
            Gx.edge[ed[0]][ed[1]]['property'][prop]['count'] = G1.edge[ed[0]][ed[1]]['property'][prop]['count']
            Gx.edge[ed[0]][ed[1]]['property'][prop]['createTime'] = G1.edge[ed[0]][ed[1]]['property'][prop]['createTime']
            Gx.edge[ed[0]][ed[1]]['property'][prop]['data'] = G1.edge[ed[0]][ed[1]]['property'][prop]['data']


    for ed in G2.edges():
        if (Gx.has_edge(ed[0],ed[1]) == False):
            Gx.add_edge(ed[0],ed[1])
            Gx.edge[ed[0]][ed[1]] = {}
            Gx.edge[ed[0]][ed[1]]['property'] = {}
            Gx.edge[ed[0]][ed[1]]['createTime'] = G2.edge[ed[0]][ed[1]]['createTime']
              

        for prop in G2.edge[ed[0]][ed[1]]['property']:
            if (Gx.edge[ed[0]][ed[1]]['property'].has_key(prop) == False):
                Gx.edge[ed[0]][ed[1]]['property'][prop]  = {}
                Gx.edge[ed[0]][ed[1]]['property'][prop]['count']  = G2.edge[ed[0]][ed[1]]['property'][prop]['count'] 
                Gx.edge[ed[0]][ed[1]]['property'][prop]['data']  = G2.edge[ed[0]][ed[1]]['property'][prop]['data'] 
                Gx.edge[ed[0]][ed[1]]['property'][prop]['createTime']  = G2.edge[ed[0]][ed[1]]['property'][prop]['createTime'] 
            else:                
                Gx.edge[ed[0]][ed[1]]['property'][prop]['data']  += G2.edge[ed[0]][ed[1]]['property'][prop]['data'] + '\n'
                Gx.edge[ed[0]][ed[1]]['property'][prop]['count']  += G2.edge[ed[0]][ed[1]]['property'][prop]['count'] 
                if (atoi(Gx.edge[ed[0]][ed[1]]['property'][prop]['createTime']) > atoi(G2.edge[ed[0]][ed[1]]['property'][prop]['createTime'])):
                    Gx.edge[ed[0]][ed[1]]['property'][prop]['createTime'] = G2.edge[ed[0]][ed[1]]['property'][prop]['createTime']

    return(Gx)

        

def build_fw_graph_db_by_hour(files, time_col, col_src, col_dst, event_attrs, edge_attrs, in_bytes_col, out_bytes_col, delim, src_port_col, line_limit):
    G = {}
    count = 0
    tmList = []
    offset = 10
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            tm = arr[time_col][0:offset]

            if (G.has_key(tm) == False):
                G[tm] = nx.DiGraph()

            if (atoi(arr[src_port_col]) > 1024):
                arr[src_port_col] = '1025'

            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            if (G[tm].has_edge(arr[col_src], arr[col_dst]) == False):
                G[tm].add_edge(arr[col_src], arr[col_dst])
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['in_bytes'] = atoi(arr[in_bytes_col])
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['out_bytes'] = atoi(arr[out_bytes_col])
                G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
            else:
                if (G[tm].edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['in_bytes'] = atoi(arr[in_bytes_col])
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['out_bytes'] = atoi(arr[out_bytes_col])
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
                else:
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['in_bytes'] += atoi(arr[in_bytes_col])
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['out_bytes'] += atoi(arr[out_bytes_col])
                    G[tm].edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G, count)



def build_graph_ip2url(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = nx.Graph()
    
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            G.add_node(arr[col_src], bipartite = 0)
            G.add_node(arr[col_dst], bipartite = 1)
            G.add_edge(arr[col_src], arr[col_dst])

            if (G.edge[arr[col_src]][arr[col_dst]].has_key('property')==False):
                G.edge[arr[col_src]][arr[col_dst]]['property'] = {}


            if (G.edge[arr[col_src]][arr[col_dst]]['property'].has_key(e_prop)==False):
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop] = {}
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] = e_val + '\n'
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] = 1
            else:
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['data'] += e_val + '\n'
                G.edge[arr[col_src]][arr[col_dst]]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)


def build_graph_ip2url_sld(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = nx.Graph()

    utf2str = {}
    count = 0
    tmList = []
    utf_count = 0
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            xx = tldextract.extract(arr[col_dst])
            if (xx.suffix == ''):
                dst = xx.domain
            else:
                dst = xx.domain + '.' + xx.suffix

            try:
                xx = dst.encode('utf-16')
            except:
                if (utf2str.has_key(dst) == False):
                    utf2str[dst] = 'xyz_' + str(utf_count)
                    utf_count += 1
                dst = utf2str[dst]
                
            G.add_node(arr[col_src], bipartite = 0)
            G.add_node(dst, bipartite = 1)
            G.add_edge(arr[col_src], dst)

            if (G.edge[arr[col_src]][dst].has_key('property')==False):
                G.edge[arr[col_src]][dst]['property'] = {}


            if (G.edge[arr[col_src]][dst]['property'].has_key(e_prop)==False):
                G.edge[arr[col_src]][dst]['property'][e_prop] = {}
                G.edge[arr[col_src]][dst]['property'][e_prop]['data'] = e_val + '\n'
                G.edge[arr[col_src]][dst]['property'][e_prop]['count'] = 1
            else:
                G.edge[arr[col_src]][dst]['property'][e_prop]['data'] += e_val + '\n'
                G.edge[arr[col_src]][dst]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)

def build_graph_ip2url_site(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = nx.Graph()

    utf2str = {}
    count = 0
    tmList = []
    utf_count = 0
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            # xx = tldextract.extract(arr[col_dst])
            # if (xx.suffix == ''):
            #     dst = xx.domain
            # else:
            #     dst = xx.domain + '.' + xx.suffix

            dst = arr[col_dst]
            try:
                xx = dst.encode('utf-16')
            except:
                if (utf2str.has_key(dst) == False):
                    utf2str[dst] = 'xyz_' + str(utf_count)
                    utf_count += 1
                dst = utf2str[dst]
                
            G.add_node(arr[col_src], bipartite = 0)
            G.add_node(dst, bipartite = 1)
            G.add_edge(arr[col_src], dst)

            if (G.edge[arr[col_src]][dst].has_key('property')==False):
                G.edge[arr[col_src]][dst]['property'] = {}


            if (G.edge[arr[col_src]][dst]['property'].has_key(e_prop)==False):
                G.edge[arr[col_src]][dst]['property'][e_prop] = {}
                G.edge[arr[col_src]][dst]['property'][e_prop]['data'] = e_val + '\n'
                G.edge[arr[col_src]][dst]['property'][e_prop]['count'] = 1
            else:
                G.edge[arr[col_src]][dst]['property'][e_prop]['data'] += e_val + '\n'
                G.edge[arr[col_src]][dst]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)








def build_graph_ip2url_site_byhour(files, time_col1, time_col2, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = {}
    #G = nx.Graph()

    utf2str = {}
    count = 0
    tmList = []
    utf_count = 0
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        while line != "":
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            curr = arr[time_col1] + arr[time_col2]
            curr_hour = atoi(curr.replace('-','').replace(':','')[0:10])
            if (G.has_key(curr_hour) == False):
                G[curr_hour] = nx.Graph()
                


            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            # xx = tldextract.extract(arr[col_dst])
            # if (xx.suffix == ''):
            #     dst = xx.domain
            # else:
            #     dst = xx.domain + '.' + xx.suffix

            dst = arr[col_dst]
            try:
                xx = dst.encode('utf-16')
            except:
                if (utf2str.has_key(dst) == False):
                    utf2str[dst] = 'xyz_' + str(utf_count)
                    utf_count += 1
                dst = utf2str[dst]
                
            G[curr_hour].add_node(arr[col_src], bipartite = 0)
            G[curr_hour].add_node(dst, bipartite = 1)
            G[curr_hour].add_edge(arr[col_src], dst)

            if (G[curr_hour].edge[arr[col_src]][dst].has_key('property')==False):
                G[curr_hour].edge[arr[col_src]][dst]['property'] = {}


            if (G[curr_hour].edge[arr[col_src]][dst]['property'].has_key(e_prop)==False):
                G[curr_hour].edge[arr[col_src]][dst]['property'][e_prop] = {}
                G[curr_hour].edge[arr[col_src]][dst]['property'][e_prop]['data'] = e_val + '\n'
                G[curr_hour].edge[arr[col_src]][dst]['property'][e_prop]['count'] = 1
            else:
                G[curr_hour].edge[arr[col_src]][dst]['property'][e_prop]['data'] += e_val + '\n'
                G[curr_hour].edge[arr[col_src]][dst]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)






def build_beacon_graph(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit):
    G = nx.Graph()
    
    count = 0
    tmList = []
    for file in files:
        print ('File: ', file)
        fp = open(file,'r')
        line = fp.readline()[:-1]
        line = fp.readline()[:-1]
        
        while line != "":
            line = 'bluecoat' + '\t' + line
            count += 1
            if (count%100000 == 0):
                print count
            if (count > line_limit):
                fp.close()
                break
            
            arr = string.split(line, delim)
            e_prop = ''
            for col in event_attrs:
                e_prop += arr[col] + '\t'
            e_prop = e_prop[:-1]

            e_val = ''
            for col in edge_attrs:
                e_val += arr[col] + '\t'
            e_val = e_val[:-1]

            xx = tldextract.extract(arr[col_dst])
            if (xx.suffix == ''):
                dst = xx.domain
            else:
                dst = xx.domain + '.' + xx.suffix

            G.add_node(arr[col_src], bipartite = 0)
            G.add_node(dst, bipartite = 1)
            G.add_edge(arr[col_src], dst)

            if (G.edge[arr[col_src]][dst].has_key('property')==False):
                G.edge[arr[col_src]][dst]['property'] = {}


            if (G.edge[arr[col_src]][dst]['property'].has_key(e_prop)==False):
                G.edge[arr[col_src]][dst]['property'][e_prop] = {}
                G.edge[arr[col_src]][dst]['property'][e_prop]['data'] = e_val + '\n'
                G.edge[arr[col_src]][dst]['property'][e_prop]['count'] = 1
            else:
                G.edge[arr[col_src]][dst]['property'][e_prop]['data'] += e_val + '\n'
                G.edge[arr[col_src]][dst]['property'][e_prop]['count'] += 1

            line = fp.readline()[:-1]
        fp.close()
        print_string = "Finished Reading" + "\t" + file + "\t" + "size" + "\t" + str(count)
        print print_string
    return(G)
