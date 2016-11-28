//
// Created by Kyle Bolton on 11/26/16.
//

#ifndef OS_MEMORY_INPUTPARSER_H
#define OS_MEMORY_INPUTPARSER_H


#include <iosfwd>
#include <istream>
#include <string>

#include "InputData.h"
#include "ReplacementStrategy.h"

class InputParser {
private:
public:
  std::unique_ptr<InputData> parse(std::istream& input, const ReplacementStrategy& replacement_strategy);

};


#endif //OS_MEMORY_INPUTPARSER_H
