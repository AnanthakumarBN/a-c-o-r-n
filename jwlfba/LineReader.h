/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_LINEREADER_H_
#define JWLFBA_LINEREADER_H_

#include<string>

using std::string;

class LineReader {
 public:
    virtual string readLine() = 0;
    virtual void nextLine() = 0;
    virtual bool hasRemainingLines() = 0;
    virtual ~LineReader() { }
};

#endif  // JWLFBA_LINEREADER_H_
