//
// Created by Kyle Bolton on 11/25/16.
//

#ifndef OS_MEMORY_REPLACEMENTALGORITHMFACTORY_H
#define OS_MEMORY_REPLACEMENTALGORITHMFACTORY_H

#include <map>
#include <vector>
#include <string>
#include "ReplacementStrategy.h"

class ReplacementStrategyFactory {
private:
  std::map<const std::string, const std::string> name_description_map;

public:
  ReplacementStrategyFactory():name_description_map{
     {"fifo", "first-in-first-out"},
     {"lru", "least recently used"},
     {"lfu", "least frequently used"},
     {"opt", "optimum MUST ALSO SET --lookahead"},
     {"ws", "Working Set"}
  }{

  }

  ReplacementStrategy getAlgorithmObject(const std::string& name) const{
    ReplacementStrategy replacement_algorithm;
    if(name == "fifo"){
      //TODO construct and return fifo ReplacementStrategy object
    } else if(name == "lru"){
      //TODO construct and return lru ReplacementStrategy object
    } else if(name == "lfu"){
      //TODO construct and return lfu ReplacementStrategy object
    } else if(name == "opt"){
      //TODO construct and return opt ReplacementStrategy object
    } else if(name == "ws"){
      //TODO construct and return ws ReplacementStrategy object
    }else{
      throw std::runtime_error("invalid replacement algorithm name: \"" + name + '"');
    }
    return replacement_algorithm;
  }

  std::vector<std::pair<const std::string, const std::string>> getNameDescriptions() const;

};


#endif //OS_MEMORY_REPLACEMENTALGORITHMFACTORY_H
