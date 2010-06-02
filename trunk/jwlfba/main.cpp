/*
 * Copyright 2010 Jakub Łącki
 */

#include<string>
#include"TextInterface.h"
using std::string;

int main(int argc, char* argv[]) {
    TextInterface ti;
    string command_line;
    for (int i = 1; i < argc; i++) {
        command_line += argv[i];
        command_line += ' ';
    }

    ti.Run(command_line);
}

