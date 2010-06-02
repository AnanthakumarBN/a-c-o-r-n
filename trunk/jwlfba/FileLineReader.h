/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_FILELINEREADER_H_
#define JWLFBA_FILELINEREADER_H_

#include<cstdio>
#include<string>
#include"LineReader.h"

using std::string;

// Interface for reading a file line-by-line

class FileLineReader : public LineReader {
    // Pointer to the file object. Note that stdin is also a FILE*
    FILE* file_;

    // Buffer containing the current line to be read.
    // Empty buffer means that a line has to be fetched.
    string next_line_;

    // Loads one line from file to next_line_ if necessary
    // (i.e. when it's empty).
    void FetchLine();
 public:
    FileLineReader();
    virtual ~FileLineReader();

    // Loads the file with a given path. '-' means standard input.
    bool LoadFile(const string& path);

    // Returns the current line (without the newline character)
    // or "" if the whole file has been read.
    virtual string ReadLine();

    // Advances to next line
    virtual void NextLine();

    // Returns true iff there are still lines to be read, that is ReadLine()
    // returns an existing line.
    virtual bool HasRemainingLines();
};

#endif  // JWLFBA_FILELINEREADER_H_
