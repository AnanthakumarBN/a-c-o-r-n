/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_LINEREADER_H_
#define JWLFBA_LINEREADER_H_

#include<string>

using std::string;

// Abstract class for reading text line-by-line

class LineReader {
 public:
    virtual string ReadLine() = 0;
    virtual void NextLine() = 0;
    virtual bool HasRemainingLines() = 0;
    virtual ~LineReader() { }
};

#endif  // JWLFBA_LINEREADER_H_
