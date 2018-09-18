package com.example.vangelis.connectingdevices.not_used.backgroundTasks2;

interface SequentialArrayProcessor<T, R> {
    R computeValue(T[] values, int lowIndex, int highIndex);
}
