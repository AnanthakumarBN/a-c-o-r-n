/*
 * Copyright 2010 Jakub Łącki
 */

#include"ModelDatabase.h"
#include<sbml/SBMLTypes.h>
#include<string>
#include"FileLineReader.h"
#include"ModelBuilder.h"
#include"StringTokenizer.h"

using std::string;

Model* ModelDatabase::GetModel(const string& path) {
    SBMLDocument* document;
    if (models_.find(path) == models_.end()) {
        document = readSBML(path.c_str());
        if (document->getNumErrors() > 0) {
            Error(document->getError(0)->getShortMessage());
            // FIXME: this shouldn't be commented out, but without that
            // using badly formed SBML models (which is very often the case)
            // does not work
            // document = NULL;
        }
        models_[path] = document;
    }
    else
        document = models_[path];

    if (document == NULL)
        return NULL;
    else
        return document->getModel();
}

Model* ModelDatabase::GetAmkfbaModel(const string& path) {
    Model* model;
    FileLineReader fr;
    if (!fr.LoadFile(path)) {
        Error("Error opening '" + path + "'");
        return NULL;
    }
    ModelBuilder mb;
    model = mb.LoadFromAmkfbaFile(&fr);

    if (model == NULL)
        Error(mb.GetError());
    return model;
}

void ModelDatabase::Error(const string& err) {
    error_description_ = err;
}

const string& ModelDatabase::GetError() const {
    return error_description_;
}
