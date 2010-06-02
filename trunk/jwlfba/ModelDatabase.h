/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_MODELDATABASE_H_
#define JWLFBA_MODELDATABASE_H_

#include<string>

using std::string;
class Model;

// Responsible for reading models from files.

// TODO(me): extend this with a model cache

class ModelDatabase {
 private:
    // list<string, Model*> models;
    // Description of an error encoutered.
    string error_description_;

    // Store an error.
    void Error(const string& err);
 public:
    // Load a XML model from the given file. '-' stands for stdin.
    Model* GetModel(const string& path);
    // Load an amkfba model from the given file. '-' stands for stdin.
    Model* GetAmkfbaModel(const string& path);
    // Return the error description.
    const string& GetError() const;
};

#endif  // JWLFBA_MODELDATABASE_H_
