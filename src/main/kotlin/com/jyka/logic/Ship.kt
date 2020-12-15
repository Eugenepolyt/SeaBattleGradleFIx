package com.jyka.logic

class Ship(ships: MutableList<Pair<Int,Int>>) {
    var cantMoveSet: MutableSet<Pair<Int,Int>> = mutableSetOf()
    var ship = ships
    var hitCounter = 0

    fun safeRadius() {
        cantMoveSet.clear()
        val radiusList = listOf(
            -1 to 0,
            -1 to -1,
            -1 to 1,
            0 to -1,
            1 to -1,
            1 to 0,
            1 to 1,
            0 to 1,
        )
        for ((row,column) in ship) {
            for ((changeRow,changeColumn) in radiusList) {
                val finalRow = row + changeRow
                val finalColumn = column + changeColumn
                if (finalRow > 9 || finalColumn > 9 || finalRow < 0 || finalColumn < 0) continue
                if (finalRow to finalColumn in ship) continue
                cantMoveSet.add(finalRow to finalColumn)
            }
        }
    }

    fun inOcean(): Boolean {
        for (i in ship) {
            if (i.first > 9 || i.second > 9) return false
        }
        return true
    }

    private var ocean: Ocean? = null

    fun setBoard(ocean: Ocean) {
        this.ocean = ocean
    }

}
