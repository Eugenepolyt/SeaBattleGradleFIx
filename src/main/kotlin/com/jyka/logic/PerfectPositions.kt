package com.jyka.logic

data class PerfectPositions(val version: Int) {
    val mapOfPositions = mutableMapOf(
            1 to listOf(
                    mutableListOf(
                            0 to 0,
                            1 to 0,
                            2 to 0,
                            3 to 0
                    ),
                    mutableListOf(
                            0 to 2,
                            1 to 2,
                            2 to 2,
                    ),
                    mutableListOf(
                            4 to 2,
                            5 to 2,
                            6 to 2,
                    ),
                    mutableListOf(
                            5 to 0,
                            6 to 0,
                    ),
                    mutableListOf(
                            8 to 0,
                            9 to 0,
                    ),
                    mutableListOf(
                            8 to 2,
                            9 to 2,
                    ),
            ),
            2 to listOf(
                    mutableListOf(
                            0 to 0,
                            1 to 0,
                            2 to 0,
                            3 to 0
                    ),
                    mutableListOf(
                            5 to 0,
                            6 to 0,
                            7 to 0,
                    ),
                    mutableListOf(
                            0 to 2,
                            0 to 3,
                            0 to 4,
                    ),
                    mutableListOf(
                            9 to 0,
                            9 to 1,
                    ),
                    mutableListOf(
                            0 to 6,
                            0 to 7,
                    ),
                    mutableListOf(
                            0 to 9,
                            1 to 9,
                    ),
            ),

            3 to listOf(
                    mutableListOf(
                            0 to 0,
                            1 to 0,
                            2 to 0,
                            3 to 0
                    ),
                    mutableListOf(
                            0 to 9,
                            1 to 9,
                            2 to 9,
                    ),
                    mutableListOf(
                            7 to 9,
                            8 to 9,
                            9 to 9,
                    ),
                    mutableListOf(
                            5 to 0,
                            6 to 0,
                    ),
                    mutableListOf(
                            8 to 0,
                            9 to 0,
                    ),
                    mutableListOf(
                            4 to 9,
                            5 to 9,
                    ),
            ),
            4 to listOf(
                    mutableListOf(
                            0 to 0,
                            0 to 1,
                            0 to 2,
                            0 to 3
                    ),
                    mutableListOf(
                            0 to 5,
                            0 to 6,
                            0 to 7,
                    ),
                    mutableListOf(
                            2 to 0,
                            3 to 0,
                            4 to 0,
                    ),
                    mutableListOf(
                            6 to 0,
                            7 to 0,
                    ),
                    mutableListOf(
                            9 to 0,
                            9 to 1,
                    ),
                    mutableListOf(
                            0 to 9,
                            1 to 9,
                    ),
            ),
    )
}