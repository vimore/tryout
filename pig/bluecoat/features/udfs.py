from pig_util import outputSchema
import tldextract
import networkx as nx

# See https://github.com/john-kurkowski/tldextract
@outputSchema("host:tuple(subdomain:chararray, domain:chararray, suffix:chararray)")
def domain_parts(hostname):
    """
    Split a hostname into its parts: subdomain, domain, suffix
    """
    e = tldextract.extract(hostname)
    return (e.subdomain, e.domain, e.suffix)

@outputSchema("degrees:bag{item:tuple(address:chararray, degree:int)}")
def degree_of_nodes(edges):
    G=nx.Graph()
    # Build a graph from the ip/hostname
    for edge in edges:
        G.add_edge(edge[0], edge[1])
    # Now calculate degrees for all edges
    degrees = []
    for node in G.nodes():
        degrees.append((node, G.degree(node)))
    return degrees