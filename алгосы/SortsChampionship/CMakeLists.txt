cmake_minimum_required(VERSION 3.16)
project(SortsChampionship)

set(CMAKE_CXX_STANDARD 14)

add_executable(SortsChampionship main.cpp BubbleSort.cpp RadixSort.cpp HeapSort.cpp BinInsertionSort.cpp CountSort.cpp MergeSort.cpp QuickSort.cpp SelectionSort.cpp checkSorted.cpp AbstractSort.cpp AbstractSort.h)