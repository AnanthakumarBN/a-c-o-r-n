/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_REACTIONFLUX_H_
#define JWLFBA_REACTIONFLUX_H_

#include<string>

using std::string;

// Represents a result of the simulation: the flux through a single reaction.

struct ReactionFlux {
    string reaction;
    double flux;
    ReactionFlux(string r, double f) : reaction(r), flux(f) { }
};

#endif  // JWLFBA_REACTIONFLUX_H_
