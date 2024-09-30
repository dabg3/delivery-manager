#include "../../concorde_build/concorde.h"
#include <stdio.h>
#include <stdlib.h>

/*
 * The following file is kept as a reference to understand concorde library
 */

// return 1 on errors, 0 otherwise
int gen_graph(int ncount, int ecount, CCdatagroup *dat, int **elist, int **elen) {
         CCutil_init_datagroup(dat);
         // allocate according to ncount (nodes count)
         dat->x = CC_SAFE_MALLOC (ncount, double);
         dat->y = CC_SAFE_MALLOC (ncount, double);
         dat->z = CC_SAFE_MALLOC (ncount, double);
         for(int i = 0; i < ncount; i++) {
                 dat->x[i] = rand() % 10 + 1; // [1..9]
                 dat->y[i] = rand() % 10 + 1; 
                 dat->z[i] = 0;
         }
         CCrandstate state;
         CCutil_dat_setnorm (dat, CC_EUCLIDEAN);
         return CCutil_genedgelist(ncount, ecount, elist, elen, dat, 0, &state);
}

void print_nodes_coordinates(int ncount, CCdatagroup *dat) {
        printf("node : x, y, z\n");
        for (int i = 0; i < ncount; i++) {
                printf("%d : %f, %f, %f\n", i, dat->x[i], dat->y[i], dat->z[i]);
        }
}

void print_edges(int *elist, int *elen, int ecount) {
        printf("(node, node) : weight\n");
        for (int i = 0; i < ecount; i++) {
                int pair_i = i * 2;
                printf("(%d, %d) : %d\n", elist[pair_i], elist[pair_i+1], elen[i]);
        }
}

/*
 * `elist`  is an array giving the ends of the edges (in pairs)
 * `elen` is an array giving the weights of the edges.
 * from UTIL/getdata.c CCutil_getedgelist
 * *elist = CC_SAFE_MALLOC(2 * (ecount), int);
 * *elen  = CC_SAFE_MALLOC(ecount, int);
 */

int main() {
         int *elist = (int *) NULL;
         int *elen = (int *) NULL;
         CCdatagroup dat;
         int ncount = 5;
         int ecount = 10;
         int resv = gen_graph(ncount, ecount, &dat, &elist, &elen);
         if (resv) {
                 return resv;
         }
         print_nodes_coordinates(ncount, &dat);
         print_edges(elist, elen, ecount);
}
