project(solver)

cmake_minimum_required(VERSION 2.8)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++1z")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -Weffc++ -pedantic")

add_executable(solver src/solver.cxx)
target_link_libraries(solver pthread)

# Locate Boost libraries: unit_test_framework, date_time and regex
set(Boost_USE_STATIC_LIBS ON)
set(Boost_USE_MULTITHREADED ON)
set(Boost_USE_STATIC_RUNTIME OFF)
find_package(Boost 1.55 REQUIRED COMPONENTS unit_test_framework date_time regex)

# We need boost includes
target_include_directories(solver PRIVATE ${Boost_INCLUDE_DIRS})

# We need boost libraries
target_link_libraries(solver ${Boost_LIBRARIES})
