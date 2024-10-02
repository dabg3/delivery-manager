#include "delivery.h"
#include <stdio.h>
#include <concorde.h>

/* Notes
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

void init_datagroup(int ncount, double coordinates[], CCdatagroup *dat) {
        CCutil_init_datagroup(dat);
        // allocate according to ncount (nodes count)
        dat->x = CC_SAFE_MALLOC (ncount, double);
        dat->y = CC_SAFE_MALLOC (ncount, double);
        for (int i = 0; i < ncount; i++) {
                dat->x[i] = coordinates[i * 2];
                dat->y[i] = coordinates[i * 2 + 1];
        }
        CCutil_dat_setnorm (dat, CC_EUCLIDEAN);
}

// obscure parameters
static double init_ub = CCtsp_LP_MAXDOUBLE;
static double in_timebound = 0.0;

void process_datagroup(int ncount, CCdatagroup *dat, CCrandstate *rstate, int out_tour[]) {
        // the following 3 variables may be useful to get informations about the result
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
                        NULL,           //prefix for tmp files. NULL is dangerous in parallel
                        mytimebound,    //timebound
                        0,              //hit_timebound
                        0,              //silent
                        rstate,         //*CCrandstate rstate
                        0,              //maxchunksize
                        0,              //hostport
                        0,              //listen_callback
                        0);             //callback_data

}

void find_best_route(int ncount, double coordinates[], int out_route[]) {
        CCrandstate rstate;
        int seed = (int) CCutil_real_zeit ();
        CCutil_sprand (seed, &rstate);
        CCdatagroup dat;
        init_datagroup(ncount, coordinates, &dat);
        process_datagroup(ncount, &dat, &rstate, out_route);
        CCutil_freedatagroup(&dat);
}

void print_route(int ncount, int *out_route) {
        printf("Best route: ");
        for(int i = 0; i < ncount - 1; i++) {
                printf("%d -> ", out_route[i]);
        }
        printf("%d\n", out_route[ncount - 1]);
}
