//
// Created by Kyle Bolton on 11/26/16.
// interface for memory replacement algorithm objects
//

#ifndef OS_MEMORY_REPLACEMENTALGORITHM_H
#define OS_MEMORY_REPLACEMENTALGORITHM_H

#include <string>

class ReplacementStrategy {
private:
  std::string name;
public:
  ReplacementStrategy(std::string name):name(name){}
  std::string getName() const;
};


#endif //OS_MEMORY_REPLACEMENTALGORITHM_H
