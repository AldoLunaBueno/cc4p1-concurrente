import random

def generate_gr_file(filename, nodes=2000, edges=10000):
    with open(filename, 'w') as f:
        f.write(f"c Grafo de gran escala para pruebas de Dijkstra\n")
        f.write(f"p sp {nodes} {edges}\n")
        
        # Ensure minimal connectivity (a simple path 1->2->3...)
        for i in range(1, nodes):
            f.write(f"a {i} {i+1} {random.randint(1, 50)}\n")
        
        # Fill the rest with random edges
        for _ in range(edges - (nodes - 1)):
            u = random.randint(1, nodes)
            v = random.randint(1, nodes)
            if u != v:
                weight = random.randint(1, 100)
                f.write(f"a {u} {v} {weight}\n")

generate_gr_file("grande.gr")
print("Archivo 'grande.gr' creado con éxito.")
