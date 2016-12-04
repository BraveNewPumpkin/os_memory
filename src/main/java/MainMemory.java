//
// Created by Kyle Bolton on 11/27/16.
//

#ifndef OS_MEMORY_MAINMEMORY_H
#define OS_MEMORY_MAINMEMORY_H

#include <unordered_map>
#include <string>
#include <memory>
#include "Segment.h"

class MainMemory {
private:
  const unsigned int max_pages;
  unsigned int used_pages;
  std::unordered_map<std::string, std::unique_ptr<Segment> > segments;
public:
  MainMemory(const unsigned int& max_pages,
             const unsigned int& min_pages,
             const unsigned int& max_pages_per_segment): max_pages(max_pages), used_pages(0){

  };
};


#endif //OS_MEMORY_MAINMEMORY_H
