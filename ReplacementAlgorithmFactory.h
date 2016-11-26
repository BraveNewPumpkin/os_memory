//
// Created by Kyle Bolton on 11/25/16.
//

#ifndef OS_MEMORY_REPLACEMENTALGORITHMFACTORY_H
#define OS_MEMORY_REPLACEMENTALGORITHMFACTORY_H

#include <map>
#include <vector>
#include <string>
#include "ReplacementAlgorithm.h"

class ReplacementAlgorithmFactory {
private:
  std::map<const std::string, const std::string> name_description_map;

public:
  ReplacementAlgorithmFactory():name_description_map{
     {"fifo", "first-in-first-out"},
     {"lru", "least recently used"},
     {"lfu", "least frequently used"},
     {"opt", "optimum MUST ALSO SET --lookahead"},
     {"ws", "Working Set"}
  }{

  }

  ReplacementAlgorithm getAlgorithmObject(const std::string& name) const{
    ReplacementAlgorithm replacement_algorithm;
    if(name == "fifo"){
      //TODO construct and return fifo ReplacementAlgorithm object
    } else if(name == "lru"){
      //TODO construct and return lru ReplacementAlgorithm object
    } else if(name == "lfu"){
      //TODO construct and return lfu ReplacementAlgorithm object
    } else if(name == "opt"){
      //TODO construct and return opt ReplacementAlgorithm object
    } else if(name == "ws"){
      //TODO construct and return ws ReplacementAlgorithm object
    }else{
      throw std::runtime_error("invalid replacement algorithm name: \"" + name + '"');
    }
    return replacement_algorithm;
  }

  std::vector<std::pair<const std::string, const std::string>> getNameDescriptions() const;

};


#endif //OS_MEMORY_REPLACEMENTALGORITHMFACTORY_H
