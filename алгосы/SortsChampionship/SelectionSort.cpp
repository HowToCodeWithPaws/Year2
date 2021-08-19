//
// Created by Natalya on 24.02.2021.
//

#include "AbstractSort.h"
#include <vector>
class SelectionSort : public AbstractSort {
public:
    void sort(std::vector<int> &unsorted) override {
        for (size_t i = 0; i < unsorted.size() - 1; ++i) {
            size_t min = i;

            for (size_t j = i + 1; j < unsorted.size(); ++j) {
                if (unsorted[j] < unsorted[min]) {
                    min = j;
                }
            }

            if (min != i) {
                std::swap(unsorted[i], unsorted[min]);
            }
        }
    }
};