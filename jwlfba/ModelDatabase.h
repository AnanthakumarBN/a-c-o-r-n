/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_MODELDATABASE_H_
#define JWLFBA_MODELDATABASE_H_

#include<string>

using std::string;
class Model;

class ModelDatabase {
 private:
    // list<string, Model*> models;

    string error_description_;
    void Error(const string& err);
 public:
    Model* GetModel(const string& path);
    Model* GetAmkfbaModel(const string& path);
    const string& GetError() const;
};

#endif  // JWLFBA_MODELDATABASE_H_
