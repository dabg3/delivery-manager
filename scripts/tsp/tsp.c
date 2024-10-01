#include "../../concorde_build/concorde.h"
#include <stdio.h>
#include <stdlib.h>

/*
 * The following file is kept as a reference to understand concorde library
 *
 * https://github.com/matthelb/concorde/blob/master/TSP/test_tsp.c
 * https://www.math.uwaterloo.ca/tsp/concorde/DOC/concorde_funcs.html
 * https://www.math.uwaterloo.ca/tsp/concorde/DOC/util.html
 * https://www.math.uwaterloo.ca/tsp/concorde/DOC/tsp.html#CCtsp_solve_dat
 *
 * What is CCrandstate ?
 * INCLUDE/util.h
 * typedef struct CCrandstate {
 *     int a;
 *     int b;
 *     int arr[55];
 * } CCrandstate;
 */

// return 1 on errors, 0 otherwise
int gen_graph(int ncount, int ecount, CCdatagroup *dat, int **elist, int **elen, CCrandstate *rstate) {
         CCutil_init_datagroup(dat);
         // allocate according to ncount (nodes count)
         dat->x = CC_SAFE_MALLOC (ncount, double);
         dat->y = CC_SAFE_MALLOC (ncount, double);
         dat->z = CC_SAFE_MALLOC (ncount, double);
         // random nodes coordinates
         for(int i = 0; i < ncount; i++) {
                 dat->x[i] = rand() % 10 + 1; // [1..9]
                 dat->y[i] = rand() % 10 + 1; 
                 dat->z[i] = 0;
         }
         CCutil_dat_setnorm (dat, CC_EUCLIDEAN);
         return CCutil_genedgelist(ncount, ecount, elist, elen, dat, 0, rstate);
}

static double init_ub = CCtsp_LP_MAXDOUBLE;
static double in_timebound = 0.0;

void find_best_route(int ncount, CCdatagroup *dat, int* out_tour, CCrandstate *rstate) {
        double optval;
        int success;
        int foundtour;
        double *mybnd = (init_ub == CCtsp_LP_MAXDOUBLE ? (double *) NULL : &init_ub);
        double *mytimebound = (in_timebound == 0.0 ? (double *) NULL : &in_timebound);
        int *in_tour = NULL;
        CCtsp_solve_dat(ncount, 
                        dat, 
                        in_tour,
                        out_tour, 
                        mybnd,          //inval
                        &optval, 
                        &success, 
                        &foundtour, 
                        "example",      //prefix for tmp files
                        mytimebound,    //timebound
                        0,              //hit_timebound
                        0,              //silent
                        rstate,         //*CCrandstate rstate
                        0,              //maxchunksize
                        0,              //hostport
                        0,              //listen_callback
                        0);             //callback_data
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

void print_out_tour(int *out_tour, int ncount) {
        printf("Best route: ");
        for(int i = 0; i < ncount - 1; i++) {
                printf("%d -> ", out_tour[i]);
        }
        printf("%d\n", out_tour[ncount - 1]);
}

/*
 * `elist` is an array giving the ends of the edges (in pairs)
 * `elen` is an array giving the weights of the edges.
 * from UTIL/getdata.c CCutil_genedgelist
 * *elist = CC_SAFE_MALLOC(2 * (ecount), int);
 * *elen  = CC_SAFE_MALLOC(ecount, int);
 *
 *
 * node at index 0 is the starting point (depot)
 */

int main() {
        int ncount = 500;
        int ecount = ncount * (ncount - 1) / 2; //MAX edges
        int *elist = (int *) NULL;
        int *elen = (int *) NULL;
        CCrandstate rstate;
        int seed = (int) CCutil_real_zeit ();
        CCutil_sprand (seed, &rstate);
        CCdatagroup dat;
        // edges are useless for this use case actually,
        // any node is connected to every other node.
        // Just fill x,y,z arrays in CCdatagroup
        int resv = gen_graph(ncount, ecount, &dat, &elist, &elen, &rstate);
        if (resv) {
                return resv;
        }
        print_nodes_coordinates(ncount, &dat);
        print_edges(elist, elen, ecount);
        int *out_tour = CC_SAFE_MALLOC(ncount, int);
        find_best_route(ncount, &dat, out_tour, &rstate);
        print_out_tour(out_tour, ncount);
}
