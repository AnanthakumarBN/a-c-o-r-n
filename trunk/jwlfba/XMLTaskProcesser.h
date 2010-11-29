/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_XMLTASKPROCESSER_H_
#define JWLFBA_XMLTASKPROCESSER_H_

#include <cstdio>
#include<string>
#include<vector>
#include"lib/pugixml.hpp"
#include"Bound.h"
#include"InputParameters.h"

using std::string;
using std::vector;
using pugi::xml_node;

// Class for handling tasks described by XML files

class XMLTaskProcesser {
    pugi::xml_document doc_;
 public:
    void Error(const string& err);
    void GetBounds(vector<Bound>* bounds);
    void Run(InputParameters parameters);
    OptimisationParameters GetOptimisationParameters(xml_node simulation);
    string GetStatus(const SimulationController& sc);
    void RunSimulation(xml_node simulation, const InputParameters& ip);
};

#endif  // JWLFBA_XMLTASKPROCESSER_H_
