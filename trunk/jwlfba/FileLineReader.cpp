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

FileLineReader::FileLineReader() : file(NULL) { }

FileLineReader::~FileLineReader() {
    if (file)
        fclose(file);
}

bool FileLineReader::loadFile(const string& path) {
    if (path == kStandardInputPath)
        file = stdin;
    else
        file = fopen(path.c_str(), "r");
    return  file != NULL;
}


void FileLineReader::fetchLine() {
    char buf[kFileLineReaderBuffer];
    bool newline_reached = false;

    if (!next_line.empty())
        return;

    while (!newline_reached && fgets(buf, kFileLineReaderBuffer, file)) {
        int len = strlen(buf);
        if (buf[len-1] == '\n') {
            buf[len-1] = '\0';
            newline_reached = true;
        }
        next_line += buf;
    }
}

string FileLineReader::readLine() {
    fetchLine();
    return next_line;
}

void FileLineReader::nextLine() {
    next_line = "";
    fetchLine();
}

bool FileLineReader::hasRemainingLines() {
    fetchLine();
    return next_line != "";
}

