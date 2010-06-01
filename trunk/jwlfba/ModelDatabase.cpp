/*
 * Copyright 2010 Jakub Łącki
 */

#include"ModelDatabase.h"
#include<sbml/SBMLTypes.h>
#include<string>
#include"FileLineReader.h"
#include"ModelBuilder.h"

using std::string;

Model* ModelDatabase::getModel(const string& path) {
    SBMLDocument* document = readSBML(path.c_str());
    if (document->getNumErrors() > 0) {
        error(document->getError(0)->getShortMessage());
        return NULL;
    }

    return document->getModel();
}

Model* ModelDatabase::getAmkfbaModel(const string& path) {
    Model* model;
    FileLineReader fr;
    if (fr.loadFile(path)) {
        error("Error opening '" + path + "'");
        return NULL;
    }
    ModelBuilder mb;
    model = mb.loadFromAmkfbaFile(&fr);

    if (model == NULL)
        error(mb.getError());
    return model;
}

bool ModelDatabase::loadBounds(Model* model, const string& path) {
    FileLineReader fr;
    if (fr.loadFile(path)) {
        error("Error opening '" + path + "'");
        return false;
    }

    while (fr.hasRemainingLines()) {
        StringTokenizer st;
        st.parse(fr.readLine());

        if (reactions_map.find(st.currentToken()) == reactions_map.end()) {
            error("Reaction id '" + st.currentToken() + "' not found");
            return NULL;
        }



        fr.nextLine();
    }
}

void ModelDatabase::error(const string& err) {
    error_description = err;
}

const string& ModelDatabase::getError() const {
    return error_description;
}
