/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_FILELINEREADER_H_
#define JWLFBA_FILELINEREADER_H_

#include<cstdio>
#include<string>
#include"LineReader.h"

using std::string;

class FileLineReader : public LineReader {
    FILE* file_;
    string next_line_;
    void FetchLine();
 public:
    FileLineReader();
    virtual ~FileLineReader();
    bool LoadFile(const string& path);

    virtual string ReadLine();
    virtual void NextLine();
    virtual bool HasRemainingLines();
};

#endif  // JWLFBA_FILELINEREADER_H_
