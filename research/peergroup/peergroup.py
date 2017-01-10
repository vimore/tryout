#!/usr/local/bin/python
import os
import sys

sys.path.insert(0, os.getcwd())
sys.path.append('/usr/lib/python2.6/site-packages')

from kcenter import *

from mds import *

from networkx import *

import json


def main():
    K = Kcenter()
    M = Mds()
    vol_thr = 5

    #for line in sys.stdin:
    #arr = array(string.split(line,'\t')[0:-1])

    fpIn = open('data2cluster','r')
    line = fpIn.readline()[:-1]
    arr = array(string.split(line,'\t'))
    
    num_cols = atoi(arr[0])
    num_rows = atoi(arr[1])
    arr = arr[2:]


    print (num_cols, num_rows, len(arr))
    indices = reshape(range(0, len(arr)), (num_rows, num_cols+1))
    rows  = arr[indices[:,0]]


    D = np.asmatrix(arr[indices[:,1:]].astype(float))
    Dn = (D-D.mean(0))/D.std(0)
    pca = K.genPCAData(Dn, True)
    Dp = pca.transform(Dn)

    k = 200
    [centers, cl_indices, radii] = K.kcenter_clustering_stopcriteria_0(Dp,k)
    C_m = np.zeros((len(centers), num_cols))
    C_p = np.zeros((len(centers), num_cols))
    volume = []
    for ii in range(len(centers)):
        I = where(cl_indices == ii)[0]
        volume.append(len(I))
        if (len(I) > 1):
            C_p[ii,:] = Dn[I,:].mean(0)
        else:
            C_p[ii,:] = Dn[I,:]

    volume = array(volume)
    [similarities,pos] = M.runMds(C_p, volume, 'Peer Group')
    

    G = nx.Graph()
    max_count = -1
    max_id = -1
    for ii in range(0, len(centers)):
        nd = 'peer_group_' + str(ii)
        G.add_node(nd)
        G.node[nd]['pos'] = pos[ii]
        G.node[nd]['count'] = volume[ii]
        I = where(cl_indices==ii)[0]
        if (len(I) < vol_thr):
            G.node[nd]['users'] = join(rows[I], '\t')
        else:
            G.node[nd]['users'] = join(rows[I[0:vol_thr]], '\t')
        if (volume[ii] > max_count):
            max_count = volume[ii]
            max_id = ii

    #for ii in range(0,len(centers-1)):
    
    node1 = 'peer_group_' + str(max_id)
    for jj in range(0, len(centers)):
        node2 = 'peer_group_' + str(jj)
        if (node1 != node2):
            G.add_edge(node1, node2)
            G.edge[node1][node2]['dist'] = similarities[ii,jj]

    return(D, C_p, volume, similarities, rows, G, pos)

def genJsonOutputForKeyLines(G):
    data = []
    ii = 1
    data.append('var chart = {' + '\n')
    data.append('\"type\": ' + '\"' + 'LinkChart' + '\"' + ',' + '\n')
    data.append('\"items\": [' + '\n')

    for nd in G.nodes():
        id_str = '{\"id\": ' + '\"' + nd + '\"' + ',' + '\n'
        t_str = '\"t\": ' + '\"label\"' + ',' + '\n'
        user_str1 = '\"Users\": ' + '\"' + str(G.node[nd]['users'])  +  '\"' + ',' + '\n'
        user_str2 = '\"User Count\": ' + '\"' + str(G.node[nd]['count'])  +  '\"' + ',' + '\n'
        type_str = '\"type\": ' + '\"' + 'node' +  '\"' + '\n' + '},' + '\n'

        data.append(id_str)
        data.append(t_str)
        data.append(user_str1)
        data.append(user_str2)
        data.append(type_str)

    ii = 1
    for ed in G.edges():
        id_str = '{\"id\": ' + '\"' + 'link'+ str(ii) + '\"' + ',' + '\n'
        id_str1 = '\"id1\": ' + '\"' + ed[0] + '\"' + ',' + '\n'
        id_str2 = '\"id2\": ' + '\"' + ed[1] + '\"' + ',' + '\n'
        t_str = '\"t\": ' + '\"label\"' + ',' + '\n'
        edge_str = '\"Similarity\": ' + '\"' + str(G.edge[ed[0]][ed[1]]['dist'])  +  '\"' + ',' + '\n'
        type_str = '\"type\": ' + '\"link\"' + '\n' + '},' + '\n'

        
        data.append(id_str)
        data.append(id_str1)
        data.append(id_str2)
        data.append(t_str)
        data.append(edge_str)
        data.append(type_str)
        ii += 1

    data.append(']' + '\n')
    data.append('};' + '\n')

    fpOut = open('graph_klines','w')
    for line in data:
        fpOut.write(line)
    fpOut.close()
    return(data)

def graph2json(G, pos):
    J = {}
    J["nodes"] = []
    J["edges"] = []

    fields = ["id", "t", "Users", "Count", "type", "x", "y"]
    for nd in G.nodes():
        nd2prop = {}
        nd2prop[fields[0]] =  nd
        nd2prop[fields[1]] =  nd 
        nd2prop[fields[2]] = G.node[nd]['users']
        nd2prop[fields[3]] = G.node[nd]['count']
        nd2prop[fields[4]] = "node"
        nd2prop[fields[5]] = G.node[nd]['pos'][0]
        nd2prop[fields[6]] = G.node[nd]['pos'][1]
        J["nodes"].append(nd2prop)


    fields = ["id", "id1", "id2", "t", "similarity", "type"]
    ii = 0
    for ed in G.edges():
        if (G.edge[ed[0]][ed[1]].has_key('dist') == True):
            similarity = G.edge[ed[0]][ed[1]]['dist']
        else:
            similarity = G.edge[ed[1]][ed[0]]['dist']

        ed2prop = {}
        ed2prop[fields[0]] = "link" + str(ii)
        ed2prop[fields[1]] = ed[0] 
        ed2prop[fields[2]] = ed[1]
        ed2prop[fields[3]] = "link" + str(ii)
        ed2prop[fields[4]] = similarity
        ed2prop[fields[5]] = "link"
        J["edges"].append(ed2prop)

    fpOut = open('graph_json','w')
    fpOut.write(json.dumps(J))
    fpOut.close()
    return(J)
    


def runPCABasedAnomalyDetection(D):
    Dn = (D-D.mean(0))/D.std(0)
    pca0 = PCA(Dn.shape[1])
    pca0.fit(Dn)

    k = 7
    Dp = pca0.transform(Dn)[:,k:]
    v = pca0.explained_variance_[k:]
    Dp2 = (Dp*Dp)/(v)
    Dp2x = Dp2.sum(1)
    I1 = where(Dp2x > 100)[0]



    k = 200
    Dp = pca0.transform(Dn)[:,0:k]
    v = pca0.explained_variance_[0:k]
    Dp2 = (Dp*Dp)/(v)
    Dp2y = Dp2.sum(1)
    I2 = where(Dp2y > 100)[0]

    figure(1)
    hist(Dp2x, 100)
    figure(2)
    hist(Dp2y, 100)
    show()

if __name__ == '__main__':
    [D, C_p, volume, similarities, users, G, pos] = main()
    #genJsonOutputForKeyLines(G)

    #graph2json(G, pos)
    M = Mds()
    M.scatterByDistance(D, volume)
