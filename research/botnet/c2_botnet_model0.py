import sys
sys.path.append('/home/hivedata/srinivas/GraphModeling/')

from networkx import *

from graphDB import *

from botnet_features import *


print "enter a number"
xx = atoi(raw_input())
if xx > 0:
    files = []
    files.append('raw_data/bluecoat_parsed.log')
    time_col = 0
    col_src = 3
    col_dst = 10
    event_attrs = [0, 4, 5, 8, 16, 17, 18, 19]
    edge_attrs = [0, 1, 2, 6, 7, 11]
    delim = '\t'
    line_limit = 1000000
    Gt = build_graph_ip2url_sld(files, time_col, col_src, col_dst, event_attrs, edge_attrs, delim, line_limit)
cleanup_the_graph(Gt)
Gn = copyGraph(Gt)

#G0 = extract_graph_by_edge_field(Gt,'2005-04-30')
#G1 = extract_graph_by_edge_field(Gt,'2005-05-01')
#G2 = extract_graph_by_edge_field(Gt,'2005-05-02')
#G3 = extract_graph_by_edge_field(Gt,'2005-05-03')
