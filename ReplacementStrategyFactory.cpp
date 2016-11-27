//
// Created by Kyle Bolton on 11/25/16.
//

#include "ReplacementStrategyFactory.h"

std::vector<std::pair<const std::string, const std::string>> ReplacementStrategyFactory::getNameDescriptions() const{
  std::vector<std::pair<const std::string, const std::string>> name_descriptions;
  for(auto iter = name_description_map.cbegin(); iter != name_description_map.cend(); ++iter){
    name_descriptions.push_back(*iter);
  }
  return name_descriptions;
}
