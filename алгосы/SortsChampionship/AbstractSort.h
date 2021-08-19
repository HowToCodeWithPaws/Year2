//
// Created by Natalya on 25.02.2021.
//

#ifndef SORTSCHAMPIONSHIP_ABSTRACTSORT_H
#define SORTSCHAMPIONSHIP_ABSTRACTSORT_H


#include <iostream>
#include <vector>
class AbstractSort{
public:
    virtual void sort(std::vector<int> &unordered){
        std::cout<<"this shouldn't ever happen";
    }
};


#endif//SORTSCHAMPIONSHIP_ABSTRACTSORT_H
