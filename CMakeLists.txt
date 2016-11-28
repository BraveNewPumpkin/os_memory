cmake_minimum_required(VERSION 2.8)
project(os_memory)

set(Boost_USE_STATIC_LIBS ON)
set(BOOST_ROOT ./)
#set(Boost_LIBRARY_DIR ./lib)
set(Boost_NO_SYSTEM_PATHS)
FIND_PACKAGE( Boost COMPONENTS program_options REQUIRED )
INCLUDE_DIRECTORIES( ${Boost_INCLUDE_DIR} )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp PageTable.cpp PageTable.h Page.cpp Page.h SegmentTable.cpp SegmentTable.h Segment.cpp Segment.h FrameTable.cpp FrameTable.h Frame.cpp Frame.h ReplacementStrategyFactory.cpp ReplacementStrategyFactory.h ReplacementStrategy.h ReplacementStrategy.cpp InputParser.cpp InputParser.h InputData.h)
add_executable(os_memory ${SOURCE_FILES})
TARGET_LINK_LIBRARIES( os_memory ${Boost_LIBRARIES} )
