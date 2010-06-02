/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_BOUND_H_
#define JWLFBA_BOUND_H_

#include<string>

using std::string;

// Represents a bound for a flux through a reaction.

struct Bound {
    string reaction_id;
    double lower_bound, upper_bound;
};

#endif  // JWLFBA_BOUND_H_
