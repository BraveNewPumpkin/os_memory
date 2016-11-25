cmake_minimum_required(VERSION 2.8)
project(os_memory)

set(BOOST_ROOT "./Boost")
FIND_PACKAGE( Boost COMPONENTS  REQUIRED )
INCLUDE_DIRECTORIES( ${Boost_INCLUDE_DIR} )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp PageTable.cpp PageTable.h Page.cpp Page.h SegmentTable.cpp SegmentTable.h Segment.cpp Segment.h FrameTable.cpp FrameTable.h Frame.cpp Frame.h)
add_executable(os_memory ${SOURCE_FILES})
TARGET_LINK_LIBRARIES( os_memory ${Boost_LIBRARIES} )
