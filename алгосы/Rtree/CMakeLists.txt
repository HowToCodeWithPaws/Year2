cmake_minimum_required(VERSION 3.16)
project(Rtree)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_FLAGS "-O3 -Wall -Wextra")

function(single_executable)
    list(GET ARGN 0 FILE_NAME)
    file(GLOB SOURCE_EXE ${FILE_NAME})
    get_filename_component(FILE_NAME_WE ${FILE_NAME} NAME_WE)
    add_executable(${PROJECT_NAME}_${FILE_NAME_WE} ${SOURCE_EXE})
endfunction()

function(single_executable_link_libraries)
    list(GET ARGN 0 FILE_NAME)
    get_filename_component(FILE_NAME_WE ${FILE_NAME} NAME_WE)
    list(REMOVE_AT ARGN 0)
    target_link_libraries(${PROJECT_NAME}_${FILE_NAME_WE} ${ARGN})
endfunction()

find_package(GDAL REQUIRED)
find_library(GDAL_LIB_FILE NAMES GDAL gdal REQUIRED)
message(${GDAL_LIB_FILE})
message(${GDAL_LIBRARIES})
get_filename_component(GDAL_LIB ${GDAL_LIB_FILE} DIRECTORY)
get_filename_component(INSTALLED_LIBS ${GDAL_LIB} DIRECTORY)
include_directories(${INSTALLED_LIBS}/include/)

single_executable(main.cpp)
single_executable_link_libraries(main.cpp ${GDAL_LIBRARIES})


add_executable(Rtree main.cpp)