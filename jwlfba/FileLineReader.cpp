/*
 * Copyright 2010 Jakub Łącki
 */

#include"FileLineReader.h"
#include<cstdio>
#include<cstring>
#include<string>

using std::string;

const int kFileLineReaderBuffer = 1000;
const char* kStandardInputPath = "-";

FileLineReader::FileLineReader() : file_(NULL) { }

FileLineReader::~FileLineReader() {
    if (file_)
        fclose(file_);
}

bool FileLineReader::LoadFile(const string& path) {
    if (path == kStandardInputPath)
        file_ = stdin;
    else
        file_ = fopen(path.c_str(), "r");
    return  file_ != NULL;
}


void FileLineReader::FetchLine() {
    char buf[kFileLineReaderBuffer];
    bool newline_reached = false;

    if (!next_line_.empty())
        return;

    while (!newline_reached && fgets(buf, kFileLineReaderBuffer, file_)) {
        int len = strlen(buf);
        if (buf[len-1] == '\n') {
            buf[len-1] = '\0';
            newline_reached = true;
        }
        next_line_ += buf;
    }
}

string FileLineReader::ReadLine() {
    FetchLine();
    return next_line_;
}

void FileLineReader::NextLine() {
    next_line_ = "";
    FetchLine();
}

bool FileLineReader::HasRemainingLines() {
    FetchLine();
    return next_line_ != "";
}

