/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_MODELDATABASE_H_
#define JWLFBA_MODELDATABASE_H_

#include<string>

using std::string;

class ModelDatabase {
 private:
    // list<string, Model*> models;

    string error_description;
    void error(const string& err);
 public:
    Model* getModel(const string& path);
    Model* getAmkfbaModel(const string& path);
    const string& getError() const;
    bool loadBounds(Model* model, const string& bounds_file_path);
};

#endif  // JWLFBA_MODELDATABASE_H_
